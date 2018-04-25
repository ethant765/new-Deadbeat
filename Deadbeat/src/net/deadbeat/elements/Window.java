/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.elements;

import net.deadbeat.elements.Canvas;
import net.deadbeat.elements.Overlay;
import net.deadbeat.layout.RawLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import net.deadbeat.core.TaskController;
import net.deadbeat.elements.ConversationPanel;
import net.deadbeat.elements.Image;
import net.deadbeat.elements.RoundedTextbox;
import net.deadbeat.elements.Textbox;
import net.deadbeat.elements.TitleBar;


// uses jar
import net.deadbeat.utility.Log;

/**
 *
 * @author darylcecile
 */
public class Window extends javax.swing.JFrame {

    /**
     * Creates new form Home
     */
    public Window() {
        
        Log.Out("Created Window");
        TaskController.runOnUiThread(()->{
            initFrames();
            Log.Out("Initialized Frames");
        });

    }
    
    public Canvas canvas;
    public Overlay overlay;
    public Image badge;
    public TitleBar tbar;
    
    public ConversationPanel conversation;
    
    public RoundedTextbox searchbox;
    
    private void initFrames(){
        
        LayoutAdapter.CustomProperty cproperties = LayoutAdapter.setProperty(this);
        
        cproperties.layout(new RawLayout());
        
        // Create the main panels
        canvas = new Canvas();
        overlay = new Overlay();
        searchbox = new RoundedTextbox();
        conversation = new ConversationPanel();
        
        add(canvas);
        
        cproperties.customPaint(true).size(900,640);
        
        LayoutAdapter.SetUp();
        
        canvas.add(tbar);
        canvas.add(overlay);
        
        overlay.add(conversation);
            
        conversation.setVisibility(true);
        
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                java.awt.Component component = e.getComponent();
                cproperties.bound(0, 0, component.getWidth(), component.getHeight());
                conversation.resize();
            }
            
        });
        
        LayoutAdapter.Update();
        
        Log.Out("Frame Initialized");

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
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Window.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        TaskController.runAfter(()->{
            Window applicationWindow = new Window();
            applicationWindow.setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    // End of variables declaration                   
    
}
