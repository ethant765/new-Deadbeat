/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

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
    public Color activeColor = Color.gray;
    public States currentState = States.DEFAULT;
    
    @Override
    public void whenReady() {
        On(EName.MOUSE_DOWN, (results) -> {
            currentState = States.DOWN;
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
                g.setColor(this.activeColor);
                break;
            default:
                g.setColor(this.color);
        }
        
        g.fillOval(0, 0, getSize().width-1, getSize().height-1);
      
    }
    
    public void setColor(Color defaultColor, Color activeColor){
        this.color = defaultColor;
        this.activeColor = activeColor;
        reflow();
    }
}
