package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;
import view.Table;
import view.TableView;


/**
 *
 * @author Faludi Péter
 */
public class ClientController {
    
    /**
     * Változók definiálása
     */
    protected  static final javafx.geometry.Rectangle2D SCREEN_RES = Screen.getPrimary().getVisualBounds();
    private ClientModel model;
    private TableView view;
    private final BufferedReader in;
    private final PrintWriter out;
    private final int tableNum;
    private Table table;
    private ChatModel chatModel;
    private final Socket gameSocket;
    private final LobbyController lobbyController;
    private CountDownLatch latch;
    TimeOut timer;
    private GUITimer guiTimer;
    
    /**
     *
     * @param lobbyController a várakozó szoba irányitó osztálya
     * @param gameSocket a játék kommunikáciért felelős socket
     * @param in a socket bemenete
     * @param out a socket kimenete
     * @param tableNum a játékasztal sorszáma
     * @param chatModel a chat modelje
     */
    public ClientController(LobbyController lobbyController,Socket gameSocket,BufferedReader in, PrintWriter out,int tableNum,ChatModel chatModel) {
        this.lobbyController = lobbyController;
        this.gameSocket = gameSocket;
        this.in = in;
        this.out = out;
        this.tableNum = tableNum;
        this.chatModel = chatModel;
    }
    
    /**
     * az osztály indítása
     */
    public void start() {
        model = new ClientModel(in, out);
        table = new Table(tableNum, chatModel, this);
        chatModel.getChatThread().setController(lobbyController);
        table.init();
        table.addChatBox(lobbyController.getChatbox());
        view = new TableView(table);
        view.start(new Stage());
        latch = new CountDownLatch(0);
        getServerMessage();
    }
    
    /**
     * a GUI manipulációjáért felelős függvény
     * a socketen érkező szöveges üzenet szerint módosítja a fellületet
     * @param message a szervertől kapott üzenet
     */
    protected void updateGUI (String message) {
//        System.err.println(message);
        String[] serverCommand = message.split("--");  
        
        if(!serverCommand[1].equals("MOVE")){
            switch (serverCommand[3]) {
                case "NEWPLAYER":
                    model.addPlayerBox(serverCommand[5],serverCommand[6],serverCommand[7]);
                    table.addNewPlayer(model.getPlayerBox(serverCommand[5]));
                    getServerMessage();
                    break;
                case "WELCOME":
                    table.setInfo("Waiting");
                    getServerMessage();
                    break;
                case "BETTURN":
                    table.setInfo("Waiting for your bet");
                    startTimers("MIN");
                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[4]);
                    model.getPlayerBox(serverCommand[2]).setMinimumLabel(serverCommand[5]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    table.setBet();
                    getServerMessage();
                    break;
                case "BETINFO":
                    switch (serverCommand[4]) {
                        case "INVALID":
                            table.setInfo("The number must be positive!");
                            getServerMessage();
                            break;
                        case "MAXIMUM":
                            table.setInfo("You have not enough money!");
                            getServerMessage();
                            break;
                        case "MINIMUM":
                            table.setInfo("Your bet is less than the minimum!");
                            getServerMessage();
                            break;
                        case "OK":
                            table.setInfo("Placed the bet!");
                            model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[5]);
                            table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                            getServerMessage();
                            break;
                    }
                    break;
                case "NEWROUND":
                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[4]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    table.setInfo("New Round");
                    getServerMessage();
                    break;
                case "HAVEBLACKJACK":
                    switch (serverCommand[4]) {
                        case "DEALER&PLAYER":
                            table.setInfo("You and the dealer have Blackjack!");
                            getServerMessage();
                            break;
                        case "PLAYER":
                            table.setInfo("You have Blackjack!");
                            getServerMessage();
                            break;
                        case "DEALER":
                            table.setInfo("The dealer has BlackJack!");
                            getServerMessage();
                            break;
                        case "NODEALER":
                            table.setInfo("The dealer does not have Blackjack!");
                            getServerMessage();
                            break;
                    }
                    break;
                case "NEWDEALERCARD":
                    table.addCardToDealerHand(serverCommand[4]);
                    getServerMessage();
                    break;
                case "INSURANCETURN":
                    table.setInfo("Please choose!");
                    startTimers("No");
                    table.setInsurance();
                    getServerMessage();
                    break;
                case "INSURANCEINFO":
                    switch (serverCommand[4]) {
                        case "ERROR":
                            table.setInfo("The number must be positive!");
                            getServerMessage();
                            break;
                        case "OK":
                            table.setInfo("The bet is placed!");
                            model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[6]);
                            table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                            getServerMessage();
                            break;
                        case "NO":
                            table.setInfo("You did not place bet!");
                            getServerMessage();
                            break;
                        case "INVALID":
                            table.setInfo("Your insurance is invalid!");
                            getServerMessage();
                    }
                    break;
                case "NOTENOUGH":
                    table.setInfo("You have not enough money!");
                    getServerMessage();
                    break;
                case "WININSURANCE":
                    table.setInfo("You won the insurance:" + serverCommand[4] + "$");
                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[5]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    getServerMessage();
                    break;
                case "LOSTINSURANCE":
                    table.setInfo("You lost the insurance");
                    getServerMessage();
                    break;
                case "INSURANCEOK":
                    table.setInfo("Insurance done");
                    getServerMessage();
                    break;
                case "YOURTURN":
                    table.setInfo("Your Turn");
                    getServerMessage();
                    break;
                case "CLEARHAND":
                    model.addHandPanel(serverCommand[2], serverCommand[4]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    getServerMessage();
                    break;
                case "DELETEHAND":
                    model.removeHandPanel(serverCommand[2], serverCommand[4]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    getServerMessage();
                    break;
                case "BET":
                    model.getPlayerBox(serverCommand[2]).setBetValue(serverCommand[5]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    getServerMessage();
                    break;
                case "VALUE":
                    model.getPlayerBox(serverCommand[2]).setHandValue(serverCommand[4],serverCommand[5]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    getServerMessage();
                    break;
                case "TURNSKIP":
					table.setInfo("You have Blackjack!");
					getServerMessage();
					break;
                case "PLAYERCARD":
                    model.getPlayerBox(serverCommand[2]).addCardToHand(serverCommand[4],serverCommand[5]);
                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                    getServerMessage();
                    break;
                case "MOVEOPTION":
                    table.setInfo("Please choose an option!");
                    startTimers("Stand");
                    switch (serverCommand[4]) {
                        case "DOUBLE":
                            table.setDoubleDown();
                            getServerMessage();
                            break;
                        case "HITSTAY":
                            table.setHitStay();
                            getServerMessage();
                            break;
                    }
                    break;
                case "MOVEERROR":
                    table.setInfo("ERROR" + serverCommand[2] + ".hand!");
                    getServerMessage();
                    break;
                case "BUST":
                    table.setInfo("You busted!");
                    getServerMessage();
                    break;
                case "DOUBLEINFO":
                    switch (serverCommand[4]) {
                        case "OK":
                            table.setInfo("Double Down succes!");
                            getServerMessage();
                            break;
                    }
                    break;
                case "RESULT":
                    table.setInfo("Waiting for the server");
                    getServerMessage();
                    break;
                case "REMOVEHIDDENCARD":
                    table.getDealerBox().removeFaceDownCard();
                    getServerMessage();
                    break;
                case "DEALERVALUE":
                    table.getDealerBox().setHandValue(serverCommand[4]);
                    getServerMessage();
                    break;
                case "WINNERS":
                    switch (serverCommand[4]) {
                        case "BUST":
                            switch (serverCommand[5]) {
                                case "TIE":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"You lose!");
                                    table.setInfo("You lose!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "DEALER":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"The dealer wins!You busted!");
                                    table.setInfo("The dealer wins!You busted!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "PLAYER":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"You Win!The Dealer busted!");
                                    table.setInfo("You Win!The Dealer busted!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                            }
                            break;
                        case "NORMAL":
                            switch (serverCommand[5]) {
                                case "TIE":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"It's tie");
                                    table.setInfo("It's tie");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "DEALER":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"The dealer wins!");
                                    table.setInfo("The dealer wins!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "PLAYER":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"You Win!");
                                    table.setInfo("You win!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                            }
                            break;
                        case "HAVEBLACKJACK":
                            switch (serverCommand[5]) {
                                case "TIE":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"You and the dealer have Blackjack");
                                    table.setInfo("You and the dealer have Blackjack!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "DEALER":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"The dealer has Blackjack!");
                                    table.setInfo("The dealer has Blackjack!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "PLAYER":
                                    model.getPlayerBox(serverCommand[2]).setWinLabel(serverCommand[6],"You have Blackjack!");
                                    table.setInfo("You have Blackjack!");
                                    model.getPlayerBox(serverCommand[2]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                            }
                            break;
                    }
                    break;
                case "CONTINUE":
                    startTimers("No");
                    table.setContinue();
                    getServerMessage();
                    break;
                case "CONTINUEINFO":
                    switch (serverCommand[4]) {
                        case "ERROR":
                            getServerMessage();
                            break;
                        case "CONTINUE":
                            model.getPlayerBox(serverCommand[2]).newRound();
                            table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                            table.setInfo("Waiting for other players to join");
                            table.newRound();
                            getServerMessage();
                            break;
                    }
                    break;
                case "GAMEOVER":
                    gameOver();
                    break;
                case "WAITING":
                    switch (serverCommand[4]) {
                        case "WELCOME":
                            table.setInfo("Wating for the server...");
                            getServerMessage();
                            break;
                        case "BET":
                            table.setInfo("Waiting for the bets!");
                            getServerMessage();
                            break;
                        case "INSURANCE":
                            table.setInfo("Waiting for the insurance bets!");
                            getServerMessage();
                            break;
                        case "TURN":
                            table.setInfo("Waiting for the other players!");
                            getServerMessage();
                            break;
                    }
                    break;
                default:
                    System.err.println("Unknown message received from server: \"" + message + "\"");
                    break;
            }
        }else{
            switch (serverCommand[4]) {
                case "WELCOME":
                    table.setInfo("Waiting");
                    getServerMessage();
                    break;
                case "BETTURN":
                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[5]);
                    model.getPlayerBox(serverCommand[3]).setMinimumLabel(serverCommand[6]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "BETINFO":
                    switch (serverCommand[5]) {
                        case "INVALID":
                            getServerMessage();
                            break;
                        case "MAXIMUM":
                            getServerMessage();
                            break;
                        case "MINIMUM":
                            getServerMessage();
                            break;
                        case "OK":
                            model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[6]);
                            table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                            getServerMessage();
                            break;
                    }
                    break;
                case "NEWROUND":
                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[5]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "HAVEBLACKJACK":
                    switch (serverCommand[5]) {
                        case "DEALER&PLAYER":
                            table.setInfo("He/She and the dealer have Blackjack!");
                            getServerMessage();
                            break;
                        case "PLAYER":
                            table.setInfo("He/she has Blackjack!");
                            getServerMessage();
                            break;
                        case "DEALER":
                            getServerMessage();
                            break;
                        case "NODEALER":
                            getServerMessage();
                            break;
                    }
                    break;
                case "NEWDEALERCARD":
                    getServerMessage();
                    break;
                case "INSURANCETURN":
                    getServerMessage();
                    break;
                case "INSURANCEINFO":
                    switch (serverCommand[5]) {
                        case "ERROR":
                            getServerMessage();
                            break;
                        case "OK":
                            table.setInfo("Placed the bet!");
                            model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[7]);
                            table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                            getServerMessage();
                            break;
                        case "NO":
                            table.setInfo("He/She did not place bet!");
                            getServerMessage();
                            break;
                    }
                    break;
                case "NOTENOUGH":
                    getServerMessage();
                    break;
                case "WININSURANCE":
                    table.setInfo("He/she won the insurance:" + serverCommand[5] + "$");
                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[6]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "LOSTINSURANCE":
                    table.setInfo("He/She lost the insurance");
                    getServerMessage();
                    break;
                case "INSURANCEOK":
                    getServerMessage();
                    break;
                case "YOURTURN":
                    table.setInfo("Not your turn");
                    getServerMessage();
                    break;
                case "CLEARHAND":
                    model.addHandPanel(serverCommand[3], serverCommand[5]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "DELETEHAND":
                    model.removeHandPanel(serverCommand[3], serverCommand[5]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "BET":
                    model.getPlayerBox(serverCommand[3]).setBetValue(serverCommand[6]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "VALUE":
                    model.getPlayerBox(serverCommand[3]).setHandValue(serverCommand[5],serverCommand[6]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "TURNSKIP":
                    switch (serverCommand[5]) {
                        case "PLAYER":
                            table.setInfo("He/She have Blackjack!");
                            getServerMessage();
                            break;
                    }
                    break;
                case "PLAYERCARD":
                    model.getPlayerBox(serverCommand[3]).addCardToHand(serverCommand[5],serverCommand[6]);
                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                    getServerMessage();
                    break;
                case "MOVEOPTION":
                    getServerMessage();
                    break;
                case "MOVEERROR":
                    getServerMessage();
                    break;
                case "BUST":
                    table.setInfo("He/she busted!");
                    getServerMessage();
                    break;
                case "DOUBLEINFO":
                    switch (serverCommand[5]) {
                        case "OK":
                            getServerMessage();
                            break;
                    }
                    break;
                case "RESULT":
                    getServerMessage();
                    break;
                case "REMOVEHIDDENCARD":
                    getServerMessage();
                    break;
                case "DEALERVALUE":
                    getServerMessage();
                    break;
                case "WINNERS":
                    switch (serverCommand[5]) {
                        case "BUST":
                            switch (serverCommand[6]) {
                                case "TIE":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"He/She and the dealer busted");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                                case "DEALER":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"The dealer wins!He/She busted!");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                                case "PLAYER":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"He/She wins!The Dealer busted!");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                            }
                            break;
                        case "NORMAL":
                            switch (serverCommand[6]) {
                                case "TIE":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"It's tie");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                                case "DEALER":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"The dealer wins!");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                                case "PLAYER":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"He/She wins!");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                            }
                            break;
                        case "HAVEBLACKJACK":
                            switch (serverCommand[6]) {
                                case "TIE":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"He/She and the dealer have BlackJack");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[7]);
                                    table.changePlayerBox(serverCommand[2], model.getPlayerBox(serverCommand[2]));
                                    getServerMessage();
                                    break;
                                case "DEALER":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"The dealer has Blackjack!");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                                case "PLAYER":
                                    model.getPlayerBox(serverCommand[3]).setWinLabel(serverCommand[7],"He/She has Blackjack!");
                                    model.getPlayerBox(serverCommand[3]).setMoneyLabel(serverCommand[8]);
                                    table.changePlayerBox(serverCommand[3], model.getPlayerBox(serverCommand[3]));
                                    getServerMessage();
                                    break;
                            }
                            break;
                    }
                    break;
                case "CONTINUE":
                    getServerMessage();
                    break;
                case "CONTINUEINFO":
                    getServerMessage();
                    break;
                case "GAMEOVER":
                    model.removePlayer(serverCommand[3]);
                    table.removePlayer(serverCommand[3]);
                    getServerMessage();
                    break;
                case "WAITING":
                    getServerMessage();
                    break;
                default:
                    System.err.println("Unknown message received from server: \"" + message + "\"");
                    getServerMessage();
                    break;
            }
            
        }
    }

    /**
     * Szerverparancsok feldolgozása és a GUI módosítását meghívó függvény
     * 
     */
    private void getServerMessage() {
        new Thread(()->{
            String string = model.getServerMessage();
            Platform.runLater(() -> {
                try{
                    updateGUI(string);
                }finally{
                    if(string.contains("GAMEOVER") && !string.contains("MOVE")){
                    latch.countDown();
                }
                }
            });
            
        }).start();
    }
    
    /**
     *A válasz küldése a szervernek
     * @param clientMessage elküldendő üzenet a model osztályon keresztül
     */
    public void sendClientMessage(String clientMessage) {
        model.sendClientMessage(clientMessage);
        timer.close();
        guiTimer.setRunning(false);
    }
    
    /**
     *A játék végén lefutó függvény,visszaírányít a lobbyba
     */
    public void  gameOver(){
        new Thread(() -> {
                Platform.runLater(() -> {
                    try {
                        latch.await();
                        lobbyController.refresh();
                        table.exit();
                    } catch (InterruptedException ex) {
                    }
                });
        }).start();
    }
    
    /**
     * A válasz lehetőségnél elinduló időzítő 
     */
    private void startTimers(String command){
        timer = new TimeOut(this, command);
        guiTimer = new GUITimer(this);
        Thread thread = new Thread(guiTimer);
        thread.start();
    }
    
    /**
     *  Mennyi ideje maradt a játékosnak a válaszra
     * @param second aktuális másodperc
     */
    protected void setTimeLeft(Integer second){
        Platform.runLater(() -> {
            table.setTime(second.toString());
        });
    }
    
    /**
     * Az írányitó-panel törlése
     */
    public void clearControlPanel(){
        Platform.runLater(() -> {
            table.setEmpty();
        });
    }

    /**
     * A játékból való kilépéskor 
     * ez a függvény felel a socket bezárásáért 
     * ezenfelül értesíti a szervert és a többi játékost
     * @throws IOException
     */
    public void leaveGame() throws IOException{
        model.sendClientMessage("LEAVE");
        gameSocket.close(); 
        System.exit(0);
    }
    
    
}
