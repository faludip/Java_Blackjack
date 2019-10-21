package view.panels;

import client.ClientController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Folytatás panel
 * @author Faludi Péter
 */
public class ContinueBox extends PanelBox{
    private ObservableList countinueList;
    private Label continueLabel;
    private Button noButton;
    private Button yesButton;
    private HBox continueHolder;
    private ObservableList continueHolderList;
    private final ClientController controller;
    

    public ContinueBox(ClientController controller) {
        this.controller = controller;
    }
    
     @Override
    public void init() {
        countinueList = this.getChildren();
        continueLabel = new Label("Continue?");
        setLabelFont(continueLabel);
        noButton = createButton("No");
        yesButton = createButton("Yes");
        continueHolder = new HBox();
        continueHolderList = continueHolder.getChildren();
        setEventHandlers();
        continueHolderList.addAll(yesButton,noButton);
        countinueList.addAll(continueLabel,continueHolder);
    }
    
    @Override
    protected void setEventHandlers(){
        
        noButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("No");
        controller.clearControlPanel();
        });
        yesButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("Yes");
            controller.clearControlPanel();
        });
    }
    
}
