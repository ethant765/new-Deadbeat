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
    
    public void set(String key,Object val){
        this.get( this.size() - 1 ).add( new Token(key, val + "" ) );
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
        return this.<List>n();
    }
    
    public int getInt(int index){
        return this.<Integer>at(index);
    }
    
    public int getInt(String search){
        return Integer.parseInt( this.<String>at(search) );
    }
    
    public String getString(int index){
        return this.<String>at(index);
    }
    
    public String getString(String search){
        return this.<String>at(search);
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
        String res = "";//"[";
        
        for (List<Token> tokes : this){
            res += "{";
            for (Token token : tokes){
                res += "";
                res += ( !token.serialName.equals("") ? token.serialName + " : " : "") + token.serialValue;
                res += ",";
            }
            res +=  "},";
        }
        
        return res ;//+ "]";
    }
    
    public void fromString(CharSequence string){
        
        List<List<Token>> t = Tokenizer.Tokenize(string.toString());
        this.clear();
        this.addAll( t );
        
    }
    
    public void fromMergedResultSets(List<ResultSet> rsets) throws SQLException{
        List<Token> lt = new ArrayList<>();
        for (ResultSet r : rsets){
            JSON j = new JSON();
            j.fromResultSet(r);
            lt.add( new Token( j.toString() ) );
        }
        this.add(lt);
    }
    
    public void fromResultSet(ResultSet rset) throws SQLException{
        

        try{
            ResultSetMetaData resultMetaData = rset.getMetaData();
            
            int ln = -1;
            
            while(rset.next()){
                ln++;
                int numCols = resultMetaData.getColumnCount();
                
                List<Token> dataObj = this.getList(ln);
                String rawCString = "";
                
                rawCString += "{";
                for(int i = 0; i < numCols; i++){
                    String nameCol = resultMetaData.getColumnName(i + 1); //as i starts at 0
                    
                    switch (resultMetaData.getColumnType(i + 1)){
                        case java.sql.Types.INTEGER:
                            dataObj.add( new Token(nameCol, rset.getInt(nameCol) + "") );
                            break;
                        case java.sql.Types.VARCHAR:
                            dataObj.add( new Token(nameCol, rset.getString(nameCol) ) );
                            break;
                        case java.sql.Types.DATE:
                            dataObj.add( new Token(nameCol, rset.getDate(nameCol) + "") );
                            break;
                        case java.sql.Types.BLOB:
                            dataObj.add( new Token(nameCol, BinResource.reference(rset.getBlob(nameCol)) ) );
                            break;
                        default:
                            dataObj.add( new Token(nameCol, rset.getObject(nameCol) + "") );
                    }
                }
                
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }
    
}
