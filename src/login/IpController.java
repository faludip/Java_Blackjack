package login;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * FXML Controller class
 *
 * @author Faludi Péter
 */
public class IpController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    private Socket gameSocket = null;
    private Socket chatSocket = null;
    
    /**
     * host
     */
    @FXML
    protected TextField hostTextField;
    
    /**
     * Játék szerver port
     */
    @FXML
    protected TextField gamePortTextField;
    
    /**
     * Chat szerver Port
     */
    @FXML
    protected TextField chatPortTextField;
    
    /**
     * Csatlakozás gomb
     */
    @FXML
    protected Button connectButton;
    
    /**
     * Gomb megnyomására végrehajtott fv
     * hiba üzenet ha nem taálható a szerver
     * ha sikeres a csatlakozás tovább jutunk a bejelentkezési felületre
     * @param event gomb megnyomása
     * @throws IOException
     */
    @FXML
    protected void handleConnectButtonAction(ActionEvent event) throws IOException {
        Window owner = connectButton.getScene().getWindow();
        
        if(hostTextField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter the host name");
            return;
        }
        if(gamePortTextField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a game port");
            return;
        }
        
        if(gamePortTextField.getText().isEmpty()) {
            MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error!", 
                    "Please enter a chat port");
            return;
        }
        
        if(gameSocket == null){
            try {
                gameSocket = new Socket(hostTextField.getText(),Integer.parseInt(gamePortTextField.getText()));
                hostTextField.setEditable(false);
                gamePortTextField.setEditable(false);
            } catch (IOException e) {
                System.err.println("Not found running game-server on:" + hostTextField.getText() + ":" + Integer.parseInt(gamePortTextField.getText()) );
                MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error!", 
                        "Not found running game-server on:" + hostTextField.getText() + ":" + Integer.parseInt(gamePortTextField.getText()));
            return;
            }
        }
        if(chatSocket == null){
            try {
                chatSocket = new Socket(hostTextField.getText(),Integer.parseInt(chatPortTextField.getText()));
                hostTextField.setEditable(false);
                chatPortTextField.setEditable(false);
            } catch (IOException e) {
                System.err.println("Not found running game-server on:" + hostTextField.getText() + ":" + Integer.parseInt(chatPortTextField.getText()) );
                MessagePopUp.popMessage(Alert.AlertType.ERROR, owner, "Form Error!", 
                        "Not found running chat-server on:" + hostTextField.getText() + ":" + Integer.parseInt(chatPortTextField.getText()));
                return;
            }
        }
        Stage stage = (Stage) owner;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginFxml.fxml"));
        AnchorPane root = (AnchorPane)loader.load();
        LoginFxmlController controller = loader.getController();
        controller.setSockets(gameSocket,chatSocket);
        stage.setTitle("Login!");
        stage.setScene(new Scene(root, 600, 400));
        
        
        
    }
    
 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
