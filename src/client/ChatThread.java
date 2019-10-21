package client;

import java.io.BufferedReader;
import java.io.IOException;
import javafx.scene.control.Label;
import view.panels.ChatPane;

/**
 *
 * @author Faludi Péter
 */
public class ChatThread implements Runnable{
    private final BufferedReader in;
    private LobbyController controller;
    private final StringBuilder builder;

    /**
     *
     * @param in a socket részére fent tartott bemenenet
     * @param controller a lobby írányító osztálya
     */
    public ChatThread(BufferedReader in,LobbyController controller) {
        this.in = in;
        this.controller = controller;
        builder = new StringBuilder();
    }
    
    /**
     *Szerverüzenet olvasása
     */
    public void getServerMessage(){
        String serverMessage = null;
        try{
            Thread.sleep(200);
        }catch(InterruptedException e){
        }
        while(serverMessage == null){
            try{
                serverMessage = in.readLine();
            } catch (IOException ex) {
            }
        }
//        System.out.println(serverMessage);
        controller.setChatMessage(getMessage(serverMessage));
    }

    /**
     *
     * @param message chat üzenet
     * @return új üzenettel visszatérő ScrollPane
     */
    public ChatPane getMessage(String message){
        builder.append(message).append("\n");
        return new ChatPane(new Label(builder.toString()));
    }

    public void setController(LobbyController controller) {
        this.controller = controller;
        controller.setChatMessage(new ChatPane(new Label(builder.toString())));
    }
    

    @Override
    public void run() {
        while(true){
          getServerMessage(); 
        }
    }
    
    
}
