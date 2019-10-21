package view.panels;

import client.ClientController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A duplázásért felelős panel
 * @author Faludi Péter
 */
public class DoubleDownBox extends PanelBox{
    private ObservableList doubleDownList;
    private Label bothLabel;
    private Button hitButton;
    private Button stayButton;
    private Button doubleDownButton;
    private HBox doubleDownHolder;
    private ObservableList doubleDownHolderList;
    private final ClientController controller;

    public DoubleDownBox(ClientController controller) {
        this.controller = controller;
    }
    
    @Override
    public void init(){
        doubleDownList = this.getChildren();
        bothLabel = new Label("Hit,stay or double down ?");
        setLabelFont(bothLabel);
        hitButton = createButton("Hit");
        stayButton = createButton("Stay");
        doubleDownButton = createButton("Double Down");
        doubleDownButton.setAlignment(Pos.CENTER);
        setMargin(doubleDownButton, new Insets(10, 0, 0, 30));
        setEventHandlers();
        doubleDownHolder = new HBox();
        doubleDownHolderList = doubleDownHolder.getChildren();
        doubleDownHolderList.addAll(hitButton,stayButton);
        doubleDownList.addAll(bothLabel,doubleDownHolder,doubleDownButton);
    }
   
    
    @Override
    protected void setEventHandlers(){
        hitButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("Hit");
            controller.clearControlPanel();
        });
        stayButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("Stand");
            controller.clearControlPanel();
        });
        doubleDownButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("Double Down");
            controller.clearControlPanel();
            
        });
    }


}
