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
    login,
    logOff,
    updateInfo, //update messageboard info, update active users list, recieve new friend request notifications, recieve friends request acceptance notifications
    recieveFriendsSharedSongs,//request a friends shared song from, song returned to user
    RecieveSimilarProfiles, //list of users with similar music preferences sent back to the user
    addToMessageBoard, //add users message to messageboard for friends to see
    sendFriendRequest, //user sends friend request to another active user           - requre current users userName AND userName of user being sent request
    ChangeFriendRequestStatus; //change friend request status                       - requires, current users userName, userName of other user, new status (connected or refused)
}
