/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package datagramclient;
import java.io.*;
import java.net.*;
import net.deadbeat.utility.*;

/**
 *
 * @author n0688008
 */
public class DatagramClient {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        BufferedReader readIn = new BufferedReader(new InputStreamReader(System.in));
        
        String usersInputtedString = "{'HEADER': 'UPDATE_MESSAGE_BOARD', 'USERNAME': 'TestUser2'}";
        String uppercaseReturn = null;
        
        try{
            DatagramSocket client = new DatagramSocket();
            
            
            byte[] data = usersInputtedString.getBytes();
            InetAddress addr = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, addr, 9090);
            client.send(sendPacket);
            
            
            
            byte[] backData = new byte[1024];
            DatagramPacket recievePacket = new DatagramPacket(backData, backData.length);
            client.receive(recievePacket);
            backData = recievePacket.getData();
            String upperMessage = new String(backData);
            System.out.println(upperMessage);

            
        }catch(IOException e){
            System.err.println("Error! - " + e.getMessage());
        }    
    }
    
}


/*
//function: handles new user creation then passes to logon to log new user on
    //Data Needed: USERNAME, PASSWORD, PROFILE_IMAGE, DOB, PLACE_OF_BIRTH, MUSIC_TYPE_ID
    //Data returned: USER_ID, OTHER_LOGIN_INFO
    LOGIN_NEW_USER,
    //"{'HEADER': 'LOGIN_NEW_USER', 'USERNAME': 'EthanThompson', 'PASSWORD':'" + Security.hash("Password1") + "', 'DOB': '1997/02/10', 'PLACE_OF_BIRTH': 'Derby', 'MUSIC_TYPE_ID': 'Blues'}"; - DOB FORMATTING NEEDS FIXING/ IMAGEFILE NEEDS TESTING
    //function: handles existing user logon
    //Data Needed: USERNAME, PASSWORD
    //Data returned: ResultSet(USER_ID, USERNAME, PLACEOFBIRTH, DOB, PROFILEIMAGE), OTHER_LOGIN_INFO
    LOGIN_EXISTING_USER,
    //"{'HEADER': 'LOGIN_EXISTING_USER', 'USERNAME': 'EthanThompson', 'PASSWORD': '" + Security.hash("Password1") + "'}";
    
    OTHER_LOGIN_INFO:
    ResultSet(FRIENDS_LIST), ResultSet(UPDATE_FRIEND_REQUESTS), ResultSet(UPDATE_MESSAGE_BOARD), ResultSet(UPDATE_ACTIVE_USERS)
    - other function calls.
    
    
    //function: handles user log off - removes users IPaddress and messages from message board
    //Data Needed: N/A
    //Data returned: confirmation message
    LOG_OFF,
    
    
    
    //------------------Each one below should be function itself which is called based on header provided-----------------
    
    //function: allows the user to share a song
    //Data Needed: SONG_NAME, ARTIST, RELEASE_DATE, ALBUM, SONG
    //Data returned: N/A
    SHARE_SONG,
    
    //function: sends the client a list of all the currently online users
    //Data Needed: N/A
    //Data returned: ResultSet(USER_ID, USERNAME)
    UPDATE_ACTIVE_USERS,
    

    
    //function: returns a list of profiles with the same music preferences - requries clients id
    //Data Needed: N/A
    //Data returned: ResultSet(USER_ID, USERNAME)
    RECIEVE_SIMILAR_PROFILES,
//{'HEADER': 'RECIEVE_SIMILAR_PROFILES', 'USERNAME': 'TestUser2'}
    


        //function: sends the client the up-to-date message board
    //Data Needed: N/A
    //Data returned: ResultSet(USER_ID, USERNAME, MESSAGETITLE, MESSAGE) - per users message
    UPDATE_MESSAGE_BOARD, 
    //{'HEADER': 'UPDATE_MESSAGE_BOARD', 'USERNAME': 'TestUser2'}

    
    


    


    //function: client sends a user_ID (could be that of a friend or themselves) then function returns all that users shared songs
    //Data Needed: USER_ID
    //Data returned: ResultSet(SHAREDSONGS_ID, SHAREDSONG, SONGNAME, ARTIST, RELEASEDATE, ALBUM)
    SHARED_SONGS_LIST,
//{'HEADER': 'SHARED_SONGS_LIST', 'USERNAME': 'TestUser2', 'USER_ID': '1'}
    //function: adds users message to messageBoard
    //Data Needed: MESSAGE
    //Data returned: N/A
    ADD_TO_MESSAGE_BOARD, 
//{'HEADER': 'ADD_TO_MESSAGE_BOARD', 'USERNAME': 'TestUser2', 'MESSAGE': 'dfudssjbdsvnjdsvbjdsvn'}

    
    //function: sends a friend request to user
    //Data Needed: FRIEND_USER_ID - (friends user ID can be gotten as returned value from similar profiles or active users)
    //Data returned: N/A
    SEND_FRIEND_REQUEST, 
    //{'HEADER': 'SEND_FRIEND_REQUEST', 'USERNAME': 'TestUser2', 'FRIEND_USER_ID': '3'}
//"{'HEADER': 'SEND_FRIEND_REQUEST', 'USERNAME': 'TestUser2', 'FRIEND_USER_ID': '1'}"
    //function: sends the clinet a list of all their non-accepted/rejected friend requests
    //Data Needed: N/A
    //Data returned: ResultSet(USER_ID, USERNAME) - per friend Request
    UPDATE_FRIEND_REQUESTS,
//{'HEADER': 'UPDATE_FRIEND_REQUESTS', 'USERNAME': 'TestUser2'}

    //function: sets the users music profile preferences
    //data Needed: MUSIC_TYPE_ID
    //Data returned: N/A
    ADD_MUSIC_PREFERENCES;
    //"{'HEADER': 'ADD_MUSIC_PREFERENCES', 'USERNAME': 'TestUser2', 'MUSIC_TYPE_ID': 'Blues'}";


 //function: allows the user to remove a message they have added from the message board
 //Data Needed: MESSAGE_TITLE
 //Data returned: N/A
    REMOVE_MESSAGE,
//"{'HEADER': 'REMOVE_MESSAGE', 'USERNAME': 'TestUser2', 'MESSAGE_TITLE': 'title2'}";

//function: removes the users profile - delete account
    //Data Needed: N/A
    //Data returned: N/A
    REMOVE_USER,
//"{'HEADER': 'REMOVE_USER', 'USERNAME': 'test'}";

    //function: sends a list of all friends to the user
    //Data Needed: N/A
    //Data returned: ResultSet(USER_ID, USERNAME) - per friend
    FRIENDS_LIST,
    //"{'HEADER': 'FRIENDS_LIST', 'USERNAME': 'TestUser2'}";


















    //function: changes the status of a friend request
    //Data Needed: FRIEND_USER_ID, ACCEPTED(boolean accepted 1/0 rejected)
    //Data returned: N/A
    CHANGE_FRIEND_REQUEST_STATUS,
    //{'HEADER': 'CHANGE_FRIEND_REQUEST_STATUS', 'USERNAME': 'TestUser2', 'FRIEND_USER_ID': 1, 'ACCEPTED': '1'}
    //{'HEADER': 'CHANGE_FRIEND_REQUEST_STATUS', 'USERNAME': 'TestUser2', 'FRIEND_USER_ID': 4, 'ACCEPTED': '0'}

//function: allows the user to remove a song they have shared from the database
    //Data Needed: SONG_ID
    //Data returned: N/A
    REMOVE_SONG,
//"{'HEADER': 'REMOVE_SONG', 'USERNAME': 'TestUser2', 'SONG_ID': '1'}";
*/