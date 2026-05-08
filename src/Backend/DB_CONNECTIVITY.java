/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;


import java.sql.*;
import javax.swing.JOptionPane;
/**
 *
 * @author hunter
 */
public class DB_CONNECTIVITY {
    private static Connection conn;
    
    public static boolean initConn() {
        final String usn = "root";
        final String pwd = "";

        final String serverUrl = "jdbc:mysql://localhost:3306/";
        final String dbName = "barangay_management";
        final String dbUrl = serverUrl + dbName;

        try {
            createDatabaseIfNotExists(serverUrl, dbName, usn, pwd);
            conn = DriverManager.getConnection(dbUrl, usn, pwd);
            System.out.println("DB_CONNECTION_STATUS: CONNECTED");
            initDbTables();
            return true;

        } catch (SQLException ex) {
            System.out.println("DB_CONNECTION_STATUS: NOT CONNECTED");
            System.out.println("CAUSE: " + ex.getMessage());
            return false;
        }
    }
    
    private static void createDatabaseIfNotExists(String serverUrl,String dbName,String username,String password) throws SQLException {
        try (Connection connection = DriverManager.getConnection(serverUrl, username, password);
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            System.out.println("DATABASE CHECKED/CREATED");
        }
    }
    
    private static void initDbTables(){
        Statement stmt;
        try {
            stmt = conn.createStatement();
            stmt.execute(DB_QUERIES.RESIDENTS.getQuery());
            stmt.execute(DB_QUERIES.USERS.getQuery());
            stmt.execute(DB_QUERIES.BLOTTER.getQuery());
            stmt.execute(DB_QUERIES.OFFICIALS.getQuery());
            stmt.execute(DB_QUERIES.CERTIFICATES.getQuery());
            System.out.println("Tables Created");
        } catch (SQLException ex) {
            System.out.println(ex.getSQLState());
        }
    }
    
    public static void closeConn(){
        try {
            conn.close();
            System.out.println("CONNECTION CLOSED");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Failed to Close Resources","ERROR",JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public  static Connection getConn(){
        return conn;
    }
    
    
}
