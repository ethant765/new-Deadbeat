/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Composers;

import java.util.List;
import java.util.UUID;
import javafx.util.Pair;

/**
 *
 * @author darylcecile
 */
public class BinResource {
    private static List< Pair<String,Object> > container;
    
    public static String reference(Object item){
        
        String r_id = UUID.randomUUID().toString();
        
        container.add(new Pair<>(r_id,item));
        
        return r_id;
        
    }
    
    public static Object ofRef(String reference){
        Object result = null;
        for (Pair<String, Object> item : container) {
            if ( item.getKey() == null ? reference == null : item.getKey().equals(reference) ){
                result = item.getValue();
            }
        }
        return result;
    }
}
