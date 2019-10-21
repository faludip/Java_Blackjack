package client;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A gui-n megjelenő visszámlálásért felelős folyamat
 * @author Faludi Péter
 */
public class GUITimer implements Runnable{
    private final AtomicBoolean running;
    private final ClientController controller;

    public GUITimer(ClientController controller) {
        running = new AtomicBoolean(true);
        this.controller = controller;
    }

    public void setRunning(boolean running) {
        this.running.set(running);
    }
    
    @Override
    public void run() {
        int second = 15;
        while(running.get() && second > 0 ){
            try {
                controller.setTimeLeft(second);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
            }
            second--;
        }
    }
    
}