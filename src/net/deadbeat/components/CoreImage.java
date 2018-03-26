/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.components;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author darylcecile
 */
public class CoreImage extends JLabel {
    
    public BufferedImage img;
    
    public CoreImage(String fileName){
        super();
        try {
            img = ImageIO.read( getClass().getResource("/net/deadbeat/app_resources/"+fileName) );
            System.out.println("set Image" + fileName);
            System.out.println( img );
        } catch (IOException ex) {
            Logger.getLogger(CoreImage.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println(ex.toString());
        }
    }
    
    public void showIn( CorePanel container ){
        if ( this.getParent() != container ) container.add(this);
        ImageIcon imageIcon = new ImageIcon(resizeImage(img, this.getWidth(), this.getHeight()));
        this.setIcon( imageIcon );
    }
    
    private Image resizeImage(Image img , int w , int h)
    {
        BufferedImage resizedimage = new BufferedImage(w,h,BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedimage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(img, 0, 0,w,h,null);
        g2.dispose();
        return resizedimage;
    }
    
}
