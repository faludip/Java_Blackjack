package client;

import java.io.InputStream;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author Faludi Péter
 * A kártyák elkészítéséért felelős osztály 
 * Url alapján állít elő képet a kártya számára
 */
public class CardFactory extends Label{
    private final String url;

    public CardFactory(String url) {
        this.url = url;
        init();
    }
    /*
    * Input stream nyitása illetve a kártya képének beállítása a label
    * grafikájának
    */
    private void init() {
        InputStream input;
//        System.out.println("image/"+ url.toLowerCase() +".png");
        input = CardFactory.class.getClass().getResourceAsStream("/image/"+ url.toLowerCase() +".png");
        Image image = new Image(input);
        this.setGraphic(new ImageView(image));
    }
    
}
