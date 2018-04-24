/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.element;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import net.deadbeat.core.Task;
import net.deadbeat.core.TaskController;
import net.deadbeat.schedule.EName;
import net.deadbeat.utility.Log;
import net.deadbeat.utility.Pair;

/**
 *
 * @author darylcecile
 */
public abstract class Component extends JComponent{
    
    private final List<Pair<EName,Task>> events;
    
    public int Width;
    public int Height;
    public int x;
    public int y;

    public Component(){
        events = new ArrayList<>();
    }
    
    public void On(String eventName,Task handler){
        events.add( new Pair(eventName,handler) );
    }
    
    public final void Create(){
        // All events have been added, process
        
        for (Pair<EName,Task> event : events){
            EName eventName = event.getKey();
            Task eventHandler = event.getValue();
            
            switch (eventName){
                
                case MOUSE_HOVER:
                    Log.Out("Hover event Added");
                    break;
                case MOUSE_DOWN:
                    Log.Out("MouseDown");
                    break;
                case MOUSE_UP:
                    Log.Out("MouseUp");
                    break;
                case MOUSE_CLICK:
                    Log.Out("MouseClick");
                    break;
                default:
                    Log.Out("DEFAULT E");
                
            }
            
        }
        
        TaskController.runOnUiThread(() -> {
            whenReady();
        });
        
    }
    
    public final void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public Dimension getPreferredSize() {
        // return super.getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
        return new Dimension(this.Width,this.Height);
    }
    
    public final void setDimension(int w, int h){
        this.Height = h;
        this.Width = w;
        
        this.setPreferredSize(new Dimension(w,h));
        this.setBounds(x,y,w,h);
    }
    
    public final void reflow(){
        // TODO finish reflow
        Log.Reminder("Finish off reflow");
        
        paintAll( getGraphics() );
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        whenPainting(g);
    }
    
    
    /**
     * Executed by Component when ready to work with UI
     * <p>
     * This Method executes on the UI thread
     */
    public abstract void whenReady();
    
    /**
     * Executed by Component when layout initialisation is complete
     * <p>
     * Use This method to set events
     */
    public abstract void whenStarted();
    
    public abstract void whenPainting(Graphics g);
}
