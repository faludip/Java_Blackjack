package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import view.PlayerBox;

/**
 *
 * @author Faludi Péter
 *  Ez az osztály felel az adatok tárolásáért a játék közben
 */
public class ClientModel {
    
    private final BufferedReader in;
    private final PrintWriter out;
    private final LinkedList<PlayerBox> playersPanel;

    /**
     *
     * @param in a socket bemenete
     * @param out a socket kimenete
     */
    public ClientModel(BufferedReader in, PrintWriter out){
        playersPanel = new LinkedList<>();
        this.in = in;
        this.out = out;
    }
    
    /**
     * 
     * a szerverüzenet fogadás
     * @return szerver parancs
     */
    public String getServerMessage(){
        String serverMessage = null;
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){
        }
        while(serverMessage == null){
            try{
                serverMessage = in.readLine();
            } catch (IOException ex) {
                System.out.println("Communication stoped");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(ClientModel.class.getName()).log(Level.SEVERE, null, ex1);
                }
                System.exit(0);
            }
        }
//        System.out.println(serverMessage);
        return serverMessage;
    }
    
    /**
     *Válasz küldése
     * @param message
     */
    public void sendClientMessage(String message){
        out.println(message);
    }
    
    /**
     *A játékos kezét szimuláló panel hozzáadása
     * @param playerId játékos azonositó
     * @param index kéz sorszáma
     */
    public void addHandPanel(String playerId,String index){
        for(PlayerBox player : playersPanel){
            if(player.getPlayersId() == Integer.parseInt(playerId)){
                player.addHand(index);
            }
        }
    }
    
    /**
     * Kezet szimuláló panel eltávoltása
     * @param playerId játékos azonositó
     * @param index kéz sorszáma
     */
    public void removeHandPanel(String playerId, String index){
        for(PlayerBox player : playersPanel){
            if(player.getPlayersId() == Integer.parseInt(playerId)){
                player.removeHand(index);
                return;
            }
        }
    }
    
    /**
     *
     * @param playerId játékos azonositó
     * @param index kéz sorszáma
     * @return játékos adott keze
     */
    public ObservableList getHand(String playerId, String index){
        for(PlayerBox player : playersPanel){
            if(player.getPlayersId() == Integer.parseInt(playerId)){
                return  player.getHand(index).getChildren();
            }
        }
        return null;
    }
    
    /**
     *
     * @param playerId játékos azonositó
     * @return a játékost szimbolizáló panel
     */
    public PlayerBox getPlayerBox(String playerId){
        for(PlayerBox player : playersPanel){
            if(player.getPlayersId() == Integer.parseInt(playerId)){
                return player;
            }
        }
        return null;
    }
    
    /**
     * Játékost szimbolizáló panel hozzásadása
     * @param id játékos azonositó
     * @param username felhasználónév
     * @param money játékos pénze
     */
    public void addPlayerBox(String id,String username,String money){
        playersPanel.add(new PlayerBox(Integer.parseInt(id), username, money));
    }
       
    /**
     * játkost szimbolizáló panel eltávolitása
     * @param id játékosazonositó
     */
    public void removePlayer(String id){
        playersPanel.remove(getPlayerBox(id));
    }
    
}
