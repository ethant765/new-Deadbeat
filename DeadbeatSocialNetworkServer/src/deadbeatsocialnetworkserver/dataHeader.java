/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

/**
 *
 * @author Ethan
 */
public enum dataHeader {
    //function: handles new user creation
    //Data Needed: "UserID,UserName,PlaceOfBirth,DOB,ProfileImage" - UserID is PK integer in database
    //Data returned: UPDATE_ACTIVE_USERS function
    LOGIN_NEW_USER,
    
    //function: handles existing user logon
    //Data Needed: USERNAME, PASSWORD
    //Data returned: ResultSet(User_ID, UserName, PlaceOFBirth, DOB, ProfileImage), ResultSet(FRIENDS_LIST), ResultSet(UPDATE_FRIEND_REQUESTS), ResultSet(UPDATE_MESSAGE_BOARD), ResultSet(UPDATE_ACTIVE_USERS)
    LOGIN_EXISTING_USER,
    
    //function: handles user log off - removes users IPaddress and messages from message board
    //Data Needed: N/A
    //Data returned: N/A
    LOG_OFF,
    
    
    
    //------------------Each one below should be function itself which is called based on header provided-----------------
    
    //function: allows the user to share a song
    //Data Needed: USER_ID, SONG_NAME, ARTIST, RELEASE_DATE, ALBUM, SONG
    //Data returned: N/A
    SHARE_SONG,
    
    //function: sends the client a list of all the currently online users
    //Data Needed: USER_ID
    //Data returned: ResultSet(User_ID, UserName)
    UPDATE_ACTIVE_USERS,
    
    //function: client sends a user_ID (could be that of a friend or themselves) then function returns all that users shared songs
    //Data Needed: USER_ID
    //Data returned: ResultSet(ShardSongs_ID, SharedSong, SongName, Artist, ReleaseDate, Album)
    SHARED_SONGS_LIST,
    
    //function: returns a list of profiles with the same music preferences - requries clients id
    //Data Needed: USER_ID
    //Data returned: ResultSet(User_ID, UserName)
    RECIEVE_SIMILAR_PROFILES,
    
    //function: sends the client the up-to-date message board
    //Data Needed: N/A
    //Data returned: ResultSet(User_ID, UserName, MessageTitle, Message) - per users message
    UPDATE_MESSAGE_BOARD, 
    
    //function: adds users message to messageBoard
    //Data Needed: USER_ID, MESSAGE_TITLE, MESSAGE
    //Data returned: N/A
    ADD_TO_MESSAGE_BOARD, 
    
    //function: sends the clinet a list of all their non-accepted/rejected friend requests
    //Data Needed: USER_ID
    //Data returned: ResultSet(User_ID, UserName) - per friend Request
    UPDATE_FRIEND_REQUESTS,
    
    //function: sends a list of all friends to the user
    //Data Needed: USER_ID
    //Data returned: ResultSet(User_ID, UserName) - per friend
    FRIENDS_LIST,
    
    //function: sends a friend request to user
    //Data Needed: USER_ID, FRIEND_USER_ID - (friends user ID can be gotten as returned value from similar profiles or active users)
    //Data returned: N/A
    SEND_FRIEND_REQUEST, 
    
    //function: changes the status of a friend request
    //Data Needed: USER_ID, FRIEND_USER_ID, ACCEPTED(boolean accepted 1/0 rejected)
    //Data returned: N/A
    CHANGE_FRIEND_REQUEST_STATUS; 
}
