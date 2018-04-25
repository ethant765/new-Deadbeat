/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.deadbeat.utility.Log;

/**
 *
 * @author darylcecile
 */
public class Image extends JLabel{
    
    public BufferedImage img;
    
    public Image(String imageName){
        super();
        try {
            img = ImageIO.read( getClass().getResource("/net/deadbeat/resources/"+imageName) );
        } catch (Exception ex) {
            Log.Throw(ex);
        }
    }
    
    public void repaintIn( Container container ){
        if ( this.getParent() != container ) container.add(this);
        ImageIcon imageIcon = new ImageIcon(resizeImage(img, this.getWidth(), this.getHeight()));
        this.setIcon( imageIcon );
    }
    
    public static java.awt.Image resizeImage(java.awt.Image img , int w , int h)
    {
        BufferedImage resizedimage = new BufferedImage(w,h,BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedimage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0,w,h,null);
        g2.dispose();
        return resizedimage;
    }
    
}
