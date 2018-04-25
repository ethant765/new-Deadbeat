/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;
import java.net.*;
import java.io.*;
import net.deadbeat.utility.Log;


/**
 *
 * @author Ethan
 */
public class DeadbeatSocialNetworkServer {

    /**
     * @param args the command line arguments
     */
    public static ServerInterface sinterface;
    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                sinterface = new ServerInterface();
                sinterface.setVisible(true);
            }
        });
        
        DeadbeatSocialNetworkServer server = new DeadbeatSocialNetworkServer();
        server.NewClient();
    }
    
    protected void NewClient(){
        
        int portNum = 9090;
        try{
            DatagramSocket serverSocket = new DatagramSocket(portNum);
            byte[] data = new byte[1024];
            
            while(true){
                
                System.out.println("Waiting for a client...");
                
                //recieves the data sent by the client and sends it through to the new thread for processing
                DatagramPacket recieveData = new DatagramPacket(data, data.length);
                serverSocket.receive(recieveData);
                
                      
                //create a new thread for each client which is connecting
                Thread newClient = new Thread(new UserThread(serverSocket, recieveData));
                newClient.start();
                
                
                String addIPtoGUI = "Client connected on IP: " + String.valueOf(recieveData.getAddress());
                sinterface.addIPtoList(addIPtoGUI);
                Log.Out(addIPtoGUI);
            }
            
        }catch(IOException e){Log.Throw(e);}
    }
}