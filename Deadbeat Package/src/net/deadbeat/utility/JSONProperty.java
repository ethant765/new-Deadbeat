/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

/**
 *
 * @author darylcecile
 */
public class JSONProperty {
    
    public final Tokenizer.TokenType data_type;
    public final String Key;
    
    private final String Value;
    
    public JSONProperty(String key,String value,Tokenizer.TokenType dataType){
        Key = key;
        Value = value;
        
        if (dataType == Tokenizer.TokenType.UNDEFINED){
            String serialValue = value.trim(); Tokenizer.TokenType type;
            if ( serialValue.equalsIgnoreCase("false") || serialValue.equalsIgnoreCase("true") ){
                type = Tokenizer.TokenType.BOOLEAN;
            }
            else if ( Tokenizer.isQuote( serialValue.charAt(0) ) && Tokenizer.isQuote( serialValue.charAt(serialValue.length() - 1) ) ){
                type = Tokenizer.TokenType.STRING;
            }
            else if ( Tokenizer.isNumber(serialValue) ){
                type = Tokenizer.TokenType.INT;
            }
            else if ( serialValue.startsWith("[") || serialValue.endsWith("]") ){
                type = Tokenizer.TokenType.LIST;
            }
            else if ( "null".equals(serialValue) ){
                type = Tokenizer.TokenType.NULL;
            }
            else if ( serialValue.startsWith("{") && serialValue.endsWith("}") ){
                type = Tokenizer.TokenType.JSON;
            }
            else if ( ":".equals(serialValue) ){
                type = Tokenizer.TokenType.COLON;
            }
            else if ( ",".equals(serialValue) ){
                type = Tokenizer.TokenType.COMMA;
            }
            else{
                type = Tokenizer.TokenType.UNDEFINED;
            }
            data_type = type;
        }
        else{
            data_type = dataType;
        }
    }
    
    public JSONProperty(String key,String value){
        this(key,value,Tokenizer.TokenType.UNDEFINED);
    }
    
    public <T> T get(){
        return (T)Value;
    }
    
}
