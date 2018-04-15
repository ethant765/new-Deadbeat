/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

import net.deadbeat.utility.Log;

/**
 *
 * @author darylcecile
 */
public final class Session {
    
    public String SessionId;
    public User user;
    
    public Session(User user,Task<String> onError){
        this.user = user;
        Log.Out("Attempting Authentication...");
        attemptLogin(onError);
    }
    
    public void attemptLogin(Task<String> onError){
        user.logIn((users)->{
            if (users[0].isAuthenticated()){
                SessionId = java.util.UUID.randomUUID().toString() +"_"+ users[0].getUserId();
                Log.Out("Starting session",SessionId);
                Log.Out("User Logged in as",users[0].getUsername());
            }
            else{
                onError.Run("User not authenticated");
            }
        },onError);
    }
    
}
