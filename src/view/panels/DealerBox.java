package view.panels;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import static view.TableView.GAME_FONT_TYPE;
import static view.TableView.YELLOW_FONT_COLOR;
import static view.TableView.SCREEN_RES;

/**
 * Az osztó megjelenitő panele
 * @author Faludi Péter
 */
public class DealerBox extends VBox{
    private Label dealerLabel;
    private Label handValueLabel;
    private HBox dealerHand;
    private HBox holder;
    private ObservableList holderList;
    private InfoBox infoBox;
    ObservableList dealerHandList ;
    ObservableList list;
    

    public DealerBox() {
        
    }
    
    public void init(){
        dealerHand = new HBox();
        dealerLabel = new Label("Dealer");
        handValueLabel = new Label("Dealer's hand value:\n0");
        handValueLabel.setTextFill(YELLOW_FONT_COLOR);
        handValueLabel.setFont(GAME_FONT_TYPE);
        dealerLabel.setFont(GAME_FONT_TYPE);
        dealerLabel.setTextFill(YELLOW_FONT_COLOR);
        holder = new HBox();
        infoBox = new InfoBox();
        infoBox.init();
        holderList = holder.getChildren();
        dealerHandList = dealerHand.getChildren();
        this.setSpacing(10);
        holder.setSpacing(10);
        setVbox(this);
        list = this.getChildren();  
        holderList.addAll(infoBox,handValueLabel,dealerHand);
        list.addAll(dealerLabel,holder);
    }
    
    private void setVbox(VBox vbox){
        VBox.setMargin(vbox, new Insets(0, 3, 0, 3)); 
        vbox.setPrefWidth(SCREEN_RES.getWidth());
        vbox.setPrefHeight(SCREEN_RES.getHeight() / 4);
        vbox.setMaxSize(SCREEN_RES.getWidth()/2, SCREEN_RES.getHeight() / 4);
    }
    
    public void addCard(Label card){
        dealerHandList.add(card);
    }
    
    public void removeFaceDownCard(){
        dealerHandList.remove(1);
    }
    public void newRound(){
        setHandValue("0");
        dealerHandList.clear();
    }
    public void setHandValue(String value){
        handValueLabel.setText("Dealer's hand value:\n" + value);
    }
    
    public void setInfo(String info){
        infoBox.setInfo(info);
    }
    
    public void setTime(String time){
        infoBox.setTime(time);
    }
    
}
