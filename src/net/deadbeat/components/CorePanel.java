/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import javax.swing.JPanel;
import net.deadbeat.main.RelativeLayout;

/**
 *
 * @author darylcecile
 */
public class CorePanel extends JPanel{

    protected int contentHeight;
    protected int contentWidth;
    protected int positionX = 0;
    protected int positionY = 0;
    
    
    public void setDimension(int w, int h){
        this.contentHeight = h;
        this.contentWidth = w;
        
        this.setPreferredSize(new Dimension(w,h));
        this.setBounds(this.positionX,this.positionY,w,h);
        
        System.out.println("Updated dimension");
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        final GradientPaint gradient = new GradientPaint(
            new Point( 0 , this.contentHeight ), new Color(15,12,34),
            new Point( this.contentWidth , 0 ), new Color(114, 104, 176),
            false);
        
        final Graphics2D g2 = (Graphics2D)g;
        g2.setPaint(gradient);
        
        g.fillRect(0, 0, this.contentWidth , this.contentHeight);
       
    }

    @Override
    public Dimension getPreferredSize() {
        // return super.getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
        return new Dimension(this.contentWidth,this.contentHeight);
    }
    
    
    
    
}
