/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

import javax.swing.SwingUtilities;

/**
 *
 * @author darylcecile
 */
public class TaskController {
    
    public static void runAfter(ITask task,int delay_ms){
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    task.Run();
                }
            }, 
            delay_ms 
        );
    }
    
    public static void runLaterOnUiThread(Runnable action,int delay_ms){
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    // here if you have to refresh UI in code: 
                    runOnUiThread(action);
                }
            }, 
            delay_ms 
        );
    }
    
    public static void runOnUiThread(Runnable action){
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> {
                runOnUiThread(action);
            });
            return;
        }
        action.run();
    }
    
    public static void runAfter(Runnable action){
        java.awt.EventQueue.invokeLater(action);
    }
    
}
