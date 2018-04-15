/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;
import java.net.*;
import java.io.*;
import java.sql.*;


/**
 *
 * @author Ethan
 */
public class DeadbeatSocialNetworkServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            int portNum = 9090;
            
        } catch(IOException e){
            System.err.println("Error! - " + e.getMessage());
        }
        
        
    }
    
}


/*try{

            int portNumber = 9090;
            DatagramSocket server = new DatagramSocket(portNumber);
            
            byte[] data = new byte[1024];
            DatagramPacket receivedPacket = new DatagramPacket(data, data.length);
            server.receive(receivedPacket);
            data = receivedPacket.getData();
            String Message = new String(data);
            
            Message = Message.toUpperCase();
            
            data = Message.getBytes();
            
            
            
        }catch(IOException e){
            System.err.println("Error! - " + e.getMessage());
        }*/