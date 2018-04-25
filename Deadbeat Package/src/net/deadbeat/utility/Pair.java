/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

/**
 *
 * @author darylcecile
 * @param <K> key type (string is default)
 * @param <V> value type
 */
public class Pair<K,V> {
    public K key;
    public V value;
    public <T> Pair(K k, V v){
        key = k;
        value = v;
    }
    
    public K getKey(){
        return (K)key;
    }
    
    public V getValue(){
        return (V)value;
    }
}
