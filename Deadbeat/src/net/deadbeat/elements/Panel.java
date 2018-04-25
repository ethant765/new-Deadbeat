/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import static net.deadbeat.elements.LayoutAdapter.win;
import net.deadbeat.schedule.EName;

/**
 *
 * @author darylcecile
 */
public class Panel extends Component {
    
    protected int strokeSize = 1;
    protected boolean shady = true;
    protected boolean highQuality = true;
    protected Dimension arcs = new Dimension(10, 10);
    protected int shadowPix = 10;
    protected int shadowAlpha =  80;// 80;
    
    public Color borderActive = Color.WHITE;
    public Color borderInactive = Color.LIGHT_GRAY;
    public Color background = Color.GRAY;

    @Override
    public void whenReady() {
        
        setOpaque(false);
        
        On(EName.MOUSE_HOVER,(results) -> {
            if (!focused) gainFocus();
            ((MouseEvent)results[0]).consume();
        });
        
        On(EName.MOUSE_LEAVE,(results) -> {
            loseFocus();
        });
        
    }

    @Override
    public void whenStarted() {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void whenPainting(Graphics g) {
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        
        //Sets antialiasing if HQ.
        if (highQuality) {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        Point p = new Point(strokeSize,strokeSize);
        Dimension boxSize = new Dimension( width - (strokeSize*2) , height - (strokeSize*2) );
        
        //Draws the rounded opaque panel with borders.
        graphics.setColor(background);
        graphics.fillRoundRect(p.x, p.y, boxSize.width,boxSize.height, arcs.width, arcs.height);
        
        if (focused){
            graphics.setColor(borderActive);
        }else{
            graphics.setColor(borderInactive);
        }
        
        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.drawRoundRect(p.x, p.y, boxSize.width, boxSize.height, arcs.width, arcs.height);

        //Sets strokes to default, is better.
        graphics.setStroke(new BasicStroke());
    }
    
}
