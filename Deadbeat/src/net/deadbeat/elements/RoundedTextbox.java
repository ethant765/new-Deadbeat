/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 *
 * @author darylcecile
 */
public class RoundedTextbox extends Textbox{
    
    public RoundedTextbox(){
        super();
    }

    @Override
    public void whenReady() {
        super.whenReady();
    }
    
    @Override
    public void whenPainting(Graphics g) {
        Graphics2D graphics = (Graphics2D)g.create();
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.setPaintMode();
        
        if (focused){
            graphics.setColor(Color.white);
        }
        else{
            graphics.setColor( new Color(255,255,255,50) );
        }
        graphics.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight() , getHeight());
        
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        
        if ( this.value.length() == 0 ){
            graphics.setColor(new Color(255,255,255, 100));
            graphics.drawString(this.placeholder, 8, getHeight() - 7);
        }
        else{
            graphics.setColor(Color.black);
            graphics.drawString(this.value, 8, getHeight() - 7);
        }
        
        if (focused){
            FontMetrics fm = graphics.getFontMetrics();
            int lenToCaret = 8;
            for (int i = 0; i < this.pointerPosition; i++){
                lenToCaret += fm.charWidth( this.value.charAt(i) );
            }
            
            graphics.setColor(new Color(114, 104, 176));
            graphics.drawLine(lenToCaret, 3, lenToCaret, getHeight() - 3);
        }
        graphics.dispose();
    }
    
    
}
