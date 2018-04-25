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
import net.deadbeat.utility.BinResource;
import net.deadbeat.utility.Tokenizer;
import java.util.*;
        
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
        
        if(String.valueOf(headers.RECEIVE_MESSAGES).equals(headerInfo)) 
            receiveMessages(jAdapter);
        else if(String.valueOf(headers.SEND_MESSAGE).equals(headerInfo)) {
            sendMessage(jAdapter);
            //pass return infromation to function which handles client interaction
            sendToUser(sendBackData);
        }
        else{
            Error(false);
            //pass return infromation to function which handles client interaction
            sendToUser(sendBackData);
        }
        
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

    
    //function to return messages to the client based on what has been sent in a chat with the specific user
    private void receiveMessages(JSONAdapter info){
        int otherUserID = info.get("OTHER_CHAT_USER_ID").get();
        
        JSONAdapter jsonData = new JSONAdapter();
        List<ResultSet> resultsHolder = new ArrayList<>();
        
        //NP = no payload
        String NPselect = "Message_ID, Sender_ID, Content";
        String NPfrom = "Message";
        String NPwhere = "((Reciever_ID = " + clientID + " AND Sender_ID = " + otherUserID + ") OR (Reciever_ID = " + otherUserID + " AND Sender_ID = " + clientID + ")) AND Attachment IS NULL";
        resultsHolder.add(dataChange.GetRecord(NPselect, NPfrom, NPwhere));
        
        //payload
        String Pselect = "Message_ID, Sender_ID, Content, Payload";
        String Pfrom = "Message LEFT JOIN Attachment ON Message.Attachment = Attachment.Attachment_ID";
        String Pwhere = "((Reciever_ID = " + clientID + " AND Sender_ID = " + otherUserID + ") OR (Reciever_ID = " + otherUserID + " AND Sender_ID = " + clientID + ")) AND Attachment IS NOT NULL";
        resultsHolder.add(dataChange.GetRecord(Pselect, Pfrom, NPwhere));
        
        try{
            jsonData.fromMergedResultSets(resultsHolder);
        }catch (Exception e){Log.Throw(e);}
        
        sendToUser(jsonData);

    }
    
    //function which allows the client to send messages to other users
    private void sendMessage(JSONAdapter info){
        //get data for attachment table
        int attachmentID = intIDgen("Attachment", "Attachment_ID");
        Object payload = BinResource.lookup(info.get("PAYLOAD").get());
        
        //get data for message table
        int senderID = clientID;
        int recieverID = info.get("RECEIVING_USER_ID").get();
        int messageID = intIDgen("Message", "Message_ID");
        String textContent = info.get("MESSAGE_TEXT").get();
        
        
        //test to see if payload is null to know whether data needs adding to the attachment table
        if(payload != null){
            //set up sql query of attachment table
            String attTable = "Attachment";
            String attCols = "(Attachment_ID, Payload)";
            String attVals = "(" + attachmentID + ", " + payload + ")";
            
            //set up sql query for message table
            String mesTable = "Message";
            String mesCols = "(Message_ID, Sender_ID, Reciever_ID, Attachment, Content)";
            String mesVals = "(" + messageID + ", " + senderID + ", " + recieverID + ", " + attachmentID + ", '" + textContent + "')";
            
            //insert data
            dataChange.InsertRecord(attTable, attCols, attVals);
            dataChange.InsertRecord(mesTable, mesCols, mesVals);
            
            //test to ensure data has been inserted correctly
            try{
                String attWhere = "Attachment_ID = " + attachmentID;
                String mesWhere = "Message_ID = " + messageID;
                ResultSet attCheck = dataChange.GetRecord("*", "Attachment", attWhere);
                ResultSet mesCheck = dataChange.GetRecord("*", "Message", mesWhere);
                
                if(!attCheck.next() || !mesCheck.next()){
                    Error(false); //false = unsuccessful - will change the status message sent back to the user to warn them of issue
                }
            }catch(Exception e){Log.Throw(e);}
        }
        else{
            //no payload so attachment table not required
            
            //set up sqlQuery for message table
            String table = "Message";
            String cols = "(Message_ID, Sender_ID, Reciever_ID, Content)";
            String vals = "(" + messageID + ", " + senderID + ", " + recieverID + ", '" + textContent + "')";
            
            //insert data
            dataChange.InsertRecord(table, cols, vals);
            
            //test data has been entered
            try{
                String where = "Message_ID = " + messageID;
                if(!dataChange.GetRecord("*", table, where).next())
                        Error(false);
            }catch(Exception e){Log.Throw(e);}
        }
        
        
    }
    
    //function to generate a new unique int id
    private int intIDgen(String table, String value){
        int valChecker = 0;
        String testIDselect, testIDtable, testIDwhere;
        try{
            do{
                valChecker++;
                testIDselect = value;
                testIDtable = table;
                testIDwhere = value + " = " + valChecker; //test for existing ID
            }while(dataChange.GetRecord(testIDselect, testIDtable, testIDwhere).next());
        }catch(Exception e){Log.Throw(e);}
        return valChecker; //should return the first available ID slot
    }
   
}
