/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.awt.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author darylcecile
 */
public class JSONObject extends ArrayList<JSONProperty> {
    
    public JSONObject(JSONProperty... items){
        addAll(Arrays.asList(items));
    }
    
    @Override
    public JSONProperty get(int index){
        return super.get(index);
    }
    
    public JSONProperty get(String search){
        for (JSONProperty j : this){
            if ( j.Key.equalsIgnoreCase(search) ) return j;
        }
        return null;
    }
    
    public boolean add(String key,String value){
        return super.add( new JSONProperty(key, value) );
    }
    
    @Override
    public boolean add(JSONProperty property){
        return super.add(property);
    }
    
}
