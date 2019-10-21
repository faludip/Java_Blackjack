/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.panels;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import view.TableView;
import static view.TableView.GAME_FONT_TYPE;
import static view.TableView.SHADOW;
import static view.TableView.YELLOW_FONT_COLOR;

/**
 * Az irányító panelek ősosztálya
 * @author Faludi Péter
 */
public abstract class PanelBox extends VBox{

    public PanelBox() {
        setSize();
    }
    
    
    
    protected void setLabelFont(Label label){
       label.setFont(GAME_FONT_TYPE);
       label.setTextFill(YELLOW_FONT_COLOR);
       
    }
    
    protected Button createButton(String name){
        Button button = new Button(name);
        button.setStyle( "-fx-background-color: #E9981D;");
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            button.setEffect(SHADOW);
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            button.setEffect(null);
        });
        HBox.setMargin(button, new Insets(10, 20, 10, 20));
        return button;
    }
    
    protected TextField makeNumeralTextField(String prompt){
        TextField textField = new TextField();
        HBox.setMargin(textField, new Insets(10, 20, 10, 20));
        textField.setStyle("-fx-background-color: #E9981D;");
        textField.setPromptText(prompt);
        textField.setPrefWidth(65);
        textField.lengthProperty().addListener(
                (ObservableValue<? extends Number> observable,
                Number oldValue,
                Number newValue) -> {
            if (newValue.intValue() > oldValue.intValue()) {
                char ch = textField.getText().charAt(oldValue.intValue());
                // Check if the new character is the  number or other's
                if (!(ch >= '0' && ch <= '9' )) {
                    // if it's not number then just setText to previous one
                    textField.setText(textField.getText().substring(0,textField.getText().length()-1));
                }
            }
        });
        return textField;
    }
    
    protected void setSize(){
        this.setMaxSize(TableView.SCREEN_RES.getWidth()/4, TableView.SCREEN_RES.getHeight()/4);
        this.setPrefSize(TableView.SCREEN_RES.getWidth()/5, TableView.SCREEN_RES.getHeight()/6);
        this.setStyle(TableView.CSS_LAYOUT);
    }
    
    
    abstract protected void init();
    
    protected abstract void setEventHandlers();
    

    
}
