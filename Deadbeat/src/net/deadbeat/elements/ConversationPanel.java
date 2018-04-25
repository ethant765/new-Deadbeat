/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Graphics;
import static net.deadbeat.elements.LayoutAdapter.win;

/**
 *
 * @author darylcecile
 */
public class ConversationPanel extends Panel{
    
    private boolean shown = false;
    
    public Textbox messageBox;
    public Panel chat;

    public final void resize(){
        
        messageBox.setBound(15, Height - (LayoutAdapter.CBAR_HEIGHT + 16) , 30 - Width , LayoutAdapter.CBAR_HEIGHT + 1 );
        
    }
    
    @Override
    public void whenReady() {
        
        messageBox = new Textbox();
        chat = new Panel();
        
        messageBox.setBound(15, 0 , 30 - Width , 24 );
        
        messageBox.setText("");
        messageBox.setPlaceholder("Type a message...");
        messageBox.setAlignmentX(0f);
        messageBox.setAlignmentY(0f);
        
        add(messageBox);
        add(chat);
        
        super.whenReady(); 
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
