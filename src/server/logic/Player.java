package server.logic;

import dao.PlayerDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import server.gameServer.GameServer;
import server.gameServer.WaitingRoom;

/**
 * A játékos viselkedését reprezentáló osztály
 * @author Faludi Péter
 */
public class Player implements Runnable{
    private static final int MAXIMUM_SCORE = 21;
    private static final double BLACKJACK_PAYOUT = 1.5;
    private static final String SERV_AUTH = "SERVERCOMMAND";
    private static final String DELIMETER = "--";
    private ServerTable table;
    private BufferedReader in;
    private PrintWriter out;
    private final LinkedList<RuleHand> playerHands = new LinkedList<>();
    private RuleHand hand;
    private double money;
    private boolean hasBlackjack = false;
    private final String username;
    private String choice ;
    private boolean isAnswered = false;
    private double insuranceBet = 0.0;
    private boolean placedInsuranceBet = false;
    private CountDownLatch startLatch;
    private CountDownLatch betLatch;
    private CountDownLatch insuranceBetLatch;
    private CountDownLatch dealLatch;
    private CountDownLatch dealerTurnLatch;
    private CountDownLatch firstPlayerCardLatch;
    private CountDownLatch secondPlayerCardLatch;
    private CountDownLatch firstDealerCardLatch;
    private boolean continuePlaying = true;
    private final int id;
    private Socket socket;
    private int lose;
    private int win;
    private GameServer server;
    private boolean left = false;
    private boolean timeOut;
    
    /**
     *
     * @param id
     * @param username
     * @param money
     * @param win
     * @param lose
     */
    public Player(int id,String username , double money ,int win,int lose){
        this.id = id;
        this.win = win;
        this.lose = lose;
        this.money = money;
        this.username = username;
    }
    
    

    /**
     * A játékos egy körét leíró metódus
     */
    public void playBlackjack() throws InterruptedException{
        reset();
        getBet();
        betLatch.await();
        dealLatch.await();
        newRound();
        dealerTurnLatch.await();
        dealerTurn();
        playerHands.forEach((_item)->{
            resultTurn(hand);
        });
        continueTurn();
        
    }
    
    /**
     * Játékos adatainak alaphelyzetbe állítása
     */
    public void reset(){
        playerHands.clear();
        hand = new RuleHand();
        playerHands.add(hand);
        hasBlackjack = false;
        isAnswered = false;
        placedInsuranceBet = false;
        continuePlaying = false;
        startLatch = new CountDownLatch(1);
        betLatch = new CountDownLatch(1);
        firstPlayerCardLatch = new CountDownLatch(1);
        secondPlayerCardLatch = new CountDownLatch(1);
        firstDealerCardLatch = new CountDownLatch(1);
        insuranceBetLatch = new CountDownLatch(1);
        dealLatch = new CountDownLatch(1);
        dealerTurnLatch = new CountDownLatch(1);
        send("WAITING WELCOME");
    }
    
    /*
    * A játékos tét rakásáért felelős metódus
    */
    private void getBet(){
        isAnswered = false;
        do{
            
            send("BETTURN" + numToString(money)+ numToString(table.getMinBet()));
            getAnswer();
            if(choice.equals("LEAVE") || choice.equals("MIN")){
                hand.setBet(table.getMinBet());
                break;
            }
            try{
                int bet = Integer.parseInt(choice);
                hand.setBet(bet);
            }catch(NumberFormatException e){
               
            }
            if(hand.getBet()> money){
                send("BETINFO MAXIMUM");
                isAnswered = false;
            }else if(hand.getBet()< table.getMinBet()){
                send("BETINFO MINIMUM");
                isAnswered = false;
            }
        }while(!isAnswered);
        money -= hand.getBet();
        table.countDown("bet");
        send("BETINFO OK" + numToString(money));
        if(table.getTableSize()> 1){
            send("WAITING BET");
        }
    }
    
    /**
     * Új kör
     */
    public void newRound(){
        send("NEWROUND" + numToString(money));
        send("CLEARHAND 0");
        send("PLAYERCARD 0 " + hand.getCard(0));
        table.countDown("firstplayer");
        try {
            firstPlayerCardLatch.await();
        } catch (InterruptedException ex) {
        }
        
        send("NEWDEALERCARD " + table.dealerFirstCard());
        table.countDown("firstdealer");
        try {
            firstDealerCardLatch.await();
        } catch (InterruptedException ex) {
        }
        send("PLAYERCARD 0 " + hand.getCard(1));
        table.countDown("secondplayer");
        try {
            secondPlayerCardLatch.await();
        } catch (InterruptedException ex) {
        }
        send("NEWDEALERCARD backside");
        send("VALUE 0 " + hand.greaterValue());
        send("BET 0" + numToString(hand.getBet()));
        
        if(hand.greaterValue()== MAXIMUM_SCORE){
            send("HAVEBLACKJACK PLAYER");
            hasBlackjack = true;
        }
        
        if(table.dealerFirstCard().getRank()== Rank.ACE){
            insuranceTurn();
        }
        table.countDown("insurance");
        try{
            insuranceBetLatch.await();
        }catch(InterruptedException e){
        }
        if(table.dealerFirstCard().getRank()== Rank.ACE 
                && table.dealersHand().greaterValue()== MAXIMUM_SCORE)
        
        if(table.dealerFirstCard().getRank()== Rank.ACE){
            send("INSURANCEOK");
        }
        table.countDown("waiting");
        if(table.getTableSize()> 1){
            send("WAITING TURN");
        }
    }
    

    /*
    * A biztosítás kötésért felelős metódus
    */
    private void insuranceTurn(){
        if(money > hand.getBet()/ 2){
            isAnswered = false;
            do{
                send("INSURANCETURN");
                getAnswer();
                if(choice.equals("LEAVE")){
                    choice = "No";
                    break;
                }
                if(!choice.equals("Yes")&& !choice.equals("No") && !choice.equals("LEAVE")){
                    send("INSURANCEBET ERROR");
                    isAnswered = false;
                }
            }while(!isAnswered);
            if(choice.equals("Yes")){
                insuranceBet = hand.getBet() / 2.0;
                money -= insuranceBet;
                placedInsuranceBet = true;
                send("INSURANCEINFO OK" + numToString(insuranceBet)+ numToString(money));
            }else if(choice.equals("No")){
                send("INSURANCEINFO NO");
            }
        }else{
            send("NOTENOUGH");
        }
        if(table.getTableSize() > 1){
            send("WAITING INSURANCE");
        }
    }
    
    /**
     * A játékos mozdulataiért felelős metódus
     * @param aHand melyik kezére játszik,csak az első kéz van implementálva
     */
    public void yourTurn(RuleHand aHand){
        if(aHand == hand){
            send("YOURTURN");
             if(hasBlackjack && !table.dealerHasBlackjack()){
                send("TURNSKIP");
            }
        }
        if(!hasBlackjack 
                && !aHand.isSplitHand()
                && aHand.getSize() == 2
                && money >= aHand.getBet()){
            doubleDownOption(hand);
        }else if(!hasBlackjack
                && !aHand.isSplitHand()
                && !aHand.isDoubleDown()){
            hitStayOption(hand);
        }
        switch(choice){
            case "Double Down":
                doubleDown(hand);
                break;
            case "Hit":
            case "Stand":
                hitStand(hand);
                break;
        }
        if(table.getTableSize()> 1 
                && !hasBlackjack
                && !table.dealerHasBlackjack()
                && aHand == playerHands.getLast()){
            send("WAITING TURN");
        }
    }



    /*
    * Duplázást végző metódus
    */
    private void doubleDownOption(RuleHand aHand){
        isAnswered = false;
        do{
            send("VALUE " + playerHands.indexOf(aHand)+ " " + aHand.greaterValue());
            send("MOVEOPTION DOUBLE " + playerHands.indexOf(aHand));
            getAnswer();
            if(choice.equals("LEAVE")){
                choice = "Stand";
                break;
            }
            if(!choice.equals("Hit")
                    && !choice.equals("Stand")
                    && ! choice.equals("Double Down")){
                send("MOVEERROR " + playerHands.indexOf(aHand));
                isAnswered = false;
            }
        }while(!isAnswered);
    }
    /*
    * lapkérést illetve megállást végző metódus 
    */
    private void hitStayOption(RuleHand aHand){
        isAnswered = false;
        do{
            send("VALUE " + playerHands.indexOf(aHand)+ " " + aHand.greaterValue());
            send("MOVEOPTION HITSTAY " + playerHands.indexOf(aHand));
            getAnswer();
            if(choice.equals("LEAVE")){
                choice = "Stand";
                break;
            }
            if(!choice.equals("Hit")
                    && !choice.equals("Stand")){
                send("MOVEERROR " + playerHands.indexOf(aHand));
                isAnswered = false;
            }
        }while(!isAnswered);
    }
    
    
    
    private void doubleDown(RuleHand aHand){
        aHand.setDoubleDown();
        money -= aHand.getBet();
        aHand.setBet(aHand.getBet()*2);
        Card newCard = table.dealCard();
        aHand.setDoubleDownCard(newCard);
        send("BET " + playerHands.indexOf(aHand)+  numToString(aHand.getBet()));
        send("PLAYERCARD " + playerHands.indexOf(aHand)+" "+ newCard);
        send("VALUE " + playerHands.indexOf(aHand)+ " " + aHand.greaterValue());
        send("DOUBLEINFO OK " + playerHands.indexOf(aHand)+  numToString(money));
    }
    
    private void hitStand(RuleHand aHand){
        if(choice.equals("Hit")){
            Card newCard = table.dealCard();
            aHand.addCard(newCard);
            send("PLAYERCARD " + playerHands.indexOf(aHand)+ " " + newCard);
            while(choice.equals("Hit")&& aHand.greaterValue()<= MAXIMUM_SCORE){
                hitStayOption(aHand);
                if(choice.equals("Hit")){
                    newCard = table.dealCard();
                    aHand.addCard(newCard);
                    send("PLAYERCARD " + playerHands.indexOf(aHand)+ " " + newCard);
                }
                send("VALUE " + playerHands.indexOf(aHand)+ " " + aHand.greaterValue());
                if(aHand.greaterValue()> MAXIMUM_SCORE){
                    send("BUST " + playerHands.indexOf(aHand));
                }
           }
        }
    }
    
    /*
    * Osztó lapjainak küldée
    */
    private void dealerTurn(){
        send("RESULT");
        send("REMOVEHIDDENCARD");
        for(int i = 1; i < table.dealersHand().getSize(); i++){
            send("NEWDEALERCARD " + table.dealersHand().getCard(i));
        }
        send("DEALERVALUE " + table.dealersHand().greaterValue());
        if(hasBlackjack && table.dealerHasBlackjack()){
            send("HAVEBLACKJACK DEALER&PLAYER");
            if(placedInsuranceBet){
                money += 3 * insuranceBet;
                send("WININSURANCE" + numToString(insuranceBet * 2)+ numToString(money));
            }
        }else if(table.dealerHasBlackjack() && !hasBlackjack){
            send("HAVEBLACKJACK DEALER");
            if(placedInsuranceBet){
                money += 3 * insuranceBet;
                send("WININSURANCE" + numToString(insuranceBet * 2)+ numToString(money));
            }
        }else if(!table.dealerHasBlackjack()){
            send("HAVEBLACKJACK NODEALER");
            if(placedInsuranceBet){
                send("LOSTINSURANCE");
            }
        }
    }
    
    /*
    * Eredmények küldése
    */
    private void resultTurn(RuleHand aHand){
        send("VALUE " + playerHands.indexOf(aHand)+ " " + aHand.greaterValue());
        if(!hasBlackjack && !table.dealerHasBlackjack()){
            if(aHand.greaterValue()> MAXIMUM_SCORE ){
                send("WINNERS BUST DEALER " + playerHands.indexOf(aHand)+ numToString(money));
                lose++;
            } else if(table.dealersHand().greaterValue()> MAXIMUM_SCORE  && aHand.getValue() <= MAXIMUM_SCORE){
                money += aHand.getBet()* 2;
                send("WINNERS BUST PLAYER " + playerHands.indexOf(aHand)+numToString(money));
                win++;
            } else{
                if(aHand.greaterValue()== table.dealersHand().greaterValue()){
                    money += aHand.getBet();
                    send("WINNERS NORMAL TIE " + playerHands.indexOf(aHand)+  numToString(money));
                } else if(aHand.greaterValue()< table.dealersHand().greaterValue()){
                    send("WINNERS NORMAL DEALER " + playerHands.indexOf(aHand)+  numToString(money));
                    lose++;
                } else if(aHand.greaterValue()> table.dealersHand().greaterValue()){
                    money += aHand.getBet()* 2;
                    send("WINNERS NORMAL PLAYER " + playerHands.indexOf(aHand)+ numToString(money));
                    win++;
                }
            }
        }else{
            if(hasBlackjack && table.dealerHasBlackjack()){
                money += aHand.getBet();
                send("WINNERS HAVEBLACKJACK TIE " + playerHands.indexOf(aHand)+ numToString(money));
            } else if(!hasBlackjack && table.dealerHasBlackjack()){
                send("WINNERS HAVEBLACKJACK DEALER " + playerHands.indexOf(aHand)+ numToString(money));
                lose++;
            } else if(hasBlackjack && !table.dealerHasBlackjack()){
                money +=(aHand.getBet()+(aHand.getBet()*(BLACKJACK_PAYOUT)));
                send("WINNERS HAVEBLACKJACK PLAYER " + playerHands.indexOf(aHand)+numToString(money));
                win++;
            }
        }
    }


    
    /*
    * A játék folyatásáért felelős függvény
    */
    private void continueTurn(){
        if(money >= table.getMinBet()){
            isAnswered = false;
            do{
                send("CONTINUE");
                getAnswer();
                if(choice.equals("LEAVE")){
                    choice = "No";
                    break;
                }
                if(!choice.equals("Yes")&& !choice.equals("No")){
                    send("CONTINUEINFO ERROR");
                    isAnswered =false;
                }
            }while(!isAnswered);
            if(choice.equals("Yes")){
                continuePlaying = true;
                send("CONTINUEINFO CONTINUE");
            }else{
                table.removePlayerFromTable(this);
                PlayerDao.getInstance().update(this);
            }
        }else{
            table.removePlayerFromTable(this);
            PlayerDao.getInstance().update(this);
        }
        table.countDown("playing");
    }
    
    /**
     * Kliens üzenet fogadása
     */
    protected void getAnswer(){
        try{
            while(!isAnswered){
                String clientMessage;
                if(socket.isClosed()){
                    clientMessage = "LEAVE";
                    left = true;
                    server.getOnlinePlayers().remove(this);
                    isAnswered = true;
                    return;
                }
                if(left){
                    choice = "LEAVE";
                    isAnswered = true;
                    return;
                }
                if((clientMessage = in.readLine())!= null ){
//                    System.out.println(clientMessage);
                    choice = clientMessage;
                    if(choice.equals("LEAVE") ){
                        left = true;
                        server.getOnlinePlayers().remove(this);
                        isAnswered = true;
                        return;
                    }
//                    System.out.println(this.username + " " +choice);
                    isAnswered = true;
                }
            }
        }catch(IOException e){
            choice ="LEAVE";
            server.getOnlinePlayers().remove(this);
            left = true;
            isAnswered = true;
        }
    }
    
    /**
     * Üzenetek küldése formázva
     * @param rawString
     */
    public void send(String rawString){
        String ans = rawString.replace(" ", DELIMETER);
        ans  = ans.toUpperCase();
//        System.out.println(SERV_AUTH + DELIMETER + table.getId()+ DELIMETER + id + DELIMETER + ans);
        if(!socket.isClosed() && !left){
            out.println(SERV_AUTH + DELIMETER + table.getId()+ DELIMETER + id + DELIMETER + ans);
        }
        table.sendMoveToPlayers(this, SERV_AUTH + DELIMETER + "MOVE" + DELIMETER + table.getId()+ DELIMETER + id + DELIMETER + ans);
        table.writeLog(table.getId()+ "_" + id + "_" + ans);
    }
    
    private String numToString(double num){
        return DELIMETER + String.format("%.2f", num);
    }

    /**
     * latch visszaszámoltatása eggyel
     */
    public void startLatchCountDown(){
        startLatch.countDown();
    }
    
    /**
     * latch visszaszámoltatása eggyel
     */
    public void betLatchCountDown(){
        betLatch.countDown();
    }

    /**
     * latch visszaszámoltatása eggyel
     */
    public void insuranceBetLatchCountDown(){
        insuranceBetLatch.countDown();
    }

    /**
     * latch visszaszámoltatása eggyel
     */
    public void dealLatchCountDown(){
        dealLatch.countDown();
    }
    
    /**
     * latch visszaszámoltatása eggyel
     */
    public void firstPlayerCardLatchCountDown(){
        firstPlayerCardLatch.countDown();
    }
    
    /**
     * latch visszaszámoltatása eggyel
     */
    public void dealerFirstCardLatchCountDown(){
        firstDealerCardLatch.countDown();
    }
    
   /**
     * latch visszaszámoltatása eggyel
     */
    public void secondPlayerCardCountDownLatc(){
        secondPlayerCardLatch.countDown();
    }

    /**
     * latch visszaszámoltatása eggyel
     */
    public void dealerTurnLatchCountDown(){
        dealerTurnLatch.countDown();
    }
    


    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.username);
        hash = 41 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Player other = (Player) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }
    
    /**
     *
     * @param socket
     */
    public void setSocket(Socket socket){
        this.socket = socket;
        try{
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(inputStreamReader);
            out = new PrintWriter(socket.getOutputStream(),true);
        }catch(IOException e){
            System.err.println("ERROR");
            }
    }
    
    /**
     *
     * @return
     */
    public boolean isLeft() {
        return left; 
    }
    
    /**
     *
     * @return
     */
    public PrintWriter getOut(){
        return out;
    }

    /**
     *
     * @return
     */
    public String getUsername(){
        return username;
    }
    
    /**
     *
     * @return
     */
    public int getId(){
        return id;
    }

    /**
     *
     * @return
     */
    public BufferedReader getIn(){
        return in;
    }
    
    /**
     *
     * @return
     */
    public Double getMoney(){
        return money;
    }

    /**
     *
     * @return
     */
    public int getLose(){
        return lose;
    }

    /**
     *
     * @return
     */
    public int getWin(){
        return win;
    }

    /**
     *
     * @param table
     */
    public void setTable(ServerTable table){
        this.table = table;
    }

    /**
     *
     * @param server
     */
    public void setServer(GameServer server) {
        this.server = server;
    }
    
    /**
     *
     * @return
     */
    public RuleHand getHand(){
        return hand;
    }
    
    /**
     *
     * @return
     */
    public Socket getSocket(){
        return socket;
    }
    
    @Override
    public void run(){
        send("WELCOME");
        do{
            try {
                playBlackjack();
            } catch (InterruptedException ex) {
            }
        }while(continuePlaying);
        send("GAMEOVER" + numToString(money));
        table.sendMoveToPlayers(this, SERV_AUTH + DELIMETER + "MOVE" + DELIMETER + table.getId()+ DELIMETER + id + DELIMETER +"GAMEOVER" + numToString(money));
        if(!left){
            server.sendDataForLobby(this);
            new Thread(() -> {
                WaitingRoom waiting = new WaitingRoom(server, this);
                waiting.getChoice();
            }).start();
    
        }
    }
}
