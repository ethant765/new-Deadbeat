/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

import java.util.List;


/**
 *
 * @author darylcecile
 */
public class User {
    
    private final UserModel _userModel;
    private final String _userId;
    private String _username = "";
    private List<String> _friendIdList;
    private Boolean _authenticated = false;
    
    public User(String emailAddress,String password){
        _userModel = new UserModel(emailAddress,password);
        _userId = _userModel.getUserId();
        
        // set to email address until it is fetched from server;
        _username = emailAddress;
    }
    
    public void logIn(ITask<User> onComplete,ITask<String> onError){
        _userModel.getServerInformation((res)->{
            try{
                _username = (String)res[0];
                _friendIdList = (List<String>)res[1];

                _authenticated = true;
            }
            catch (Exception ex){
                onError.Run("InfoMalformed: "+ex.getMessage());
            }
            onComplete.Run(this);
        });
    }
    
    public Boolean isAuthenticated(){
        return _authenticated;
    }
    
    public String getUsername(){
        return _username;
    }
    
    public String getUserId(){
        return _userModel.getUserId();
    }
    
    public List<User> getFriends(){
        // TODO get list of friends
        return null;
    }
}
