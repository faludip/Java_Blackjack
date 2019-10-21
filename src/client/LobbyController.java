package client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.Stage;
import view.panels.ChatPane;
import view.Lobby;
import view.LobbyView;
import view.panels.ChatBox;

/**
 *
 * @author Faludi Péter
 * Ez az osztály vezérli a Lobby felületét
 */
public class LobbyController {
    private final Socket gameSocket;
    private Socket chatSocket;
    private final BufferedReader in;
    private final PrintWriter out;
    private List<List<String>> leaderBoardDatas;
    private List<List<String>> tableDatas;
    private Lobby lobby;
    private String name,money,win,lose;
    private final ChatModel chatModel;
    private ChatBox chatBox;
    private LobbyView view;
    
    /**
     * A belépés után lefutó konstruktor
     * @param gameSocket játék kommunikációjáért felelős socket
     * @param chatSocket chat kommunikációjáért felelős socket
     * @param in játék kommunikációjáért felelős bemenet
     * @param out játék kommunikációjáért felelős kimenet
     */
    public LobbyController(Socket gameSocket, Socket chatSocket, BufferedReader in, PrintWriter out) {
        this.gameSocket = gameSocket;
        this.chatSocket = chatSocket;
        this.in = in;
        this.out = out;
        chatModel = new ChatModel(chatSocket,this);
        chatModel.init();
    }

    /**
     * A játék után lefutó konstruktor
     * @param gameSocket játék kommunikációjáért felelős socket
     * @param in játék kommunikációjáért felelős bemenet
     * @param out játék kommunikációjáért felelős kimenet
     * @param chatModel a chat modellje
     */
    public LobbyController(Socket gameSocket, BufferedReader in, PrintWriter out, ChatModel chatModel) {
        this.gameSocket = gameSocket;
        this.in = in;
        this.out = out;
        this.chatModel = chatModel;
    }
    
    /**
     * változók inicializása
     */
    public void init(){
        tableDatas = new ArrayList<>();
        leaderBoardDatas = new ArrayList<>();
        getPlayerInfo();
        getTableListInfo();
        getLeaderBoardInfo();
        lobby = new Lobby(name, money, win, lose,chatModel,this);
        lobby.init();
        chatBox = lobby.getChatBox();
        lobby.setUpLeaderBoard(leaderBoardDatas);
        lobby.setComboBox(tableDatas);
        view = new LobbyView(lobby);
        try {
            view.start(new Stage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * chatablak hozzáadása
     * @param chatPane chatablak
     */
    public void setChatMessage(ChatPane chatPane){
        lobby.setChatPanel(chatPane);
    }

    /**
     *
     * @return chatablak
     */
    public ChatBox getChatbox() {
        return chatBox;
    }
    
    /**
     * adatok frissítése
     */
    public void refresh(){
        leaderBoardDatas.clear();
        getPlayerInfo();
        getLeaderBoardInfo();
        lobby.updateInfo(money, win, lose);
        lobby.setChatBox(chatBox);
        lobby.setUpLeaderBoard(leaderBoardDatas);
        try {
            view.start(new Stage());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * játékos adatatainak frissítése
     */
    private void getPlayerInfo(){
        String[] serverMessage= getServerMessage().split("--");
        if(!serverMessage[0].equals("SERVERLOBBY")){
            System.err.println("UNKNOWN SERVER MESSAGE");
            out.println("ERROR");
            getPlayerInfo();
        }
        if(serverMessage[1].equals("PLAYER") || serverMessage.length == 7){
            name = serverMessage[2];
            money = serverMessage[3];
            win = serverMessage[4];
            lose = serverMessage[5];
            out.println("OK");
        }else{
            out.println("ERROR");
            getPlayerInfo();
        }
    }
    
    /**
     * táblaadatok frissítése
     */
    private void getTableListInfo(){
        String[] serverMessage= getServerMessage().split("--");
        if(!serverMessage[0].equals("SERVERLOBBY")){
            System.err.println("UNKNOWN SERVER MESSAGE");
            out.println("ERROR");
            getTableListInfo();
        }
        if(serverMessage[1].equals("TABLELIST")){
            for(int i = 2; i <= serverMessage.length - 2; i += 2){
                List<String> table = new ArrayList<>();
                table.add(serverMessage[i]);
                table.add(serverMessage[i + 1]);
                tableDatas.add(table);
            }
            out.println("OK");
        }else{
            out.println("ERROR");
            getTableListInfo();
        }
    }
    
    /**
     * ranglista adatok frissítése
     */
    private void getLeaderBoardInfo(){
        String[] serverMessage= getServerMessage().split("--");
        if(!serverMessage[0].equals("SERVERLOBBY")){
            System.err.println("UNKNOWN SERVER MESSAGE");
            out.println("ERROR");
            getLeaderBoardInfo();
        }
        if(serverMessage[1].equals("LEADERBOARD")){
            for(int i = 2; i <= serverMessage.length - 4; i += 4){
                List<String> leaderBoard = new ArrayList<>();
                leaderBoard.add(serverMessage[i]);
                leaderBoard.add(serverMessage[i+1]);
                leaderBoard.add(serverMessage[i+2]);
                leaderBoard.add(serverMessage[i+3]);
                leaderBoardDatas.add(leaderBoard);
                Collections.sort(leaderBoardDatas, (List<String> a, List<String> b) -> Integer.valueOf(b.get(2)).compareTo(Integer.valueOf(a.get(2))));
            }
            out.println("OK");
        }else{
            out.println("ERROR");
            getLeaderBoardInfo();
        }
    }
    
    /**
     *
     * @return szerverüzenet
     */
    public String getServerMessage(){
        String serverMessage = null;
        while(serverMessage == null){
            try {
                serverMessage = in.readLine();
            } catch (IOException ex) {
                System.exit(0);
            }
        }
//        System.out.println(serverMessage);
        return serverMessage;
    }
    
    /**
     * Csatlakozás játékasztalhoz
     * @param tableNum asztal sorszáma
     */
    public void joinTable(int tableNum){
        ClientController controller = new ClientController(this,gameSocket,in, out,tableNum,chatModel);
        controller.start();
    }
    
    /**
     * szerverüzenet küldése
     * @param message üzenet
     */
    public void sendToServer(String message){
        out.println(message);
    }

    public void setChatbox(ChatBox chatbox) {
        this.chatBox = chatbox;
        lobby.setChatBox(chatbox);
    }

    /**
     * játék elhagyása
     */
    public void leaveGame() {
        out.println("LEAVE");
        try {
            gameSocket.close();
            chatSocket.close();
        } catch (IOException ex) {
        }
        System.exit(0);
    }
}
