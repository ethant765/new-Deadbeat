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
    
    //initalise requried functions for use throughout the class
    DataHeader headers;
    DBInteractions dataChange = new DBInteractions();
    
    //class required variab;es
    DatagramSocket socket;
    DatagramPacket packet;
    InetAddress userIP;
    int userPort;
    int clientUsersID; //set based on IPaddress when that is stored for active users table
    boolean opSuccess;
    
    //constructor
    UserThread(DatagramSocket sok, DatagramPacket pak){
        socket = sok;
        packet = pak;
        userIP = pak.getAddress();
        userPort = pak.getPort();
        
        //test for active user - if they are set their UserID, otherwise this will be set during their login process
        String table = "members";
        String select = "User_ID";
        String condition = "IPAddress = '" + userIP.toString().substring(1) + "'";
        ResultSet rs = dataChange.GetRecord(select, table, condition);
        
        try{
            if(rs != null){
                clientUsersID = rs.getInt("User_ID");
            }
        }catch(Exception e){Log.Throw(e);}
        
        opSuccess = true; // default to true and chenge to false later if an error occured during an operation
    }
    
    public UserThread(DataHeader headers){
        this.headers = headers;
    }
    
    public void run(){
        //get the data sent from the user on their connection to the server
        byte[] data = new byte[1024];
        data = packet.getData();
        String Message = new String(data); //add square brackets to help compatability with JSON class

        JSONAdapter recievedObject = new JSONAdapter();
        recievedObject.fromString(Message);//create a jsonObject from the string sent by the client
        String header = recievedObject.get(0).get("HEADER").get();
                
        //create the JSONobject which will be used to return data
        JSONAdapter returnObject = new JSONAdapter();
        
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
            else if(String.valueOf(headers.REMOVE_MESSAGE).equals(header)) removeMessage(recievedObject);
            else if(String.valueOf(headers.REMOVE_SONG).equals(header)) removeSong(recievedObject);
            else if(String.valueOf(headers.REMOVE_USER).equals(header)) removeUser();
            else if(String.valueOf(headers.ADD_MUSIC_PREFERENCES).equals(header)) addMusicPreferences(recievedObject);
            else{//handle error of incorrect or no header infromation in recieved string
                ErrorToUser(false);//false returns error to client
            }
        }catch(Exception e){Log.Throw(e);}
        //after the required function has been called, returned its data
        //send this data on to the function which handles returning the data to the client
        sendToUser(returnObject);
    }

    //function used to return data to the client
    private void sendToUser(JSONAdapter sendData){
        try{
            
            //add status header onto the json string to send to client
            sendData.get(0).add( new JSONProperty( "STATUS", String.valueOf(opSuccess), Tokenizer.TokenType.BOOLEAN ) );
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
        opSuccess = success;
    }
    
    
    
    
    //-----------------------------login and log off functions---------------------------------
    
    //recieves the data from the client for new user
    //and stores data in the database
    private void newUser(JSONAdapter obj){
        
        try{
            String ErrorTable = "Profiles";
            String ErrorSelect = "*";
            String ErrorWhere = "UserName = '" + obj.get(0).get("USERNAME").get() + "'";
            if(dataChange.GetRecord(ErrorSelect, ErrorTable, ErrorWhere) != null){//test to see if username surplied has been taken
                ErrorToUser(false);
            }
            else{
                //get data for the new user from the recieved json string object
                int id = newUserID();
                String userName = obj.get(0).get("USERNAME").get();
                String PlaceOfBirth = obj.get(0).get("PLACE_OF_BIRTH").get();
                Date DOB = new SimpleDateFormat("yyyy/MM/dd").parse(obj.get(0).get("DOB").get());
                Object imageFile = BinResource.lookup(obj.get(0).get("PROFILE_IMAGE").get()); 
                String Password = obj.get(0).get("PASSWORD").get();

                //insert the data into the database for the user
                String tableName = "Profiles";
                String tableCols = "(user_ID, UserName, PlaceOfBirth, DOB, ProfileImage, Password)";
                String Values = "(" + id + ", '" + userName + "', '" + PlaceOfBirth + "', '" + DOB + "', " + imageFile + ", '" + Password + "')";
                dataChange.InsertRecord(tableName, tableCols, Values);
                
                
                //add users music preference type
                 addMusicPreferences(obj);
                 
                
                //get user id in result set to send back to client
                String ReturnID = "User_ID";
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
    private void returningUser(JSONAdapter obj){
         try{
            String IDval = "User_ID";
            String IDtable = "Profiles";
            String IDcondition = "UserName = '" + obj.get(0).get("USERNAME").get() + "'";
            ResultSet idResult = dataChange.GetRecord(IDval, IDtable, IDcondition);
            int ID = idResult.getInt("User_ID");

            //get resultset of userName data from the databse
            String value = "*";
            String table = "Profiles";
            String Condition = "UserName = " + obj.get(0).get("USERNAME").get() + "' AND Password = " + Security.hash(obj.get(0).get("PASSWORD").get());//getHash(obj.getJSON().getString("PASSWORD"));
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
    private void loginInfoSend(JSONAdapter obj, ResultSet userData){
        try{
            //create a JSON object to store all the required information
            JSONAdapter returnData = new JSONAdapter();
            
            //create a result set list to use when creating a json string of it
            List<ResultSet> resultsHolder = new ArrayList<>();
            
            //add all the collated resultSets into a list to send them as 1 to the client
            //call all the function required for a new user sign in
            resultsHolder.add(userData);
            resultsHolder.add(FriendsList());
            resultsHolder.add(updateFriendRequests());
            resultsHolder.add(updateMessageBoard());
            resultsHolder.add(updateActiveUsers());
            
            
            //add the users IP address to the active members table
            addIP(obj.get(0).get("USER_ID").get());
            
            //turn all the resultSet list data into a JSON string ready to be sent
            returnData.fromMergedResultSets(resultsHolder);

            //send this data to the function which will return it to client
            sendToUser(returnData);
        }catch(Exception e){Log.Throw(e);}
    }
    
    //should remove users IPaddress and info from active users table (Members table)
    //removes any messages the user has put on the message board while
    private void logOff(){
        //call function to clear users message board messages as they log off
        removeUserMessageBoardMessages();
        
        //call function to remove user from active members table
        removeUserMembersTable();
    }
    
    
    
    
    //-------------------------------------------enum functions-----------------------------------------------------
    //allows the client to share a song
    private void shareSong(JSONAdapter obj){        
        try{
            //get data out of JSON string object
            int userID = clientUsersID;
            int songID = songIdGen();
            String songName = obj.get(0).get("SONG_NAME").get();
            String Artist = obj.get(0).get("ARTIS").get();
            Date ReleaseDate = new SimpleDateFormat("yyyy/MM/dd").parse(obj.get(0).get("RELEASE_DATE").get());
            String Album = obj.get(0).get("ALBUM").get();
            Object songFile = BinResource.lookup(obj.get(0).get("SONG").get());
            
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
    private void sendFriendRequest(JSONAdapter obj){
        int clientUserID = clientUsersID;
        int otherUsersID = obj.get(0).get("FRIEND_USER_ID").get();
        
        String table = "Friends";
        String where = "User_ID = " + clientUserID + " AND Friend_ID = " + otherUsersID;
        //test to ensure they havn't already been added to the friends table together
        if(dataChange.GetRecord("*", table, where) != null){ //if it isn't null the two users have been added together on this table before
            ErrorToUser(false);
        }
        else{
            
            String cols = "(User_ID, Friend_ID, Status_ID)";
            String vals = "(" + clientUserID + ", " + otherUsersID + ", 'Wait')";
            dataChange.InsertRecord(table, cols, vals);

            //test to ensure that the friend request is sent
            String select = "*";
            String whereTest = "User_ID = " + clientUserID + " AND Friend_ID = " + otherUsersID + " AND Status_ID = 'wait'";
            if(dataChange.GetRecord(select, table, whereTest) == null)//error occured during insert opperation
                ErrorToUser(false); //ensure user is sent status with error
        }
    }
    
    
    //chanegs the status of a friend request (accept or reject)
    private void updateFriendRequestStatus(JSONAdapter obj){
        int clientUserID = clientUsersID;
        int FriendRequestUserID = obj.get(0).get("FRIEND_USER_ID").get();
        boolean accepted = Boolean.valueOf(obj.get(0).get("ACCEPTED").get());

        String newStatus;
        if(accepted == true)
            newStatus = "con"; //connected
        else
            newStatus = "ref"; //refused
        
        String table = "Friends";
        String valChange = "Status_ID = '" + newStatus + "'";
        String condition = "User_ID = " + FriendRequestUserID + " AND Friend_ID = " + clientUserID;
        dataChange.UpdateRecord(table, valChange, condition);
        
        //test that the data has been updated
        String where = "User_ID = " + clientUserID + " AND Friend_ID = " + FriendRequestUserID + " AND Status_ID = '" + newStatus + "'";
        if(dataChange.GetRecord("*", table, where) == null)//if null error has occured somewhere in the sql data update
            ErrorToUser(false);
    }
        
    //adds the users message to the message board for their friends to see
    private void addToMessageBoard(JSONAdapter obj){
        int userID = clientUsersID;
        String title = obj.get(0).get("MESSAGE_TITLE").get();
        String message = obj.get(0).get("MESSAGE").get();
        
        String table = "MessageBoard";
        String TestWhere = "User_ID = " + userID + " AND MessageTitle = '" + title + "'";
        
        //test to ensure that the currernt user hasn't already got a message with that title
        //this is as the title & userID are used as a joint PK
        if(dataChange.GetRecord("*", table, TestWhere) != null){ //if not null then already exists
            ErrorToUser(false);
        }
        else{
            String cols = "(User_ID, MessageTitle, Messages)";
            String vals = "(" + userID + ", '" + title + "', '" + message + "')";
            dataChange.InsertRecord(table, cols, vals);

            //test to ensure that the message has been added
            //same where can be used as initial test in function
            if(dataChange.GetRecord("*", table, TestWhere) == null)//should now be a value there so null would mean error
                ErrorToUser(false);
        }
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
    private ResultSet SharedSongsList(JSONAdapter obj){
        int userID = obj.get(0).get("USER_ID").get();

        String select = "SharedSongs.SharedSongs_ID, SharedSong, SongName, Artist, ReleaseDate, Album";
        String from = "SharedSongs LEFT JOIN ProfileSharedSongs ON SharedSongs.SharedSongs_ID = ProfileSharedSongs.SharedSong_ID";
        String where = "ProfileSharedSongs.USER_ID = " + userID;
        
        return dataChange.GetRecord(select, from, where);
    }
    

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
        int userID = 2; //clientUsersID;
                
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
    
        //adds the users music preferences to their account
    private void addMusicPreferences(JSONAdapter obj){
        String preferenceID = obj.get(0).get("MUSIC_TYPE_ID").get();
        int id = clientUsersID;
        
        //the music preferences are already in the MusicTypes table and are static so that doesn't need changing
        //so link the users music types with their ID in the profileMusicPreference table
        
        //test to ensure the user hasn't already got that specific music preference
        String table = "ProfileMusicPreferences";
        String where = "User_ID = " + id + " AND MusicType_ID = '" + preferenceID + "'";
        if(dataChange.GetRecord("*", table, where) != null) //error as user already has this music preference set
            ErrorToUser(false);
        else{        
            //no error yet so proceed with inserting data
            String insertCols = "(User_ID, MusicType_ID)";
            String insertVals = "(" + id + ", '" + preferenceID + "')";
            
            dataChange.InsertRecord(table, insertCols, insertVals);
            
            //now data should have been inserted, test to ensure it has been
            if(dataChange.GetRecord("*", table, where) == null) //should now be a record there so error if null
                ErrorToUser(false);
        }
    }
        
    //removes the specified message from the message board for the client
    private void removeMessage(JSONAdapter obj){
        int userID = clientUsersID;
        String title = obj.get(0).get("MESSAGE_TITLE").get();
        
        //test to ensure there is a message with the current title by that user
        String tableName = "MessageBoard";
        String where = "User_ID = " + userID + " AND MessageTitle = '" + title + "'";
        if(dataChange.GetRecord("*", tableName, where) == null) //if there is no result then message doesn't exist
            ErrorToUser(false);
        else{
            dataChange.DeleteRecord(tableName, where);//delete table
            
            //test to ensure deletion successful
            if(dataChange.GetRecord("*", tableName, where) != null) //if deleted should be no result
                ErrorToUser(false);
        }
    }
    
    
    
    //----------------------------------------functions which remove user data from the database---------------------------------------------------
    
        //removes the specified song from the server for the client
    private void removeSong(JSONAdapter obj){
        int songID = obj.get(0).get("SONG_ID").get();
        
        //test to ensure that there is a song with that ID first
        String songTable = "SharedSongs";
        String whereTest = "SharedSongs_ID = " + songID;
        if(dataChange.GetRecord("*", songTable, whereTest) == null){ //if no song to begin with there has been an error somewhere
            ErrorToUser(false);
        }
        else{
            //remove song form song table, and profileSongTable
            String ProfileSongTable = "ProfileSharedSongs";
            String whereProfileSong = "SharedSong_ID = " + songID;
            dataChange.DeleteRecord(ProfileSongTable, whereProfileSong);
            String whereSong = "SharedSongs_ID = " + songID;
            dataChange.DeleteRecord(songTable, whereSong);
            
            //test that the database deletes have performed correctly
            ResultSet songResult = dataChange.GetRecord("*", songTable, whereSong);
            ResultSet profileSongResult = dataChange.GetRecord("*", ProfileSongTable, whereProfileSong);
            
            if(songResult != null || profileSongResult != null)//if either return data there has been a deleting error
                ErrorToUser(false);
        }
    }
    
    //removes/deletes the user account - removing all their data with them
    private void removeUser(){
        int userID = clientUsersID;
        
        //user needs removing from 'Friends', 'ProfileSharedSongs', 'SharedSongs', 'ProfileMusicPreferences', 'MessageBoard', 'Members' and 'Profiles' tables.
        removeUserFriendsTable();
        removeUserSongs();
        removeUserProfileMusicPreferencesTable();
        removeUserMessageBoardMessages();
        removeUserMembersTable();
        removeUserProfileTable();
        
        //test to ensure removal should have occured in each indervidual function
    }
    
    //removes the user from the ProfileSharedSongs and the SharedSongs tables
    private void removeUserSongs(){
        int id = clientUsersID;
        
        String profileTable = "ProfileSharedSongs";
        String songsTable = "SharedSongs";
        
        //get a list of all the users shared songs and remove them from the songs table
        //this will also check that the user has shared songs
        String conditionUserID = "USER_ID = " + id;
        ResultSet songs = dataChange.GetRecord("SharedSong_ID", profileTable, conditionUserID);
        try{
            while(songs.next()){
                String songID = songs.getString("SharedSong_ID");
                
                //use retrieved song ids to remove data from the SharedSongs table
                String conditionSong = "SharedSongs_ID = " + songID;
                dataChange.DeleteRecord(songsTable, conditionSong);
                
                //test to ensure the deletion
                if(dataChange.GetRecord("*", songsTable, conditionSong) != null)
                    ErrorToUser(false);
            }
        }catch(Exception e){Log.Throw(e);}
        
        //once all data is remove from SharedSongs clear profileSharedSongs
        dataChange.DeleteRecord(profileTable, conditionUserID);
        
        //test to ensure the user is no long in the table
        if(dataChange.GetRecord("*", profileTable, conditionUserID) != null)
            ErrorToUser(false);
    }
    
    //removes the user from the Profile table - account removal
    private void removeUserProfileTable(){
        int id = clientUsersID;
        
        String table = "Profiles";
        String condition = "User_ID = " + id;
        
        dataChange.DeleteRecord(table, condition);
        
        //test for removal
        if(dataChange.GetRecord("*", table, condition) != null)
            ErrorToUser(false);
    }
    
    //removes the user from the Friends table - account deletion
    private void removeUserFriendsTable(){
        int id = clientUsersID;
        
        String table = "Friends";
        String condition = "User_ID = " + id + " OR Friend_ID = " + id;
        
        //test to ensure the user has friends before removing
        if(dataChange.GetRecord("*", table, condition) != null){ 
            //if not null remove
            dataChange.DeleteRecord(table, condition);
            
            //test to ensure the deletion has worked correctly
            if(dataChange.GetRecord("*", table, condition) != null)//shouldnt be anything anymore
                ErrorToUser(false);
        }
    }
    
    //remove users profile music preferences - account removal
    private void removeUserProfileMusicPreferencesTable(){
        int id = clientUsersID;
        
        String table = "ProfileMusicPreferences";
        String condition = "User_ID = " + id;
        //test for user music preferences
        if(dataChange.GetRecord("*", table, condition) != null){
            //remove the user from table
            dataChange.DeleteRecord(table, condition);
            
            //test to ensure removal
            if(dataChange.GetRecord("*", table, condition) != null)//if still not null then an error has occured
                ErrorToUser(false);
        }
    }

    //remove the user for the members table - log off or account removal
    private void removeUserMembersTable(){
        
        //members table is the table which stores active members
        String tableToEdit = "Members";
        // remove logging off user based on their IPAddress as this will be unique per user, and set each time a user logs in
        String conditionForEdit = "IPAddress = " + userIP;
        
        dataChange.DeleteRecord(tableToEdit, conditionForEdit);
        
        //test to ensure that the user has been taken off the active members table correctly
        if(dataChange.GetRecord("*", tableToEdit, conditionForEdit) != null)//if not null then error removing user has occured
            ErrorToUser(false);
    }
    
    //when user logs off/deletes account removes any messages which they put on the message board
    private void removeUserMessageBoardMessages(){
        try{
            String removeTable = "MessageBoard";
            String removeCondition = "User_ID = " + clientUsersID;
            
            //before trying to remove ensure the user has messages on the message board
            if(dataChange.GetRecord("*", removeTable, removeCondition) != null) {//if not null user has messages
                //use users ID to remove any messages they have posted on the message board
                dataChange.DeleteRecord(removeTable, removeCondition);

                //test to ensure that the users message board items have been removed
                String select = "*";
                String where = "User_ID = " + clientUsersID;
                if(dataChange.GetRecord(select, removeTable, where) != null) //if it doesn't equal null then row deletion hasn't worked correctly
                    ErrorToUser(false);
            }
        }catch(Exception e){Log.Throw(e);}
    }
}
