/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author darylcecile
 */
public class Tokenizer {
    
    public enum TokenType{
        SQ_BRACKET,
        RN_BRACKET,
        CL_BRACKET,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        INT,
        STRING,
        BOOLEAN,
        LIST,
        JSON,
        NULL,
        COLON,
        COMMA,
        KEY_VALUE,
        UNDEFINED;
    }
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';
    
    public static List<String> Tokenize(String input){
        List<String> tokens = new ArrayList<>();
        TokenType stringOpen = null;
        String current = "";
        
        //clean up parameter before using
        input = input.trim();
        if ( input.charAt(0) == '{' ) tokens.add( ("{") );
        if ( input.charAt(0) == '[' ) tokens.add( ("[") );
        
        String scannedString = input.substring(1, input.length()-1);
        
        for (int i = 1; i < input.length()-1; i++){
            
            char c = input.charAt(i);
            current += c;
            
            if ( Tokenizer.isQuote(c) && stringOpen == Tokenizer.quoteVal(c) ){
                stringOpen = null;
                tokens.add((current));
                current = "";
            }
            else if ( isQuote(c) && stringOpen == null ){
                stringOpen = quoteVal(c);
            }
            else if ( c == ':' && stringOpen == null ){
                if ( !"".equals(current) ) tokens.add((current));
                tokens.add((""+c));
                current = "";
            }
            else if ( c == ',' && stringOpen == null ){
                if ( !"".equals(current) ) {
                    if ( !current.endsWith(""+c) ){
                        tokens.add((current));
                    }
                    else{
                        tokens.add((current.substring(0, current.length()-1)));
                    }
                }                
                if ( !(""+c).equals(current) ) tokens.add((""+c));
                current = "";
            }
            else if ( c == '{' || c == '[' ){
                current = "";
                Pair<String,Integer> enclosedContent = Tokenizer.getEnclosedContent(scannedString, c, i);
                tokens.add(( enclosedContent.getKey() ) );
                i = enclosedContent.getValue();
            }
            
            
        }
        
        if ( !"".equals(current) ) tokens.add((current));
        
        if ( input.charAt(0) == '{' ) tokens.add(("}"));
        if ( input.charAt(0) == '[' ) tokens.add(("]"));
        
        List<String> tokenSanitized = new ArrayList<>();
        for (int i=0; i<tokens.size();i++){
            String c = tokens.get(i).trim();
            if (c.length() > 0) tokenSanitized.add( c );
        }
        
        return tokenSanitized;
    }
    
    private static Pair<String,Integer> getEnclosedContent(String input,char openingChar,int startPos){
        String soFarRes = "" + openingChar;
        TokenType stringOpen = null;
        int LSBC = (openingChar == '[' ? 1 : 0);
        int LCBC = (openingChar == '{' ? 1 : 0);
        if (startPos >= input.length()) startPos = 0;
        int pos = startPos;
        
        for ( int i = startPos; i < input.length(); i++ ){
            
            char cp = input.charAt(i);
            pos = i;
            
            if ( LSBC == LCBC && LCBC == 0 ){
                break;
            }
            else if ( isQuote(cp) && stringOpen == null ){
                stringOpen = quoteVal(cp);
            }
            else if ( isQuote(cp) && stringOpen == quoteVal(cp) ){
                stringOpen = null;
            }
            else if ( cp == '[' && stringOpen == null ){
                LSBC ++;
            }
            else if ( cp == ']' && stringOpen == null ){
                LSBC --;
            }
            else if ( cp == '{' && stringOpen == null ){
                LCBC ++;
            }
            else if ( cp == '}' && stringOpen == null ){
                LCBC --;
            }
            
            soFarRes += cp;
            
        }
        
        return (new Pair<>(soFarRes, pos));
    }
    
    public static boolean isQuote(char c){
        return ( c == SINGLE_QUOTE || c == DOUBLE_QUOTE );
    }
    
    private static TokenType quoteVal(char c){
        return ( c == SINGLE_QUOTE ? TokenType.SINGLE_QUOTE : TokenType.DOUBLE_QUOTE );
    }
    
    public static boolean isNumber(String input){
        boolean res = true;
        for (int i=0; i< input.length(); i++){
            char c = input.charAt(i);
            res = res && (( i == 0 && c == '-') || Character.isDigit(c));
        }
        return res;
    }
    
    public static String removeQuotation(String content){
        content = content.trim();
        if ( isQuote(content.charAt(0)) ){
            return getWrappedChars(content, ""+content.charAt(0),""+content.charAt(0) );
        }
        else return content;
    }
    
    public static String getWrappedChars(String content){
        return getWrappedChars(content,"","");
    }
    
    public static String getWrappedChars(String content,String fromFirst,String toLast){
        content = content.trim();
        if ((fromFirst == null ? toLast == null : fromFirst.equals(toLast)) && "".equals(fromFirst)){
            return content;
        }
        else if ( !content.startsWith(fromFirst) || !content.endsWith(toLast) ){
            return null;
        }
        else{
            return content.substring(1, content.length() - 1);
        }
    }
}
