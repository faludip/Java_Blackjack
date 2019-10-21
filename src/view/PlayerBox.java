package view;

import client.CardFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import static view.TableView.GAME_FONT_TYPE;
import static view.TableView.YELLOW_FONT_COLOR;

/**
 * A játékost megjelenitő panel
 * @author Faludi Péter
 */
public class PlayerBox extends HBox{
    private final String username;
    private final Integer id;
    private final String money;
    private Label minimumLabel;
    private Label usernameLabel;
    private Label handValueLabel;
    private Label betLabel;
    private Label winLabel;
    private Label moneyLabel;
    private HBox firstCardHolder;
    private HBox secondCardHolder;
    private VBox infoBox;
    private VBox splitHand;
    private ObservableList playerBoxList;
    private ObservableList firstCardList;
    private ObservableList secondCardList;
    private ObservableList infoList;
    private ObservableList splitHandList;
    private int firstHandValue,secondHandValue;
   
    javafx.geometry.Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    
    /**
     *
     * @param id
     * @param username
     * @param money
     */
    public PlayerBox(int id,String username,String money) {
        this.username = username;
        this.id = id;
        this.money = money;
        init();
    }
    
    private void init(){
        handValueLabel = new Label();
        minimumLabel = new Label();
        setLabelFont(minimumLabel);
        setLabelFont(handValueLabel);
        usernameLabel = new Label(username);
        setLabelFont(usernameLabel);
        betLabel = new Label();
        setBetValue("");
        setLabelFont(betLabel);
        betLabel.setWrapText(true);
        winLabel = new Label();
        winLabel.setWrapText(true);
        setLabelFont(winLabel);
        moneyLabel = new Label(money);
        moneyLabel.setWrapText(true);
        setLabelFont(moneyLabel);
        setHbox(this);
        playerBoxList = super.getChildren();
        firstCardHolder = new HBox();
        secondCardHolder = new HBox();
        secondCardList = secondCardHolder.getChildren();
        firstCardList = firstCardHolder.getChildren();
        infoBox = new VBox();
        infoList = infoBox.getChildren();
        infoList.addAll(usernameLabel,moneyLabel,handValueLabel,betLabel,minimumLabel,winLabel);
        splitHand = new VBox();
        setVbox(splitHand);
        splitHandList = splitHand.getChildren();
        splitHandList.add(infoBox);
        playerBoxList.addAll(splitHand);
    }

    public String getName() {
        return username;
    }
    
    /**
     * A parméterben kapott kéz értékének beállítása
     * @param index kéz indexe
     * @param value a kéz értéke
     */
    public void setHandValue(String index,String value){
        switch(index){
            case("0"):
                firstHandValue = Integer.parseInt(value);
                break;
            case("1"):
                secondHandValue = Integer.parseInt(value);
                break;
            default:System.err.println("ERROR HANDVALUE");
        }
        if(secondCardList.isEmpty()){
            handValueLabel.setText("Hand value : " + value + "\n");
        }else handValueLabel.setText("Hand value : (1.):" + firstHandValue + "\n (2.):" + secondHandValue);
    }
     
    /**
     * A tét beállítása
     * @param bet
     */
    public void setBetValue(String bet){
        betLabel.setText("You'r(s) bet:" + bet +"$");
    }
    
    private void setLabelFont(Label label){
       label.setFont(GAME_FONT_TYPE);
       label.setTextFill(YELLOW_FONT_COLOR);
    }
    
    
    
    private void setHbox(HBox hbox){
        HBox.setMargin(hbox, new Insets(0, 3, 0, 3)); 
        hbox.setPrefWidth(primaryScreenBounds.getWidth()/4);
        hbox.setPrefHeight(primaryScreenBounds.getHeight() / 3);
        
        //hbox.setStyle(CSS_LAYOUT);
    }
    
    private void setVbox(VBox vbox){
        VBox.setMargin(vbox, new Insets(0, 3, 0, 3)); 
        vbox.setPrefWidth(primaryScreenBounds.getWidth()/2);
        vbox.setPrefHeight(primaryScreenBounds.getHeight() / 4);
        vbox.setMaxSize(primaryScreenBounds.getWidth()/2, primaryScreenBounds.getHeight() / 4);
       // vbox.setStyle(CSS_LAYOUT);
    }
    
    /**
     * Kártya hozzáadása a második kézhez
     * @param card
     */
    public void addCardToFirstHand(Label card){
        firstCardList.add(card);
        HBox.setMargin(card, new Insets(2, 0, 0, -20));
    }
    
    /**
     * Kéz hozzáadása a játékoshoz
     * @param index
     */
    public void addHand(String index){
        switch(index){
            case "0":
                firstCardList.clear();
                if(splitHandList.contains(firstCardHolder)){
                    splitHandList.remove(firstCardHolder);
                }
                splitHandList.add(firstCardHolder);
                break;
            case "1":
                secondCardList.clear();
                if(splitHandList.contains(secondCardHolder)){
                    splitHandList.remove(secondCardHolder);
                }
                splitHandList.add(secondCardHolder);
                
                break;
            default:System.err.println("Error hand index");
        }
    }
    
    /**
     * Kéz eltávolítása
     * @param index kéz sorszáma
     */
    public void removeHand(String index){
        switch(index){
            case "0":
                firstCardList.clear();
                splitHandList.remove(firstCardHolder);
                firstHandValue = 0;
                break;
            case "1":
                secondCardList.clear();
                splitHandList.remove(secondCardHolder);
                secondHandValue = 0;
                
                break;
            default:System.err.println("Error hand index");
        }
    }
    
    /**
     * Kárty hozzáadása kézhez
     * @param index a kéz sorszáma
     * @param url kártya képének elérési címe
     */
    public void addCardToHand(String index,String url){
        CardFactory card = new CardFactory(url.toLowerCase());
        switch(index){
            case "0":
                firstCardList.add(card);
                break;
            case "1":
                secondCardList.add(card);
                break;
            default:System.err.println("Error hand index");
        }
    }
    
    /**
     * Kártya hozzáadása a második kézhez
     * @param card
     */
    public void addCardToSecondHand(Label card){
        secondCardList.add(card);
        
    }

    public int getPlayersId() {
        return id;
    }
    
    /** 
     * Új kör kártyák türlése
     */
    public void newRound(){
        firstCardList.clear();
        secondCardList.clear();
        setHandValue("0","0");
        if(splitHandList.contains(secondCardHolder)){
            splitHandList.remove(secondCardHolder);
        }
        if(splitHandList.contains(firstCardHolder)){
            splitHandList.remove(firstCardHolder);
        }
        setBetValue("");
        setWinLabel("0","");
    }
    
    /**
     * Kéz törlése
     * @param hand
     */
    public void removeHand(HBox hand){
       splitHandList.remove(hand);
   }
    public HBox getHand(String index){
       return (HBox)splitHandList.get(Integer.parseInt(index));
   }

    public void setMoneyLabel(String money) {
        
        moneyLabel.setText("Money:" + money +"$");
    }

    public void setMinimumLabel(String minBet) {
        minimumLabel.setText("Minimum:" + minBet + "$");
    }

    public void setWinLabel(String index,String str){
        winLabel.setText(str);
    }
    
}
