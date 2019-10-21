package server.chatServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Egy játékos chat folyamata
 * @author Faludi Péter
 */
public class ChatPartner implements Runnable{
    Socket chatSocket = null;
    ChatServer server = null;
    BufferedReader in;
    PrintWriter out;
    String username;

    public ChatPartner(Socket chatSocket,ChatServer chatServer) throws IOException {
        this.chatSocket = chatSocket;
        this.server = chatServer;
        InputStreamReader inputStreamReader = new InputStreamReader(chatSocket.getInputStream());
        in = new BufferedReader(inputStreamReader);
        out = new PrintWriter(chatSocket.getOutputStream(),true);
    }
    
    private void getName() throws IOException{
        String clientMessage = null;
        while(clientMessage == null){
            clientMessage = in.readLine();
        }
        username = clientMessage;
//        System.err.println(username);
        server.addPartner(this);
    }
    private void getPartnerMessage() throws IOException {
        String clientMessage = null;
        while(clientMessage == null){
            clientMessage = in.readLine();
        }
//        System.out.println(username+ ":"+clientMessage);
        server.sendToClient(username+ ":"+clientMessage);
        
    }

    public PrintWriter getOut() {
        return out;
    }

    @Override
    public void run() {
        try {
            getName();
            while(!chatSocket.isClosed()){
                try {
                    getPartnerMessage();
                    Thread.sleep(500);
                } catch (IOException | InterruptedException ex) {
                    server.removePartner(this);
                    break;
                    
                }
            }
        } catch (IOException ex) {
        }
    }
    
}
