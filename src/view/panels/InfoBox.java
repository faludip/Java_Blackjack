package view.panels;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import static view.TableView.GAME_FONT_TYPE;
import static view.TableView.YELLOW_FONT_COLOR;

/**
 * Információkat megjelenitő panel
 * @author Faludi Péter
 */
public class InfoBox extends VBox{
    private Label infoLabel;
    private Label logLabel;
    private ObservableList infoList;
    private Label timeLabel;

    public InfoBox() {
    }
    
    public void init(){
        infoList = this.getChildren();
        this.setMaxSize(150, 150);
        this.setMinSize(150, 150);
        logLabel = new Label("Information:");
        infoLabel = new Label();
        timeLabel = new Label();
        setLabelFont(infoLabel);
        setLabelFont(logLabel);
        setLabelFont(timeLabel);
        infoList.addAll(logLabel,infoLabel,timeLabel);
        this.setStyle("-fx-padding: 2;" +
                        "-fx-background-color: rgb(0, 91, 19);"+
                        "-fx-border-style: solid inside;" + 
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 5;" + 
                        "-fx-border-color: #E9981D;"  );
        infoLabel.setWrapText(true);
        InfoBox.setMargin(logLabel, new Insets(10, 0,30, this.getPrefWidth()/2 -logLabel.getWidth()/2));
    }
    
    public void setInfo(String info){
        infoLabel.setText(info);
        timeLabel.setText("");
    }
    
    public void setTime(String time){
        timeLabel.setText("\nYou have: " + time + " second!");
    }
    
    private void setLabelFont(Label label){
       label.setFont(GAME_FONT_TYPE);
       label.setTextFill(YELLOW_FONT_COLOR);
    }
}
