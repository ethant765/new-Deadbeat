/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

/**
 *
 * @author darylcecile
 * @param <I> Identifier
 * @param <K> Key
 * @param <V> Value
 */
public class Triplet<I,K,V> extends Pair{
    
    public I identifier;
    
    public Triplet(I i,K k, V v) {
        super(k, v);
        identifier = i;
    }
    
    public I getIdentifier(){
        return identifier;
    }
    
}
