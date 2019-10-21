package server.gameServer;

import server.logic.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Ez az osztály felelős a játékasztalhoz való csatlakozásért
 * @author Faludi Péter
 */
public class WaitingRoom {

    
    GameServer server;
    BufferedReader in;
    PrintWriter out;
    Player player;

    /**
     *
     * @param server
     * @param player
     */
    public WaitingRoom(GameServer server, Player player) {
        
        this.server = server;
        this.player = player;
        this.in = player.getIn();
        this.out = player.getOut();
    }
    
    /**
     * A játékasztal sorszámárá váró metódus
     * Megpróbál csatlakozni az asztalhoz
     * 
     */
    public void getChoice(){
        String[] clientMessage = getMessage().split("--");
        if(clientMessage[0].equals("TABLEJOIN")){
            if(server.addPlayersToTable(player, Integer.parseInt(clientMessage[1]))){
                return;
            }
        }
        if(clientMessage[0].equals("LEAVE")){
            server.getOnlinePlayers().remove(player);
            return;
        }
        getChoice();
    }
    
    /*
    * Kliens üzenet fogadása
    */
    private String getMessage(){
        String message = null;
        while(message == null){
            try {
                message = in.readLine();
            } catch (IOException ex) {
                message = "LEAVE";
            }
        }
//        System.out.println(message);
        return message;
    }
    
   
}
