/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;

import java.sql.*;
import org.JSON.JSONArray;
import org.JSON.JSONObject;

/**
 *
 * @author Ethan
 */
public class resultSetToJson {
    
    public static void resultSetToJson(){}
    
    public JSONArray convertToJSON(ResultSet result){
        JSONArray jArray = new JSONArray();

        try{
            ResultSetMetaData resultMetaData = result.getMetaData();
            
            while(result.next()){
                int numCols = resultMetaData.getColumnCount();
                JSONObject dataObj = new JSONObject();
                
                for(int i = 0; i < numCols; i++){
                    String nameCol = resultMetaData.getColumnName(i + 1); //as i starts at 0
                    
                    switch (resultMetaData.getColumnType(i + 1)){
                        case java.sql.Types.INTEGER:
                            dataObj.put(nameCol, result.getInt(nameCol));
                            break;
                        case java.sql.Types.VARCHAR:
                            dataObj.put(nameCol, result.getString(nameCol));
                            break;
                        case java.sql.Types.DATE:
                            dataObj.put(nameCol, result.getDate(nameCol));
                            break;
                        case java.sql.Types.BLOB:
                            dataObj.put(nameCol, result.getBlob(nameCol));
                            break;
                        default:
                            dataObj.put(nameCol, result.getObject(nameCol));
                    }
                }
                jArray.put(dataObj);
            }
        }catch(Exception e){System.err.println(e.getMessage());}
        return jArray;
    }
}
