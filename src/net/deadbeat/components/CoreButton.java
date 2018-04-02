/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JButton;


/**
 *
 * @author darylcecile
 */
public class CoreButton extends JButton{
    
    public Color color = Color.white;
    public Color activeColor = Color.gray;
    
    public CoreButton(){
        super();
     
        CoreButton that = this;
        java.awt.EventQueue.invokeLater(() -> {
            CoreButton.setUp(that);
        });
    }
    
    public CoreButton(ImageIcon ii){
        super(ii);
     
        CoreButton that = this;
        java.awt.EventQueue.invokeLater(() -> {
            CoreButton.setUp(that);
        });
    }
    
    public static void setUp(CoreButton that){
        that.setBorderPainted(false);
        that.setFocusPainted(false);
        that.setBorderPainted(false);
        that.setFocusPainted(false);
        that.setContentAreaFilled(false);
        //set background
        that.setBackground(Color.orange);
    }
    
    public void setColor(Color defaultColor, Color activeColor){
        this.color = defaultColor;
        this.activeColor = activeColor;
    }
    
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
               RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION,RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        
        g2.setRenderingHints(rh);
      
        if (getModel().isArmed()) {
           g.setColor(this.activeColor);
        } else {
           g.setColor(this.color);
        }
  
        g.fillOval(0, 0, getSize().width-1, getSize().height-1);
      
        super.paintComponent(g2);
   }
    
}
