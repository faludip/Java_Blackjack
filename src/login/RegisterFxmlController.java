package login;

import client.LobbyController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 * A regisztrációs ablakot irányító osztály
 * @author Faludi Péter
 */
public class RegisterFxmlController implements Initializable {
    
    LocalDate value;
    private Socket gameSocket = null;
    private Socket chatSocket = null;
    private PrintWriter out;
    private BufferedReader in;
    @FXML
    private TextField nameTextfield;
    @FXML
    private TextField usernameTextfield;
    @FXML
    private TextField emailAgainTextfield;;
    @FXML
    private TextField emailTextfield;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField passwordAgainField;
    @FXML
    private Button signUpButton,backButton;
    @FXML
    private DatePicker birthdayField;

    /**
     * Regisztációs gomb megnyomására lefutattot fv
     * Ez a függvény ellenőrzi,hogy ki van-e töltve minden mező
     * Nem engedélyez az adatbázis mezőinek hosszától nagyobb bemeneteket
     * Ha a szerver nem fogadja el az adatokat,akkor hibaüzenet ír ki
     * Ha rendben vannak az adatok visszairányít a bejelentkezési felületre
     * @param event gomb megnyomása
     */
    @FXML
    protected void handleSignUpButtonAction(ActionEvent event) {
        Window owner = signUpButton.getScene().getWindow();
        if(nameTextfield.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your name");
            return;
        }
        if(usernameTextfield.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your username");
            return;
        }
        if(passwordField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter a password");
            return;
        }
        if(emailTextfield.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your e-mail");
            return;
        }
        if(passwordAgainField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your password again");
            return;
        }
        if(emailAgainTextfield.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your e-mail again");
            return;
        }
        if(birthdayField.toString().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please enter your bithday");
            return;
        }
        if(!passwordField.getText().equals(passwordAgainField.getText())){
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "The passwords are not same!");
            return;
        }
        if(!emailTextfield.getText().equals(emailAgainTextfield.getText())){
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "The e-mails are not same!");
            return;
        }
        if( birthdayField.getValue() == null){
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Please give your birthday!");
            return;
        }
        if(nameTextfield.getText().length() > 250) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Your name is too long. Max length is 250 character.");
            return;
        }
        if(usernameTextfield.getText().length() > 100) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Your username is too long. Max length is 100 character.");
            return;
        }
        if(emailTextfield.getText().length() > 250) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Your e-mail is too long. Max length is 250 character.");
            return;
        }
        if(passwordField.getText().length() > 25) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error", 
                    "Your password is too long. Max length is 25 character.");
            return;
        }
        
        LinkedList<String> data = new LinkedList<>();
        data.add(usernameTextfield.getText());
        data.add(passwordField.getText() );
        data.add(emailTextfield.getText());
        data.add(nameTextfield.getText());
        data.add(birthdayField.getValue().toString());
        StringBuilder builder = new StringBuilder("REGISTER");
        for(String string : data){
            builder.append("--").append(string);
        }
        try {
            out = new PrintWriter(gameSocket.getOutputStream(),true);
        } catch (IOException ex) {
            System.err.println("Socket error");
        }
        out.println(builder.toString());
        String[] serverMessage = getServerMessage().split("--");
        switch(serverMessage[0]){
            case "OK":
                switch(serverMessage[1]){
                    case "OK":
                        MessagePopUp.popMessage(Alert.AlertType.INFORMATION, owner, "Succes", 
                            "Please log in!");
                        Stage stage = (Stage) owner;
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginFxml.fxml"));
                        AnchorPane root = null;
                        try {
                            root = (AnchorPane)loader.load();
                        } catch (IOException ex) {
                            Logger.getLogger(RegisterFxmlController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        LoginFxmlController controller = loader.getController();
                        controller.setSockets(gameSocket,chatSocket);
                        stage.setTitle("Login!");
                        stage.setScene(new Scene(root, 600, 400));
                        break;
                    case "EMAIL":
                        MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Wrong Email", 
                            "Your e-mail address must be unique!");
                }
                break;
            case "USERNAME":
                MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Wrong Username", 
                    "Your username must be unique!");
                return;
            default :
                MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Server error", 
                    "Something went wrong.Please try again later!!");
        }
    }
    
    /**
     * Előző oldalra írányító eljárás
     * @param event
     * @throws IOException
     */
    @FXML
    protected void handleBackButtonAction(ActionEvent event) throws IOException{
        Window owner = backButton.getScene().getWindow();
        Stage stage = (Stage) owner;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginFxml.fxml"));
        AnchorPane root = (AnchorPane)loader.load();
        LoginFxmlController controller = loader.getController();
        controller.setSockets(gameSocket,chatSocket);
        stage.setTitle("Login!");
        stage.setScene(new Scene(root, 600, 400));
        stage.setOnCloseRequest((WindowEvent we) -> {
            leaveGame();
        });
        
    }
    
    
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
        } catch (IOException ex) {
            Logger.getLogger(LoginFxmlController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(gameSocket.getInputStream());
            in = new BufferedReader(inputStreamReader);
        } catch (IOException ex) {
            Logger.getLogger(RegisterFxmlController.class.getName()).log(Level.SEVERE, null, ex);
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
                //System.out.println(message);
            } catch (IOException ex) {
                Logger.getLogger(RegisterFxmlController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return message;
    }
    
    /**
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
