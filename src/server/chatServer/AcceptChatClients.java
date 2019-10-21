/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.chatServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Faludi Péter
 */
class AcceptChatClients implements Runnable{
    
    ChatServer server;
    ServerSocket serverSocket;

    public AcceptChatClients(ChatServer server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
    }
    
    
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
