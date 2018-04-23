/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import net.deadbeat.utility.Tokenizer.TokenType;

/**
 *
 * @author darylcecile
 */
public class Token {
    public TokenType type;
    public String serialValue = "";
    public String serialName = "";
    
    public Token(String name,String val){
        serialValue = val.trim();
        
        if ( !name.equals("") ) {
            type = TokenType.KEY_VALUE;
        } 
        else if ( serialValue.equalsIgnoreCase("false") || serialValue.equalsIgnoreCase("true") ){
            type = TokenType.BOOLEAN;
        }
        else if ( Tokenizer.isNumber(serialValue) ){
            type = TokenType.INT;
        }
        else if ( Tokenizer.isQuote( serialValue.charAt(0) ) && Tokenizer.isQuote( serialValue.charAt(serialValue.length() - 1) ) ){
            type = TokenType.STRING;
        }
        else if ( serialValue == "]" || serialValue == "[" ){
            type = TokenType.SQ_BRACKET;
        }
        else if ( serialValue.startsWith("[") || serialValue.endsWith("]") ){
            type = TokenType.LIST;
        }
        else if ( "null".equals(serialValue) ){
            type = TokenType.NULL;
        }
        else if ( serialValue.startsWith("{") && serialValue.endsWith("}") ){
            type = TokenType.JSON;
        }
        else if ( ":".equals(serialValue) ){
            type = TokenType.COLON;
        }
        else if ( ",".equals(serialValue) ){
            type = TokenType.COMMA;
        }
        else{
            type = TokenType.NULL;
        }
        serialName = name;
    }
    
    public Token(String val){
        this("",val);
    }
    
    public <T> T get(){
        if ( this.type == TokenType.JSON ){
            JSON o = new JSON();
            o.fromString(this.serialValue);
            return (T)o;
        }
        else if ( this.type == TokenType.LIST ){
            JSON o = new JSON();
            o.fromString(this.serialValue.substring(1,this.serialValue.length()-1));
            return (T)o;
        }
        else if ( this.type == TokenType.KEY_VALUE ){
            if ( this.serialValue.startsWith("[") && this.serialValue.endsWith("]") ){
                JSON o = new JSON();
                o.fromString(this.serialValue.substring(1,this.serialValue.length()-1));
                return (T)o;
            }
            else{
                return (T)this.serialValue;
            }
        }
        else{
            return (T)this.serialValue;
        }
    }
    
    @Override
    public String toString(){
        if ( get().getClass() == JSON.class ){
            return get().toString();
        }
        else {
            return get();
        }
    }
}
