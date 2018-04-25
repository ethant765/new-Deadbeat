/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.beta;

import java.awt.Dimension;
import java.awt.LayoutManager;
import javax.swing.JFrame;

/**
 *
 * @author darylcecile
 */
public class LayoutAdapter {
    
    public static class CustomProperty{
        
        private final Window container;
        public CustomProperty(Window container){ this.container = container; }
        
        public CustomProperty customPaint(Boolean b){
            this.container.setUndecorated(b);
            JFrame.setDefaultLookAndFeelDecorated(!b);
            return this;
        }
        
        public CustomProperty size(int width,int height){
            this.container.setSize(width,height);
            this.container.setMinimumSize(new Dimension(width,height));
            this.container.canvas.setDimension(width, height);
            
            return bound(0,0,width,height);
        }
        
        public CustomProperty bound(int x,int y,int w, int h){
            final int sidebarWidth = (w/100) * 25;
            this.container.overlay.setBounds(sidebarWidth, 0, w - sidebarWidth , h);
            this.container.canvas.setBounds(x, y, w, h);
            return this;
        }
        
        public CustomProperty layout(LayoutManager lm){
            this.container.setLayout(lm);
            return this;
        }
        
    }
    
    /**
     * Set the property value of the Layout.
     * <p>
     * This method is chain-able
     * 
     * @param container JFrame
     * @return
     */
    public static CustomProperty setProperty(Window container){
        return (new CustomProperty(container));
    }
    
}
