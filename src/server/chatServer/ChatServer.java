package server.chatServer;

import java.io.BufferedReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import server.gameServer.GameServer;

/**
 * Chat szerver
 * @author Faludi Péter
 */
public class ChatServer {
    
    ServerSocket chatServerSocket;
    int port;
    List<ChatPartner> partners;
    private BufferedReader in;
    Thread acceptChatClientsThread ;
    GameServer gameServer;
    

    public ChatServer(int port,GameServer gameServer) {
        this.port = port;
        this.gameServer = gameServer;
        startServer();
    }
    
    /**
     * A szerver indítása
     */
    private void startServer(){
        partners = new ArrayList<>();
        chatServerSocket = null;
        try {
            chatServerSocket = new ServerSocket(port);
            ChatServerListener accept = new ChatServerListener(this,chatServerSocket);
            acceptChatClientsThread = new Thread(accept);
            acceptChatClientsThread.start();
        } catch (Exception e) {
        }
    }
    
    public void addPartner(ChatPartner chatPartner){
        partners.add(chatPartner);
    }
    
    
    public void sendToClient(String message){
        for(ChatPartner partner : partners){
            partner.getOut().println(message);
        }
    }   
    
    public void removePartner(ChatPartner chatPartner){
        if(partners.contains(chatPartner)){
            partners.remove(chatPartner);
        }
        
    }
        
        
    
}
