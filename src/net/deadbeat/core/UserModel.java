
package net.deadbeat.core;

import java.util.UUID;

/**
 *
 * @author darylcecile
 */
public class UserModel {
    
    private final String _handle;
    private final String _email;
    private final String _userId;
    
    public UserModel(String emailAddress,String handle){
        this._email = emailAddress;
        this._handle = handle;
        this._userId = UUID.randomUUID().toString();
        
        getServerInformation(()->{
            //NOTE server info ready
        });
    }
    
    private void getServerInformation(Runnable callback){
        // Run on later when system is not busy, to prevent UI hanging
        TaskController.runAfter(()->{
            //TODO download/connect to server and fetch user info (if exists) or create info
            
            //INFO when complete
            callback.run();
        });
    }
    
}
