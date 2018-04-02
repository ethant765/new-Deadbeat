/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.components;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author darylcecile
 */
public class CoreOverlayPanel extends CorePanel {
 
    public void setPosition(int x, int y){
        this.positionX = x;
        this.positionY = y;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        final GradientPaint gradient = new GradientPaint(
            new Point( this.getWidth() , this.getHeight() ), new Color(15,12,34),
            new Point( 0 , 0 ), new Color(114, 104, 176),
            false);
        
        final Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(gradient);
        
        g.fillRect( 0 , 0, this.getWidth() , this.getHeight());
       
    }
    
}
