/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import raven.toast.Notifications;

public class DB_FETCHER {

    public static ResultSet fetchAllResidents() {
        try {
            PreparedStatement pps =
                    DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_ALL_RESIDENTS.getQuery());
            return pps.executeQuery();
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT FETCH RESIDENTS");
            System.out.println("CAUSE: " + ex.getMessage());
            return null;
        }
    }
    
    public static ResultSet getOfficialsResidentialInfo(int id){
        try {
            PreparedStatement pps =
                    DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_BARANGAY_OFFICIALS_NAME.getQuery());
            pps.setInt(1, id);
            return pps.executeQuery();
        } catch (SQLException ex) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Error: "+ex.getMessage());
            return null;
        }
    }
    
    public static ResultSet fetchOfficialsByStatus(String status) {
        try {
            if (status == null || status.equals("ALL")) {
                PreparedStatement pps = DB_CONNECTIVITY.getConn()
                        .prepareStatement(DB_QUERIES.FETCH_ALL_BARANGAY_OFFICIALS.getQuery());
                return pps.executeQuery();
            } else {
                PreparedStatement pps = DB_CONNECTIVITY.getConn()
                        .prepareStatement(DB_QUERIES.FETCH_OFFICIALS_BY_STATUS.getQuery());
                pps.setString(1, status);
                return pps.executeQuery();
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    public static ResultSet fetchOfficialById(int officialId) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_BARANGAY_OFFICIAL_BY_ID.getQuery());
            pps.setInt(1, officialId);
            return pps.executeQuery();
        } catch (SQLException ex) {
            return null;
        }
    }

    public static ResultSet fetchAllOfficials(){
        try {
            PreparedStatement pps =
                    DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_ACTIVE_BARANGAY_OFFICIALS.getQuery());
            return pps.executeQuery();
        } catch (SQLException ex) {
            Notifications.getInstance().show(Notifications.Type.ERROR, "Error: "+ex.getMessage());
            return null;
        }
    }

    public static ResultSet fetchResidentByID(int residentID) {
        try {
            PreparedStatement pps =
                    DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_RESIDENT_BY_ID.getQuery());
            pps.setInt(1, residentID);
            return pps.executeQuery();
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT FETCH RESIDENT BY ID");
            System.out.println("CAUSE: " + ex.getMessage());
            return null;
        }
    }

    public static ResultSet fetchUserForLogin(String username, String password) {
        try {
            PreparedStatement pps =
                    DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_USER_FOR_LOGIN.getQuery());
            pps.setString(1, username);
            pps.setString(2, password);
            return pps.executeQuery();
        } catch (SQLException ex) {
            System.out.println("ERROR: LOGIN FETCH FAILED");
            System.out.println("CAUSE: " + ex.getMessage());
            return null;
        }
    }
    
    public static ResultSet fetchAllBlotters() {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.FETCH_ALL_BLOTTERS_WITH_NAMES.getQuery());
            return pps.executeQuery();
        } catch (SQLException ex) {
            return null;
        }
    }

    public static ResultSet fetchBlotterById(int blotterId) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.FETCH_BLOTTER_BY_ID.getQuery());
            pps.setInt(1, blotterId);
            return pps.executeQuery();
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public static ResultSet fetchAllCertificates() {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.FETCH_ALL_CERTIFICATES_WITH_NAMES.getQuery());
            return pps.executeQuery();
        } catch (SQLException ex) {
            return null;
        }
    }

    public static ResultSet fetchCertificatesByStatus(String status) {
        try {
            if (status == null || status.equals("ALL")) {
                PreparedStatement pps = DB_CONNECTIVITY.getConn()
                        .prepareStatement(DB_QUERIES.FETCH_ALL_CERTIFICATES_WITH_NAMES.getQuery());
                return pps.executeQuery();
            } else {
                PreparedStatement pps = DB_CONNECTIVITY.getConn()
                        .prepareStatement(DB_QUERIES.FETCH_CERTIFICATES_BY_STATUS_WITH_NAMES.getQuery());
                pps.setString(1, status);
                return pps.executeQuery();
            }
        } catch (SQLException ex) {
            return null;
        }
    }

    public static ResultSet fetchCertificateById(int certId) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.FETCH_CERTIFICATE_BY_ID_WITH_NAMES.getQuery());
            pps.setInt(1, certId);
            return pps.executeQuery();
        } catch (SQLException ex) {
            return null;
        }
    }

    public static ResultSet fetchAllActiveOfficialsWithNames() {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.FETCH_ALL_ACTIVE_OFFICIALS_WITH_NAMES.getQuery());
            return pps.executeQuery();
        } catch (SQLException ex) {
            return null;
        }
    }
    
    /*COUNTER FUNCTIONS*/
    public static int fetchNumberOfCertsRecoreded(){
        try (PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_NUMBER_OF_REQ_CERTS.getQuery());
             ResultSet rs = pps.executeQuery()){
             if(rs.next()) return rs.getInt("total_req_certs");
        } catch (SQLException ex) {return -1;}
        return 0;
    }
    
    public static int fetchOfficialCount(){
        try (PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_OFFICIAL_COUNTS.getQuery());
             ResultSet rs = pps.executeQuery()){
             if(rs.next()) return rs.getInt("total_officials");
        } catch (SQLException ex) {return -1;}
        return 0;
    }
    
     public static int fetchNumberOfResidents(){
        try (PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_NUMBER_OF_REC_RESIDENTS.getQuery());
             ResultSet rs = pps.executeQuery()){
             if(rs.next()) return rs.getInt("total_residents");
        } catch (SQLException ex) {return -1;}
        return 0;
    }
    
    public static int fetchNumberOfActiveBlotters(){
        try (PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.FETCH_NUMBER_OF_BLOTTERS.getQuery());
             ResultSet rs = pps.executeQuery()){
             if(rs.next()) return rs.getInt("total_active_blotters");
        } catch (SQLException ex) {return -1;}
        return 0;
    }
}

