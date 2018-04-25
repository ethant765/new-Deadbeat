/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.deadbeat.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import javafx.concurrent.Task;

/**
 *
 * @author darylcecile
 */
public class JSONAdapter extends JSONCollection {
    
    public static void main(String args[]) {
        
        JSONAdapter ja = new JSONAdapter();
        ja.fromString("{'name':'bob','age':24,'songs':[12,42]}");
        
        Log.Out( ja.get(0).get("songs").<String[]>get()[1] );
        
    }
    
    /**
     * Get the first {@link JSONObject} in this adapter.
     * 
     * @return 
     */
    public JSONObject get(){
        return this.get(0);
    }
    
    /**
     * Get the {@link JSONProperty} associated with the {@code propertyName} from the first {@link JSONObject}.
     * <p>
     * This is the same as calling {@code get(0).get(propertyName)}.
     * <p>
     * This method will call the {@code JSONAdapter::get()} method and search for the property in the returned result
     * 
     * @param propertyName the property name of the property you want to get
     * @return 
     */
    public JSONProperty get(String propertyName){
        return this.get().get(propertyName);
    }
    
    /**
     * Generate Standard-compliant JSON String
     * @param collection
     * @param formatted
     * @return 
     */
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
    
    /**
     * Loads the JSON string into this adapter.
     * <p>
     * 
     * @param string Valid JSON String
     */
    public void fromString(CharSequence string){
        
        List<String> t = Tokenizer.Tokenize(string.toString());
        
        //remove parenthesies
        t.remove(t.size()-1);
        t.remove(0);
        
        this.add( new JSONObject());
        
        for (int i=0; i < t.size(); i+=2){
            String n = t.get(i);
            String k = t.get(i+1);
            this.get(0).add(n, k);
        }
        
    }
    
    /**
     * Takes a {@link List} of {@link ResultSet} and populates this adapter.
     * <p>
     * Once completed, the current adapter will contain a list of {@link JSONObject}. 
     * <p>
     * This method will call {@code fromMergedResultSets} on each of the elements in the list provided.
     * @param rsets List of ResultSets
     * @throws SQLException 
     */
    public void fromMergedResultSets(List<ResultSet> rsets) throws SQLException{
        for (ResultSet r : rsets){
            addResultSet(r);
        }
    }
    
    /**
     * Populates this adapter with a {@link JSONObject} from the {@link ResultSet} provided.
     * @param rset ResultSet
     */
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
                            dataObj.add( new JSONProperty(nameCol, "BLOB::"+BinResource.reference(rset.getBlob(nameCol)) ) );
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
