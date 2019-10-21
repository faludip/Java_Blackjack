package server.gameServer;

import server.logic.Player;
import dao.PlayerDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import server.BCrypt;

/**
 * A validálásért felelős osztály
 * @author Faludi Péter
 */
public class Validate implements Runnable{
    
    private Socket socket; 
    private BufferedReader in;
    private PrintWriter out;
    private GameServer server;

    /**
     *
     * @param socket felhasználó sockete
     * @param server játékszerver
     */
    public Validate(Socket socket,GameServer server) {
        this.socket = socket;
        this.server = server;
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(inputStreamReader);
            out = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            System.err.println("ERROR");
            
        }
    }
    /*
    * A kliens üzeneteket váró függvény
    */
    private String getMessage(){
        String message = null;
        while(message == null){
            try {
                message = in.readLine();
            } catch (IOException ex) {
                return "LEAVE";
            }
        }
        return message;
    }
    
    /**
     * Az üzenetek feldolgozásáért felelős függvény 
     * @param message kliens üzenete
     */
    private boolean processAnswer(String message){
        if(message.equals("LEAVE")){
            try {
                socket.close();
            } catch (IOException ex) {
            }
            return true;
        }
//        System.out.println(message);
        String[] clientmessage = message.split("--");
        String answer;
        List<String> datas = new ArrayList<>();
        switch(clientmessage[0]){
            case "LOGIN":
                if(server.isOnline(clientmessage[1])){
                    out.println("ONLINE");
                    return false;
                }
                try {
                    datas = PlayerDao.getInstance().findLoginDatas(clientmessage[1]);
                } catch (SQLException ex) {
                    System.err.println("SQL error");
                }
                if(datas.isEmpty()){
                    out.println("NOTEXIST");
                }else if(BCrypt.checkpw(clientmessage[2], datas.get(1))){
                    out.println("OK");
                    Player player = PlayerDao.getInstance().findPlayerByUsername(clientmessage[1]);
                    player.setSocket(socket);
//                    System.out.println(clientmessage[2]);
                    WaitingRoom waitingRoom = new WaitingRoom(server, player);
                    server.addPlayer(socket, player);
                    waitingRoom.getChoice();
                    return true;
                }else out.println("WRONGPASS");
                break;
            case "REGISTER":
                try {
                   answer = PlayerDao.getInstance().alreadyContains(clientmessage[1], clientmessage[3]);
                   out.println(answer);
                   if(answer.equals("OK--OK")){
                        PlayerDao.getInstance().createPlayer(clientmessage[1],
                               clientmessage[2],
                               clientmessage[3],
                               clientmessage[4],
                               clientmessage[5]);
                   }
                } catch (SQLException ex) {
                }
                break;
        }
        return false;
    }
    
    @Override
    public void run() {
        while(!processAnswer(getMessage())){}
    }
    
}
