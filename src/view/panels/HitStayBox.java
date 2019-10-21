package view.panels;

import client.ClientController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * A lapkérés illetve a megállásért felelős panel
 * @author Faludi Péter
 */
public class HitStayBox extends PanelBox{
    private ObservableList hitStayList;
    private Label hitLabel;
    private Button hitButton;
    private Button stayButton;
    private HBox hitStayHolder;
    private ObservableList hitStayHolderList;
    private final ClientController controller;

    public HitStayBox(ClientController controller) {
        this.controller = controller;
    }
    
    @Override
    public void init(){
        hitStayList = this.getChildren();
        hitLabel = new Label("Hit or Stay?");
        setLabelFont(hitLabel);
        hitButton = createButton("Hit");
        stayButton = createButton("Stay");
        setEventHandlers();
        hitStayHolder = new HBox();
        hitStayHolderList = hitStayHolder.getChildren();
        hitStayHolderList.addAll(hitButton,stayButton);
        hitStayList.addAll(hitLabel,hitStayHolder);
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
        
       
    }
}
