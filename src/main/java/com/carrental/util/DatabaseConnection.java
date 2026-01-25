package com.carrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Factory für Datenbankverbindungen zu IBM Db2.
 */
public class DatabaseConnection {

    private static void loadDriver() {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                "Db2 JDBC-Treiber (com.ibm.db2.jcc.DB2Driver) nicht gefunden. " +
                "Stelle sicher, dass lib/db2jcc4.jar im Klassenpfad ist.", e);
        }
    }

    /**
     * Erstellt eine neue Verbindung zur Db2-Datenbank.
     * 
     * @param config DatabaseConfig mit URL, Benutzer, Passwort, SSL-Flag
     * @return Connection zur Datenbank
     * @throws SQLException falls Verbindung fehlschlägt
     */
    public static Connection create(DatabaseConfig config) throws SQLException {
        loadDriver();
        
        // URL: jdbc:db2://host:port/db (OHNE user/password/properties)
        String baseUrl = config.url();
        
        // Properties separieren
        Properties props = new Properties();
        props.setProperty("user", config.user());
        props.setProperty("password", config.password());
        
        // SSL-Connection als Property, nicht in URL
        if (config.ssl()) {
            props.setProperty("sslConnection", "true");
        }
        
        // Verbindung mit getrennten Properties
        return DriverManager.getConnection(baseUrl, props);
    }
}
