/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;
import java.net.*;
import java.sql.ResultSet;
import net.deadbeat.utility.Log;
import java.sql.*;
        
/**
 *
 * @author Ethan
 */
public class ChatThread implements Runnable {
    //constructors for class used through this class
    DBInteractions dataChange = new DBInteractions();
    dataHeaders headers;
    
    //class required global variables
    DatagramSocket socket;
    DatagramPacket packet;
    InetAddress userIP;
    int userPort;
    int clientID;
    
    //constructor
    ChatThread(DatagramSocket sok, DatagramPacket pak){
        socket = sok;
        packet = pak;
        userIP = pak.getAddress();
        userPort = pak.getPort();
        
        //get clientID from members table (active users)
        //user must be an active users to be using the chat server so no check requried
        String table = "Members";
        String select = "User_ID";
        String where =  "IPAddress = '" + userIP.toString().substring(1) + "'";//remove slash from from of InetAddress for database access
        ResultSet IDresult = dataChange.GetRecord(select, table, where);
        
        try{
            if(IDresult != null){ //IPAddress is PK so only 1 result if there is a result
                clientID = IDresult.getInt("User_ID");
            }
        }catch(Exception e){Log.Throw(e);}
    }
    
    public void run(){
        
    }
}
