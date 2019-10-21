package server.gameServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A kliensek fogadásáért felelős folyamat
 * @author Faludi Péter
 */
public class ServerListener implements Runnable{
   private final GameServer server;
   private final ServerSocket serverSocket;
    

    public ServerListener(GameServer server, ServerSocket serverSocket) {
        this.server = server;
        this.serverSocket = serverSocket;
        
    }
    /**
     *  Kliensek fogadása és új folyamat indítása a kliensnek
     */
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
