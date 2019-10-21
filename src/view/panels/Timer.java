/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.panels;

import client.ClientController;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Faludi Péter
 */
public class Timer implements Runnable{
    private int time;
    private String message;
    private ClientController controller;
    PanelBox panel;
    private volatile boolean running = true;
    
    public Timer(ClientController controller,String message) {
        time = 10;
        this.controller = controller;
        this.message = message;
    }
    
    public Timer(ClientController controller,String message,int second) {
        time = second;
        this.controller = controller;
        this.message = message;
    }
    
    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        for(int i = time; i > 0; i--){
            if(!running){
                return;
            }
//            panel.setCountDownLabel(i);
            try {
                Thread.sleep(1000);
                
            } catch (InterruptedException ex) {
            }
        }
        
    }
    
    
    
    
    
}
