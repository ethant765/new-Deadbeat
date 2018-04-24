
package net.deadbeat.core;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.deadbeat.core.Task;
import net.deadbeat.utility.Security;

/**
 *
 * @author darylcecile
 */
public class UserModel {
    private final String _email;
    private final String _userId;
    
    public UserModel(String emailAddress,String password){
        this._email = emailAddress;
        this._userId = generateUserId(password);
    }
    
    public String getUserId(){
        return this._userId;
    }
    
    private String generateUserId(String password){
        byte[] raw;
        String res = "";
        try {
            // Use this function to check and generate user id to connect with server
            byte[] raw_email_bytes = this._email.getBytes("UTF-8");
            byte[] raw_password_bytes = password.getBytes("UTF-8");
            
            raw = new byte[raw_email_bytes.length + raw_password_bytes.length];
                        
            System.arraycopy( raw_email_bytes , 0, raw, 0, raw_email_bytes.length);            
            System.arraycopy( raw_password_bytes , 0, raw, raw_email_bytes.length, raw_password_bytes.length);
            
            res = Security.hash(new String(raw,"UTF-8"));
        } catch (Exception ex) {
            Logger.getLogger(UserModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }
    
    public void getServerInformation(Task<Object> task){
        // Run on later when system is not busy, to prevent UI hanging
        TaskController.runAfter(()->{
            //TODO download/connect to server and fetch user info (if exists) or create info
            
            //USE _userId
            
            //INFO when complete
                task.Run("Test","Me");
        });
    }
    
}
