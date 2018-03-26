/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.main;

import net.deadbeat.components.CoreImage;
import net.deadbeat.components.CoreOverlayPanel;
import net.deadbeat.components.CorePanel;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import net.deadbeat.components.CoreRoundedTextbox;

/**
 *
 * @author darylcecile
 */
public class RelativeLayout implements LayoutManager {
    
    CorePanel cpanel;
    CoreOverlayPanel copanel;
    Home dashboard;
    
    CoreImage logo;
    CoreRoundedTextbox searchbox;
    
    public RelativeLayout(CorePanel cpanel,CoreOverlayPanel copanel,Home dashboard){
        this.cpanel = cpanel;
        this.copanel = copanel;
        this.dashboard = dashboard;
        
        this.logo = new CoreImage("logo.png");
        
        this.searchbox = new CoreRoundedTextbox();
        this.searchbox.setParent(this.copanel);
        
    }
    
    public void performLayout(){
        int sidebarWidth = ( dashboard.getWidth() / 100 ) * 20;
        if (sidebarWidth == 0) sidebarWidth = 185;
        int logoHeight = (int) ( sidebarWidth / 1.85 );
        this.logo.setBounds(31, 40, sidebarWidth , logoHeight );
        this.logo.showIn(cpanel);
        
        // Searchbox
        this.searchbox.setText("");
        this.searchbox.setBounds(200,200,150, 24);
        this.searchbox.setPreferredSize(new Dimension(150 ,24));
        this.searchbox.setFocusable(true);
        this.searchbox.setPlaceholder("Search");
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {}

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return null;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
       return new Dimension(0,0);
    }

    @Override
    public void layoutContainer(Container parent) {}
    
}
