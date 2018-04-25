/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.json;

import java.util.List;
import net.deadbeat.utility.BinResource;
import net.deadbeat.utility.Tokenizer;
import net.deadbeat.utility.Tokenizer.TokenType;

/**
 *
 * @author darylcecile
 */
public class JSONProperty {
    
    public final Tokenizer.TokenType data_type;
    public final String Key;
    
    private final String Value;
    
    public JSONProperty(String key,String value,Tokenizer.TokenType dataType){
        Key = Tokenizer.removeQuotation(key);
        Value = value;
        
        if (dataType == Tokenizer.TokenType.UNDEFINED){
            String serialValue = value.trim(); Tokenizer.TokenType type;
            if ( serialValue.startsWith("BLOB::") ){
                type = Tokenizer.TokenType.BLOB;
            }
            else if ( serialValue.equalsIgnoreCase("false") || serialValue.equalsIgnoreCase("true") ){
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
    
    /**
     * Get the preserved value of the property.
     * <p>
     * When a data type {@code T} is defined, the returned value will be cast into the request data type
     * 
     * @param <T> Data type (Default Object)
     * @return 
     */
    public <T> T get(){
        switch (data_type) {
            case STRING:
                return (T)Tokenizer.removeQuotation( Value );
            case LIST:
                return (T)Tokenizer.getWrappedChars(Value, "[", "]").split(",");
            case INT:
                return (T)Value;
            case BOOLEAN:
                return (T)Boolean.valueOf(Value);
            case BLOB:
                return (T)BinResource.lookup(Value);
            default:
                return (T)Value;
        }
    }
    
    public String getValue(){
        
        if ( TokenType.NULL == data_type ){
            return null;
        }
        else switch (data_type) {
            case STRING:
                return Tokenizer.removeQuotation( Value );
            default:
                return Value;
        }
        
    }
    
}
