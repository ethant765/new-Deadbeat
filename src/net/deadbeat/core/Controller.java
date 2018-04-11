/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import net.deadbeat.ui.CoreBar;
import net.deadbeat.ui.CoreButton;
import net.deadbeat.ui.CoreImage;
import net.deadbeat.ui.CoreOverlayPanel;
import net.deadbeat.ui.CorePanel;
import net.deadbeat.ui.CoreRoundedTextbox;

/**
 *
 * @author darylcecile
 */
public class Controller {
    
    public static CoreImage logo;
    public static CoreRoundedTextbox searchbox;
    public static Home dash;
    public static CorePanel corepanel;
    public static CoreOverlayPanel coreoverlaypanel;
    public static CoreButton closeBtn;
    public static CoreButton minBtn;
    public static CoreButton maxBtn;
    public static CoreBar cbar;
    
    public static final int CBAR_GAP = 5;
    public static final int CBAR_DIAMETER = 13;
    public static final int CBAR_HEIGHT = 23;
    
    public static void prepareElements(CorePanel cpanel,CoreOverlayPanel copanel,Home dashboard,CoreBar titleBar){
        
        logo = new CoreImage("logo.png");
        dash = dashboard;
        searchbox = new CoreRoundedTextbox();
        corepanel = cpanel;
        searchbox.setParent( copanel );
        coreoverlaypanel = copanel;
        searchbox.setLocation(0, 0);
        cbar = titleBar;
        
        java.awt.EventQueue.invokeLater(() -> {
            closeBtn = new CoreButton();
            minBtn = new CoreButton();
            maxBtn = new CoreButton();
            
            closeBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.exit(0);
                }
            });
            
            minBtn.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    dash.setState(JFrame.ICONIFIED);
                }
            });
            
            maxBtn.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    
                    if ( dash.getExtendedState() == JFrame.MAXIMIZED_BOTH ){
                        dash.setExtendedState(JFrame.NORMAL);
                    }
                    else{
                        dash.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                    dash.paintAll(dash.getGraphics());
                    reflow();
                }
            });
            
            System.out.println("CREATE ELEMENT");
        });
        
        
    }
    
    public static void reflow(){
        final int sidebarWidth = (dash.getWidth()/100) * 25;
        
        dash.setShape(new RoundRectangle2D.Double(0, 0, dash.getWidth(), dash.getHeight(), 6, 6));
        
        corepanel.setDimension(dash.getWidth(), dash.getHeight());
        corepanel.setBounds(0, 0, dash.getWidth(), dash.getHeight());
        coreoverlaypanel.setBounds(sidebarWidth, 0, dash.getWidth() - sidebarWidth , dash.getHeight());
        
        int logoHeight = (int) ( (sidebarWidth - 32) / 1.85 );
        logo.setBounds(14, 20, sidebarWidth - 32 , logoHeight );
        logo.showIn(corepanel);
        
        cbar.setBounds(0,0,dash.getWidth(),CBAR_HEIGHT);
                
        cbar.add(closeBtn);
        cbar.add(minBtn);
        cbar.add(maxBtn);
        
        closeBtn.setColor(new Color(244, 67, 54), new Color(198, 40, 40));
        minBtn.setColor(new Color(255, 193, 7), new Color(255, 143, 0));
        maxBtn.setColor(new Color(76, 175, 80), new Color(46, 125, 50));

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

        
        System.out.println("REFLOW");
        
        // Searchbox
        searchbox.setAlignmentX(0f);
        searchbox.setAlignmentY(0f);
        
        searchbox.setText("");
        searchbox.setPreferredSize(new Dimension(150 ,24));
        searchbox.setBounds( ( dash.getWidth() - sidebarWidth ) - ( 162 ) ,11,150, 24);
        searchbox.setFocusable(true);
        searchbox.setPlaceholder("Search");
    }
    
}
