/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datagramclient;
import java.io.*;
import java.net.*;
/**
 *
 * @author n0688008
 */
public class DatagramClient {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));
        String usersInputtedString = "{'enum': 'FRIENDS_LIST', 'USER_ID': 2}";
        String uppercaseReturn = null;
        
        try{
            DatagramSocket client = new DatagramSocket();
            
            
            byte[] data = usersInputtedString.getBytes();
            InetAddress addr = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr, 9090);
            client.send(sendPacket);
            
            
            
            byte[] backData = new byte[1024];
            DatagramPacket recievePacket = new DatagramPacket(backData, backData.length);
            client.receive(recievePacket);
            backData = recievePacket.getData();
            String upperMessage = new String(backData);
            System.out.println(upperMessage);

            
        }catch(IOException e){
            System.err.println("Error! - " + e.getMessage());
        }    
    }
    
}
