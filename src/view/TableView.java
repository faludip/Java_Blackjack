/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 *
 * @author Faludi Péter
 */
public class TableView extends Application {
    
    /**
     *
     */
    public static final Color YELLOW_FONT_COLOR = new Color(0.91, 0.60, 0.11,1);

    /**
     *
     */
    public static final Font GAME_FONT_TYPE = Font.font("Times New Roman",FontWeight.BOLD,14);

    /**
     *
     */
    public static final String CSS_LAYOUT = "-fx-padding: 10;" + 
                      "-fx-border-style: solid inside;" + 
                      "-fx-border-width: 2;" +
                      "-fx-border-insets: 5;" + 
                      "-fx-border-radius: 5;" + 
                      "-fx-border-color: black;" +
                      "-fx-background-color: rgb(0, 91, 19)"  ;

    /**
     *
     */
    public  static final javafx.geometry.Rectangle2D SCREEN_RES = Screen.getPrimary().getVisualBounds();

    /**
     *
     */
    public static final DropShadow SHADOW = new DropShadow();
    Scene scene;
    
    private Table table;

    /**
     *
     * @param table
     */
    public TableView(Table table) {
        this.table = table;
        scene = new Scene(table);
    }
    
    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Blackjack");
        primaryStage.setScene(scene);
        primaryStage.setX(SCREEN_RES.getMinX());
        primaryStage.setY(SCREEN_RES.getMinY());
        primaryStage.setWidth(SCREEN_RES.getWidth());
        primaryStage.setHeight(SCREEN_RES.getHeight());
        primaryStage.getIcons().add(new Image(TableView.class.getClass().getResourceAsStream("/image/lobby1.png")));
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                try {
                    table.getController().leaveGame();
                } catch (IOException ex) {
                    Logger.getLogger(TableView.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });    
        
    }
    
}
