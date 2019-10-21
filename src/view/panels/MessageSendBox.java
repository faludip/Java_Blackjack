package view.panels;

import client.ChatModel;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import static view.TableView.SHADOW;

/**
 * A chat panel beviteli mezője és a küldés gömb
 * @author Faludi Péter
 */
public class MessageSendBox extends HBox{
    private TextField messageTextField;
    private Button sendButton;
    private ObservableList sendList;
    private ChatModel model;

    public MessageSendBox(ChatModel model) {
        super();
        
        this.model = model;
        init();
    }
    
    private void init(){
        sendList = this.getChildren();
        messageTextField = new TextField();
        messageTextField.setStyle("-fx-background-color: #E9981D;");
        messageTextField.setPromptText("Message");
        sendButton = createButton("Send");
        setActionListener();
        sendList.addAll(messageTextField,sendButton);
        
    }
    
    private Button createButton(String prompt){
        Button button = new Button(prompt);
        button.setStyle("-fx-background-color: #E9981D;");
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            button.setEffect(SHADOW);
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            button.setEffect(null);
        });
        HBox.setMargin(button, new Insets(0, 20, 0, 20));
        return button;
    }
    private void setActionListener(){
        sendButton.setOnAction((ActionEvent event) -> {
            if(!messageTextField.getText().isEmpty()){
                model.sendClientMessage(messageTextField.getText());
                messageTextField.clear();
            }
        });
        messageTextField.setOnKeyReleased(event -> {
            if (!messageTextField.getText().isEmpty() && event.getCode() == KeyCode.ENTER){
                model.sendClientMessage(messageTextField.getText());
                messageTextField.clear();
            }
        });
    }
    
}
