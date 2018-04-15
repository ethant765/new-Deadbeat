/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.core;

/**
 *
 * @author darylcecile
 */
public class MusicSession {
    
    private State _currentState = State.STOPPED;
    
    public MusicSession(){}
    
    public static enum State{
        PLAYING,
        STOPPED,
        PAUSED;
    }
    
    public void Play(){
        _currentState = State.PLAYING;
    }
    
    public void Stop(){
        _currentState = State.STOPPED;
    }
    
    public void Pause(){
        _currentState = State.PAUSED;
    }
    
    public State getState(){
        return _currentState;
    }
}
