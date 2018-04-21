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
            res += prop + "\n";
        }
        return res + "}";
    }
    
    public void fromString(String string){
        
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
