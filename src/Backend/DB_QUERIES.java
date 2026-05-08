/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Backend;

/**
 *
 * @author hunter
 */
public enum DB_QUERIES {
/*---> TABLE CREATION Queries <---*/
    RESIDENTS("""
    CREATE TABLE IF NOT EXISTS residents (
        resident_id INT AUTO_INCREMENT PRIMARY KEY,

        first_name VARCHAR(100) NOT NULL,
        middle_name VARCHAR(100),
        last_name VARCHAR(100) NOT NULL,

        sex ENUM('MALE', 'FEMALE', 'OTHER') NOT NULL,
        birth_date DATE NOT NULL,

        civil_status VARCHAR(50),
        nationality VARCHAR(100),

        address VARCHAR(255),
        purok VARCHAR(100),
        length_of_stay VARCHAR(50),
        profile_image VARCHAR(100),
        status ENUM(
            'ACTIVE',
            'INACTIVE',
            'MOVED_OUT',
            'DECEASED',
            'TEMPORARY',
            'UNDER_REVIEW',
            'BLACKLISTED'
        ) NOT NULL DEFAULT 'ACTIVE',

        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );
    """),

    USERS("""
    CREATE TABLE IF NOT EXISTS users (
        user_id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL
    );
    """),

    OFFICIALS("""
    CREATE TABLE IF NOT EXISTS officials (
        official_id INT AUTO_INCREMENT PRIMARY KEY,
        profile_image varchar(500),
        official_resedential_id INT,
        position VARCHAR(100),
        term_start DATE,
        term_end DATE,
        status ENUM('LIVED', 'ARCHIVED') DEFAULT 'LIVED',
        
        FOREIGN KEY (official_resedential_id) REFERENCES residents(resident_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE
    );
    """),

    ALTER_OFFICIALS_ADD_STATUS("""
    ALTER TABLE officials ADD COLUMN status ENUM('LIVED', 'ARCHIVED') DEFAULT 'LIVED'
    """),

    ALTER_CERTIFICATES_ADD_STATUS("""
    ALTER TABLE certificates ADD COLUMN status ENUM('ACTIVE','ARCHIVED') DEFAULT 'ACTIVE'
    """),

    BLOTTER("""
    CREATE TABLE IF NOT EXISTS blotter (
        blotter_id INT AUTO_INCREMENT PRIMARY KEY,
        complainant_id INT,
        respondent_id INT,
        incident TEXT NOT NULL,
        status VARCHAR(30),
        date_reported DATE,

        FOREIGN KEY (complainant_id) REFERENCES residents(resident_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

        FOREIGN KEY (respondent_id) REFERENCES residents(resident_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE
    );
    """),

    CERTIFICATES("""
    CREATE TABLE IF NOT EXISTS certificates (
        certificate_id INT AUTO_INCREMENT PRIMARY KEY,
        resident_id INT,
        certificate ENUM('Barangay Clearance','Barangay Indigency','Barangay Residency') NOT NULL,
        purpose VARCHAR(1000),
        issued_by INT,
        date_issued DATE,
        date_released DATE,

        FOREIGN KEY (resident_id) REFERENCES residents(resident_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE,

        FOREIGN KEY (issued_by) REFERENCES officials(official_id)
            ON DELETE SET NULL
            ON UPDATE CASCADE
    );
    """),

/*---> INSERTION Queries <---*/
    INSERT_RESIDENT("""
        INSERT INTO residents
        (
            first_name,
            middle_name,
            last_name,
            sex,
            birth_date,
            civil_status,
            nationality,
            address,
            purok,
            length_of_stay,
            profile_image,
            status,
            created_at
        )
        VALUES (?,?,?,?,?,?,?,?,?,?,?, ?, NOW())
    """),

    INSERT_BARANGAY_OFFICIAL("""
        INSERT INTO officials
        (profile_image, official_resedential_id, position, term_start, term_end, status)
        VALUES (?,?,?,?,?, 'LIVED')
        """),

    INSERT_BLOTTER("""
        INSERT INTO blotter
        (complainant_id, respondent_id, incident, status, date_reported)
        VALUES (?,?,?,?,?)
        """),

    INSERT_CERTIFICATE("""
        INSERT INTO certificates
        (resident_id, certificate, purpose, issued_by, date_issued, date_released)
        VALUES (?,?,?,?,?,?)
        """),

    INSERT_USER("""
        INSERT INTO users (username, password)
        VALUES (?,?)
        """),


/*---> DATA FETCHING Queries <---*/
    /* Without Requirements */
    FETCH_ALL_RESIDENTS("SELECT * FROM residents"),
    FETCH_ALL_BARANGAY_OFFICIALS("SELECT * FROM officials"),
    FETCH_ALL_BLOTTERS("SELECT * FROM blotter"),

    FETCH_ALL_BLOTTERS_WITH_NAMES("""
        SELECT b.blotter_id, b.incident, b.status, b.date_reported,
               COALESCE(CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name), 'N/A') AS complainant_name,
               COALESCE(CONCAT_WS(' ', r.first_name, r.middle_name, r.last_name), 'N/A') AS respondent_name
        FROM blotter b
        LEFT JOIN residents c ON b.complainant_id = c.resident_id
        LEFT JOIN residents r ON b.respondent_id = r.resident_id
        ORDER BY b.date_reported DESC
        """),
    FETCH_ALL_CERTIFICATES("SELECT * FROM certificates"),

    FETCH_ALL_CERTIFICATES_WITH_NAMES("""
        SELECT c.certificate_id, c.certificate, c.purpose, c.date_issued, c.date_released,
               COALESCE(CONCAT_WS(' ', r.first_name, r.middle_name, r.last_name), 'N/A') AS applicant_name,
               COALESCE(CONCAT_WS(' ', iss.first_name, iss.middle_name, iss.last_name), 'N/A') AS issued_by_name,
               COALESCE(c.status, 'ACTIVE') AS cert_status
        FROM certificates c
        LEFT JOIN residents r ON c.resident_id = r.resident_id
        LEFT JOIN officials o ON c.issued_by = o.official_id
        LEFT JOIN residents iss ON o.official_resedential_id = iss.resident_id
        ORDER BY c.date_issued DESC
        """),

    FETCH_CERTIFICATES_BY_STATUS_WITH_NAMES("""
        SELECT c.certificate_id, c.certificate, c.purpose, c.date_issued, c.date_released,
               COALESCE(CONCAT_WS(' ', r.first_name, r.middle_name, r.last_name), 'N/A') AS applicant_name,
               COALESCE(CONCAT_WS(' ', iss.first_name, iss.middle_name, iss.last_name), 'N/A') AS issued_by_name,
               COALESCE(c.status, 'ACTIVE') AS cert_status
        FROM certificates c
        LEFT JOIN residents r ON c.resident_id = r.resident_id
        LEFT JOIN officials o ON c.issued_by = o.official_id
        LEFT JOIN residents iss ON o.official_resedential_id = iss.resident_id
        WHERE COALESCE(c.status, 'ACTIVE') = ?
        ORDER BY c.date_issued DESC
        """),

    FETCH_CERTIFICATE_BY_ID_WITH_NAMES("""
        SELECT c.certificate_id, c.certificate, c.purpose, c.date_issued, c.date_released,
               COALESCE(CONCAT_WS(' ', r.first_name, r.middle_name, r.last_name), '') AS applicant_name,
               COALESCE(CONCAT_WS(' ', iss.first_name, iss.middle_name, iss.last_name), '') AS issued_by_name,
               COALESCE(c.status, 'ACTIVE') AS cert_status
        FROM certificates c
        LEFT JOIN residents r ON c.resident_id = r.resident_id
        LEFT JOIN officials o ON c.issued_by = o.official_id
        LEFT JOIN residents iss ON o.official_resedential_id = iss.resident_id
        WHERE c.certificate_id = ?
        """),

    FETCH_ALL_ACTIVE_OFFICIALS_WITH_NAMES("""
        SELECT o.official_id,
               COALESCE(CONCAT_WS(' ', r.first_name, r.middle_name, r.last_name), 'N/A') AS official_name
        FROM officials o
        LEFT JOIN residents r ON o.official_resedential_id = r.resident_id
        WHERE o.status = 'LIVED'
        """),
    FETCH_ALL_USERS("SELECT * FROM users"),


    /* With Requirements */
    FETCH_RESIDENT_BY_ID("SELECT * FROM residents WHERE resident_id=?"),
    FETCH_BARANGAY_OFFICIAL_BY_ID("SELECT * FROM officials WHERE official_id=?"),
    FETCH_BLOTTER_BY_ID("""
        SELECT b.blotter_id, b.incident, b.status, b.date_reported,
               COALESCE(CONCAT_WS(' ', c.first_name, c.middle_name, c.last_name), '') AS complainant_name,
               COALESCE(CONCAT_WS(' ', r.first_name, r.middle_name, r.last_name), '') AS respondent_name
        FROM blotter b
        LEFT JOIN residents c ON b.complainant_id = c.resident_id
        LEFT JOIN residents r ON b.respondent_id = r.resident_id
        WHERE b.blotter_id = ?
        """),
    FETCH_CERTIFICATE_BY_ID("SELECT * FROM certificates WHERE certificate_id=?"),
    FETCH_CERTIFICATES_BY_RESIDENT("SELECT * FROM certificates WHERE resident_id=?"),
    FETCH_USER_FOR_LOGIN("SELECT username FROM users WHERE username=? AND password=?"),

    FETCH_RESIDENT_FULLNAME("""
        SELECT full_name
        FROM residents WHERE resident_id=?
        """),

    FETCH_ACTIVE_BARANGAY_OFFICIALS("""
        SELECT * FROM officials
        WHERE status = 'LIVED'
        """),

    FETCH_OFFICIALS_BY_STATUS("""
        SELECT * FROM officials WHERE status = ?
        """),

    FETCH_BARANGAY_OFFICIALS_NAME("""
        SELECT first_name, middle_name, 
        last_name from residents where resident_id=?
                                    """),
    
    FETCH_PENDING_CERTIFICATES("""
        SELECT * FROM certificates
        WHERE date_released IS NULL
        """),

    FETCH_BLOTTERS_BY_STATUS("""
        SELECT * FROM blotter WHERE status=?
        """),


    FETCH_NUMBER_OF_REC_RESIDENTS("""
        SELECT COUNT(*) AS total_residents FROM residents
        """),

    FETCH_NUMBER_OF_BLOTTERS("""
        SELECT COUNT(*) AS total_active_blotters
        FROM blotter
        """),

    FETCH_OFFICIAL_COUNTS("""
        SELECT COUNT(*) AS total_officials FROM officials
        """),
    
    FETCH_NUMBER_OF_REQ_CERTS("""
        SELECT COUNT(*) AS total_req_certs FROM certificates                     
                              """),


/*---> UPDATION Queries <---*/
    UPDATE_RESIDENT("""
        UPDATE residents SET
        first_name=?, middle_name=?, last_name=?, sex=?, birth_date=?, civil_status=?, 
        nationality=?, address=?, purok=?, length_of_stay=?, profile_image=?, status=?
        WHERE resident_id=?
        """),
    
    UPDATE_RESIDENT_STATUS("""
                           UPDATE residents SET status
                           """),

    UPDATE_BARANGAY_OFFICIAL("""
        UPDATE officials SET
        profile_image=?, official_resedential_id=?, position=?, term_start=?, term_end=?
        WHERE official_id=?
        """),

    UPDATE_OFFICIAL_STATUS("""
        UPDATE officials SET status=? WHERE official_id=?
        """),

    UPDATE_BLOTTER_STATUS("""
        UPDATE blotter SET status=? WHERE blotter_id=?
        """),

    UPDATE_BLOTTER("""
        UPDATE blotter SET complainant_id=?, respondent_id=?, incident=?, status=?, date_reported=?
        WHERE blotter_id=?
        """),

    UPDATE_CERTIFICATE_RELEASE("""
        UPDATE certificates SET
        date_released=?
        WHERE certificate_id=?
        """),

    UPDATE_CERTIFICATE("""
        UPDATE certificates SET resident_id=?, certificate=?, purpose=?, issued_by=?, date_issued=?, date_released=?
        WHERE certificate_id=?
        """),

    UPDATE_CERTIFICATE_STATUS("""
        UPDATE certificates SET status=? WHERE certificate_id=?
        """),

    UPDATE_USER_PASSWORD("""
        UPDATE users SET password=? WHERE username=?
        """),


/*---> DELETION Queries <---*/
    DELETE_RESIDENT("DELETE FROM residents WHERE resident_id=?"),
    DELETE_BARANGAY_OFFICIAL("DELETE FROM officials WHERE official_id=?"),
    DELETE_BLOTTER("DELETE FROM blotter WHERE blotter_id=?"),
    DELETE_CERTIFICATE("DELETE FROM certificates WHERE certificate_id=?"),
    DELETE_USER("DELETE FROM users WHERE user_id=?");
    
    private final String query;

    DB_QUERIES(String q) {
        this.query = q;
    }

    public String getQuery() {
        return query;
    }
}
