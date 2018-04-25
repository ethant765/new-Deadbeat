/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import net.deadbeat.ui.CoreImage;
import static net.deadbeat.ui.CoreImage.resizeImage;
import net.deadbeat.ui.CorePanel;
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
    
}
