/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import net.deadbeat.utility.Log;
import net.deadbeat.utility.Pair;
import net.deadbeat.utility.Triplet;

/**
 *
 * @author darylcecile
 */
public class EventAdapter {
    
    public static List< Pair<String , List<Pair<Boolean,ITask>> > > globalEvents = new ArrayList<>();
    
    public static boolean On(String eventName ,ITask handler){
        return On(eventName,handler,false);
    }
    
    public static boolean On(String eventName, ITask handler, Boolean once){
        Log.Out("Added listener on component");
        return get(eventName).add( new Pair<>(once,handler) );
    }
    
    private static List<Pair<Boolean,ITask>> get(String e){
        
        for (Pair<String, List<Pair<Boolean,ITask>> > p : globalEvents){
            if ( p.getKey().equalsIgnoreCase(e) ) return p.getValue();
        }
        
        List<Pair<Boolean,ITask>> l = new ArrayList<>();
        globalEvents.add( new Pair<>( e , l ) );
        return l;
    }
    
    public static void Trigger(String eventName){
        
        int listenerTouchCount = 0;
        
        List<Pair<Boolean,ITask>> e = get(eventName);
        
        for (int i = e.size()-1; i > -1; i--){
            Pair<Boolean,ITask> p = e.get(i);
            p.getValue().Run();
            listenerTouchCount ++;
            if ( p.getKey() == true ) e.remove(i);
        }
        
        Log.Out("Triggered Global Event:",eventName.toUpperCase(),"affected",listenerTouchCount,"listener(s)");
        
    }
    
}
