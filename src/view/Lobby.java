package view;

import view.panels.ChatPane;
import view.panels.MessageSendBox;
import view.panels.ChatBox;
import client.ChatModel;
import client.LobbyController;
import java.util.Collections;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Window;
import login.MessagePopUp;
import static view.TableView.GAME_FONT_TYPE;
import static view.TableView.SHADOW;
import static view.TableView.YELLOW_FONT_COLOR;

/**
 * A Lobby a játék előtti várakozó szoba,ahol láthatjuk a saját adatainkat
 * a ranglistát,chatelhetünk illetve csatlakozhatunk egy ablakhoz
 * Ez az osztály a grafikus megjelenítéséhez tartozó adatok tárolja egy 
 * BorderPane-t terjeszt ki
 * @author Faludi Péter
 */
public class Lobby extends BorderPane{
    
    private ScrollPane leaderBoardPane;
    private String money;
    private String username;
    private HBox holder;
    private HBox tablePickerHolder;
    private VBox informationBox;
    private ObservableList informationList;
    private ObservableList holderList;
    /**
     * A képernyő felbontása
     */
    protected  static final javafx.geometry.Rectangle2D SCREEN_RES = Screen.getPrimary().getVisualBounds();
    private ComboBox tablePickerBox;
    private Button pickerButton;
    private Label tablePickerLabel;
    private ObservableList tablePickerList;
    private ObservableList tablePickerHolderList;
    private ChatBox chatBox;
    private VBox leaderBoardBox;
    private String win;
    private String lose;
    private ObservableList leaderBoardList;
    private Button numButton,usernameButton,moneyButton,winButton,loseButton;
    private List<List<String>> leaderBoard;
    private HBox lineBoard;
    private ObservableList lineList;
    private ChatModel chatModel;
    private MessageSendBox sendBox;
    private final LobbyController controller;
    
    /**
     *
     * @param username
     * @param money
     * @param win
     * @param lose
     * @param chatModel
     * @param controller
     */
    public Lobby( String username, String money,String win,String lose,ChatModel chatModel,LobbyController controller) {
        this.username = username;
        this.money = money;
        this.win = win;
        this.lose = lose;
        this.chatModel = chatModel;
        this.controller = controller;
    }

    /**
     * Szükséges változók inicializálása
     */
    public void init() {
        this.setBackground(Background.EMPTY);
        String style = "-fx-background-color: rgb(0, 91, 19);";
        this.setStyle(style);
        holder = new HBox();
        holderList = holder.getChildren();
        informationBox = new VBox();
        informationList = informationBox.getChildren();
        Lobby.setMargin(informationBox, new Insets(30, 0, 0,30));
        sendBox = new MessageSendBox(chatModel);
        chatBox = new ChatBox(new ChatPane(new Label()), sendBox);
        tablePickerList = FXCollections.observableArrayList();
        tablePickerBox = new ComboBox(tablePickerList);
        tablePickerBox.setStyle("-fx-background-color: #E9981D;");
        tablePickerBox.setPromptText("Table");
        tablePickerHolder = new HBox();
        tablePickerHolderList = tablePickerHolder.getChildren();
        tablePickerLabel = new Label("Select a table : ");
        HBox.setMargin(tablePickerLabel, new Insets(5, 0, 10, 30)); 
        setText(tablePickerLabel);
        pickerButton = createButton("OK");
        tablePickerHolderList.addAll(tablePickerLabel,tablePickerBox,pickerButton);
        setInformation();
        Image table = null;
        table = new Image(Lobby.class.getClass().getResourceAsStream("/image/lobby1.png"));
        informationList.add(tablePickerHolder);
        chatBox.setMaxWidth(300);
        leaderBoardBox = new VBox();
        leaderBoardList = leaderBoardBox.getChildren();
        this.setLeft(chatBox);
        ImageView imageView = new ImageView(table);
        imageView.setFitWidth(SCREEN_RES.getWidth() / 3);
        imageView.setFitHeight(imageView.getFitWidth()*984/1280);
        holderList.addAll(informationBox,imageView);
        leaderBoardPane = new ScrollPane(leaderBoardBox);
        leaderBoardPane.setMaxSize(150, 200);
        setMargin(leaderBoardPane, new Insets(0, 20, 0, 0));
        leaderBoardPane.setMaxWidth(SCREEN_RES.getWidth() / 3);
        leaderBoardPane.setStyle("-fx-background: #E9981D;");
        lineBoard = new HBox();
        lineList = lineBoard.getChildren();
        numButton = new Button("#");
        usernameButton = new Button("Username");
        moneyButton = new Button("Money");
        winButton = new Button("Win");
        loseButton = new Button("Lost");
        numButton.setPrefWidth(30);
        numButton.setPadding(new Insets(3, 0, 3, 10));
        usernameButton.setPrefWidth(100);
        usernameButton.setPadding(new Insets(3, 0, 3, 10));
        moneyButton.setPrefWidth(70);
        moneyButton.setPadding(new Insets(3, 0, 3, 10));
        winButton.setPrefWidth(60);
        winButton.setPadding(new Insets(3, 0, 3, 10));
        loseButton.setPrefWidth(80);
        loseButton.setPadding(new Insets(3, 0, 3, 10));
        numButton.setStyle("-fx-background-color: #E9981D;");
        usernameButton.setStyle("-fx-background-color: #E9981D;");
        moneyButton.setStyle("-fx-background-color: #E9981D;");
        winButton.setStyle("-fx-background-color: #E9981D;");
        loseButton.setStyle("-fx-background-color: #E9981D;");
        setUpActionListener();
        Lobby.setMargin(informationBox, new Insets(0, 0, 0, 30));
        Lobby.setMargin(chatBox, new Insets(0, 0, 0, 30));
        Lobby.setMargin(leaderBoardBox, new Insets(0, 30, 30, 0));
        this.setRight(leaderBoardPane);
        this.setTop(holder);
    }
    
    /*
    * A gombok kinézetenék és megjelenített szöveg beállítása
    * és a gombok létrehozása
    */
    private Button createButton(String name){
        Button button = new Button(name);
        button.setStyle("-fx-background-color: #E9981D;");
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            button.setEffect(SHADOW);
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            button.setEffect(null);
        });
        HBox.setMargin(button, new Insets(0, 20, 0, 20));
        return button;
    }
    
    /*
    * InformationBox adatainak beállítása
    */
    private void setInformation(){
        informationList.clear();
        setInformationLabel("WELCOME");
        setInformationLabel("Name : " + username);
        setInformationLabel("Money : " + money);
        setInformationLabel("You won: " + win + " game(s)" );
        setInformationLabel("You lost: " + lose + " game(s)");
        informationBox.setPrefHeight(SCREEN_RES.getHeight() / 3);
        informationBox.setPrefWidth(SCREEN_RES.getWidth() / 3);
    }
    
    /*
    * Információk Label-é konvertálása
    */
    private void setInformationLabel(String str){
       Label label = new Label(str);
       setText(label);
       informationList.add(label);
       VBox.setMargin(label, new Insets(10, 0, 10, 30)); 
    }
    
    
    /*
    * Label-szöveg kinézetének beállítása
    */
    private void setText(Label label){
       label.setFont(GAME_FONT_TYPE);
       label.setTextFill(YELLOW_FONT_COLOR);
    }

    /**
     *
     * @return
     */
    public LobbyController getController() {
        return controller;
    }
    
    /**
     * Játék után a lobby adatainak frissítése
     * @param money játékos pénze
     * @param win játékos nyert játékainak száma
     * @param lose játékos vesztett játékainak száma
     */
    public void updateInfo(String money,String win,String lose){
        informationList.clear();
        this.setLeft(chatBox);
        this.money = money;
        this.win = win;
        this.lose = lose;
        setInformation();
        informationList.add(tablePickerHolder);
        pickerButton.setDisable(false);
        
    }
    
    
    /**
     * A tábla listát tartalmazó ComboBox adatainak beállítása
     * @param tables
     */
    public void setComboBox(List<List<String>>tables){
        for(List<String> table : tables){
            tablePickerList.add(table.get(0)+  ". min:"+table.get(1) + "$");
        }
    }
    
    /*
    * A gombokhoz tartozó ActionListener-ek inicializálása
    */
    private void setUpActionListener(){
        usernameButton.setOnAction((ActionEvent event) -> {
            Collections.sort(leaderBoard, (List<String> a, List<String> b) -> String.valueOf(a.get(0)).compareTo(String.valueOf(b.get(0))));
            setUpLeaderBoard(leaderBoard);
        });
        moneyButton.setOnAction((ActionEvent event) -> {
            Collections.sort(leaderBoard, (List<String> a, List<String> b) -> Double.valueOf(b.get(1)).compareTo(Double.valueOf(a.get(1))));
            setUpLeaderBoard(leaderBoard);
        });
        winButton.setOnAction((ActionEvent event) -> {
            Collections.sort(leaderBoard, (List<String> a, List<String> b) -> Integer.valueOf(b.get(2)).compareTo(Integer.valueOf(a.get(2))));
            setUpLeaderBoard(leaderBoard);
        });
        loseButton.setOnAction((ActionEvent event) -> {
            Collections.sort(leaderBoard, (List<String> a, List<String> b) -> Integer.valueOf(b.get(3)).compareTo(Integer.valueOf(a.get(3))));
            setUpLeaderBoard(leaderBoard);
        });
        
        pickerButton.setOnAction((ActionEvent event) -> {
            Window owner = pickerButton.getScene().getWindow();
            String tableNum =(String)tablePickerBox.getValue();
            if(tableNum == null){
                return;
            }
//            System.out.println(tableNum);
            String[] array = tableNum.split("\\.");
            String pickedTable = array[0];
//            System.out.println(pickedTable);
            controller.sendToServer("TABLEJOIN--" + pickedTable );
            switch(controller.getServerMessage()){
                case "WAITING":
                    MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "Waiting", 
                    "Waiting for new round just a couple second!");
                    pickerButton.setDisable(true);
                    waiting(owner,pickedTable);
                    break;
                case "NOMONEY":
                    MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "You don't have enough money", 
                    "You don't have enough money.Please choose another table or get some money!");
                    break;
                case "FULL":
                    MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "The table is full", 
                    "Please choose another table or try again later!");
                    break;
                case "EMPTY":
                    waiting(owner,pickedTable);
                    break;
            }
        });
    }
    
    /**
     * A ranglista adatainak beállítása
     * @param list
     */
    public void setUpLeaderBoard(List<List<String>> list){
        leaderBoardList.clear();
        lineList.clear();
        lineList.addAll(numButton,usernameButton,moneyButton,winButton,loseButton);
        setButtonBorder(numButton);
        setButtonBorder(usernameButton);
        setButtonBorder(moneyButton);
        setButtonBorder(winButton);
        setButtonBorder(numButton);
        setButtonBorder(loseButton);
        leaderBoardList.add(lineBoard);
        leaderBoard = list;
        Integer i = 1;
        for(List<String> line: list){
            Label numLabel,usernameLabel,moneyLabel,winLabel,loseLabel;
            HBox lineBoard1 = new HBox();
            ObservableList lineList1 = lineBoard1.getChildren();
            numLabel = new Label(i.toString());
            setLabelBorder(numLabel);
            usernameLabel = new Label(line.get(0));
            setLabelBorder(usernameLabel);
            moneyLabel= new Label(line.get(1));
            setLabelBorder(moneyLabel);
            winLabel= new Label(line.get(2));
            setLabelBorder(winLabel);
            loseLabel= new Label(line.get(3));
            setLabelBorder(loseLabel);
            numLabel.setPrefWidth(30);
            numLabel.setPadding(new Insets(3, 0, 3, 10));
            usernameLabel.setPrefWidth(100);
            usernameLabel.setPadding(new Insets(3, 10, 3, 10));
            moneyLabel.setPrefWidth(70);
            moneyLabel.setPadding(new Insets(3, 10, 3, 10));
            winLabel.setPrefWidth(60);
            winLabel.setPadding(new Insets(3, 10, 3, 10));
            loseLabel.setPrefWidth(80);
            loseLabel.setPadding(new Insets(3, 10, 3, 10));
            lineList1.addAll(numLabel,usernameLabel,moneyLabel,winLabel,loseLabel);
            leaderBoardList.add(lineBoard1);
            i++;
        }
    }

    /*
    * Label-ök szegélyének beállítása
    */
    private void setLabelBorder(Label label){
        label.setStyle("-fx-padding: 2;" +
                        "-fx-background-color: #E9981D;"+
                      "-fx-border-style: solid inside;" + 
                      "-fx-border-width: 1;" +
                      "-fx-border-color: black;"  );
    }
    
    /*
    * A gombok szegélyének beállítása
    */
    private void setButtonBorder(Button button){
        button.setStyle("-fx-padding: 2;" +
                        "-fx-background-color: #E9981D;"+
                      "-fx-border-style: solid inside;" + 
                      "-fx-border-width: 1;" +
                      "-fx-border-color: black;"  );
    
    }
  
    public void setChatPanel(ChatPane chatPane){
        chatBox.setChatPane(chatPane);
    }
  
    public ChatBox getChatBox() {
        return chatBox;
    }
 
    public void setChatBox(ChatBox chatBox) {
        this.chatBox = chatBox;
    }

    public void setChatModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    
    /*
    * Várakozás a játékhoz való csatlakozása
    */
    private void waiting(Window owner,String pickedTable){
        new Thread(() -> {
            String string = controller.getServerMessage();
            if(string.equals("OK")){
                        Platform.runLater(() -> {
                            owner.hide();
                            controller.joinTable(Integer.parseInt(pickedTable));
                        });
                    }
        }).start();
    }

    
}
