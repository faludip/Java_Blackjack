package server.chatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A (chat)kliensek fogadásáért felelős folyamat
 * @author Faludi Péter
 */
class ChatServerListener implements Runnable{
    
    ChatServer server;
    ServerSocket serverSocket;

    public ChatServerListener(ChatServer server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
    }
    
     /**
     *  Kliensek fogadása és új folyamat indítása a kliensnek
     */
    private void acceptClients() {
        System.out.println("chat-server starts on port = " + serverSocket.getLocalSocketAddress());
        while(true){
            try{
                Socket socket = serverSocket.accept();
                System.out.println("chat accepts : " + socket.getRemoteSocketAddress());
                ChatPartner chatPartner = new ChatPartner(socket, server);
                Thread chatPartnerThread = new Thread(chatPartner);
                chatPartnerThread.start();
            } catch (IOException ex){
            }
        }
    }
    

    @Override
    public void run() {
        while(true){
            acceptClients();
        }
    }
    
    
    
}
