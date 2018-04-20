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
    //Data Needed: "UserID,UserName,PlaceOfBirth,DOB,ProfileImage" UserID is PK integer in database
    //Data returned: "ActiveUsersList"
    LoginNewUser,
    
    //function: handles existing user logon
    //Data Needed: "UserID"
    //Data returned: 6 Strings:- "ActiveUsersList" + "FriendsList" + "FriendRequests" + "FriendsSharedSongs" + "UsersSharedSongs" + "MessageBoardMessages"
    LoginExistingUser,
    
    //function: handles user log off - removes users IPaddress and messages from message board
    //Data Needed: logoff header
    //Data returned: "Confirmation message"
    LogOff,//implimented
    
    
    
    //------------------Each one below should be function itself which is called based on header provided-----------------
    
    //function: allows the user to share a song
    //Data Needed: "Song_ID,SongName,Artist,ReleaseDate,Album,song"
    //Data returned: "confirmation message"
    ShareSong,
    
    //function: sends the client a list of all the currently online users
    //Data Needed: header
    //Data returned: 
    UpdateActiveUsers,
    
    //function: sends user list of songs they have shared
    //Data Needed: "userID"
    //Data returned: "numberOfSharedSongs" + ("SharedSongID,SongName,Artist,ReleaseDate,Album,Song" * number of shared songs)
    RecieveUsersSharedSongs,
    
    //function: returns songs shared by friends
    //Data Needed: "UserID"
    //Data returned: "NumOfSongs" + ("friendsID,SongID,SongName,Artist,ReleaseDate,Album,Song" * numOfSongs)
    RecieveFriendsSharedSongs,
    
    //function: returns a list of profiles with the same music preferences 
    //Data Needed: "UserID"
    //Data returned: "numOfProfiles" + ("SimilarProfileUserId,UserName" * numOfUsers)
    RecieveSimilarProfiles,
    
    //function: sends the client a list of messages by current user and their friends on the messageBoard
    //Data Needed: "UserID"
    //Data returned: "numOfMessages" + ("messageByUserName,MessageTitle,Message" * numOfMessages)
    UpdateMessageBoard, 
    
    //function: adds users message to messageBoard
    //Data Needed: "UserID,MessageTitle,Message"
    //Data returned: **UpdateMessageBoard**
    AddToMessageBoard, 
    
    //function: sends the clinet a list of all their non-accepted/rejected friend requests
    //Data Needed: "UserID"
    //Data returned: "numOfFriendRequests" + ("FriendRequestUserID,UserName" * numOfFriendRequests)
    UpdateFriendRequests,
    
    //function: sends a list of all friends to the user
    //Data Needed: "UserID"
    //Data returned: "numOfFriends" + ("FriendUserID,FriendUserName" * numOfFriends)
    FriendsList,
    
    //function: sends a friend request to user
    //Data Needed: "ClientUserID,FriendRequestRecipientUserID"
    //Data returned: "confirmation of message"
    SendFriendRequest, 
    
    //function: changes the status of a friend request
    //Data Needed: "ClientUserID,FriendUserID,NewStatusID"
    //Data returned: "confirmation of change"
    ChangeFriendRequestStatus; 
}
