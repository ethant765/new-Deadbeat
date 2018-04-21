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
    //Data Needed: "UserID"
    //Data returned: sends client info from functions: UPDATE_ACTIVE_USERS + FRIENDS_LIST + UPDATE_FRIEND_REQUESTS + RECIEVEFRIENDSSHAREDSONGS + RECIEVEUSERSSHAREDSONGS + UPDATE_MESSAGE_BOARD
    LOGIN_EXISTING_USER,
    
    //function: handles user log off - removes users IPaddress and messages from message board
    //Data Needed: logoff header
    //Data returned: 
    LOG_OFF,//implimented
    
    
    
    //------------------Each one below should be function itself which is called based on header provided-----------------
    
    //function: allows the user to share a song
    //Data Needed: "Song_ID,SongName,Artist,ReleaseDate,Album,song"
    //Data returned: "confirmation message"
    SHARE_SONG,
    
    //function: sends the client a list of all the currently online users
    //Data Needed: header
    //Data returned: List of userIDs, and userNames for online users
    UPDATE_ACTIVE_USERS,
    
    //function: client sends a user_ID (could be that of a friend or themselves) then function returns all that users shared songs
    //Data Needed: "userID" - friends_ID or clients_ID
    //Data returned: "numberOfSharedSongs" + ("SharedSongID,SongName,Artist,ReleaseDate,Album,Song" * number of shared songs)
    SHARED_SONGS_LIST,
    
    //function: returns a list of profiles with the same music preferences 
    //Data Needed: "UserID"
    //Data returned: "numOfProfiles" + ("SimilarProfileUserId,UserName" * numOfUsers)
    RECIEVE_SIMILAR_PROFILES,
    
    //function: sends the client the up-to-date message board
    //Data Needed: "UserID"
    //Data returned: "numOfMessages" + ("messageByUserName,MessageTitle,Message" * numOfMessages)
    UPDATE_MESSAGE_BOARD, 
    
    //function: adds users message to messageBoard
    //Data Needed: "UserID,MessageTitle,Message"
    //Data returned: **UpdateMessageBoard**
    ADD_TO_MESSAGE_BOARD, 
    
    //function: sends the clinet a list of all their non-accepted/rejected friend requests
    //Data Needed: "UserID"
    //Data returned: "numOfFriendRequests" + ("FriendRequestUserID,UserName" * numOfFriendRequests)
    UPDATE_FRIEND_REQUESTS,
    
    //function: sends a list of all friends to the user
    //Data Needed: "UserID"
    //Data returned: "numOfFriends" + ("FriendUserID,FriendUserName" * numOfFriends)
    FRIENDS_LIST,
    
    //function: sends a friend request to user
    //Data Needed: "ClientUserID,FriendRequestRecipientUserID"
    //Data returned: "confirmation of message"
    SEND_FRIEND_REQUEST, 
    
    //function: changes the status of a friend request
    //Data Needed: "ClientUserID,FriendUserID,NewStatusID"
    //Data returned: "confirmation of change"
    CHANGE_FRIEND_REQUEST_STATUS; 
}
