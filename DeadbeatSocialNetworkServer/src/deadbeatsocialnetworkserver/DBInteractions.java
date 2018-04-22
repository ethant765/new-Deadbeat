/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;
import java.sql.*;

/**
 *
 * @author Ethan
 */

//class handles all the interactions with the database
//requries the SQL Query passing for the DB commands
public class DBInteractions {
    
    public static String SQLPath = "jdbc:derby://localhost:1527/DeadbeatDatabase;create=true;ssl=peerAuthentication";

    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    
    public void DeleteRecord(String tableName, String condition){
        String SQLQuery = "delete from " + tableName;
        if(condition != null){
            SQLQuery += " where " + condition;
        }
        EditData(SQLQuery, "delete");
    }
    
    public void UpdateRecord(String tableName, String newValues, String condition){
        String SQLQuery = "update " + tableName + " set " + newValues;
        if(condition != null){
            SQLQuery += " where " + condition;
        }
        EditData(SQLQuery, "update");
    }
    
    public void InsertRecord(String tableName, String tableCols, String vals){
        String SQLQuery = "insert into " + tableName + tableCols + " values " + vals;
        EditData(SQLQuery, "insert");
    }
    
    public ResultSet GetRecord(String values, String tableName, String condition){
        String SQLQuery = "select" + values + " from " + tableName;
        if(condition != null){
            SQLQuery += " where " + condition;
        }
        return EditData(SQLQuery, "select");
    }
    
    public ResultSet GetCustomRecord(String command){
        return EditData(command, "select");
    }
    
    //will update,add,remove, fetch data from DB
    //dependant on String passed
    private synchronized ResultSet EditData(String SQLQuery, String SQLCommand){
        try{
            conn = DriverManager.getConnection(SQLPath);
            stmt = conn.createStatement();
            
            if("select".equals(SQLCommand)){
                res = stmt.executeQuery(SQLQuery);
            }
            else{
                stmt.executeUpdate(SQLQuery);
            }
        }catch(Exception e){
            System.err.println("ERR in DBInteractions.EditData>"+e.getMessage());
        }
        return res;
    }
}
