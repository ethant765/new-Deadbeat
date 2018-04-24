/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

import net.deadbeat.layout.RawLayout;
import net.deadbeat.ui.CoreOverlayPanel;
import net.deadbeat.ui.CorePanel;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import net.deadbeat.ui.CoreBar;

import static net.deadbeat.core.LayoutAdapter.cbar;

// uses jar
import net.deadbeat.utility.JSONAdapter;
import net.deadbeat.utility.Log;

/**
 *
 * @author darylcecile
 */
public class Home extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    public Home() {
        
        TaskController.runOnUiThread(()->{
            initFrames();
        });

    }
    
    private void initFrames(){
        
        // Add custom layout to control how things are position
        this.setLayout(new RawLayout());
        
        // Create the main panels
        corepanel = new CorePanel();
        overlaypanel = new CoreOverlayPanel();
        
        // add the titlebar so windows can be moved about and closed or resized
        cbar = new CoreBar(corepanel,this);
        
        // Remove default layout for panels
        corepanel.setLayout( null );
        overlaypanel.setLayout( null );
        cbar.setLayout(null);
        
        LayoutAdapter.prepareElements(corepanel, overlaypanel, this, cbar);
        
        setUndecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(false);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(corepanel);

        this.setSize(900,640);
        this.setMinimumSize(new Dimension(900,640));

        corepanel.setDimension(getWidth(), getHeight());
        
        final int sidebarWidth = (getWidth()/100) * 25;
        overlaypanel.setBounds(sidebarWidth, 0, getWidth() - sidebarWidth , getHeight());
        
        corepanel.add(cbar);
        corepanel.add(overlaypanel);
     
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // force update the dimensions so panel can update graphics
                overlaypanel.setBounds(sidebarWidth, 0, getWidth() - sidebarWidth , getHeight());
                corepanel.setBounds(0, 0, getWidth(), getHeight());
                
                LayoutAdapter.reflow();
            }
            
        });
        
        System.out.println("Frame Initialized");

        initComponents();
       
    }
 
    public int getContainerWidth(){
        return getWidth();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>                        

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Home.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        TaskController.runAfter(()->{
            Home application = new Home();
            application.setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   
    
    protected CorePanel corepanel;
    protected CoreOverlayPanel overlaypanel;
}
