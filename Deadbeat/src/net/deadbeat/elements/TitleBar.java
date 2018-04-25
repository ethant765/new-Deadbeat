/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import net.deadbeat.beta.Window;
import net.deadbeat.core.TaskController;
import net.deadbeat.schedule.EName;

/**
 *
 * @author darylcecile
 */
public class TitleBar extends Component{

    private final Window window;
    private Point offset;
    
    public TitleBar(Window win){
        
        TaskController.runAfter(() -> {
            this.setOpaque(false);
        });
        window = win;
    }
    
    @Override
    public void whenReady() {
        TitleBar that = this;
        On(EName.MOUSE_DOWN,(results) -> {
            MouseEvent e = (MouseEvent)results[0];
            that.offset = new Point(e.getX(),e.getY());
        });
        
        On(EName.MOUSE_DRAG, (results) -> {
            MouseEvent e = (MouseEvent)results[0];
            window.setLocation(e.getXOnScreen() - that.offset.x, e.getYOnScreen() - that.offset.y);
        });
    }

    @Override
    public void whenStarted() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void whenPainting(Graphics g) {
        this.setBackground( window.canvas.getBackground() );
    }
    
}
