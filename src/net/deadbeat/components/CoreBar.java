/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;
import net.deadbeat.main.Home;

/**
 *
 * @author darylcecile
 */
public class CoreBar extends JPanel {
    
    public int contentHeight;
    public int contentWidth;
    public int positionX;
    public int positionY;
    
    private CorePanel corepanel;
    private boolean willDrag = false;
    private Point offset;
    
    public CoreBar(CorePanel cpanel,Home application){
        this.setOpaque(false);
        this.corepanel = cpanel;
        
        CoreBar that = this;
        java.awt.EventQueue.invokeLater(() -> {
            that.setUpMouseEvent(that, application);
        });
        
    }
    
    private void setUpMouseEvent(CoreBar that,Home application){
        
        that.addMouseMotionListener(new MouseMotionListener(){
            @Override
            public void mouseDragged(MouseEvent e) {
                application.setLocation(e.getXOnScreen() - that.offset.x, e.getYOnScreen() - that.offset.y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        
        });

        that.addMouseListener(new MouseListener() {

            @Override
            public void mousePressed(MouseEvent e) {
                that.willDrag = true;
                that.offset = new Point(e.getX(),e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                that.willDrag = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
    }
    
    public void setDimension(int w, int h){
        this.contentHeight = h;
        this.contentWidth = w;
        
        this.setPreferredSize(new Dimension(w,h));
        this.setBounds(this.positionX,this.positionY,w,h);
        
        System.out.println("Updated dimension");
    }

    @Override
    public Dimension getPreferredSize() {
        // return super.getPreferredSize(); //To change body of generated methods, choose Tools | Templates.
        return new Dimension(this.contentWidth,this.contentHeight);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //To change body of generated methods, choose Tools | Templates.
        
        this.setBackground( this.corepanel.getBackground() );
    }
    
}
