package com.carrental.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Konfigurationsklasse für Datenbankverbindungen.
 * Lädt Einstellungen aus config.properties.
 */
public class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;
    private final boolean ssl;

    /**
     * Konstruktor für DatabaseConfig.
     * 
     * @param url Datenbankverbindungs-URL
     * @param user Benutzername
     * @param password Passwort
     * @param ssl SSL-Verbindung aktivieren
     */
    public DatabaseConfig(String url, String user, String password, boolean ssl) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.ssl = ssl;
    }

    /**
     * Gibt die Datenbank-URL zurück.
     * 
     * @return URL
     */
    public String url() {
        return url;
    }

    /**
     * Gibt den Benutzernamen zurück.
     * 
     * @return Benutzername
     */
    public String user() {
        return user;
    }

    /**
     * Gibt das Passwort zurück.
     * 
     * @return Passwort
     */
    public String password() {
        return password;
    }

    /**
     * Gibt an, ob SSL aktiviert ist.
     * 
     * @return true falls SSL aktiv
     */
    public boolean ssl() {
        return ssl;
    }

    /**
     * Lädt die Konfiguration aus einer Properties-Datei.
     * 
     * @param propertiesFile Der Pfad zur Properties-Datei (Klassenpfad oder absolut)
     * @return Eine neue DatabaseConfig-Instanz
     * @throws IOException falls Datei nicht gelesen werden kann
     */
    public static DatabaseConfig load(String propertiesFile) throws IOException {
        Properties props = new Properties();
        
        // Versuche zuerst aus Klassenpfad zu laden (für JAR/IDE)
        try (InputStream is = DatabaseConfig.class.getClassLoader().getResourceAsStream(propertiesFile)) {
            if (is != null) {
                props.load(is);
            } else {
                // Fallback: direkte Datei
                props.load(new java.io.FileInputStream(propertiesFile));
            }
        }
        
        String url = props.getProperty("db.url", "");
        String user = props.getProperty("db.user", "");
        String password = props.getProperty("db.password", "");
        String sslStr = props.getProperty("db.ssl", "false");
        boolean ssl = Boolean.parseBoolean(sslStr);
        
        if (url.isEmpty() || user.isEmpty() || password.isEmpty()) {
            throw new IOException("Erforderliche Properties nicht gefunden (db.url, db.user, db.password)");
        }
        
        return new DatabaseConfig(url, user, password, ssl);
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", ssl=" + ssl +
                '}';
    }
}
