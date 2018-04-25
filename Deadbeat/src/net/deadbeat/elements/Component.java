/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import net.deadbeat.utility.Log;
import net.deadbeat.utility.Pair;

import net.deadbeat.core.ITask;
import net.deadbeat.core.TaskController;
import net.deadbeat.schedule.EName;

// for compiling purposes
/**
 *
 * @author darylcecile
 */
public abstract class Component extends JComponent{
    
    private final List<Pair<EName,List<ITask>>> events;
    
    public int Width;
    public int Height;
    public int x;
    public int y;
    
    public boolean focused = false;

    public Component(){
        events = new ArrayList<>();
        TaskController.runAfter(() -> {
            setLayout( null );
        });
        Create();
    }
    
    public void On(EName eventName,ITask handler){
        eventHandlerList(eventName).add(handler);
    }
    
    private List<ITask> eventHandlerList(EName eventName){
        for (Pair<EName,List<ITask>> p : events){
            if ( p.key.equals(eventName) ) return p.getValue();
        }
        List<ITask> tl = new ArrayList<>();
        events.add( new Pair<>(eventName,tl) );
        return tl;
    }
    
    private void Trigger(EName eventName,Object... param){
        List<ITask> tasks = eventHandlerList(eventName);
        for (ITask task : tasks){
            task.Run(param);
        }
    }
    
    public final void gainFocus(){
        this.focused = true;
        requestFocusInWindow(true);
        this.repaint();
    }
    
    public final void loseFocus(){
        this.focused = false;
        this.getParent().requestFocusInWindow();
        this.repaint();
    }
    
    private final void Create(){
        // All events have been added, process
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                Trigger(EName.KEY_DOWN,e);
                Trigger(EName.KEY_PRESSED,e);
            }
            
            @Override
            public void keyTyped(KeyEvent e) {
                Trigger(EName.KEY_TYPED,e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                Trigger(EName.KEY_UP,e);
            }
            
        });
        
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Trigger(EName.MOUSE_DRAG, e);
            }
            
        });
        
        addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                Trigger(EName.MOUSE_DOWN,e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                Trigger(EName.MOUSE_CLICK,e);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Trigger(EName.MOUSE_LEAVE,e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Trigger(EName.MOUSE_HOVER,e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Trigger(EName.MOUSE_UP,e);
            }
            
            
        });
        
        TaskController.runOnUiThread(() -> {
            Log.Out("Calling Ready");
            whenReady();
        });
        
    }
    
    public void setBound(int x, int y, int w, int h){
        setPreferredSize( new Dimension(w,h) );
        setPosition(x,y);
        setDimension(w,h);
        reflow();
    }
    
    public void setPosition(int x, int y){
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
