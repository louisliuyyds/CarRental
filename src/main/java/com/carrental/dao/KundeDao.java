package com.carrental.dao;

import com.carrental.model.Kunde;
import com.carrental.util.DatabaseConfig;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-Implementierung für Kunde-Entitäten.
 * Verwaltet alle Datenbankoperationen für Kunden.
 */
public class KundeDao implements GenericDao<Kunde> {

    private final DatabaseConfig config;

    public KundeDao(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Kunde create(Kunde kunde) throws SQLException {
        String sql = "INSERT INTO Kunde (AccountName, Passwort, Vorname, Nachname, Email, " +
                     "Kundennummer, Strasse, Hausnummer, PLZ, Ort, Geburtstag, " +
                     "FuehrerscheinNummer, IstAktiv) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, kunde.getAccountName());
            stmt.setString(2, kunde.getPasswort());
            stmt.setString(3, kunde.getVorname());
            stmt.setString(4, kunde.getNachname());
            stmt.setString(5, kunde.getEmail());
            stmt.setInt(6, kunde.getKundennummer());
            stmt.setString(7, kunde.getStrasse());
            stmt.setString(8, kunde.getHausnummer());
            stmt.setString(9, kunde.getPlz());
            stmt.setString(10, kunde.getOrt());
            stmt.setDate(11, kunde.getGeburtstag() != null ? Date.valueOf(kunde.getGeburtstag()) : null);
            stmt.setString(12, kunde.getFuehrerscheinNummer());
            stmt.setInt(13, kunde.isIstAktiv() ? 1 : 0);
            
            stmt.executeUpdate();
            
            // Generierte ID abrufen
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    // Hinweis: Kunde hat keine setId-Methode in unserem Model
                    // In einer vollständigen Implementierung sollte eine ID-Eigenschaft hinzugefügt werden
                }
            }
            
            return kunde;
        }
    }

    @Override
    public Optional<Kunde> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Kunde WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToKunde(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet einen Kunden anhand der Kundennummer.
     * 
     * @param kundennummer Die Kundennummer
     * @return Optional mit dem Kunden, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public Optional<Kunde> findByKundennummer(int kundennummer) throws SQLException {
        String sql = "SELECT * FROM Kunde WHERE Kundennummer = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kundennummer);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToKunde(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet einen Kunden anhand des Account-Namens.
     * 
     * @param accountName Der Account-Name
     * @return Optional mit dem Kunden, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public Optional<Kunde> findByAccountName(String accountName) throws SQLException {
        String sql = "SELECT * FROM Kunde WHERE AccountName = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, accountName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToKunde(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    @Override
    public List<Kunde> findAll() throws SQLException {
        String sql = "SELECT * FROM Kunde";
        List<Kunde> kunden = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                kunden.add(mapResultSetToKunde(rs));
            }
        }
        
        return kunden;
    }

    @Override
    public boolean update(Kunde kunde) throws SQLException {
        String sql = "UPDATE Kunde SET AccountName = ?, Passwort = ?, Vorname = ?, " +
                     "Nachname = ?, Email = ?, Strasse = ?, Hausnummer = ?, PLZ = ?, " +
                     "Ort = ?, Geburtstag = ?, FuehrerscheinNummer = ?, IstAktiv = ? " +
                     "WHERE Kundennummer = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kunde.getAccountName());
            stmt.setString(2, kunde.getPasswort());
            stmt.setString(3, kunde.getVorname());
            stmt.setString(4, kunde.getNachname());
            stmt.setString(5, kunde.getEmail());
            stmt.setString(6, kunde.getStrasse());
            stmt.setString(7, kunde.getHausnummer());
            stmt.setString(8, kunde.getPlz());
            stmt.setString(9, kunde.getOrt());
            stmt.setDate(10, kunde.getGeburtstag() != null ? Date.valueOf(kunde.getGeburtstag()) : null);
            stmt.setString(11, kunde.getFuehrerscheinNummer());
            stmt.setInt(12, kunde.isIstAktiv() ? 1 : 0);
            stmt.setInt(13, kunde.getKundennummer());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Kunde WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Löscht einen Kunden anhand der Kundennummer (Soft-Delete durch Deaktivierung).
     * 
     * @param kundennummer Die Kundennummer
     * @return true bei Erfolg, false wenn nicht gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public boolean softDeleteByKundennummer(int kundennummer) throws SQLException {
        String sql = "UPDATE Kunde SET IstAktiv = 0 WHERE Kundennummer = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kundennummer);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Hilfsmethode zum Mappen eines ResultSet auf ein Kunde-Objekt.
     */
    private Kunde mapResultSetToKunde(ResultSet rs) throws SQLException {
        Kunde kunde = new Kunde(
            rs.getInt("Kundennummer"),
            rs.getString("AccountName"),
            rs.getString("Passwort"),
            rs.getString("Vorname"),
            rs.getString("Nachname"),
            rs.getString("Email")
        );
        
        kunde.setStrasse(rs.getString("Strasse"));
        kunde.setHausnummer(rs.getString("Hausnummer"));
        kunde.setPlz(rs.getString("PLZ"));
        kunde.setOrt(rs.getString("Ort"));
        
        Date geburtstag = rs.getDate("Geburtstag");
        if (geburtstag != null) {
            kunde.setGeburtstag(geburtstag.toLocalDate());
        }
        
        kunde.setFuehrerscheinNummer(rs.getString("FuehrerscheinNummer"));
        kunde.setIstAktiv(rs.getInt("IstAktiv") == 1);
        
        return kunde;
    }
}
