/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deadbeatsocialnetworkserver;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ethan
 */

//class handles all the interactions with the database
//requries the SQL Query passing for the DB commands
public class BDInteractions {
    
    public static String SQLPath = "jdbc:derby://localhost:1527/DeadbeatDatabase";
    Connection conn = null;
    Statement stmt = null;
    ResultSet res = null;
    
    //will return the results of any Select statement passed to it
    public synchronized ResultSet selectData(String SQLQuery){
        try{
            conn = DriverManager.getConnection(SQLPath);
            stmt = conn.createStatement();
            res = stmt.executeQuery(SQLQuery);
        }catch(Exception e){System.err.println(e.getMessage());}
        return res;
    }
    //will update,add,remove data from DB
    //dependant on String passed
    public synchronized void EditData(String SQLQuery){
        try{
            conn = DriverManager.getConnection(SQLPath);
            stmt = conn.createStatement();
            stmt.executeUpdate(SQLQuery);
        }catch(Exception e){System.err.println(e.getMessage());}
    }
}
