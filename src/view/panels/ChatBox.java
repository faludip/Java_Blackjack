package view.panels;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/**
 * Chat ablak
 * @author Faludi Péter
 */
public class ChatBox extends VBox{
   
   private ObservableList chatList;
   private ChatPane chatPane;
   private MessageSendBox sendBox;
   
    
    public ChatBox(ChatPane chatPane,MessageSendBox sendBox) {
        this.chatPane = chatPane;
        this.sendBox = sendBox;
        init();
    }
    
    private void init(){
        chatList = this.getChildren();
        setMargin(sendBox, new Insets(5, 0, 0, 0));
        setMaxWidth(300);
        chatList.addAll(chatPane,sendBox);
    }

    public ChatPane getChatPane() {
        return chatPane;
    }

    public void setChatPane(ChatPane chatPane) {
        
       // this.chatPane = chatPane;
       Platform.runLater(() -> {
           chatList.set(0, chatPane);
       });

    }
    

    
    
    
    
}
