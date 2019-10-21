package client;

import java.util.Timer;

/**
 *  idözítő 15 másodperc után meghívja a TimeOut Tasket
 * @author Faludi Péter
 */
public class TimeOut {
    /**
     * Timer
     */
    private Timer timer;

    /**
     *
     * @param controller
     * @param command Az idözítő lejárása esetén ezt a parancsot küldi el a szervernek
     */
    public TimeOut(ClientController controller,String command) {
        timer = new Timer();
        timer.schedule(new TimeOutTask(controller,this,command), 15 * 1000);
    }
    
    /**
     * Timer megszüntetése
     */
    public void close(){
        timer.cancel();
    }
}
