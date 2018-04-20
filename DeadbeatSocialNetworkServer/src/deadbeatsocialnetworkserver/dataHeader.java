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
    //Data returned: UPDATEACTIVEUSERS function
    LOGINNEWUSER,
    
    //function: handles existing user logon
    //Data Needed: "UserID"
    //Data returned: sends client info from functions: UPDATEACTIVEUSERS + FRIENDSLIST + UPDATEFRIENDREQUESTS + RECIEVEFRIENDSSHAREDSONGS + RECIEVEUSERSSHAREDSONGS + UPDATEMESSAGEBOARD
    LOGINEXISTINGUSER,
    
    //function: handles user log off - removes users IPaddress and messages from message board
    //Data Needed: logoff header
    //Data returned: 
    LOGOFF,//implimented
    
    
    
    //------------------Each one below should be function itself which is called based on header provided-----------------
    
    //function: allows the user to share a song
    //Data Needed: "Song_ID,SongName,Artist,ReleaseDate,Album,song"
    //Data returned: "confirmation message"
    SHARESONG,
    
    //function: sends the client a list of all the currently online users
    //Data Needed: header
    //Data returned: List of userIDs, and userNames for online users
    UPDATEACTIVEUSERS,
    
    //function: sends user list of songs they have shared
    //Data Needed: "userID"
    //Data returned: "numberOfSharedSongs" + ("SharedSongID,SongName,Artist,ReleaseDate,Album,Song" * number of shared songs)
    RECIEVEUSERSSHAREDSONGS,
    
    //function: returns songs shared by friends
    //Data Needed: "UserID"
    //Data returned: "NumOfSongs" + ("friendsID,SongID,SongName,Artist,ReleaseDate,Album,Song" * numOfSongs)
    RECIEVEFRIENDSSHAREDSONGS,
    
    //function: returns a list of profiles with the same music preferences 
    //Data Needed: "UserID"
    //Data returned: "numOfProfiles" + ("SimilarProfileUserId,UserName" * numOfUsers)
    RECIEVESIMILARPROFILES,
    
    //function: sends the client a list of messages by current user and their friends on the messageBoard
    //Data Needed: "UserID"
    //Data returned: "numOfMessages" + ("messageByUserName,MessageTitle,Message" * numOfMessages)
    UPDATEMESSAGEBOARD, 
    
    //function: adds users message to messageBoard
    //Data Needed: "UserID,MessageTitle,Message"
    //Data returned: **UpdateMessageBoard**
    ADDTOMESSAGEBOARD, 
    
    //function: sends the clinet a list of all their non-accepted/rejected friend requests
    //Data Needed: "UserID"
    //Data returned: "numOfFriendRequests" + ("FriendRequestUserID,UserName" * numOfFriendRequests)
    UPDATEFRIENDREQUESTS,
    
    //function: sends a list of all friends to the user
    //Data Needed: "UserID"
    //Data returned: "numOfFriends" + ("FriendUserID,FriendUserName" * numOfFriends)
    FRIENDSLIST,
    
    //function: sends a friend request to user
    //Data Needed: "ClientUserID,FriendRequestRecipientUserID"
    //Data returned: "confirmation of message"
    SENDFRIENDREQUEST, 
    
    //function: changes the status of a friend request
    //Data Needed: "ClientUserID,FriendUserID,NewStatusID"
    //Data returned: "confirmation of change"
    CHANGEFRIENDREQUESTSTATUS; 
}
