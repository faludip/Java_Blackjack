package view;

import view.panels.DealerBox;
import view.panels.MessageSendBox;
import view.panels.ChatBox;
import client.ChatModel;
import client.ClientController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import static view.TableView.CSS_LAYOUT;
import view.panels.BetBox;
import view.panels.BothBox;
import view.panels.ContinueBox;
import view.panels.ControllerEmpty;
import view.panels.DoubleDownBox;
import view.panels.HitStayBox;
import view.panels.InsuranceBox;

/**
 * Az asztal megjelenítéséért felelős adatok tárolására
 * való osztály
 * @author Faludi Péter
 */
public class Table extends BorderPane{
    private final ClientController controller;
    private final ChatModel chatModel;
    private final int id;
    private HBox playersBox;
    private ObservableList players;
    private AnchorPane controllBox;
    private MessageSendBox sendBox;
    private BetBox betBox;
    private BothBox bothBox;
    private HitStayBox hitStayBox;
    private InsuranceBox insuranceBox;
    private DealerBox dealerBox;
    private DoubleDownBox doubleDownBox;
    private ContinueBox continueBox;
    private ChatBox chatBox;

    /**
     *
     * @param id
     * @param chatModel
     * @param controller
     */
    public Table(int id,ChatModel chatModel,ClientController controller) {
        this.id = id;
        this.chatModel= chatModel;
        this.controller = controller;
    }
    
    /**
     * Szükséges változók inincializása
     */
    public void init() {
        controllBox = new ControllerEmpty();
        this.setBackground(Background.EMPTY);
        controllBox.setMaxSize(TableView.SCREEN_RES.getWidth()/4, TableView.SCREEN_RES.getHeight()/4);
        controllBox.setPrefSize(TableView.SCREEN_RES.getWidth()/5, TableView.SCREEN_RES.getHeight()/6);
        controllBox.setStyle(CSS_LAYOUT);
        setUpPanels();
        playersBox = new HBox();
        players = playersBox.getChildren();
        this.setLeft(controllBox);
        Image table = null;
        table = new Image(Table.class.getClass().getResourceAsStream("/image/blackjacktable1.png"));
        ImageView imageView = new ImageView(table);
        imageView.setFitHeight(212);
        imageView.setFitWidth(595);
        this.setCenter(imageView);
        BorderPane.setAlignment(imageView,Pos.CENTER);
        this.setBottom(playersBox);
        BorderPane.setAlignment(dealerBox, Pos.TOP_CENTER);
        dealerBox.setAlignment(Pos.TOP_CENTER);
        this.setTop(dealerBox);
        this.setStyle(CSS_LAYOUT);
    }
    
    /**
     * Új játékos hozzáadása az asztalhou
     * @param playerBox
     */
    public void addNewPlayer(PlayerBox playerBox){
           players.add(playerBox);
    }
    
    public void addChatBox(ChatBox chatBox){
        this.chatBox = chatBox;
        this.setRight(chatBox);
    }
    
    /**
     * Az irányitó panel ütitése
     */
    public void setEmpty(){
        this.setLeft(controllBox);
    }
    
    /**
     * Tét rakás panel
     */
    public void setBet(){
        this.setLeft(betBox);
    }
    
    /**
     * Biztosítás panel
     */
    public void setInsurance(){
        this.setLeft(insuranceBox);
    }
    
    /**
     * Duplázás panel
     */
    public void setDoubleDown(){
        this.setLeft(doubleDownBox);
    }
    
  
    
    /**
     * Lapkérés, megállás panel
     */
    public void setHitStay(){
        this.setLeft(hitStayBox);
    }
    
    /**
     * Folytatás panel
     */
    public void setContinue(){
        this.setLeft(continueBox);
    }
    
    /**
     * Idözitő beállítása
     * @param time
     */
    public void setTime(String time){
        dealerBox.setTime(time);
    }
    
  
    
    /*
    * Játékos keresése azonositó alapján
    */
    private PlayerBox findPlayerById(int id){
        for(Object player : players){
            PlayerBox thisPlayer = (PlayerBox)player;
            if(thisPlayer.getPlayersId() == id){
                return thisPlayer;
            }
        }
        return null;
    }
    
    /**
     * Játékos paneljának a cserejéje
     * @param id
     * @param playerBox
     */
    public void changePlayerBox(String id,PlayerBox playerBox){
        Platform.runLater(() -> {
           players.set(findIndex(Integer.parseInt(id)), playerBox);
        });
    }
    
    /**
     * A játékos listabeli indexét keresi meg
     * @param id
     * @return index
     */
    public int findIndex(int id){
        for(Object player : players){
            PlayerBox box = (PlayerBox) player;
            if(box.getPlayersId() == id){
                return players.indexOf(player);
            }
        }
        return -1;
    }
    
    /**
     * Játékos eltávolitása az asztalról
     * @param id
     */
    public void removePlayer(String id){
        if(null != findPlayerById(Integer.parseInt(id))){
            players.remove(findPlayerById(Integer.parseInt(id)));
        }
    }
    
    /**
     * Kártya hozzáadása az soztó kezéhez
     * @param url
     */
    public void addCardToDealerHand(String url){
        dealerBox.addCard(new client.CardFactory(url));
    }
    
    public void removeDealerHand(){
        dealerBox.newRound();
    }
    
    /**
     * Lap hozzáadása a játékos kezéhez
     * @param id
     * @param url
     */
    public void addCardToPlayerFirstHand(int id, String url){
        if(null != findPlayerById(id)){
            findPlayerById(id).addCardToFirstHand(new client.CardFactory(url));
        }
    }

    public ClientController getController() {
        return controller;
    }

    public DealerBox getDealerBox() {
        return dealerBox;
    }
    
    /**
     * Új körnél lefutó metódus
     */
    public void newRound(){
        dealerBox.newRound();
        for(Object object : players){
            PlayerBox playerBox = (PlayerBox) object;
            playerBox.newRound();
        }
    }
    
    /**
     * Játékos pénzének megjelenítése
     * @param id
     * @param money
     */
    public void setMoneyForPlayer(int id, Double money){
        if(null != findPlayerById(id)){
            findPlayerById(id).setMoneyLabel(money.toString());
        }
    }

    /*
    * Panelek létrehozás és inicializálása
    */
    private void setUpPanels() {
        betBox = new BetBox(controller);
        bothBox = new BothBox(controller);
        dealerBox = new DealerBox();
        hitStayBox = new HitStayBox(controller);
        insuranceBox = new InsuranceBox(controller);
        doubleDownBox = new DoubleDownBox(controller);
        continueBox = new ContinueBox(controller);
        continueBox.init();
        doubleDownBox.init();
        betBox.init();
        dealerBox.init();
        bothBox.init();
        hitStayBox.init();
        insuranceBox.init();
    }  
    
    /**
     * Osztó kezének értékének beállítása
     * @param value
     */
    public void setDealerHandValue(String value){
        dealerBox.setHandValue(value);
    }
    
    /**
     * Információ megjelenítése
     * @param info
     */
    public void setInfo(String info){
        dealerBox.setInfo(info);
    }
    
    /**
     * Kilépéskor lefutó függvény
     * @return
     */
    public boolean exit(){
        Window owner = this.getScene().getWindow();
        owner.hide();
        return true;
    }
}
