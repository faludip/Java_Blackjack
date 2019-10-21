package view.panels;

import client.ClientController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Tér rakásért felelős panel
 * @author Faludi Péter
 */
public class BetBox extends PanelBox{
    private ObservableList betBoxList;
    private ObservableList betList;
    private Label betLabel;
    private Button betButton;
    private HBox betBox;
    private TextField betTextField;
    ClientController controller;

    public BetBox(ClientController controller) {
        this.controller = controller;
    }
    
    @Override
    public void init(){
        betBoxList = this.getChildren();
        betLabel = new Label("Place a bet!");
        setLabelFont(betLabel);
        betBox = new HBox();
        betList = betBox.getChildren();
        betButton = createButton("Bet");
        betTextField = makeNumeralTextField("Bet");
        betList = betBox.getChildren();
        setEventHandlers();
        betList.addAll(betTextField,betButton);
        betLabel.setAlignment(Pos.CENTER);
        betBoxList.addAll(betLabel,betBox);
    }
    
    
    @Override
    protected void setEventHandlers(){
        betButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage(betTextField.getText());
            betTextField.clear();
            controller.clearControlPanel();
        });
    }
    
}
