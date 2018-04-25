/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

/**
 *
 * @author Ethan
 */
public enum dataHeaders {
    //function header enums for the chat server
    //ALL DATA SENT WILL REQUIRE INFROMATION HEADER
    //ALL FUNCTIONS WILL RETURN STATUS: (boolean - success/failure)
    
    //function: sends the user all the messages they have recieved, specifying who sent these messages to them
    //Data Required: N/A
    //Data Returned:2 resultSets merged 1(Message_ID, SENDER_ID, CONTENT), 2( Message_ID, Sender_ID, Content, Payload) - splt as payload is optional to send so might be null

    //message_ID will increase incrementally with each sent message so display to the user, lowest first to get correct send order
    RECEIVE_MESSAGES,
    
    //function: allows the user to send a message to a specified user
    //Data Required: MESSAGE_TEXT, PAYLOAD, RECEIVING_USER_ID
    //Data Returned: N/A
    SEND_MESSAGE,
}
