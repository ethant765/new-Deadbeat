/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Composers;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author darylcecile
 */
public class JSON {
    
    private String[] props;
    
    public JSON(){}
    
    private boolean isTypeString(String content){
        content = content.trim();
        return content.startsWith("\"") && content.endsWith("\"");
    }
    
    private String getWrappedChars(String content){
        return getWrappedChars(content,"","");
    }
    
    private String[] splitOutsideString(String content,char splitter,boolean includeQuote){
    
        String[] res = {};
        boolean stringIsOpen = false;
        String stringSoFar = "";
        
        for (char c : content.toCharArray()) {
            
            if ( c == '"' ){
                stringIsOpen = !stringIsOpen;
            }
            
            if ( c == splitter && stringIsOpen == false ){
                res[res.length] = stringSoFar;
                stringSoFar = "";
            }
            else{
                if ( ( c == '"' && includeQuote == true ) || c != '"' ){
                    stringSoFar += String.valueOf(c);
                }
            }
            
        }
        
        return res;
        
    }
    
    private String[] splitOutsideString(String content,char splitter){
        return splitOutsideString(content,splitter,true);
    }
    
    private String getWrappedChars(String content,String fromFirst,String toLast){
        content = content.trim();
        if ((fromFirst == null ? toLast == null : fromFirst.equals(toLast)) && "".equals(fromFirst)){
            return content;
        }
        else if ( !content.startsWith(fromFirst) || !content.endsWith(toLast) ){
            return null;
        }
        else{
            return content.substring(1, content.length() - 2);
        }
    }
    
    private String[] getAsArrayTypedStrings(String content){
        String inputString = content.trim();
        String[] parts = splitOutsideString( getWrappedChars(inputString,"[","]") , ',' );
        return parts;
    }
    
    private String getStoredContentString(String rawContent){
        if (isTypeString(rawContent)) {
            rawContent = rawContent.trim();
            rawContent = rawContent.substring(1, rawContent.length() - 2);
        }
        return rawContent;
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
                    return BinResource.reference( rset.getBlob(colName) );
                default:
                    return rset.getObject(colName).toString();
            }
        } catch (SQLException ex) {
            Logger.getLogger(JSON.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public void set(String key, Object val){
        props[props.length] = key + ":" + val;
    }
    
    public <T extends Object> T get(String key){
        T res = null;
        for (String prop : props) {
            if (prop.split(":")[0] == null ? key == null : prop.split(":")[0].equals(key)){
                String content = prop.split(":")[1];
                res = (T)getStoredContentString(content);
            }
        }
        return res;
    }
    
    public int length(){
        return props.length;
    }
    
    @Override
    public String toString(){
        String res = "{\n";
        for (String prop : props) {
            res += prop + ",\n";
        }
        return res + "}";
    }
    
    public void fromString(String string){
        
        String inputString = string.trim();
        
        //check if json_string root is an array
        String[] parts = splitOutsideString( getWrappedChars(string,"{","}") , ',' );
        
        for (String part : parts){
            // split str by ":" and set
            set ( getWrappedChars(part.split(":")[0],"\"","\"") , part.split(":")[1] );
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
