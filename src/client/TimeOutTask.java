package client;

import java.util.TimerTask;

/**
 * TimerTask a paraméterben kapott parancsot elküldi a szervernek
 * és megszűnteti a timer szálat
 * @author Faludi Péter
 */
public class TimeOutTask extends TimerTask{
    private final ClientController controller;
    private final TimeOut timeOut;
    private final String command;
    
    /**
     * @param controller ClientController
     * @param timeOut Az idözítő folyamat
     * @param command Az idözítő lejárása esetén ezt a parancsot küldi el a szervernek
     */
    protected TimeOutTask(ClientController controller,TimeOut timeOut,String command){
        this.controller= controller;
        this.timeOut = timeOut;
        this.command = command;
    }
    
    @Override
    public void run(){
        controller.sendClientMessage(command);
        controller.clearControlPanel();
        timeOut.close();
    }
}
