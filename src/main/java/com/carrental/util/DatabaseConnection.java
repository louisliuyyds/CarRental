package com.carrental.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Stellt Db2-Verbindungen bereit; nutzt PreparedStatement für Operationen.
 */
public final class DatabaseConnection {

    private DatabaseConnection() {
        // Utility-Klasse
    }

    /**
     * Baut eine neue Connection auf Basis der geladenen Konfiguration auf.
     */
    public static Connection create(DatabaseConfig config) throws SQLException {
        // Db2-Treiber sicher laden (hilfreich bei manchen Umgebungen)
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (ClassNotFoundException ignored) {
            // Wenn der Treiber bereits via Service Provider gefunden wird, ist dies unkritisch.
        }

        Properties props = new Properties();
        props.put("user", config.getUser());
        props.put("password", config.getPassword());
        config.getSslCert().ifPresent(cert -> props.put("sslCertLocation", cert));

        return DriverManager.getConnection(config.getUrl(), props);
    }

    /**
     * Hilfsfunktion zum sicheren Anlegen eines PreparedStatement.
     */
    public static PreparedStatement prepare(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
