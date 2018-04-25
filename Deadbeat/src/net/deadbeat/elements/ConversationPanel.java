/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Dimension;
import java.awt.Graphics;
import net.deadbeat.layout.RawLayout;
import net.deadbeat.utility.Log;

/**
 *
 * @author darylcecile
 */
public class ConversationPanel extends Panel{
    
    private boolean shown = false;
    
    public Textbox messageBox;
    public Panel chat;

    public final void resize(){
        
        int w = Width - 30;
        int h = LayoutAdapter.CBAR_HEIGHT + 1; //24
        int _y = Height - (LayoutAdapter.CBAR_HEIGHT + 16);
        int _x = 15;
        
        messageBox.setBound( _x,_y  ,w, h );
        messageBox.setPreferredSize(new Dimension(w,h));
        messageBox.setBounds( _x,_y,w,h);
        
    }
    
    @Override
    public void whenReady() {
        super.whenReady(); 
        
        this.setLayout(new RawLayout());
        
        messageBox = new Textbox();
        chat = new Panel();
        
        Log.Out(Width,Height);
        
        messageBox.setBound(15, 0 , 30 - Width , 24 );        
        
        messageBox.setPreferredSize( new Dimension(30 - Width , 24) );

        Log.Out(Width,Height);
        
        messageBox.setText("");
        messageBox.setPlaceholder("Type a message...");
        messageBox.setAlignmentX(0f);
        messageBox.setAlignmentY(0f);
        
        this.add(messageBox);
        // this.add(chat);
        
    }

    @Override
    public void whenPainting(Graphics g) {
        
        if ( shown ){
            
            if (focused){
                
            }
            else{
                
            }
            
            super.whenPainting(g);
            
        }
        
    }
    
    public void setVisibility(boolean isVisible){
        shown = isVisible;
        reflow();
    }
    
    
    
}
