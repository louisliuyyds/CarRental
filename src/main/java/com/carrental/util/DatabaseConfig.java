package com.carrental.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

/**
 * Liest die Datenbank-Parameter aus einer Properties-Datei (Deutsch kommentiert).
 */
public final class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;
    private final Optional<String> sslCert;

    private DatabaseConfig(String url, String user, String password, Optional<String> sslCert) {
        this.url = Objects.requireNonNull(url, "url darf nicht null sein");
        this.user = Objects.requireNonNull(user, "user darf nicht null sein");
        this.password = Objects.requireNonNull(password, "password darf nicht null sein");
        this.sslCert = Objects.requireNonNull(sslCert, "sslCert darf nicht null sein");
    }

    /**
     * Lädt die Konfiguration aus einer Klassenpfad-Ressource (z. B. src/main/resources/config.properties).
     */
    public static DatabaseConfig load(String resourcePath) throws IOException {
        Properties props = new Properties();
        try (InputStream in = DatabaseConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Konfigurationsdatei nicht gefunden: " + resourcePath);
            }
            props.load(in);
        }

        String url = getRequired(props, "db.url");
        String user = getRequired(props, "db.user");
        String password = getRequired(props, "db.password");
        Optional<String> sslCert = Optional.ofNullable(props.getProperty("db.sslCert"))
                .filter(s -> !s.isBlank());

        return new DatabaseConfig(url, user, password, sslCert);
    }

    private static String getRequired(Properties props, String key) throws IOException {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IOException("Pflicht-Property fehlt: " + key);
        }
        return value.trim();
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public Optional<String> getSslCert() {
        return sslCert;
    }
}
