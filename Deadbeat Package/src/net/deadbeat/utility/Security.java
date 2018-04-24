/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author darylcecile
 */
public class Security {
    
    // MUST NOT CHANGE
    public final static int HASH_SEED = 59;
    public final static int HASH_MULTIPLIER = 97;
    
    public static String hash(String input){
        
        int h = HASH_SEED;
        for ( char c : input.toCharArray() ){
            h = h*HASH_MULTIPLIER + c;
        }
        
        int n = (int) ( Math.ceil(32 / String.valueOf(h).length()) );
        String r = String.join("", Collections.nCopies( n+1 , String.valueOf(h) ));
        
        return r.substring(0, 32);
    }
}
