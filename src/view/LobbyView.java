package view;

import java.io.FileNotFoundException;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import static view.TableView.SCREEN_RES;

/**
 * A Lobby-t megjelenitő osztály
 * @author Faludi Péter
 */
public class LobbyView extends Application {

    private final Scene scene;
    private final Lobby lobby;

    /**
     *
     * @param lobby
     */
    public LobbyView(Lobby lobby) {
        this.lobby = lobby;
        scene = new Scene(lobby);
    }
    @Override
    public void start(Stage primaryStage) throws FileNotFoundException {
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(scene);
        primaryStage.setX(SCREEN_RES.getMinX());
        primaryStage.setY(SCREEN_RES.getMinY());
        primaryStage.setWidth(SCREEN_RES.getWidth());
        primaryStage.setHeight(SCREEN_RES.getHeight());
        primaryStage.getIcons().add(new Image(LobbyView.class.getClass().getResourceAsStream("/image/lobby1.png")));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent we) {
                lobby.getController().leaveGame();
            }
        });    
    }
    
}
