package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Faludi Péter
 */
public class ChatModel {
    private final Socket chatSocket;
    private final LobbyController controller;
    private BufferedReader in;
    private PrintWriter out;
    private ChatThread chatThread;
    private Thread thread;
    
    /**
     *
     * @param chatSocket csevegésre használt socket
     * @param controller irányitó osztály
     */
    public ChatModel(Socket chatSocket,LobbyController controller) {
        this.chatSocket = chatSocket;
        this.controller = controller;
    }
    
    /**
     *Osztály változóinak inicializása
     */
    public void init(){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(chatSocket.getInputStream());
            in = new BufferedReader(inputStreamReader);
            out = new PrintWriter(chatSocket.getOutputStream(),true);
        } catch (IOException e) {
        }
        chatThread = new ChatThread( in, controller);
        thread = new Thread(chatThread);
        thread.start();
    }
    
    /**
     * Kliensnek üzenet küldése
     * @param message elküldendő üzenet
     */
    public void sendClientMessage(String message){
        out.println(message);
    }
    
    public void setController(LobbyController controller){
        chatThread.setController(controller);
    }
    
    public Socket getChatSocket() {
        return chatSocket;
    }

    public ChatThread getChatThread() {
        return chatThread;
    }
    
}
