/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import net.deadbeat.schedule.EName;

/**
 *
 * @author darylcecile
 */
public class Button extends Component {
    
    public static enum States{
        DOWN,
        UP,
        HOVER,
        DEFAULT;
    }

    public Color color = Color.white;
    public States currentState = States.DEFAULT;
    //public Color borderColor = new Color( Color.BLACK. );
    
    @Override
    public void whenReady() {
        On(EName.MOUSE_DOWN, (results) -> {
            currentState = States.DOWN;
            reflow();
        });
        
        On(EName.MOUSE_HOVER,(results) -> {
            currentState = States.HOVER;
            reflow();
        });
        
        On(EName.MOUSE_UP,(results)->{
            currentState = States.DEFAULT;
            reflow();
        });
        
        On(EName.MOUSE_LEAVE,(results)->{
            currentState = States.DEFAULT;
            reflow();
        });
    }

    @Override
    public void whenStarted() {}

    @Override
    public void whenPainting(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
               RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        g2.setRenderingHints(rh);
      
        switch (currentState){
            case DOWN:
                g.setColor(this.color.darker());
                break;
            default:
                g.setColor(this.color);
        }
        
        g2.setStroke(new BasicStroke(1));
        g2.fillOval(0, 0, getSize().width, getSize().height);
        
        if ( currentState == States.HOVER ){
            g2.setColor(this.color.brighter());
            g2.fillOval(4, 4, getSize().width - 8, getSize().height - 8);
//            g2.drawOval(1, 1, getSize().width - 3, getSize().height - 3);
        }
      
    }
    
    public void setColor(Color defaultColor){
        this.color = defaultColor;
        reflow();
    }
}
