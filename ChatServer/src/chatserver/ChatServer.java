/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;
import java.net.*;
import java.io.*;
import net.deadbeat.utility.Log;

/**
 *
 * @author Ethan
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ChatServer chat = new ChatServer();
        chat.newClient();
    }
    
    private void newClient(){
        int port = 2020;
        try{
            DatagramSocket socket = new DatagramSocket();
            byte[] data = new byte[1024];
            
            //loop continuously so clients can easily connect
            while(true){
                System.out.println("Waiting for a client to chat...");
                
                //recieve connection and data from client machine
                DatagramPacket dataPacket = new DatagramPacket(data, data.length);
                socket.receive(dataPacket);
                
                //create a new thread for to sort the data sent by the client and give any reposnce necessary to the client
                Thread newClient = new Thread(new ChatThread(socket, dataPacket));
                newClient.start();
            }
            
        }catch(Exception e){Log.Throw(e);}
    }
}
