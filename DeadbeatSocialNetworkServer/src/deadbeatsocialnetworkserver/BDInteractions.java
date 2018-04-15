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
public class BDInteractions {
    
    public static String SQLPath = "jdbc:derby://localhost:1527/DeadbeatDatabase";
    Connection conn = null;
    Statement stmt = null;
    
    
    //will return the results of any Select statement passed to it
    public synchronized ResultSet selectDate(String SQLQuery) throws Exception{
        conn = DriverManager.getConnection(SQLPath);
        stmt = conn.createStatement();
        return stmt.executeQuery(SQLQuery);
    }
    //will update,add,remove data from DB
    //dependant on String passed
    public synchronized void EditData(String SQLQuery) throws Exception{
        conn = DriverManager.getConnection(SQLPath);
        stmt = conn.createStatement();
        stmt.executeUpdate(SQLQuery);
    }
    

}
