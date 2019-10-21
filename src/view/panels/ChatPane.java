package view.panels;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;

/**
 * Chat ablak megjelenitő felülete
 * @author Faludi Péter
 */
public class ChatPane extends ScrollPane{
    Label label;

    public ChatPane(Label label) {
        this.label = label;
        this.setBackground(Background.EMPTY);
        this.setStyle("-fx-background-color:transparent;" );
        this.setPannable(true);
        this.setPrefWidth(200);
        this.setPrefHeight(150);
        this.setStyle("-fx-background: #E9981D;");
        label.setPrefWidth(this.getPrefWidth());
        label.setWrapText(true);
        this.vvalueProperty().bind(label.heightProperty());
        this.setContent(label);
        
    }
    
    
//    public void getMessage(String message){
//        stringBuilder.append(message);
//        stringBuilder.append("\n");
//        Label label = new Label(stringBuilder.toString());
//        label.setMaxWidth(this.getMaxWidth()-10);
//        label.setWrapText(true);
//        this.vvalueProperty().bind(label.heightProperty());
//        this.setContent(label);
//        
//        
//    }
//    
}
