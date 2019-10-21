package login;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author Faludi Péter
 */
public class Login extends Application {
    
    /**
     * A grafikus felületet inditó metódus
     */
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Ip.fxml"));
        Scene sceneLogin = new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Login!");
        primaryStage.getIcons().add(new Image(Login.class.getClass().getResourceAsStream("/image/lobby1.png")));
        primaryStage.setScene(sceneLogin);
        primaryStage.show();
    }
    
    /**
     * @param args the command line arguments
     * A kliens programot inditó függvény
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
