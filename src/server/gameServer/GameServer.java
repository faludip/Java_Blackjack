package server.gameServer;

import server.chatServer.ChatServer;
import server.logic.ServerTable;
import server.logic.Player;
import dao.PlayerDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A szerver osztály
 * @author Faludi Péter
 */
public class GameServer{
    
    private static final String SERV_AUTH ="SERVERLOBBY";
    private ServerSocket serverSocket ;
    private final int serverPort;
    private final int chatPort;
    private List<Player> onlinePlayers;
    private List<ServerTable> tables;
    private ChatServer chatServer;
    private ServerListener serverListener;
    private Thread serverListenerThread;
    
    /**
     *
     * @param serverPort
     * @param chatPort
     */
    public GameServer( int serverPort,int chatPort) {
        this.serverPort = serverPort;
        this.chatPort = chatPort;
    }
    /*
    *Szerver indítása
    *
    */
    private void startServer(){
        onlinePlayers = new ArrayList<>();
        tables = new ArrayList<>();
//        tableThreads = new ArrayList<>();
        createTables();
        serverSocket = null;
        chatServer = new ChatServer(chatPort,this);
        try {
            serverSocket = new ServerSocket(serverPort);
            serverListener = new ServerListener(this, serverSocket);
            serverListenerThread = new Thread(serverListener);
            serverListenerThread.start();
        } catch (IOException e) {
            System.err.println("Could not listen on port: "+serverPort);
            System.exit(1);
        }
    }
    
    /*
    * Játék asztalok térehozása
    */
    private void createTables(){
        tables.add(new ServerTable(10,3,30,1));
        tables.add(new ServerTable(50,3,30,2));
        tables.add(new ServerTable(100,3,30,3));
        tables.add(new ServerTable(200,3,30,4));
        for(ServerTable table : tables){
            Thread thread = new Thread(table);
//            tableThreads.add(thread);
            thread.start();
        }
    }
    
    /**
     * Játékasztalok hozzáadása a játékhoz
     * @param minBet
     * @param numOfDecks
     * @param minimumCardsBeforeShuffle
     */
    public void addTable(int minBet, int numOfDecks,int minimumCardsBeforeShuffle){
       int thisId = 0;
       for(ServerTable table : tables){
           if(table.getId() > thisId){
               thisId = table.getId() + 1;
           }
       }
       ServerTable table = new ServerTable(minBet,numOfDecks,minimumCardsBeforeShuffle,thisId);
       tables.add(table);
       Thread thread = new Thread(table);
//       tableThreads.add(thread);
       thread.start();
    }

    
    /**
     *
     * @param player
     * @param index
     * @return
     */
    public boolean addPlayersToTable(Player player,int index){
        for(ServerTable table : tables){
            if(table.getId() == index && table.getTableSize() < 4){
                return table.addPlayerToWaiters(player);
            }
        }
        return false;
    }
    
    /**
     * A Lobby betöltéséhez szükséges adatok elküldése a kliensnek 
     * @param player kliens
     */
    public void sendDataForLobby(Player player){
        Socket socket = player.getSocket();
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder;
            builder = new StringBuilder(SERV_AUTH + "--PLAYER"); 
            builder//.append("--").append(player.getId())
                    .append("--").append(player.getUsername())
                    .append("--").append(player.getMoney().toString())
                    .append("--").append(player.getWin())
                    .append("--").append(player.getLose());
            out.println(builder.toString());
            if(!getClientMessage(socket).equals("OK")){
                addPlayer(socket, player);
            }
//            sendTableList(socket, out);
            sendLeaderBoard(socket,out);
        }catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    /**
     * Az online játékosokhoz adás belépés után
     * @param socket
     * @param player
     */
    public void addPlayer(Socket socket,Player player){
        if(socket.isClosed()){return;}
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(),true);
            StringBuilder builder;
            builder = new StringBuilder(SERV_AUTH + "--PLAYER"); 
            builder.append("--").append(player.getUsername())
                    .append("--").append(player.getMoney().toString())
                    .append("--").append(player.getWin())
                    .append("--").append(player.getLose());
            out.println(builder.toString());
            if(!getClientMessage(socket).equals("OK")){
                addPlayer(socket, player);
            }
            player.setServer(this);
            onlinePlayers.add(player);
            sendTableList(socket, out);
            sendLeaderBoard(socket,out);
        } catch (IOException ex) {
        }
    }
    
    private void sendTableList(Socket socket,PrintWriter out){
        if(socket.isClosed()){return;}
        StringBuilder builder;
        builder = new StringBuilder(SERV_AUTH + "--TABLELIST"); 
        for(ServerTable table : tables){
            builder.append("--").append(table.getInfo()[0]).append("--").append(table.getInfo()[1]);
        }
        out.println(builder.toString());
        if(!getClientMessage(socket).equals("OK")){
            sendTableList(socket,out);
        }
    }
    
    /*
    * Ranglista adatok elküldése
    */      
    private void sendLeaderBoard(Socket socket,PrintWriter out){
        if(socket.isClosed()){return;}
        StringBuilder builder = new StringBuilder();
        builder.append(SERV_AUTH + "--LEADERBOARD");
        List<Player> players = PlayerDao.getInstance().findAllPlayers();
        for(Player player : players){
            builder.append("--").append(player.getUsername())
                    .append("--").append(player.getMoney().toString())
                    .append("--").append(player.getWin())
                    .append("--").append(player.getLose());
        }
        out.println(builder.toString());
        String message = getClientMessage(socket) ;
        if(!message.equals("OK")){
            if(message.equals("LEAVE")){
                return;
            }
            sendLeaderBoard(socket,out);
        }
    }

    
    
    /*
    * Távozó játékosokat távolítja el az online játékosok közül
    */
    private void removeOfflineClient(Socket socket){
        Player offlinePlayer = null;
        for(Player player :onlinePlayers){
            if(socket.equals(player.getSocket())){
                offlinePlayer = player;
                break;
            }
        }
        if(onlinePlayers.contains(offlinePlayer)){
            onlinePlayers.remove(offlinePlayer);
        }
    }
    
    /**
     * Kliens üzenetének fogadása
     * @param socket kliens sockete
     * @return kliens üzenete
     */
    public String getClientMessage(Socket socket){
        BufferedReader in;
        if(socket.isClosed()){
            return null;
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
            in = new BufferedReader(inputStreamReader);
            String message = null;
            while(message == null){
                message = in.readLine();
            }
            if(message.equals("LEAVE")){
                removeOfflineClient(socket);
                socket.close();
            }
            return message;
        } catch (IOException e) {
            System.err.println("ERROR SOCKET");
            removeOfflineClient(socket);
        }
        return null;
    }

    /**
     *
     * @param username
     * @return A játékos online van-e?
     */
    public boolean isOnline(String username){
        for(Player player : onlinePlayers){
            if(username.equals(player.getUsername())){
                return true;
            }
        }
        return false;
    }
    
    /**
     *
     * @return
     */
    public List<Player> getOnlinePlayers() {
        return onlinePlayers;
    }
    
    /**
     * 
     * @return játékasztal
     */
    public List<ServerTable> getTables() {
        return tables;
    }
   
    /**
     * Szerver indításáért felelős metódus 
     * paraméter nélkül játék-port:4444 ,chat-port-4445
     * paraméterrer args[0] a játék-port, args[1] a chat-port
     * @param args paraméterek
     */
    public static void main(String[] args){
        if(args.length == 2){
            try{
                GameServer server = new GameServer(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
                server.startServer();
            }catch(NumberFormatException e){
                System.err.println("Invalid parameter");
            }
        }else{
            System.out.println("Try to run on port 4444,4445");
            GameServer server = new GameServer(4444,4445);
            server.startServer();
        }
    }
   
}
