/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JFrame;

import net.deadbeat.elements.Image;
import net.deadbeat.elements.Button;
import net.deadbeat.elements.TitleBar;
import net.deadbeat.schedule.EName;
import net.deadbeat.utility.Log;

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
        win = container;
        return (new CustomProperty(container));
    }
    
    public static Window win;
    
    public static Button closeBtn;
    public static Button minBtn;
    public static Button maxBtn;
    
    public static final int CBAR_GAP = 5;
    public static final int CBAR_DIAMETER = 13;
    public static final int CBAR_HEIGHT = 23;
    
    public static void SetUp(){
        
        win.badge = new Image("logo.png");
        win.tbar = new TitleBar(win);
        
        win.overlay.add(win.searchbox);
        
        win.overlay.On(EName.MOUSE_CLICK, (results) -> {
            Log.Out("los");
            win.searchbox.loseFocus();
        });
        
        win.searchbox.setPosition(0, 0);
        
            closeBtn = new Button();
            minBtn = new Button();
            maxBtn = new Button();
            
            closeBtn.On(EName.MOUSE_UP, (results) -> {
                System.exit(0);
            });
            
            minBtn.On(EName.MOUSE_UP, (results) -> {
                win.setState(JFrame.ICONIFIED);
            });
            
            maxBtn.On(EName.MOUSE_UP, (results) -> {
                if ( win.getExtendedState() == JFrame.MAXIMIZED_BOTH ){
                    win.setExtendedState(JFrame.NORMAL);
                }
                else{
                    win.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
                win.paintAll(win.getGraphics());
                Update();
            });
            
        
    }
    
    public static void Update(){
        final int sidebarWidth = (win.getWidth()/100) * 25;
        
        win.conversation.setBound(50, 50,300, 300);
        
        win.setShape(new RoundRectangle2D.Double(0, 0, win.getWidth(), win.getHeight(), 6, 6));
        
        win.canvas.setDimension(win.getWidth(), win.getHeight());
        win.canvas.setBounds(0, 0, win.getWidth(), win.getHeight());
        win.overlay.setBounds(sidebarWidth, 0, win.getWidth() - sidebarWidth , win.getHeight());
        
        int logoHeight = (int) ( (sidebarWidth - 32) / 1.85 );
        win.badge.setBounds(14, 20, sidebarWidth - 32 , logoHeight );
        win.badge.repaintIn( win.canvas );
        
        win.tbar.setBounds(0,0,win.getWidth(),CBAR_HEIGHT);
                
        win.tbar.add(closeBtn);
        win.tbar.add(minBtn);
        win.tbar.add(maxBtn);
        
        closeBtn.setColor(new Color(244, 67, 54));
        minBtn.setColor(new Color(255, 193, 7));
        maxBtn.setColor(new Color(76, 175, 80));

        closeBtn.setBounds(
                CBAR_GAP,
                CBAR_GAP,
                CBAR_DIAMETER,
                CBAR_DIAMETER
        );
        minBtn.setBounds(
                (CBAR_GAP * 2) + CBAR_DIAMETER,
                CBAR_GAP,
                CBAR_DIAMETER,
                CBAR_DIAMETER
        );
        maxBtn.setBounds(
                (CBAR_GAP * 3) + (CBAR_DIAMETER * 2),
                CBAR_GAP,
                CBAR_DIAMETER,
                CBAR_DIAMETER
        );
        
        // Searchbox
        win.searchbox.setAlignmentX(0f);
        win.searchbox.setAlignmentY(0f);
        
        win.searchbox.setText("");
        win.searchbox.setPreferredSize(new Dimension(150 ,24));
        win.searchbox.setBounds( ( win.getWidth() - sidebarWidth ) - ( 162 ) ,11,150, 24);
        win.searchbox.setFocusable(true);
        win.searchbox.setPlaceholder("Search");
    }
    
}
