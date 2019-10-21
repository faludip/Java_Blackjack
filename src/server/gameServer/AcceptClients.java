/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.gameServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Faludi Péter
 */
public class AcceptClients implements Runnable{
   private final GameServer server;
   private final ServerSocket serverSocket;
    

    public AcceptClients(GameServer server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
        
    }
    
    private void acceptClients() {
        System.out.println("server starts port = " + serverSocket.getLocalSocketAddress());
        while(true){
            try{
                Socket socket = serverSocket.accept();
                System.out.println("accepts : " + socket.getRemoteSocketAddress());
                Validate validate = new Validate(socket, server);
                
                
                Thread validateThread = new Thread(validate);
                validateThread.start();
            } catch (IOException ex){
                System.out.println("Accept failed on : " + serverSocket.getLocalPort());
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
