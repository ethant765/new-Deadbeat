/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import java.net.*;

/**
 *
 * @author Ethan
 */

//Thread class which is resposible for each client connected to the server
public class UserThread implements Runnable{
    
    DatagramSocket socket;
    DatagramPacket packet;
    InetAddress userIP;
    int userPort;
    
    UserThread(DatagramSocket sok, DatagramPacket pak){
        socket = sok;
        packet = pak;
        userIP = pak.getAddress();
        userPort = pak.getPort();
    }
    
    //each byte of data is a string. the string could start with integer/key word indicating to the server which operation needs performing
    
    public void run(){
        boolean logOff = false;
        
        LoginHandler();
        
        while(logOff == false){
            //thread includes 2 second pause
            //loop data to client every 2 seconds like spec asks
            //if client sends logoff boolean data then exit loop - which should end connection with client
        }
        
        //before connection lost clear user from active users table
        logOff();
    }
    
    //deals with the login and new users when client first connects
    protected void LoginHandler(){
        byte[] data = new byte[1024];
        data = packet.getData();       
        
        String Message = new String(data);
        boolean existingUser = Boolean.parseBoolean(Message);
        String ReturnMessages;
        int success;
        
        do{
            if(existingUser == true){
                //1 for error - 0 for success
                success = returningUser();
                ReturnMessages = Integer.toString(success);
            }
            else{
                //1 for error - 0 for success
                success = newUser();
               ReturnMessages = Integer.toString(success);
            }

            try{
                //send data to client informing of successful login
                byte[] successData = ReturnMessages.getBytes();
                DatagramPacket sendData = new DatagramPacket(successData, successData.length, userIP, userPort);
                socket.send(sendData);
            }catch(Exception e){System.err.println(e.getMessage());}
            
        }while(success != 0);//loop till login successful
    }
    
    //recieves the data from the client for new user
    //and stores data in the database
    protected int newUser(){
        return 0;
    }
    //recieves the data from the client for returning user
    //checks user credentials
    //adds IPaddress to active users table in DB
    protected int returningUser(){
        return 0;
    }
    
    //should remove users IPaddress and info from active users table (Members table)
    protected void logOff(){
        String SQLStatement = "delete from Members where IPAddress = " + userIP; //should clear the row in the DB table for clients IP
        DBInteractions dataChange = new DBInteractions();
        //dataChange.EditData(SQLStatement);
    }
}