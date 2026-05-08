/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DB_UPDATE {

    public static void updateResident(
            int residentID, String firstName, String middleName, String lastName,
            String sex, String birthDate, String civilStatus, String nationality,
            String address, String purok, String lengthOfStay, String profileImage, String status
    ) {
        try (PreparedStatement pps =
                     DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.UPDATE_RESIDENT.getQuery())) {

            pps.setString(1, firstName);
            pps.setString(2, middleName);
            pps.setString(3, lastName);
            pps.setString(4, sex);
            pps.setString(5, birthDate);
            pps.setString(6, civilStatus);
            pps.setString(7, nationality);
            pps.setString(8, address);
            pps.setString(9, purok);
            pps.setString(10, lengthOfStay);
            pps.setString(11, profileImage);
            pps.setString(12, status);
            pps.setInt(13, residentID);

            pps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT UPDATE RESIDENT");
            System.out.println("CAUSE: " + ex.getMessage());
        }
    }
    
    public static String updateUserPassword(String username, String password) {
        try (PreparedStatement pps =
                     DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.UPDATE_USER_PASSWORD.getQuery())) {

            pps.setString(1, password);
            pps.setString(2, username);
            return pps.executeUpdate()==1 ? "Password Updated" : "Password Reset Failed";
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT UPDATE USER PASSWORD");
            System.out.println("CAUSE: " + ex.getMessage());
            return "Password Reset Failed";
        }
    }
}
