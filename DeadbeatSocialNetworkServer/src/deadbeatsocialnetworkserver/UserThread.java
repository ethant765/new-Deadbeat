/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import java.net.*;
import java.sql.ResultSet;
import java.util.*;
import org.json.*;

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
        
        
        if(String.valueOf(headers.LOGINNEWUSER).equals(messageParts[0])){
           
        }
        else if(String.valueOf(headers.LOGINEXISTINGUSER).equals(messageParts[0])){
            
        }
        else if(String.valueOf(headers.LOGOFF).equals(messageParts[0])){
            
        }
        else if(String.valueOf(headers.SHARESONG).equals(messageParts[0])){ 
            shareSong();
        }
        else if(String.valueOf(headers.UPDATEACTIVEUSERS).equals(messageParts[0])){
            sendToUser(updateActiveUsers(0)); //int clients user_ID
        }
        else if(String.valueOf(headers.SHAREDSONGSLIST).equals(messageParts[0])){
            sendToUser(SharedSongsList(0)); //int user_ID (could be user or friends ID)
        }
        else if(String.valueOf(headers.RECIEVESIMILARPROFILES).equals(messageParts[0])){
            sendToUser(similarProfiles());
        }
        else if(String.valueOf(headers.UPDATEMESSAGEBOARD).equals(messageParts[0])){
            sendToUser(updateMessageBoard());
        }
        else if(String.valueOf(headers.ADDTOMESSAGEBOARD).equals(messageParts[0])){
            addToMessageBoard();
        }
        else if(String.valueOf(headers.UPDATEFRIENDREQUESTS).equals(messageParts[0])){
            sendToUser(updateFriendRequests(0)); //int clients user_ID
        }
        else if(String.valueOf(headers.FRIENDSLIST).equals(messageParts[0])){
            sendToUser(FriendsList(0)); //int clients user_ID
        }
        else if(String.valueOf(headers.SENDFRIENDREQUEST).equals(messageParts[0])){
            sendFriendRequest();
        }
        else if(String.valueOf(headers.CHANGEFRIENDREQUESTSTATUS).equals(messageParts[0])){
            changeFriendRequestStatus();
        }
        else{
            //error
        }
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
    

    //function used to return data to the client
    private void sendToUser(ResultSet sendData){
        try{
            JSONArray jArray = new JSONArray();
            resultSetToJson convert = new resultSetToJson();
            
            //use function which converts database resultset to JSONArray
            jArray = convert.convertToJSON(sendData);
            
            //convert JSONArray to strng
            String message = jArray.toString();
            
            //convert string to bytes ready to send to client
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, userIP, userPort);
            socket.send(sendPacket);
        }catch(Exception e){System.err.println(e.getMessage());}
    }

    
    
    
    
    //-----------------------------login and log off functions---------------------------------
    
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
                
                //send list of friends to the user
                
                //send list of recieved but not accepted friend requests
                
                //after credentials checked, friend lsit sent - create and send messageBoard Messages
                
                //create list of active users and send to clinet
                
                //return loginInfo;
            }
        }catch(Exception e){System.err.println(e.getMessage());}
        
        return "Error: No matching user!";//error - no user matches provided userName
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
    //when user logs off removes any messages which they put on the message board
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
    
    
    
    
    
    
    //-------------------------------------------enum functions-----------------------------------------------------
    //allows the client to share a song
    private void shareSong(){
        
    }
    
    //sends client list of all currently online users
    private ResultSet updateActiveUsers(int userID){
        String select = "Members.User_ID, Profiles.UserName";
        String from = "FROM Profiles RIGHT JOIN Members ON Profiles.User_ID = Members.User_ID";
        String where = "WHERE Profiles.User_ID <> " + userID;
        
        return dataChange.GetRecord(select, from, where);
    }
    
    //sends the client a list of songs shared by their specified friends
    private ResultSet SharedSongsList(int UserID){        
        String select = "SharedSongs.SharedSongs_ID, SharedSong, SongName, Artist, ReleaseDate, Album";
        String from = "SharedSongs LEFT JOIN ProfileSharedSongs ON SharedSongs.SharedSongs_ID = ProfileSharedSongs.SharedSong_ID";
        String where = "ProfileSharedSongs.USER_ID = " + UserID;
        
        return dataChange.GetRecord(select, from, where);
    }
    
    //sends the client a list of all other profiles with similar music preferences
    private ResultSet similarProfiles(int userID){
        
        /*
        SELECT Profiles.User_ID, Profiles_UserName
        FROM Profiles LEFT JOIN ProfileMusicPreferences ON Profiles.User_ID = ProfileMusicPreferences.User_ID
        WHERE
        */
        
        return null;
    }
    
    //sends an up-to-date message board to the clinet
    private ResultSet updateMessageBoard(){
        return null;
    }
    
    //adds the users message to the message board for their friends to see
    private void addToMessageBoard(){
        
    }
    
    //sends a list to the client of all their friend requests
    private ResultSet updateFriendRequests(int userID){
        String vals = "Profiles.User_ID, Profiles.UserName";
        String tables = "Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.User_ID";
        String condition = "Friends.Friend_ID =" + userID + "AND Status_ID = 'Wait'";
        
        return dataChange.GetRecord(vals, tables, condition);
    }
    
    
    //sends a list to the client of their friends - (friendsID and friends userName)
    private ResultSet FriendsList(int UserID){
       String sqlCmd = "(SELECT Profiles.user_ID, Profiles.UserName " +
               "FROM Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.User_ID " +
               "WHERE Friends.User_ID <> " + UserID +
               " AND Friends.Status_ID = 'con') " +
               "UNION " +
               "(SELECT Profiles.user_ID, Profiles.UserName " +
               "FROM Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.Friend_ID " +
               "WHERE Friends.Friend_ID <> " + UserID +
               " AND Friends.Status_ID = 'con')";

        return dataChange.GetCustomRecord(sqlCmd);
    }
    
    //allows the client to send a friend request to another user who isn't already their friend
    private void sendFriendRequest(){
        
    }
    
    //chanegs the status of a friend request (accept or reject)
    private void changeFriendRequestStatus(){
        
    }
}
