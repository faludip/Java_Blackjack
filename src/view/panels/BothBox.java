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
import javafx.scene.layout.HBox;

/**
 * Nincs használva,nem lett implementálva funkció
 * @author Faludi Péter
 */
public class BothBox extends PanelBox{
    private ObservableList bothList;
    private Label bothLabel;
    private Button splitButton;
    private Button hitButton;
    private Button stayButton;
    private Button doubleDownButton;
    private HBox bothHolder;
    private ObservableList bothHolderList;
    private final ClientController controller;

    public BothBox(ClientController controller) {
        this.controller = controller;
    }
    
    @Override
    public void init(){
        bothList = this.getChildren();
        bothLabel = new Label("Hit,stay,double down or split pair?");
        setLabelFont(bothLabel);
        hitButton = createButton("Hit");
        stayButton = createButton("Stay");
        splitButton = createButton("Split");
        doubleDownButton = createButton("Double Down");
        setEventHandlers();
        bothHolder = new HBox();
        bothHolderList = bothHolder.getChildren();
        bothHolderList.addAll(hitButton,stayButton);
        bothList.addAll(bothHolder,splitButton,doubleDownButton);
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
        splitButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("Split Pairs");
            controller.clearControlPanel();
        });
        doubleDownButton.setOnAction((ActionEvent event) -> {
            controller.sendClientMessage("Double Down");
            controller.clearControlPanel();
        });
    }
    
}
