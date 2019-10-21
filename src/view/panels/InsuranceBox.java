/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.panels;

import client.ClientController;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 *
 * @author Faludi Péter
 */
public class InsuranceBox extends PanelBox{
    private ObservableList insuranceList;
    private Label insuranceLabel;
    private HBox insuranceHolder;
    private Button noButton;
    private Button yesButton;
    private ObservableList insuranceHolderList;
    private final ClientController controller;

    public InsuranceBox(ClientController controller) {
        this.controller = controller;
    }
    
    @Override
    public void init(){
        insuranceList = this.getChildren();
        insuranceLabel = new Label("Do you make insurance?");
        setLabelFont(insuranceLabel);
        noButton = createButton("No");
        yesButton = createButton("Yes");
        insuranceHolder = new HBox();
        insuranceHolderList = insuranceHolder.getChildren();
        setEventHandlers();
        insuranceHolderList.addAll(yesButton,noButton);
        insuranceList.addAll(insuranceLabel,insuranceHolder);
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
