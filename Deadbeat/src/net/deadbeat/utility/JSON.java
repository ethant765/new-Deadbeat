/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.deadbeat.utility.Token;

/**
 *
 * @author darylcecile
 */
public class JSON extends ArrayList< List<Token> > {
    
    private enum T_SEP{
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        BRACKET_CHAR;
    }
    
    public JSON(){
        this.add(new ArrayList<>());
    }
    
    public void set(int index, String key, Object val){
        this.get(index).add( new Token(key,val.toString()) );
    }
    
    public void set(String key,Object val){
        set(this.size()-1,key,val);
    }
    
  
    public <V,T> V get(T a){
        if ( a.getClass() == String.class ){
            return (V)at( (String)a );
        }
        else if ( a.getClass() == Integer.class ){
            return (V)at( (Integer)a );
        }
        else{
            return (V)at( (String)a );
        }
    }
    
    public JSON getJSON(){
        // automatically return first JSON if no indices are present
        return getJSON(0);
    }
    
    public JSON getJSON(int index){
        Token t = this.<Token>at(index);
        if ( t.type == Tokenizer.TokenType.JSON ){
            return t.get();
        }
        else{
            return null;
        }
    }
    
    public List getList(int index){
        return this.getJSON(index);
    }
    
    public int getInt(int index){
        return this.<Integer>at(index);
    }
    
    public String getString(int index){
        return this.<String>at(index);
    }
    
    public <T> T val(int index){
        return this.<T>at(index);
    }
    
    public <T> T val(String search){
        return this.<T>at(search);
    }
    
    public <T> T at(int index, String format){
        List<Token> item = super.get(index);
        if ( item.size() == 1 && ((Token)item.get(0)).type == Tokenizer.TokenType.JSON ){
            if ( "JSON".equalsIgnoreCase(format) ){
                return (T)item.get(0).<JSON>get();
            }
            else{
                return (T)item.get(0);
            }
        }
        else{
            return (T)item;
        }
    }
    
    public <T> T at(int index){
        return at(index,"Token");
    }
    
    public <T> T at(String search){
        List<Token> items = this.get(0);
        
        for (Token item : items){
            if ( Tokenizer.removeQuotation(item.serialName).equals(search) ){
                String v = item.serialValue.trim();
                if ( v.startsWith("[") && v.endsWith("]") ){
                    // TODO make list work with non digits
                    return (T)( (String[])v.substring(1,v.length()-1).split(",") );
                }
                else{
                    return (T)Tokenizer.removeQuotation(item.serialValue);
                }
            }
        }
        return null;
    }
    
    //aliases
    public <T> T n(int a) {
        return (T)at(a);
    }
    public <T> T n(String s){
        return (T)at(s);
    }
    public <T> T n(){
        return (T)at( size()-1 );
    }
    
    public int length(int index){
        return this.get(index).size();
    }
    
    public int length(){
        return this.size();
    }
    
    
    @Override
    public String toString(){
        String res = "[\n";
        
        for (List<Token> tokes : this){
            res += "{\n";
            for (Token token : tokes){
                res += "\t";
                res += ( !token.serialName.equals("") ? token.serialName + " : " : "") + token.serialValue;
                res += ",\n";
            }
            res +=  "},\n";
        }
        
        return res + "]";
    }
    
    public void fromString(CharSequence string){
        
        List<List<Token>> t = Tokenizer.Tokenize(string.toString());
        this.clear();
        this.addAll( t );
        
    }
    
    public void fromResultSet(ResultSet rset) throws SQLException{
        
        this.clear();

        try{
            ResultSetMetaData resultMetaData = rset.getMetaData();
            
            while(rset.next()){
                int numCols = resultMetaData.getColumnCount();
                
                JSON dataObj = new JSON();
                String rawCString = "";
                
                rawCString += "{";
                for(int i = 0; i < numCols; i++){
                    String nameCol = resultMetaData.getColumnName(i + 1); //as i starts at 0
                    
                    switch (resultMetaData.getColumnType(i + 1)){
                        case java.sql.Types.INTEGER:
                            dataObj.set(nameCol, rset.getInt(nameCol));
                            rawCString += "'" + nameCol + "' : '" + rset.getInt(nameCol) + "',";
                            break;
                        case java.sql.Types.VARCHAR:
                            dataObj.set(nameCol, rset.getString(nameCol));
                            rawCString += "'" + nameCol + "' : '" + rset.getString(nameCol) + "',";
                            break;
                        case java.sql.Types.DATE:
                            dataObj.set(nameCol, rset.getDate(nameCol));
                            rawCString += "'" + nameCol + "' : '" + rset.getDate(nameCol) + "',";
                            break;
                        case java.sql.Types.BLOB:
                            dataObj.set(nameCol, rset.getBlob(nameCol));
                            rawCString += "'" + nameCol + "' : '" + rset.getBlob(nameCol) + "',";
                            break;
                        default:
                            dataObj.set(nameCol, rset.getObject(nameCol));
                            rawCString += "'" + nameCol + "' : '" + rset.getObject(nameCol) + "',";
                    }
                }
                
                rawCString += "}";
                
                JSON j = this.getJSON();
                Token t = new Token(rawCString);
                List<Token> l = new ArrayList<Token>();
                l.add(t);
                j.add( l );
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    
}
