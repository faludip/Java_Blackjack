package view.panels;

import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Box;
import view.TableView;

/**
 * Üres irányitó panel
 * @author Faludi Péter
 */
public class ControllerEmpty extends AnchorPane{

    public ControllerEmpty() {
        this.setMaxSize(TableView.SCREEN_RES.getWidth()/4, TableView.SCREEN_RES.getHeight()/4);
        this.setPrefSize(TableView.SCREEN_RES.getWidth()/5, TableView.SCREEN_RES.getHeight()/6);
        this.setStyle(TableView.CSS_LAYOUT);
    }
}
