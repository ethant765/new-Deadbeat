/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;
import java.net.*;
import java.io.*;


/**
 *
 * @author Ethan
 */
public class DeadbeatSocialNetworkServer {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        DeadbeatSocialNetworkServer server = new DeadbeatSocialNetworkServer();
        server.NewClient();
    }
    
    protected void NewClient(){
        int portNum = 1527;
        try{
            DatagramSocket serverSocket = new DatagramSocket(portNum);
            byte[] data = new byte[1024];
            
            while(true){
                System.out.println("Waiting for a client...");
                
                //initial data recieved here should be a boolean
                //true fro existingUser, false for newUser
                DatagramPacket recieveData = new DatagramPacket(data, data.length);
                serverSocket.receive(recieveData);
                
                System.out.println("Created...");
                
                //create a new thread for each client which is connecting
                Thread newClient = new Thread(new UserThread(serverSocket, recieveData));
                newClient.start();
                
                System.out.println("Started...");
            }
            
        }catch(Exception e){
            System.err.println("ERR in MainThread>"+e.getMessage());
        }
    }
}