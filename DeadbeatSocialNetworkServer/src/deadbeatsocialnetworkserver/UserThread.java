/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import net.deadbeat.utility.*;
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
    int clientUsersID; //set based on IPaddress when that is stored for active users table
    boolean opSuccsess;
    
    UserThread(DatagramSocket sok, DatagramPacket pak){
        socket = sok;
        packet = pak;
        userIP = pak.getAddress();
        userPort = pak.getPort();
        
        //test for active user - if they are set their UserID, otherwise this will be set during their login process
        String table = "members";
        String select = "User_ID";
        String condition = "IPAddress = " + userIP;
        ResultSet rs = dataChange.GetRecord(select, table, condition);
        
        try{
            if(rs != null){
                clientUsersID = rs.getInt("User_ID");
            }
        }catch(Exception e){Log.Throw(e);}
        
        opSuccsess = true; // default to true and chenge to false later if an error occured during an operation
    }
    
    public UserThread(dataHeader headers){
        this.headers = headers;
    }
    
    public void run(){
        //get the data sent from the user on their connection to the server
        byte[] data = new byte[1024];
        data = packet.getData();
        String Message = '[' + new String(data)+']'; //add square brackets to help compatability with JSON class

        JSON recievedObject = new JSON();
        recievedObject.fromString(Message);//create a jsonObject from the string sent by the client
        String header = recievedObject.getJSON().getString("HEADER"); //retrieve the header from the string
        
        //create the JSONobject which will be used to return data
        JSON returnObject = new JSON();
        
        try{
            //used header infromation to direct the client to the function it requires
            if(String.valueOf(headers.LOGIN_NEW_USER).equals(header)) newUser(recievedObject);
            else if(String.valueOf(headers.LOGIN_EXISTING_USER).equals(header)) returningUser(recievedObject); 
            else if(String.valueOf(headers.LOG_OFF).equals(header)) logOff();
            else if(String.valueOf(headers.SHARE_SONG).equals(header)) shareSong(recievedObject);
            else if(String.valueOf(headers.UPDATE_ACTIVE_USERS).equals(header)) returnObject.fromResultSet(updateActiveUsers()); 
            else if(String.valueOf(headers.SHARED_SONGS_LIST).equals(header)) returnObject.fromResultSet(SharedSongsList(recievedObject));
            else if(String.valueOf(headers.RECIEVE_SIMILAR_PROFILES).equals(header)) returnObject.fromResultSet(similarProfiles()); 
            else if(String.valueOf(headers.UPDATE_MESSAGE_BOARD).equals(header)) returnObject.fromResultSet(updateMessageBoard());
            else if(String.valueOf(headers.ADD_TO_MESSAGE_BOARD).equals(header)) addToMessageBoard(recievedObject); 
            else if(String.valueOf(headers.UPDATE_FRIEND_REQUESTS).equals(header)) returnObject.fromResultSet(updateFriendRequests()); 
            else if(String.valueOf(headers.FRIENDS_LIST).equals(header)) returnObject.fromResultSet(FriendsList()); 
            else if(String.valueOf(headers.SEND_FRIEND_REQUEST).equals(header)) sendFriendRequest(recievedObject); 
            else if(String.valueOf(headers.CHANGE_FRIEND_REQUEST_STATUS).equals(header)) updateFriendRequestStatus(recievedObject); 
            else{//handle error of incorrect or no header infromation in recieved string
                ErrorToUser(false);//false returns error to client
            }
        }catch(Exception e){Log.Throw(e);}
        //after the required function has been called, returned its data
        //send this data on to the function which handles returning the data to the client
        sendToUser(returnObject);
    }

    //function used to return data to the client
    private void sendToUser(JSON sendData){
        try{
            
            //add status header onto the json string to send to client
            sendData.getJSON().set("STATUS", opSuccsess);
            
            //convert json object to string then string to bytes ready to send to client
            String message = sendData.toString();
            
            //send message back to the client
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, userIP, userPort);
            socket.send(sendPacket);
            
        }catch(Exception e){Log.Throw(e);}
    }
    
    //if there is an error performing an operation, send user a message expressing this error
    private void ErrorToUser(boolean success){
        //if function is called with a boolean false - then error has occured and change error boolean at top of class to false
        opSuccsess = success;
    }
    
    
    
    
    //-----------------------------login and log off functions---------------------------------
    
    //recieves the data from the client for new user
    //and stores data in the database
    private void newUser(JSON obj){
        
        try{
            String ErrorTable = "Profiles";
            String ErrorSelect = "*";
            String ErrorWhere = "UserName = '" + obj.getJSON().getString("USERNAME") + "'";
            if(dataChange.GetRecord(ErrorSelect, ErrorTable, ErrorWhere) != null){//test to see if username surplied has been taken
                ErrorToUser(false);
            }
            else{
                //get data for the new user from the recieved json string object
                int id = newUserID();
                String userName = obj.getJSON().getString("USERNAME");
                String PlaceOfBirth = obj.getJSON().getString("PLACE_OF_BIRTH");
                Date DOB = new SimpleDateFormat("yyyy/MM/dd").parse(obj.getJSON().getString("DOB"));
                Object imageFile = BinResource.lookup(obj.getJSON().getString("PROFILE_IMAGE")); 
                String Password = obj.getJSON().getString("PASSWORD");

                //insert the data into the database for the user
                String tableName = "Profiles";
                String tableCols = "(user_ID, UserName, PlaceOfBirth, DOB, ProfileImage, Password)";
                String Values = "(" + id + ", '" + userName + "', '" + PlaceOfBirth + "', '" + DOB + "', " + imageFile + ", '" + Password + "')";
                dataChange.InsertRecord(tableName, tableCols, Values);

                //get user id in result set to send back to client
                String ReturnID = "*";
                //use same table as insert operation above
                String condition = "UserName = " + userName;

                //call function to send all other required login infromation back to the client
                loginInfoSend(obj, dataChange.GetRecord(ReturnID, tableName, condition));
            }
        }catch(Exception e){Log.Throw(e);}
    }
    //get a new unique User_ID for the user
    private int newUserID(){        
        int newUserID = 0;
        String testIDselect, testIDtable, testIDwhere;
        try{
            do{
                newUserID++;
                //test to ensure this is a new id
                testIDselect = "User_ID";
                testIDtable = "Profiles";
                testIDwhere = "User_ID = " + newUserID;//test for an existing ID
            }while(dataChange.GetRecord(testIDselect, testIDtable, testIDwhere) != null);
        }catch(Exception e){Log.Throw(e);}
        //return the unique ID
        return newUserID;
    }
    
     //recieves the data from the client for returning user
    //checks user credentials
   //adds IPaddress to active users table in DB
    private void returningUser(JSON obj){
         try{
            String IDval = "User_ID";
            String IDtable = "Profiles";
            String IDcondition = "UserName = '" + obj.getJSON().getString("USERNAME") + "'";
            ResultSet idResult = dataChange.GetRecord(IDval, IDtable, IDcondition);
            int ID = idResult.getInt("User_ID");

            //get resultset of userName data from the databse
            String value = "*";
            String table = "Profiles";
            String Condition = "UserName = " + obj.getJSON().getString("USERNAME") + "' AND Password = " + Security.hash(obj.getJSON().getString("PASSWORD"));//getHash(obj.getJSON().getString("PASSWORD"));
            ResultSet result = dataChange.GetRecord(value, table, Condition);
       
            //if result isn't null then it must have found a user, and their password has matched
            if(result != null){
                //call function which handles sending remaining login info
                loginInfoSend(obj, result); //will send the client all their required info
            }
            else{
                ErrorToUser(false);
            }
        }catch(Exception e){Log.Throw(e);}
        
    }
    //adds new users IP to the active member sql table
    private void addIP(int ID){
        
        String insertInto = "Members";
        String cols = "(IPAddress, User_ID)";
        String vals = "(" + userIP + ", " + ID + ")";
        dataChange.InsertRecord(insertInto, cols, vals);
        
        //after data is entered into the database check to endure it has been added correctly
        String select = "*";
        String where = "IPAddress = " + userIP;
        if(dataChange.GetRecord(select, insertInto, where) == null) //if null data has not entered into the database correctly
            ErrorToUser(false);
        else
            clientUsersID = ID; //data is entered properly so store the users IP
    }
    
    //when a new user creates an accout or an exisint user signs in, this is info relayed back to client
    private void loginInfoSend(JSON obj, ResultSet userData){
        try{
            //create a JSON object to store all the required information
            JSON returnData = new JSON();

            //send a list of their friends to the user
            FriendsList();

            //send list of recieved but not accepted/rejected friend requests
            updateFriendRequests();

            //send list of messageboard items - ResultSet updateMessageBoard()
            updateMessageBoard();

            //create list of active users and send to clinet - ResultSet updateActiveUsers(int userID)
            updateActiveUsers();
            
            //add the users IP address to the active members table
            addIP(obj.getJSON().getInt("USER_ID"));

            //send this data to the function which will return it to client
            sendToUser(returnData);
        }catch(Exception e){Log.Throw(e);}
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
        
        //test to ensure that the user has been taken off the active members table correctly
        if(dataChange.GetRecord("*", tableToEdit, conditionForEdit) != null)//if not null then error removing user has occured
            ErrorToUser(false);
    }
    //when user logs off removes any messages which they put on the message board
    private void removeUserMessageBoardMessages(){
        try{
            //use users ID to remove any messages they have posted on the message board
            String removeTable = "MessageBoard";
            String removeCondition = "User_ID = " + clientUsersID;
            dataChange.DeleteRecord(removeTable, removeCondition);
            
            //test to ensure that the users message board items have been removed
            String select = "*";
            String where = "User_ID = " + clientUsersID;
            if(dataChange.GetRecord(select, removeTable, where) != null) //if it doesn't equal null then row deletion hasn't worked correctly
                ErrorToUser(false);
        }catch(Exception e){Log.Throw(e);}
    }
    
    
    
    
    
    
    //-------------------------------------------enum functions-----------------------------------------------------
    //allows the client to share a song
    private void shareSong(JSON obj){        
        try{
            //get data out of JSON string object
            int userID = clientUsersID;
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
            
            //test to ensure the data has been added into both different tables
            String selectTest = "*";
            String fromTest = "SharedSongs, ProfileSharedSongs";
            String whereTest = "SharedSongs.SharedSongs_ID = " + songID + ", ProfileSharedSongs.SharedSong_ID = " + songID;
            if(dataChange.GetRecord(selectTest, fromTest, whereTest) == null) //if it = null then error has occured
                ErrorToUser(false);//fasle = operation failed
            
        } catch(Exception e){Log.Throw(e);}
    }
    //generates a new unique ID for songs being added
    private int songIdGen(){
        //loop IDs until a valid one has been found then return the first valid one
        int newID = 0;
        String selectID, fromID,whereID;
        try{
            do{
                selectID = "SharedSongs_ID";
                fromID = "SharedSongs";
                whereID = "SharedSongs_ID = " + newID;
                newID++;
            }while(dataChange.GetRecord(selectID, fromID, whereID) != null);
            
        }catch(Exception e){Log.Throw(e);}
        return newID;
    }
    
    
    //allows the client to send a friend request to another user who isn't already their friend
    private void sendFriendRequest(JSON obj){
        int clientUserID = clientUsersID;
        int otherUsersID = obj.getJSON().getInt("FRIEND_USER_ID");
        
        String table = "Friends";
        String cols = "(User_ID, Friend_ID, Status_ID)";
        String vals = "(" + clientUserID + ", " + otherUsersID + ", 'Wait')";
        dataChange.InsertRecord(table, cols, vals);
        
        //test to ensure that the 
    }
    
    
    //chanegs the status of a friend request (accept or reject)
    private void updateFriendRequestStatus(JSON obj){
        int clientUserID = clientUsersID;
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
        int userID = clientUsersID;
        String title = obj.getJSON().getString("MESSAGE_TITLE");
        String message = obj.getJSON().getString("MESSAGE");
        
        
        String table = "MessageBoard";
        String cols = "(User_ID, MessageTitle, Messages)";
        String vals = "(" + userID + ", '" + title + "', '" + message + "')";
        dataChange.InsertRecord(table, cols, vals);
    }
    
    //sends client list of all currently online users
    private ResultSet updateActiveUsers(){
        int userID = clientUsersID;
        
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
    private ResultSet similarProfiles(){
        int userID = clientUsersID;
        
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
    private ResultSet updateFriendRequests(){
        int userID = clientUsersID;
        
        String vals = "Profiles.User_ID, Profiles.UserName";
        String tables = "Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.User_ID";
        String condition = "Friends.Friend_ID =" + userID + "AND Status_ID = 'Wait'";
        
        return dataChange.GetRecord(vals, tables, condition);
    }
    
    
    //sends a list to the client of their friends - (friendsID and friends userName)
    private ResultSet FriendsList(){
        int userID = clientUsersID;
                
       String sqlCmd = "(SELECT Profiles.user_ID, Profiles.UserName " +
               "FROM Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.User_ID " +
               "WHERE Friends.User_ID <> " + userID +
               " AND Friends.Status_ID = 'con') " +
               "UNION " +
               "(SELECT Profiles.user_ID, Profiles.UserName " +
               "FROM Profiles LEFT JOIN Friends ON Profiles.User_ID = Friends.Friend_ID " +
               "WHERE Friends.Friend_ID <> " + userID +
               " AND Friends.Status_ID = 'con')";
       
        return dataChange.GetCustomRecord(sqlCmd);
    }
}
