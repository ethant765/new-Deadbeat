/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Composers;

/**
 *
 * @author darylcecile
 */
public class Pair {
    public String key;
    public Object value;
    public Pair(String k, Object v){
        key = k;
        value = v;
    }
    
    public String getKey(){
        return key;
    }
    
    public Object getValue(){
        return value;
    }
}
