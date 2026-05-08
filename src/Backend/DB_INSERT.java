/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Backend;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DB_INSERT {

    public static void insertResident(
            String firstName,
            String middleName,
            String lastName,
            String sex,
            String birthDate,
            String civilStatus,
            String nationality,
            String address,
            String purok,
            String lengthOfStay,
            String profileImage,
            String status
    ) {
        try (PreparedStatement pps =
                    DB_CONNECTIVITY.getConn().prepareStatement(
                            DB_QUERIES.INSERT_RESIDENT.getQuery()
                    )) {

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

            pps.executeUpdate();

        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT INSERT RESIDENT");
            System.out.println("CAUSE: " + ex.getMessage());
        }
    }

    public static int insertUser(String username, String password) {
        try (PreparedStatement pps = DB_CONNECTIVITY.getConn()
                .prepareStatement(
                        DB_QUERIES.INSERT_USER.getQuery()
                )
        ) {
            pps.setString(1, username);
            pps.setString(2, password);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("ERROR: CANNOT INSERT USER");
            System.out.println("CAUSE: " + ex.getMessage());
            return 0;
        }
    }
    
    public static int insertOfficial(String profileImage, int resID, String position, Date StartDate, Date endDate){
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.INSERT_BARANGAY_OFFICIAL.getQuery());
            pps.setString(1, profileImage);
            pps.setInt(2, resID);
            pps.setString(3, position);
            pps.setDate(4, (java.sql.Date) StartDate);
            pps.setDate(5, (java.sql.Date) endDate);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int updateOfficial(int officialId, String profileImage, int resID, String position, java.sql.Date termStart, java.sql.Date termEnd) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.UPDATE_BARANGAY_OFFICIAL.getQuery());
            pps.setString(1, profileImage);
            pps.setInt(2, resID);
            pps.setString(3, position);
            pps.setDate(4, termStart);
            pps.setDate(5, termEnd);
            pps.setInt(6, officialId);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int archiveOfficial(int officialId) {
        return setOfficialStatus(officialId, "ARCHIVED");
    }

    public static int setOfficialStatus(int officialId, String status) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn().prepareStatement(DB_QUERIES.UPDATE_OFFICIAL_STATUS.getQuery());
            pps.setString(1, status);
            pps.setInt(2, officialId);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int insertBlotter(Integer complainantId, Integer respondentId, String incident, String status, java.sql.Date dateReported) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.INSERT_BLOTTER.getQuery());
            if (complainantId == null || complainantId < 0) pps.setNull(1, java.sql.Types.INTEGER);
            else pps.setInt(1, complainantId);
            if (respondentId == null || respondentId < 0) pps.setNull(2, java.sql.Types.INTEGER);
            else pps.setInt(2, respondentId);
            pps.setString(3, incident);
            pps.setString(4, status);
            pps.setDate(5, dateReported);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int updateBlotter(int blotterId, Integer complainantId, Integer respondentId, String incident, String status, java.sql.Date dateReported) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.UPDATE_BLOTTER.getQuery());
            if (complainantId == null || complainantId < 0) pps.setNull(1, java.sql.Types.INTEGER);
            else pps.setInt(1, complainantId);
            if (respondentId == null || respondentId < 0) pps.setNull(2, java.sql.Types.INTEGER);
            else pps.setInt(2, respondentId);
            pps.setString(3, incident);
            pps.setString(4, status);
            pps.setDate(5, dateReported);
            pps.setInt(6, blotterId);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int deleteBlotter(int blotterId) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.DELETE_BLOTTER.getQuery());
            pps.setInt(1, blotterId);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int insertCertificate(Integer residentId, String certType, String purpose, Integer issuedById, java.sql.Date dateIssued, java.sql.Date dateReleased) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.INSERT_CERTIFICATE.getQuery());
            if (residentId == null || residentId < 0) pps.setNull(1, java.sql.Types.INTEGER);
            else pps.setInt(1, residentId);
            pps.setString(2, certType);
            pps.setString(3, purpose);
            if (issuedById == null || issuedById < 0) pps.setNull(4, java.sql.Types.INTEGER);
            else pps.setInt(4, issuedById);
            pps.setDate(5, dateIssued);
            if (dateReleased == null) pps.setNull(6, java.sql.Types.DATE);
            else pps.setDate(6, dateReleased);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int updateCertificate(int certId, Integer residentId, String certType, String purpose, Integer issuedById, java.sql.Date dateIssued, java.sql.Date dateReleased) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.UPDATE_CERTIFICATE.getQuery());
            if (residentId == null || residentId < 0) pps.setNull(1, java.sql.Types.INTEGER);
            else pps.setInt(1, residentId);
            pps.setString(2, certType);
            pps.setString(3, purpose);
            if (issuedById == null || issuedById < 0) pps.setNull(4, java.sql.Types.INTEGER);
            else pps.setInt(4, issuedById);
            pps.setDate(5, dateIssued);
            if (dateReleased == null) pps.setNull(6, java.sql.Types.DATE);
            else pps.setDate(6, dateReleased);
            pps.setInt(7, certId);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static int setCertificateStatus(int certId, String status) {
        try {
            PreparedStatement pps = DB_CONNECTIVITY.getConn()
                    .prepareStatement(DB_QUERIES.UPDATE_CERTIFICATE_STATUS.getQuery());
            pps.setString(1, status);
            pps.setInt(2, certId);
            return pps.executeUpdate();
        } catch (SQLException ex) {
            return 0;
        }
    }

    public static void setupCertificatesTable() {
        try {
            DB_CONNECTIVITY.getConn()
                .prepareStatement(DB_QUERIES.ALTER_CERTIFICATES_ADD_STATUS.getQuery())
                .executeUpdate();
        } catch (SQLException ex) {
            // Column already exists — safe to ignore
        }
    }

    public static void setupOfficialsTable() {
        try {
            DB_CONNECTIVITY.getConn()
                .prepareStatement(DB_QUERIES.ALTER_OFFICIALS_ADD_STATUS.getQuery())
                .executeUpdate();
        } catch (SQLException ex) {
            // Column already exists — safe to ignore
        }
    }
}
