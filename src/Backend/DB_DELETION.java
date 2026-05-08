/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_DELETION {

    public static void deleteResident(int residentID) {
        try (PreparedStatement pps =
                     DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.DELETE_RESIDENT.getQuery())) {

            pps.setInt(1, residentID);
            pps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT DELETE RESIDENT");
            System.out.println("CAUSE: " + ex.getMessage());
        }
    }

    public static void deleteUser(int userID) {
        try (PreparedStatement pps =
                     DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.DELETE_USER.getQuery())) {

            pps.setInt(1, userID);
            pps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT DELETE USER");
            System.out.println("CAUSE: " + ex.getMessage());
        }
    }
}
