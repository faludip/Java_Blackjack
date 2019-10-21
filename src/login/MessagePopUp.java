package login;

import javafx.scene.control.Alert;
import javafx.stage.Window;

/**
 * A felugró ablakot előállító osztály
 * @author Faludi Péter
 */
public class MessagePopUp {
    public static void popMessage(Alert.AlertType type, Window owner, String title, String prompt) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(prompt);
        alert.initOwner(owner);
        alert.show();
    }
}