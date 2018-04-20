/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import java.net.*;
import java.sql.ResultSet;
import java.util.*;

/**
 *
 * @author Ethan
 */

//Thread class which is resposible for each client connected to the server
public class UserThread implements Runnable{
    
    dataHeader headers;
    DBInteractions dataChange = new DBInteractions();
    
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
    
    public UserThread(dataHeader headers){
        this.headers = headers;
    }
    
    public void run(){
        //get the data sent from the user on their connection to the server
        byte[] data = new byte[1024];
        data = packet.getData();
        String Message = new String(data);
        String messageParts[] = SplitString(Message);
        
        
        if(String.valueOf(headers.updateInfo).equals(messageParts[0])){
            //update messageboard info, update active users list, recieve new friend request notifications, recieve friends request acceptance notifications
        }
        else if(String.valueOf(headers.RecieveSimilarProfiles).equals(messageParts[0])){
            //list of users with similar music preferences sent back to the user
        }
        else if(String.valueOf(headers.recieveFriendsSharedSongs).equals(messageParts[0])){
            //request a friends shared song from, song returned to user
        }
        else if(String.valueOf(headers.logOff).equals(messageParts[0])){ 
            //sent string for log off only requires header vals
            //as IP address is used for log off this can be retrieved server side
            logOff();
        }
        else if(String.valueOf(headers.login).equals(messageParts[0])){
            //handles the login for the client
            //For exisint user:         sent string requries header, boolean isExistingUser = true, userName    - in that order
            //For new user:             sent string requires header, boolean isExistingUser = false, UserName, PlaceOfBirth, DOB, ProfileImage BLOB DATA        - in that order
            LoginHandler(messageParts);
        }
        else if(String.valueOf(headers.addToMessageBoard).equals(messageParts[0])){
            
        }
        else{
            //error
        }
    }
    
    //function used to return data to the client
    private void sendToUser(String message){
        try{
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, userIP, userPort);
            socket.send(sendPacket);
        }catch(Exception e){System.err.println(e.getMessage());}
    }
    
    //used to split recieved strings into their indervidual tokens
    private String[] SplitString(String passedString){
        StringTokenizer tokens = new StringTokenizer(passedString, ",");
        int numOfTokens = tokens.countTokens();
        String holder[] = new String[numOfTokens];
        
        for(int i = 0; i < numOfTokens; i++){
            holder[i] = tokens.nextToken().trim();
        }
        return holder;
    }
    
    //deals with the login and new users when client first connects to server
    private void LoginHandler(String[] loginData){
        boolean existingUser = Boolean.valueOf(loginData[1]);
        String returnMessage;
        
        if(existingUser == true){
            returnMessage = returningUser(loginData[2]);
        }
        else{
            returnMessage = newUser(loginData);
        }
        
        
        //once login/sign up is complete send success/failure message to clinet machine
        
        //message should contain the users information for the clients profile when logged in
        
        //byte[] LoginStatus = returnMessage.getBytes();
        //DatagramPacket sendPacket = new DatagramPacket(LoginStatus, LoginStatus.length, userIP, userPort);
        //try{ socket.send(sendPacket); }catch(Exception e){System.err.println(e.getMessage());}
    }
    
    //recieves the data from the client for new user
    //and stores data in the database
    private String newUser(String[] loginData){
        String tableName = "Profiles";
        String tableCols = "(user_ID, UserName, PlaceOfBirth, DOB, ProfileImage)";
        //String Values = "('" + userID + "', '" + loginData[2] + "', '" + loginData[3] + "', '" + loginData[4] + "', " + loginData[5] + ")";
        return "Error";
    }
    //recieves the data from the client for returning user
    //checks user credentials
    //adds IPaddress to active users table in DB
    private String returningUser(String ID){
        //get resultset of userName data from the databse
        String value = "*";
        String table = "Profiles";
        String Condition = "Username = '" + ID + "'";
        ResultSet result = dataChange.GetRecord(value, table, Condition);
        
        try{
            //if result isn't null then it must have found a user, therefor login is correct
            if(result != null){
                //after credentials checked create friends list and send it to user
                String friendsList = "FriendsList" + GetFriends(result.getInt("User_ID"));
                sendToUser(friendsList);
                //send list of recieved but not accepted friend requests
                
                //after credentials checked, friend lsit sent - create and send messageBoard Messages
                
                //create list of active users and send to clinet
                
                
                
                
                //return loginInfo;
            }
        }catch(Exception e){System.err.println(e.getMessage());}
        
        return "Error: No matching user!";//error - no user matches provided userName
    }
    //returns the list of a users friends
    private String GetFriends(int UserID){
        String friends = null;
        
        //get a list of all the users frinds
        String Value = "User_ID, Friend_ID";
        String Table = "Friends";
        String Condition = "User_ID = " + UserID + " OR Friend_ID = " + UserID;
        ResultSet result = dataChange.GetRecord(Value, Table, Condition);
        
        try{
            //loop through each friend
            while(result.next()){
                //test if this is friendsID or current users ID
                if(result.getInt("User_ID") != UserID){ 
                    friends += "," + getUserName(result.getInt("User_ID"));
                }
                else{
                    friends += "," + getUserName(result.getInt("Friend_ID"));
                }
            }
        }catch(Exception e){System.err.println(e.getMessage());}
        
        //either null (for no friends) or a list of comma seporated friend userNames should be returned
        return friends;
    }
    //returns userName from userID
    private String getUserName(int userID){
        String userName = null;
        String val = "UserName";
        String table = "Profiles";
        String condition = "User_ID" + userID;
        ResultSet result = dataChange.GetRecord(val, table, condition);
        try{
            userName = result.getString("UserName");
        }catch(Exception e){System.err.println(e.getMessage());}
        return userName;
    }

    //returns all the messageBoard Messages from users friends
    private String getMessageBoardItems(){
        return "";
    }
    
    
    //should remove users IPaddress and info from active users table (Members table)
    //removes any messages the user has put on the message board while
    private void logOff(){
        //call function to clear users message board messages
        //uses IPadress so needs calling before clearing users IP
        removeUserMessageBoardMessages();
        
        String tableToEdit = "Members";//members table is the table which stores active members
        String conditionForEdit = "IPAddress = " + userIP; // remove logging off user based on their IPAddress as this will be unique per user, and set each time a user logs in

        //also remove users messages from the message board
        
        dataChange.DeleteRecord(tableToEdit, conditionForEdit);
    }
    private void removeUserMessageBoardMessages(){
        //from users IP get users ID
        String selectVals = "User_ID";
        String selectTable = "Members";
        String SelectCondition = "IPAddress = " + userIP;
        ResultSet result = dataChange.GetRecord(selectVals, selectTable, SelectCondition);
        
        try{
            //use users ID to remove any messages they have posted on the message board
            String removeTable = "MessageBoard";
            String removeCondition = "User_ID = " + result.getInt("User_ID");
            dataChange.DeleteRecord(removeTable, removeCondition);
        }catch(Exception e){System.err.println(e.getMessage());}
    }
}
