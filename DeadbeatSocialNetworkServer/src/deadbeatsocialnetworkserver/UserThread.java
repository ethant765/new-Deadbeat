/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import Composers.JSON;
import java.net.*;
import java.sql.ResultSet;
import java.util.*;
import org.json.JSONArray;

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
        
        //sendToUser(FriendsList(2)); //int clients user_ID
        
        if(String.valueOf(headers.LOGIN_NEW_USER).equals(messageParts[0])){
           
        }
        else if(String.valueOf(headers.LOGIN_EXISTING_USER).equals(messageParts[0])){
             returningUser(0); //int clients User_ID
        }
        else if(String.valueOf(headers.LOG_OFF).equals(messageParts[0])){
            logOff();
        }
        else if(String.valueOf(headers.SHARE_SONG).equals(messageParts[0])){ 
            //shareSong();
        }
        else if(String.valueOf(headers.UPDATE_ACTIVE_USERS).equals(messageParts[0])){
            sendToUser(updateActiveUsers(0)); //int clients user_ID
        }
        else if(String.valueOf(headers.SHARED_SONGS_LIST).equals(messageParts[0])){
            sendToUser(SharedSongsList(0)); //int user_ID (could be user or friends ID)
        }
        else if(String.valueOf(headers.RECIEVE_SIMILAR_PROFILES).equals(messageParts[0])){
            sendToUser(similarProfiles(1)); //int clients user_ID
        }
        else if(String.valueOf(headers.UPDATE_MESSAGE_BOARD).equals(messageParts[0])){
            sendToUser(updateMessageBoard());
        }
        else if(String.valueOf(headers.ADD_TO_MESSAGE_BOARD).equals(messageParts[0])){
            addToMessageBoard(0 , "x", "y"); //userID, messageTitle, Message
        }
        else if(String.valueOf(headers.UPDATE_FRIEND_REQUESTS).equals(messageParts[0])){
            sendToUser(updateFriendRequests(0)); //int clients user_ID
        }
        else if(String.valueOf(headers.FRIENDS_LIST).equals(messageParts[0])){
            sendToUser(FriendsList(2)); //int clients user_ID
        }
        else if(String.valueOf(headers.SEND_FRIEND_REQUEST).equals(messageParts[0])){
            sendFriendRequest(0,0); // clients userID and user ID for user recieveing friend request
        }
        else if(String.valueOf(headers.CHANGE_FRIEND_REQUEST_STATUS).equals(messageParts[0])){
            updateFriendRequestStatus(0,0,true); //clientID, ID for user who sent clinet request, boolean friend request accepted/rejected
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
            //JSON jobject = new JSON();
            
            //use function which converts database resultset to JSON
            //jobject.fromResultSet(sendData); //fromResultSet function not working
            
            //convert JSONArray to strng
            //String message = jobject.toString();
            
           
            
            resultSetToJson test = new resultSetToJson();
             JSONArray jArray = new JSONArray();
             jArray = test.convertToJSON(sendData);
             String message = jArray.toString();
             
             
            
            //convert string to bytes ready to send to client
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, userIP, userPort);
            socket.send(sendPacket);
            
            testFunction(message);
            
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
    private void returningUser(int ID){
        //get resultset of userName data from the databse
        String value = "*";
        String table = "Profiles";
        String Condition = "User_ID = " + ID ;
        ResultSet result = dataChange.GetRecord(value, table, Condition);
        
        try{
            //if result isn't null then it must have found a user, therefor login is correct
            if(result != null){
                //send the user all their requried information
                sendToUser(result);
                
                //send a list of their friends to the user
                sendToUser(FriendsList(ID));
                
                //send list of recieved but not accepted/rejected friend requests
                sendToUser(updateFriendRequests(ID));
                
                //send list of messageboard items - ResultSet updateMessageBoard()
                sendToUser(updateMessageBoard());
                
                //create list of active users and send to clinet - ResultSet updateActiveUsers(int userID)
                sendToUser(updateActiveUsers(ID));
                
                
                //add the user to the members table - Stores their IP and logs them as an active user
                addIP(ID);
            }
        }catch(Exception e){System.err.println(e.getMessage());}
        
    }
    
    //adds new users IP to the active member sql table
    private void addIP(int ID){
        String insertInto = "Members";
        String cols = "(IPAddress, User_ID)";
        String vals = "(" + userIP + ", " + ID + ")";
        dataChange.InsertRecord(insertInto, cols, vals);
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
    private void shareSong(int userID, int songID, String songName, String Artist, Date ReleaseDate, String Album/*Blob Song*/){
        
    }
    
    //allows the client to send a friend request to another user who isn't already their friend
    private void sendFriendRequest(int clientUserID, int otherUsersID){
        String table = "Friends";
        String cols = "(User_ID, Friend_ID, Status_ID)";
        String vals = "(" + clientUserID + ", " + otherUsersID + ", 'Wait')";
        dataChange.InsertRecord(table, cols, vals);
    }
    
    //chanegs the status of a friend request (accept or reject)
    private void updateFriendRequestStatus(int clientUserID, int FriendRequestUserID, boolean accepted){
        String newStatus;
        if(accepted == true)
            newStatus = "con"; //connected
        else
            newStatus = "ref"; //refused
        
        String table = "Friends";
        String valChange = "Status_ID = '" + newStatus + "'";
        String condition = "User_ID = " + FriendRequestUserID + " AND Friend_ID = " + clientUserID;
         dataChange.UpdateRecord(table, valChange, condition);
    }
        
    //adds the users message to the message board for their friends to see
    private void addToMessageBoard(int userID, String title, String message){
        String table = "MessageBoard";
        String cols = "(User_ID, MessageTitle, Messages)";
        String vals = "(" + userID + ", '" + title + "', '" + message + "')";
        dataChange.InsertRecord(table, cols, vals);
    }
    
    //sends client list of all currently online users
    private ResultSet updateActiveUsers(int userID){
        String select = "Members.User_ID, Profiles.UserName";
        String from = "FROM Profiles RIGHT JOIN Members ON Profiles.User_ID = Members.User_ID";
        String where = "WHERE Profiles.User_ID <> " + userID;
        
        return dataChange.GetRecord(select, from, where);
    }
    
    //sends the client a list of songs shared by their specified friend or themself
    private ResultSet SharedSongsList(int UserID){        
        String select = "SharedSongs.SharedSongs_ID, SharedSong, SongName, Artist, ReleaseDate, Album";
        String from = "SharedSongs LEFT JOIN ProfileSharedSongs ON SharedSongs.SharedSongs_ID = ProfileSharedSongs.SharedSong_ID";
        String where = "ProfileSharedSongs.USER_ID = " + UserID;
        
        return dataChange.GetRecord(select, from, where);
    }
    
    //sends the client a list of all other profiles with similar music preferences
    private ResultSet similarProfiles(int userID){
        String sqlCmd = "select Profiles.User_ID, Profiles.UserName " +
                "from Profiles LEFT JOIN ProfileMusicPreferences ON Profiles.User_ID = ProfileMusicPreferences.User_ID " +
                "where ProfileMusicPreferences.User_ID <> " + userID +
                " AND ProfileMusicPreferences.MusicType_ID IN ( " +
                "select MusicType_ID " +
                "from ProfileMusicPreferences " +
                "where User_ID = " + userID + ")";
        
        return dataChange.GetCustomRecord(sqlCmd);
    }
    
    //sends an up-to-date message board to the clinet
    private ResultSet updateMessageBoard(){        
        String vals = "Profiles.User_ID, Profiles.UserName, MessageBoard.MessageTitle, MessageBoard.Message";
        String tables = "Profiles LEFT JOIN MessageBoard ON Profiles.User_ID = MessageBoard.User_ID";
        
        return dataChange.GetRecord(vals, tables, null);
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
    
    private void testFunction(String testing){
        String testing2 = null;
        JSON test = new JSON();
        testing2 = test.getWrappedChars(testing,"[","]");
        testing2 += "}";
        
        System.out.println(testing2);
        
        test.fromString(testing2);
        String name = test.get("USERNAME");
        
        int id = Integer.getInteger(test.get("USER_ID"));
        
        System.out.println(name + ", " + id);
    }
    
}
