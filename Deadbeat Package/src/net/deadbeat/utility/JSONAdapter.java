/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author darylcecile
 */
public class JSONAdapter extends JSONCollection {
    
    public static void main(String args[]) {
        
        JSONAdapter ja = new JSONAdapter();
        ja.fromString("{'name':'bob','age':24,'songs':[12,42]}");
        
    }
    
    public final static String buildString(JSONCollection collection , boolean formatted){
        String new_line = (formatted ? "\n" : "");
        String tab_line = (formatted ? "\t" : "");
        
        String res = "[" + new_line;
        for (int i = 0 ; i < collection.size() ; i ++){
            JSONObject j_obj = collection.get(i);
            
            res += "{" + new_line;
            for (int x = 0; x < j_obj.size() ; x++){
                JSONProperty j_prop = j_obj.get(x);
                res += tab_line + "'" + j_prop.Key + "' : " + j_prop.get() + (x==j_obj.size()-1?new_line:","+new_line);
            }
            res += "}" + (i==collection.size()-1?new_line:","+new_line);
            
        }
        
        return res += new_line + "]";
    }
  
    @Override
    public String toString(){
        return buildString(this, false);
    }
    
    public void fromString(CharSequence string){
        
        List<String> t = Tokenizer.Tokenize(string.toString());
        
        Log.Out("Lists", String.join( "\n" , t) );
        
    }
    
    
    public void fromMergedResultSets(List<ResultSet> rsets) throws SQLException{
        for (ResultSet r : rsets){
            addResultSet(r);
        }
    }
    
    public void fromResultSet(ResultSet rset){
        
        try{
            addResultSet(rset);
        }
        catch (Exception e){
            Log.Throw(e);
        }
        
    }
    
    private void addResultSet(ResultSet rset) throws SQLException{
        try{
            ResultSetMetaData resultMetaData = rset.getMetaData();
            
            int ln = -1;
            
            while(rset.next()){
                ln++;
                int numCols = resultMetaData.getColumnCount();
                
                JSONObject dataObj = new JSONObject();
                
                for(int i = 0; i < numCols; i++){
                    String nameCol = resultMetaData.getColumnName(i + 1); //as i starts at 0
                    
                    switch (resultMetaData.getColumnType(i + 1)){
                        case java.sql.Types.INTEGER:
                            dataObj.add( new JSONProperty(nameCol, rset.getInt(nameCol) + "") );
                            break;
                        case java.sql.Types.VARCHAR:
                            dataObj.add( new JSONProperty(nameCol, rset.getString(nameCol) ) );
                            break;
                        case java.sql.Types.DATE:
                            dataObj.add( new JSONProperty(nameCol, rset.getDate(nameCol) + "") );
                            break;
                        case java.sql.Types.BLOB:
                            dataObj.add( new JSONProperty(nameCol, BinResource.reference(rset.getBlob(nameCol)) ) );
                            break;
                        default:
                            dataObj.add( new JSONProperty(nameCol, rset.getObject(nameCol) + "") );
                    }
                }
                
                this.add(dataObj);
            }
        }
        catch(Exception e){
            Log.Throw(e);
        }
    }
    
    
}
