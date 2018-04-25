/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import net.deadbeat.schedule.EName;
import net.deadbeat.utility.Log;

/**
 *
 * @author darylcecile
 */
public class Textbox extends Component{
    
    public String value;
    public String placeholder;
    
    public int pointerPosition = 0;
    public int selectionLength = 0;
    
    public Boolean mouseOver = false;
    
    public Textbox(){
    }

    @Override
    public void whenReady() {
        Log.Out("Textbox Ready");
        
        On(EName.MOUSE_DOWN, (results) -> {
            this.gainFocus();
            this.reflow();
        });
        
        On(EName.MOUSE_CLICK, (results) -> {
            MouseEvent e = (MouseEvent)results[0];
            handleClick(e);
            e.consume();
            this.reflow();
        });
        
        On(EName.MOUSE_HOVER, (results)->{
            this.setCursor( Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR) );
        });
        
        this.On(EName.KEY_TYPED, (results) -> {
            KeyEvent e = (KeyEvent)results[0];
            if ( ((int)e.getKeyChar()) != 8 ) insertText(e.getKeyChar()+"");
            else deleteText();
        });
        
        this.On(EName.KEY_PRESSED, (results) -> {
            KeyEvent e = (KeyEvent)results[0];
            if (this.focused){
                switch (e.getKeyCode()) {
                    case 37:
                        //left
                        moveCaret(-1, (e.getModifiers() == 1) );
                        break;
                    case 39:
                        //right
                        moveCaret(1, (e.getModifiers() == 1) );
                        break;
                    case 38:
                        //top
                        moveCaret( this.pointerPosition * -1 );
                        break;
                    case 40:
                        //down
                        moveCaret( this.value.length() - this.pointerPosition );
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void whenStarted() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void whenPainting(Graphics g) {
        Graphics2D graphics = (Graphics2D)g.create();
        
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics.setPaintMode();
        
        if (this.focused){
            graphics.setColor(Color.white);
        }
        else{
            graphics.setColor( new Color(255,255,255,50) );
        }
        graphics.fillRect(0, 0, getWidth(), getHeight());
        
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        
        if ( this.value.length() == 0 ){
            graphics.setColor(new Color(255,255,255, 100));
            graphics.drawString(this.placeholder, 8, getHeight() - 7);
        }
        else{
            graphics.setColor(Color.black);
            graphics.drawString(this.value, 8, getHeight() - 7);
        }
        
        if (this.focused){
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
    
    private void handleClick(MouseEvent e){
        if ( this.value.length() < 1 ) {
            return;
        }
        
        Graphics2D graphics = (Graphics2D) this.getGraphics();
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 14)); 
        FontMetrics fm = graphics.getFontMetrics();
        int segLen = 8;
        int _charWidth = 2;
        ArrayList<Integer> segments = new ArrayList<Integer>();
        for (int c = 0; c < this.value.length(); c++){
            int charWidth = fm.charWidth( this.value.charAt(c) );
            _charWidth = charWidth / 2;
            segLen += charWidth;
            segments.add( segLen );
        }
        
        Point m = e.getPoint();
        
        
        if ( m.x < 8 ){
            this.pointerPosition = 0;
            this.selectionLength = 0;
        }
        else if ( m.x > segLen ){
            this.pointerPosition = this.value.length();
            this.selectionLength = 0;
        }
        else{
            
            for ( int p = 0; p < segments.size() ; p++ ){
                
                int lowerBound = segments.get(p) - _charWidth;
                int upperBound = segments.get(p) + _charWidth ;
                
                if ( m.x > lowerBound && m.x < upperBound ){
                    this.pointerPosition = p + 1;
                    this.selectionLength = 0;
                }
                
            }
            
        }
    }
    
    private void moveCaret(int moves,Boolean shift){
        if (shift == true){
            this.selectionLength += moves;
        }
        else{
            this.selectionLength = 0;
            this.pointerPosition += moves;
        }
        if ( this.pointerPosition < 0 ) this.pointerPosition = 0;
        if ( this.pointerPosition > this.value.length() ) this.pointerPosition = this.value.length();
        
        if ( this.selectionLength < 0 ){
            this.pointerPosition += this.selectionLength;
            this.selectionLength = this.selectionLength * -1;
            // invert if opposite
        }
        this.reflow();
    }
    
    private void moveCaret(int moves){
        //default false shift
        moveCaret(moves,false);
    }
    
    private void selectAll(){
        this.selectionLength = this.value.length();
        this.pointerPosition = 0;
    }
    
    public void setText(String text){
        this.value = text;
        this.repaint();
    }
    
    public void setPlaceholder(String text){
        this.placeholder = text;
        this.repaint();
    }
    
    public void insertText(String text){
        this.setText(this.value.substring(0 , this.pointerPosition) + text + this.value.substring(this.pointerPosition + this.selectionLength));
        moveCaret(1);
    }
    
    public void deleteText(){
        if ( this.selectionLength > 0 ){
            this.setText(
                this.value.substring(0, this.pointerPosition) + this.value.substring(this.pointerPosition + this.selectionLength)
            );
            moveCaret(-1);
        }
        else{
            if ( this.pointerPosition > 0 ){
                this.setText(
                    this.value.substring(0, this.pointerPosition - 1) + this.value.substring(this.pointerPosition)
                );
                moveCaret(-1);
            }
        }
    }
    
}
