/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/***************************************************************************************
*    Title: Java SecretKey Encode/Decode
*    Author: Jabari
*    Date: 13/04/18
*    Code version: 1
*    Availability: https://stackoverflow.com/questions/5355466/converting-secret-key-into-a-string-and-vice-versa
*
***************************************************************************************/

public class Security {
    
    public static String makeKey(String key){
        try{
            byte[] decodedKey = key.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(decodedKey);
            String res = Base64.getEncoder().encodeToString(md.digest());
            return res.substring(0,res.length() - 1);
        }catch(Exception ex){
            return "";
        }
    }
}
