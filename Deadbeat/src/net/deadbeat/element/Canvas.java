/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.element;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author darylcecile
 */
public class Canvas extends Component {
    
    private final int shadowWidth = 100;
    
    public Canvas(){
        
    }

    @Override
    public void whenReady() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void whenPainting(Graphics g) {
         // draw background
        GradientPaint gradient = new GradientPaint(
            new Point( 0 , Width ), new Color(15,12,34),
            new Point( 600 , Height - 700 ), new Color(114, 104, 176),
            false);
        
        Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(gradient);
        
        g.fillRect(0, 0, Width , Height);
        
        // manually draw shadow
        int alphaStart = 0;
        int alphaEnd = 200;
        int xStart = (( Width / 100 ) * 25) - (this.shadowWidth / 5);
        int xEnd = (( Width / 100 ) * 25) + ((this.shadowWidth / 5) * 4) ;
        int alphaStep = (alphaEnd - alphaStart)/100;
        int positionStep = (xEnd - xStart) / 100;
        
        for (int i = 0; i < 100; i++){
            g.setColor( new Color(0,0,0, alphaStart + (alphaStep * i) ) );
            g.fillRect( xStart + (positionStep * i) , 0, 1, this.getHeight());
        }
        
       
    }

    @Override
    public void whenStarted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
