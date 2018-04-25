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
    //Data Returned: 
    RECEIVE_MESSAGES,
    
    //function: allows the user to send a message to a specified user
    //Data Required: MESSAGE_TEXT, PAYLOAD, RECEIVING_USER_ID
    //Data Returned: N/A
    SEND_MESSAGE,
}
