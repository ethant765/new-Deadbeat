/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import java.net.*;
import java.sql.ResultSet;
import java.util.*;
import java.text.*;

// uses jar
import net.deadbeat.utility.*;

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
        String Message = '[' + new String(data)+']';

        JSON stringObject = new JSON();
        stringObject.fromString(Message);
        String header = stringObject.getJSON().<String>val("enum");
        
        if(String.valueOf(headers.LOGIN_NEW_USER).equals(header)) newUser(stringObject);
        else if(String.valueOf(headers.LOGIN_EXISTING_USER).equals(header)) returningUser(stringObject); //int clients User_ID
        else if(String.valueOf(headers.LOG_OFF).equals(header)) logOff();
        else if(String.valueOf(headers.SHARE_SONG).equals(header)) shareSong(stringObject);
        else if(String.valueOf(headers.UPDATE_ACTIVE_USERS).equals(header)) sendToUser(updateActiveUsers(stringObject)); //int clients user_ID
        else if(String.valueOf(headers.SHARED_SONGS_LIST).equals(header)) sendToUser(SharedSongsList(stringObject)); //int user_ID (could be user or friends ID)
        else if(String.valueOf(headers.RECIEVE_SIMILAR_PROFILES).equals(header)) sendToUser(similarProfiles(stringObject)); //int clients user_ID
        else if(String.valueOf(headers.UPDATE_MESSAGE_BOARD).equals(header)) sendToUser(updateMessageBoard());
        else if(String.valueOf(headers.ADD_TO_MESSAGE_BOARD).equals(header)) addToMessageBoard(stringObject); //userID, messageTitle, Message
        else if(String.valueOf(headers.UPDATE_FRIEND_REQUESTS).equals(header)) sendToUser(updateFriendRequests(stringObject)); //int clients user_ID
        else if(String.valueOf(headers.FRIENDS_LIST).equals(header)) sendToUser(FriendsList(stringObject)); //int clients user_ID
        else if(String.valueOf(headers.SEND_FRIEND_REQUEST).equals(header)) sendFriendRequest(stringObject); // clients userID and user ID for user recieveing friend request
        else if(String.valueOf(headers.CHANGE_FRIEND_REQUEST_STATUS).equals(header)) updateFriendRequestStatus(stringObject); //clientID, ID for user who sent clinet request, boolean friend request accepted/rejected
        else{
            //error
        }
    }    

    //function used to return data to the client
    private void sendToUser(ResultSet sendData){
        try{
            //JSON jobject = new JSON();
            
            //use function which converts database resultset to JSON
            //jobject.fromResultSet(sendData); //fromResultSet function not working
            
            //convert JSONArray to strng
            //String message = jobject.toString();
            
           
            
             /* test = new resultSetToJson();
             JSONArray jArray = new JSONArray();
             jArray = test.convertToJSON(sendData);
             String message = jArray.toString();*/
             
             
            
            //convert string to bytes ready to send to client
            /*byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, userIP, userPort);
            socket.send(sendPacket);*/
            
            //testFunction(message);
            
            
            
            JSON jsonString = new JSON();
            jsonString.fromResultSet(sendData);
            Log.Out(jsonString.getString("USERNAME"));
            
        }catch(Exception e){Log.Throw(e);}
    }

    
    
    
    
    //-----------------------------login and log off functions---------------------------------
    
    //recieves the data from the client for new user
    //and stores data in the database
    private void newUser(JSON obj){
        String tableName = "Profiles";
        String tableCols = "(user_ID, UserName, PlaceOfBirth, DOB, ProfileImage)";
        //String Values = "('" + userID + "', '" + loginData[2] + "', '" + loginData[3] + "', '" + loginData[4] + "', " + loginData[5] + ")";
       
    }
    
    
     //recieves the data from the client for returning user
    //checks user credentials
   //adds IPaddress to active users table in DB
    private void returningUser(JSON obj){
        int ID = 0;
        
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
                sendToUser(FriendsList(obj));
                
                //send list of recieved but not accepted/rejected friend requests
                sendToUser(updateFriendRequests(obj));
                
                //send list of messageboard items - ResultSet updateMessageBoard()
                sendToUser(updateMessageBoard());
                
                //create list of active users and send to clinet - ResultSet updateActiveUsers(int userID)
                sendToUser(updateActiveUsers(obj));
                
                //add the user to the members table - Stores their IP and logs them as an active user
                addIP(ID);
            }
        }catch(Exception e){Log.Throw(e);}
        
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
        //uses IPadress so needs calling before clearing users IP from active members table
        removeUserMessageBoardMessages();
        
        //members table is the table which stores active members
        String tableToEdit = "Members";
        // remove logging off user based on their IPAddress as this will be unique per user, and set each time a user logs in
        String conditionForEdit = "IPAddress = " + userIP; 
        
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
        }catch(Exception e){Log.Throw(e);}
    }
    
    
    
    
    
    
    //-------------------------------------------enum functions-----------------------------------------------------
    //allows the client to share a song
    private void shareSong(JSON obj){        
        try{
            //get data out of JSON string object
            int userID = obj.getJSON().getInt("USER_ID");
            int songID = songIdGen();
            String songName = obj.getJSON().getString("SONG_NAME");
            String Artist = obj.getJSON().getString("ARTIS");
            Date ReleaseDate = new SimpleDateFormat("yyyy/MM/dd").parse(obj.getJSON().getString("RELEASE_DATE"));
            String Album = obj.getJSON().getString("ALBUM");
            Object songFile = BinResource.lookup(obj.getJSON().getString("SONG"));
            
            //insert data into SharedSong table
            String SongTable = "SharedSongs";
            String SongCols = "(SharedSongs_ID, SharedSong, SongName, Artist, ReleaseDate, Album)";
            String SongVals = "(" + songID + ", " + songFile + ", '" + songName + "', '" + Artist + "', '" + ReleaseDate + "', '" + Album + "')";
            dataChange.InsertRecord(SongTable, SongCols, SongVals);
            
            //insert data into ProfileSharedSongs table
            String PSStable = "ProfileSharedSongs";
            String PSScols = "(USER_ID, SharedSongs_ID)";
            String PPSvals = "(" + userID + ", " + songID + ")";
            dataChange.InsertRecord(PSStable, PSScols, PPSvals);
        } catch(Exception e){System.err.println(e.getMessage());}
    }
    //generates a new unique ID for songs being added
    private int songIdGen(){
        String Vals = "SharedSong_ID";
        String from = "SharedSongs";
        ResultSet IDs = dataChange.GetRecord(Vals, from, null);
        
        int newID = 0; // start at 0
        
        try{
            while(IDs.next()){
                newID++;//increment for each item
            }
        }catch(Exception e){System.err.println(e.getMessage());}
        return newID += 1; //add 1 to be a higher number than any existing IDs
    }
    
    
    //allows the client to send a friend request to another user who isn't already their friend
    private void sendFriendRequest(JSON obj){        
        int clientUserID = obj.getJSON().getInt("USER_ID");
        int otherUsersID = obj.getJSON().getInt("FRIEND_USER_ID");
        
        
        String table = "Friends";
        String cols = "(User_ID, Friend_ID, Status_ID)";
        String vals = "(" + clientUserID + ", " + otherUsersID + ", 'Wait')";
        dataChange.InsertRecord(table, cols, vals);
    }
    
    
    //chanegs the status of a friend request (accept or reject)
    private void updateFriendRequestStatus(JSON obj){
        int clientUserID = obj.getJSON().getInt("USER_ID");
        int FriendRequestUserID = obj.getJSON().getInt("FRIEND_USER_ID");
        boolean accepted = Boolean.valueOf(obj.getJSON().getString("ACCEPTED"));

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
    private void addToMessageBoard(JSON obj){
        int userID = obj.getJSON().getInt("USER_ID");
        String title = obj.getJSON().getString("MESSAGE_TITLE");
        String message = obj.getJSON().getString("MESSAGE");
        
        
        String table = "MessageBoard";
        String cols = "(User_ID, MessageTitle, Messages)";
        String vals = "(" + userID + ", '" + title + "', '" + message + "')";
        dataChange.InsertRecord(table, cols, vals);
    }
    
    //sends client list of all currently online users
    private ResultSet updateActiveUsers(JSON obj){
        int userID = obj.getJSON().getInt("USER_ID");
        
        String select = "Members.User_ID, Profiles.UserName";
        String from = "FROM Profiles RIGHT JOIN Members ON Profiles.User_ID = Members.User_ID";
        String where = "WHERE Profiles.User_ID <> " + userID;
        
        return dataChange.GetRecord(select, from, where);
    }
    
    //sends the client a list of songs shared by their specified friend or themself
    //send the function a user_ID not nesseserally the clients - (friends IDs are also sent to the client with friends list)
    private ResultSet SharedSongsList(JSON obj){
        int userID = obj.getJSON().getInt("USER_ID");

        String select = "SharedSongs.SharedSongs_ID, SharedSong, SongName, Artist, ReleaseDate, Album";
        String from = "SharedSongs LEFT JOIN ProfileSharedSongs ON SharedSongs.SharedSongs_ID = ProfileSharedSongs.SharedSong_ID";
        String where = "ProfileSharedSongs.USER_ID = " + userID;
        
        return dataChange.GetRecord(select, from, where);
    }
    
    //sends the client a list of all other profiles with similar music preferences
    private ResultSet similarProfiles(JSON obj){
        int userID = obj.getJSON().getInt("USER_ID");
        
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
    private ResultSet updateFriendRequests(JSON obj){
        int userID = obj.getJSON().getInt("USER_ID");
        
        String vals = "Profiles.User_ID, Profiles.UserName";
        String tables = "Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.User_ID";
        String condition = "Friends.Friend_ID =" + userID + "AND Status_ID = 'Wait'";
        
        return dataChange.GetRecord(vals, tables, condition);
    }
    
    
    //sends a list to the client of their friends - (friendsID and friends userName)
    private ResultSet FriendsList(JSON obj){
        int userID = obj.getJSON().getInt("USER_ID");
                
       String sqlCmd = "(SELECT Profiles.user_ID, Profiles.UserName " +
               "FROM Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.User_ID " +
               "WHERE Friends.User_ID <> " + userID +
               " AND Friends.Status_ID = 'con') " +
               "UNION " +
               "(SELECT Profiles.user_ID, Profiles.UserName " +
               "FROM Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.Friend_ID " +
               "WHERE Friends.Friend_ID <> " + userID +
               " AND Friends.Status_ID = 'con')";
       
       
       ResultSet results = dataChange.GetCustomRecord(sqlCmd);
       /*try{
           while(results.next()){
               String stringHolder = results.getString("USERNAME");
               int idHolder = results.getInt("USER_ID");
               System.out.println(stringHolder + ",    " + idHolder);
           }
       }catch(Exception e){}*/

        return results;
    }
    
    
    /* REMOVE BEFORE HANDIN
    private void testFunction(String testing){
        String testing2 = null;
        JSON test = new JSON();
        
        testing2 = Tokenizer.getWrappedChars(testing,"[","]");
        testing2 += "}";
        
        System.out.println(testing2);
        
        
        test.fromString(testing2);
        
        String name = ((String[])test.get("USERNAME"))[0];
        
       //id = (int)test.get("USER_ID");
        System.out.println(name);
        //System.out.println(id);
        
    }*/
        
    //used to split recieved strings into their indervidual tokens
    /*private String[] SplitString(String passedString){
        StringTokenizer tokens = new StringTokenizer(passedString, ",");
        int numOfTokens = tokens.countTokens();
        String holder[] = new String[numOfTokens];
        
        for(int i = 0; i < numOfTokens; i++){
            holder[i] = tokens.nextToken().trim();
        }
        return holder;
    }*/
}
