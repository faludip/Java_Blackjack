package server.logic;

import dao.PlayerDao;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

/**
 * Ez az ostály egy asztalt szimbolizál
 * A játék egy körének a sorrendje itt van implementálva
 * @author Faludi Péter
 */
public class ServerTable implements Runnable{
    
    private final static int MAX_POINT = 21;
    private final static int DEALER_MIN_POINT = 17;
    private final Integer minBet, numOfDecks;
    private final int minimumCardsBeforeShuffle;                                  
    private final Integer id;
    private Shoe shoe;
    private boolean dealerHasBlackjack = false;
    private final RuleHand dealersHand = new RuleHand();
    private final LinkedList<Player> table;
    private final LinkedList<Player> waiters;
    private File logFile;
    private FileWriter fileWriter;
    private CountDownLatch betLatch, insuranceLatch, waitingLatch,
            playingLatch,firstPlayerCardLatch,firstDealerCardLatch,secondPlayerCardLatch;
    
    /**
     *
     * @param minBet
     * @param numOfDecks
     * @param minimumCardsBeforeShuffle
     * @param id
     */
    public ServerTable(int minBet, int numOfDecks,int minimumCardsBeforeShuffle,int id) {
        this.id = id;
        this.minBet = minBet;
        this.numOfDecks = numOfDecks;
        this.minimumCardsBeforeShuffle = minimumCardsBeforeShuffle;
        table = new LinkedList<>();
        waiters = new LinkedList<>();
    }
    
    /**
     * Az asztal azonosítóját és a minimális tétet kigyűjti egy tömbbe
     * @return informásciós tömb
     */
    public String[] getInfo(){
        String[] info = new String[]{id.toString(),minBet.toString()};
        return info;
    }

    /*
    * Az asztal felállításához szükséges változók inicializása
    */
    private void init() {
        shoe = new Shoe(numOfDecks);
        shoe.shuffle();
        logFile = new File("TableLog" + getId() + ".log");
        try {
            logFile.createNewFile();
            fileWriter = new FileWriter(logFile);
        } catch (IOException ex) {}
    }
    
    /*
    * Ez a metódus ciklusokkal szimulál egy kör végigjátszását
    * A kör végén a várakozó játékosokat,hozzáadja az asztalhoz illetve a 
    * játékosok adatait frissíti az adatbázisban
    */
    private void playBlackjack() throws InterruptedException {
        setup();
        if(table.size() > 0){
            for (Player player : table) {
                player.startLatchCountDown();
            }
            betLatch.await();
            for (Player player : table) {
                player.betLatchCountDown();
            }
            dealFirstRound();
            for (Player player : table) {
                player.dealLatchCountDown();
            }
            firstPlayerCardLatch.await();
            for(Player player : table){
                player.firstPlayerCardLatchCountDown();
            }
            firstDealerCardLatch.await();
            for(Player player : table){
                player.dealerFirstCardLatchCountDown();
            }
            secondPlayerCardLatch.await();
            for(Player player : table){
                player.secondPlayerCardCountDownLatc();
            }
            insuranceLatch.await();
            for (Player player : table) {
                player.insuranceBetLatchCountDown();
            }
            waitingLatch.await();
            for (Player player : table) {
                player.yourTurn(player.getHand());
            }
            dealerTurn();
            for (Player player : table) {
                player.dealerTurnLatchCountDown();
            }
            playingLatch.await();
        }
        for(Player player : table){
            PlayerDao.getInstance().update(player);
        }
        newArrivals();
    }

    /*
    * Újonnan érkező játékosok asztalhoz adása a kör végén 
    */
    private void newArrivals() {
        if(!waiters.isEmpty()){
            for(Player waiter : waiters){
                waiter.getOut().println("OK");
            }
            for(Player waiter : waiters){
                //Az éppen jatszok adatai a várakozoknak
                for(Player player : table){
                        waiter.getOut().println("SERVERCOMMAND--" + id +"--"+waiter.getId() + "--NEWPLAYER--" + id 
                        +  "--" + player.getId() + "--" + player.getUsername() +"--" + player.getMoney());
                }
                //A várakozók egymást értesítik
                for(Player thisWaiter : waiters){
                    waiter.getOut().println("SERVERCOMMAND--" + id +"--"+waiter.getId() + "--NEWPLAYER--" + id 
                            +  "--" + thisWaiter.getId() + "--" + thisWaiter.getUsername() +"--" + thisWaiter.getMoney());
                }
                //Az jétkosokat értesíti a várakozók adatairól
                for(Player player : table){
                    player.getOut().println("SERVERCOMMAND--" + id +"--"+player.getId() + "--NEWPLAYER--" + id 
                            +  "--" + waiter.getId() + "--" + waiter.getUsername() +"--" + waiter.getMoney());
                }
            }
            for(Player waiter : waiters){
                addPlayerToTable(waiter);
            }
            waiters.clear();
        }
    }

   
    public int getId() {
        return id;
    }
    
    /*
    * Az osztó körét reprezentáló metódus
    */
    private void dealerTurn() {
        while ((dealersHand.isSoftHand() 
                && dealersHand.greaterValue() == DEALER_MIN_POINT) 
                || dealersHand.greaterValue() < DEALER_MIN_POINT) {
            dealersHand.addCard(dealCard());
        }
    }
    
    /**
     * A többi játékosnak elküldi az aktuális játékos lépését
     * @param player
     * @param message
     */
    public void sendMoveToPlayers(Player player,String message){
        try{
            for(Player opponent : table){
                if(!opponent.equals(player)){
                    opponent.getOut().println(message);
                }
            }
        }catch(ConcurrentModificationException e){
            sendMoveToPlayers(player, message);
        }
    }
    
    /**
     * Log fájlba írja a lépéseket
     * @param message
     */
    protected void writeLog(String message){
         try {
            fileWriter.write(message +"--"+ LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)+ "\n" );
            fileWriter.flush();
        } catch (IOException ex) {}
    }
    
    /*
    * változzók alaphelyzetbe állítása
    */
    private void setup() {
        if (shoe.getSize() <= minimumCardsBeforeShuffle) {
            shoe = new Shoe(numOfDecks);
            shoe.shuffle();
        }
        dealersHand.clear();
        dealerHasBlackjack = false;
        betLatch = new CountDownLatch(getTableSize());
        insuranceLatch = new CountDownLatch(getTableSize());
        waitingLatch = new CountDownLatch(getTableSize());
        playingLatch = new CountDownLatch(getTableSize());
        firstPlayerCardLatch = new CountDownLatch(getTableSize());
        firstDealerCardLatch = new CountDownLatch(getTableSize());
        secondPlayerCardLatch = new CountDownLatch(getTableSize());
        
    }
    
    /**
     * latch visszaszámlálás
     * @param string
     */
    protected void countDown(String string){
        switch(string){
            case "bet":
                betLatch.countDown();
                break;
            case "insurance":
                insuranceLatch.countDown();
                break;
            case "waiting":
                waitingLatch.countDown();
                break;
            case "playing":
                playingLatch.countDown();
                break;
            case "firstplayer":
                firstPlayerCardLatch.countDown();
                break;
            case "firstdealer":
                firstDealerCardLatch.countDown();
                break;
            case "secondplayer":
                secondPlayerCardLatch.countDown();
                break;
                    
                
            default:System.err.println("ERROR");
        }
    }
    
    /**
     * Az első kártyák kiosztásáért felelős függvény
     */
    protected void dealFirstRound() {
        for (int i = 0; i < 2; i++) {
            dealersHand.addCard(dealCard());
            for (Player player : table) {
                player.getHand().addCard(dealCard());
            }
        }
        if(dealersHand.greaterValue() == MAX_POINT) {
            dealerHasBlackjack = true;
        }
    }
    
    /**
     * Játékos hozzáadása az asztalhoz
     * @param player játékos
     */
    public void addPlayerToTable(Player player){
        try {
            player.setTable(this);
            Thread playerThread = new Thread(player);
            playerThread.start();
            Thread.sleep(600);
            table.addLast(player);
        } catch (InterruptedException ex) {}
    }
    
    /**
     * Várakozó játékos hozzáadása az asztalhoz
     * @param player játékos
     * @return sikeres volt-e a hozzádadás
     */
    public boolean addPlayerToWaiters(Player player){
        if((this.getTableSize() + waiters.size()) >= 4){
            player.getOut().println("FULL");
            return false;
        }
        if(table.isEmpty() && player.getMoney() >= this.minBet){
            player.getOut().println("EMPTY");
            waiters.addLast(player);
            return true;
            
        }
        if(player.getMoney() < this.minBet){
            player.getOut().println("NOMONEY");
            return false;
        }
        player.getOut().println("WAITING");
        waiters.addLast(player);
        return true;
        
    }
    
    /**
     * Egy lap osztása
     * @return lap
     */
    protected Card dealCard(){
        if(shoe.getSize() == 0){
            shoe = new Shoe(numOfDecks);
            shoe.shuffle();
        }
        return shoe.dealNextCard();
    }
    
    /**
     * Az osztó ismert lapjával tér vissza
     * @return
     */
    protected Card dealerFirstCard(){
        return dealersHand.getCard(0);
    }
    
    /**
     * Az osztó lapjai
     * @return osztó lapjai
     */
    protected RuleHand dealersHand(){
        return dealersHand;
    }
    
    /**
     *
     * @return az osztónak blackjack-je van?
     */
    protected boolean dealerHasBlackjack(){
        return dealerHasBlackjack;
    }
    
    /**
     * A az asztalon játszó játékosok száma
     * @return 
     */
    public int getTableSize(){
        return table.size();
    }
    
    /**
     * Eltávólítja a játékost az asztalról
     * @param player játékos
     */
    protected void removePlayerFromTable(Player player) {
        table.remove(player);
    }
    
    /**
     * Az asztalon játszó játékosokkal visszatérő függvény
     * @return játékosok
     */
    public LinkedList<Player> getTable() {
        return table;
    }

    /**
     * Az asztalon várakozó játékosokkal visszatérő függvény
     * @return várakozók
     */
    public LinkedList<Player> getWaiters() {
        return waiters;
    }
    
    /**
     *
     * @return
     */
    public int getMinBet(){
        return minBet;
    }
    
    @Override
    public void run() {
        this.init();
        while(true){
           while(table.isEmpty() && waiters.isEmpty()){try {
               Thread.sleep(200);
               } catch (InterruptedException ex) {}
            }
            try {
                this.playBlackjack();
            } catch (InterruptedException ex) {}
        }
        
    }
}
