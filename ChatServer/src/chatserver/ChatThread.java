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
import net.deadbeat.json.JSONAdapter;
import net.deadbeat.json.JSONProperty;
import net.deadbeat.utility.Tokenizer;
        
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
    boolean opSuccess;
    
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
        
        //default opSuccess to true/successfull
        //then if error caught change boolean later
        opSuccess = true;
    }
    
    public ChatThread(dataHeaders headers){
        this.headers = headers;
    }
    
    public void run(){
        //get the data sent from the user to use in this thread
        byte[] data = new byte[1024];
        data = packet.getData();
        String Message = new String(data);
        
        //create a JSON object to convert the sent string into the initial data sent by the user
        JSONAdapter jAdapter = new JSONAdapter();
        jAdapter.fromString(Message);
        String headerInfo = jAdapter.get("HEADER").get();
        
        //create a JSON object used for storing and sending return information back to the client
        JSONAdapter sendBackData = new JSONAdapter();
        
        if(String.valueOf(headers.RECEIVE_MESSAGES).equals(headerInfo)) sendBackData.fromResultSet(receiveMessages());
        else if(String.valueOf(headers.SEND_MESSAGE).equals(headerInfo)) sendMessage(jAdapter);
        else{
            //error
        }
        //pass return infromation to function which handles client interaction
        sendToUser(sendBackData);
    }
     //send data back to client function
    private void sendToUser(JSONAdapter sendData){
        try{
            //adds status header onto the JSON string, ready for sending to client
            sendData.get(0).add(new JSONProperty("STATUS", String.valueOf(opSuccess), Tokenizer.TokenType.BOOLEAN ));
            
            //convert the JSON object into string , then bytes ready to be sent to client
            String message = sendData.toString();
            byte[] data = message.getBytes();
            
            //send data back to client
            DatagramPacket dataToSend = new DatagramPacket(data, data.length, userIP, userPort);
            socket.send(dataToSend);
            
        }catch(Exception e){Log.Throw(e);}
    }
    
    //function to change error status if issue with performing an operation for the client
    private void Error(boolean error){
        opSuccess = error;
    }
    
    //function to return messages to the client based on what has been sent to them
    private ResultSet receiveMessages(){
        return null;
    }
    
    //function which allows the client to send messages to other users
    private void sendMessage(JSONAdapter info){
        
    }
}
