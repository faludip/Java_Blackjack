package login;

import client.LobbyController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author Faludi Péter
 */
public class LoginFxmlController implements Initializable {
    private Socket gameSocket = null;
    private Socket chatSocket = null;
    private PrintWriter out;
    private PrintWriter chatOut;
    private BufferedReader in;
    private LobbyController lobbyController; 
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private Button signButton;
    @FXML
    private Button registerButton;
    
    /**
     * Bejelentkezés gomb lenyomására végrehajtott fv
     * Ez a függvény ellenőrzi,hogy ki van-e töltve minden mező
     * Ha a szerver nem fogadja el az adatokat,akkor hibaüzenet ír ki
     * Ha rendben vannak az adatok a Lobbyba irányít
     * @param event gomb megnyomása
     */
    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        Window owner = signButton.getScene().getWindow();
        
        if(usernameField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your name");
            return;
        }
        if(loginPasswordField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "The password is incorrect");
            return;
        }
        out.println("LOGIN--"+usernameField.getText()+"--"+loginPasswordField.getText());
//        System.out.println(usernameField.getText() + "___" + loginPasswordField.getText());
        String[] serverMessage = getServerMessage().split("--");
        switch(serverMessage[0]){
            case "OK":
                chatOut.println(usernameField.getText());
                lobbyController = new LobbyController(gameSocket, chatSocket, in, out);
                owner.hide();
                lobbyController.init();
                break;
            case "NOTEXIST":
                MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "Cant Find The Account", 
                    "You'r username does not exist.");
                    return;
            case "WRONGPASS":
                MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "Wrong Password", 
                    "You entered a wrong password!");
                return;
            case "ONLINE":
                MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "Can't Connect", 
                    "Somebody is playing with this account!");
                return;
            case "FATAL":
                MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Can't Connect", 
                    "Server is closed!");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(LoginFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
                return;
            default :
                MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Server error!", 
                    "Something went wrong.Please try again later!!");
                return;
        }
        
    }
    
    /**
     * Regisztációs gomb megnyomására lefutattot fv
     * Regisztációs felületre irányít
     * @param event gomb megnyomása
     * @throws IOException
     */
    @FXML
    protected void handleRegisterButtonAction(ActionEvent event) throws IOException {
        Window owner = registerButton.getScene().getWindow();
        Stage stage = (Stage) owner;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("RegisterFxml.fxml"));
        AnchorPane root = (AnchorPane)loader.load();
        RegisterFxmlController controller = loader.getController();
        controller.setSockets(gameSocket,chatSocket);
        stage.setTitle("Register!");
        stage.setScene(new Scene(root, 600, 400));
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                leaveGame();
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
    
    /**
     *
     * @param gameSocket
     * @param chatSocket
     */
    public void setSockets(Socket gameSocket,Socket chatSocket){
        this.gameSocket = gameSocket;
        this.chatSocket = chatSocket;
        try {
            out = new PrintWriter(gameSocket.getOutputStream(),true);
            chatOut = new PrintWriter(chatSocket.getOutputStream(),true);
        } catch (IOException ex) {
            Logger.getLogger(LoginFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(gameSocket.getInputStream());
            in = new BufferedReader(inputStreamReader);
        } catch (IOException ex) {
        }
    }
    
     /**
     *  Szerverüzenet fogadása
     */
    private String getServerMessage(){
        String message = null;
        
        while(message == null){
            
            try {
                message = in.readLine();
            } catch (IOException ex) {
                return "FATAL";
            }
        }
//        System.err.println(message);
        return message;
    }
    
    /*
     * Program elhagyásakor lefutó metódus
     */
    public void leaveGame() {
        out.println("LEAVE");
        try {
            gameSocket.close();
            chatSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(LobbyController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
    }
}


