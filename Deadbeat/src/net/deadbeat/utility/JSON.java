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

/**
 *
 * @author darylcecile
 */
public class JSON {
    
    private List<Pair> props = new ArrayList<>();
    
    private enum T_SEP{
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        BRACKET_CHAR;
    }
    
    public JSON(){}
    
    private boolean isTypeString(Object content){
        content = content.toString().trim();
        return ((content.toString().startsWith("\"") && content.toString().endsWith("\"")) ||
                (content.toString().startsWith("'") && content.toString().endsWith("'")));
    }
    
    private String getWrappedChars(String content){
        return getWrappedChars(content,"","");
    }
    
    private String[] splitOutsideString(String content,char splitter,boolean includeQuote){
    
        List<String> res = new ArrayList<>();
        char[] lc = content.toCharArray();
        boolean stringIsOpen = false;
        T_SEP qtype = T_SEP.SINGLE_QUOTE;
        String stringSoFar = "";
        
        for (char c : lc) {
            
            if ( 
                    ( c == '"' && qtype == T_SEP.DOUBLE_QUOTE) || 
                    ( c == '\'' && qtype == T_SEP.SINGLE_QUOTE) ){
                stringIsOpen = !stringIsOpen;
                if (stringIsOpen){
                    qtype = (c == '\'' ? T_SEP.SINGLE_QUOTE : T_SEP.DOUBLE_QUOTE);
                }
            }
            else if ( c == '[' && stringIsOpen == false ){
                stringIsOpen = true;
                qtype = T_SEP.BRACKET_CHAR;
            }
            else if ( c == ']' && stringIsOpen == true && qtype == T_SEP.BRACKET_CHAR ){
                stringIsOpen = false;
            }
            if ( c == splitter && stringIsOpen == false ){
                res.add(stringSoFar);
                stringSoFar = "";
            }
            else{
                boolean isQuote = ( 
                        (c=='"' && qtype == T_SEP.DOUBLE_QUOTE) || 
                        (c=='\'' && qtype == T_SEP.SINGLE_QUOTE)||
                        ((c=='[' || c==']') && qtype == T_SEP.BRACKET_CHAR));
                
                if ( ( isQuote && includeQuote == true ) || !isQuote ){
                    stringSoFar += String.valueOf(c);
                }
            }
            
        }
        
        if ( stringSoFar.length() > 0 ) res.add(stringSoFar);
        
        return res.toArray(new String[0]);
        
    }
    
    private String[] splitOutsideString(String content,char splitter){
        return splitOutsideString(content,splitter,true);
    }
   
    //made public for test
    public String getWrappedChars(String content,String fromFirst,String toLast){
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
    
    private String[] getAsArrayTypedStrings(String content){
        String inputString = content.trim();
        String[] parts = splitOutsideString( getWrappedChars(inputString,"[","]") , ',' );
        return parts;
    }
    
    private <T extends Object> T getStoredContent(T rawContent){
        T retVal = null;
        if ( rawContent.toString().trim().startsWith("r.") ){
            String dtype =rawContent.toString().trim().split(".")[1].split("::")[0];
            String dval = rawContent.toString().trim().split("::")[1];
            return (T)BinResource.lookup(dval);
        }
        if (isTypeString(rawContent)) {
            retVal = (T)rawContent.toString().trim();
            retVal = (T)retVal.toString().substring(1, retVal.toString().length() - 1);
            return retVal;
        }
        else return rawContent;
    }
    
    private String getResultSetTypedString(ResultSet rset,int col){
        try {
            ResultSetMetaData rsmdata = rset.getMetaData();
            String colName = rsmdata.getColumnName(col);
            switch (rsmdata.getColumnType(col)){
                case java.sql.Types.INTEGER:
                    return "" + rset.getInt(colName);
                case java.sql.Types.VARCHAR:
                    return rset.getString(colName);
                case java.sql.Types.DATE:
                    return rset.getDate(colName).toString();
                case java.sql.Types.BLOB:
                    return "r.blob::"+BinResource.reference( rset.getBlob(colName) );
                default:
                    return rset.getObject(colName).toString();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void set(String key, Object val){
        props.add( new Pair(key , val) );
    }
    
    public <T extends Object> T get(String key){
        T res = null;
        for (Pair prop : props) {
            String _key = prop.getKey();
            Object _val = prop.getValue();
            if (_key == null ? _key == null : _key.equals(key)){
                T content = (T)_val;
                res = (T)getStoredContent(content);
            }
        }
        return res;
    }
    
    public int length(){
        return props.size();
    }
    
    @Override
    public String toString(){
        String res = "{";
        res = "\n" + props.stream().map((prop) -> prop.getKey() + ":" + prop.getValue().toString() + ",\n").reduce(res, String::concat);
        return res + "}";
    }
    
    public void fromString(CharSequence string){
        
        String inputString = string.toString().trim();
        
        //check if json_string root is an array
        String pre = getWrappedChars(inputString,"{","}");
        String[] parts = splitOutsideString( pre , ',' );
        
        for (String part : parts){
            Object vn = null;
            String propName = part.split(":")[0].trim();
            propName = getWrappedChars(propName,"'","'");
            propName = (propName==null? getWrappedChars(part.split(":")[0].trim(),"\"","\""): propName );
            
            String rawPropVal = part.split(":")[1].trim();
            Object propVal = getWrappedChars(rawPropVal,"\"","\"");
            propVal = (propVal == null ? getWrappedChars(rawPropVal,"'","'") : propVal );
            if ( propVal == null ){
                try{vn = (int)Integer.parseInt(rawPropVal);}catch (Exception e){ Log.Err(">> Not an int",rawPropVal,vn); };
                if ( rawPropVal.startsWith("[") && rawPropVal.endsWith("]") ){
                    propVal = getAsArrayTypedStrings(rawPropVal);
                }
                else if ( !"is_null".equals("is_"+vn) ){
                    propVal = vn;
                }
                else{
                    propVal = null;
                }
            }
            set ( propName , propVal );
        }
        
    }
    
    public void fromResultSet(ResultSet rset){
        try {
            int rowCount = rset.getMetaData().getColumnCount();
            for (int i = 0; i < rowCount; i++) {
                set(rset.getMetaData().getColumnLabel(i + 1).toLowerCase(), getResultSetTypedString(rset,i + 1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
