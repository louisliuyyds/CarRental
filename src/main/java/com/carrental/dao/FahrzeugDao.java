package com.carrental.dao;

import com.carrental.model.Antriebsart;
import com.carrental.model.Fahrzeug;
import com.carrental.model.FahrzeugZustand;
import com.carrental.model.Fahrzeugtyp;
import com.carrental.util.DatabaseConfig;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-Implementierung für Fahrzeug-Entitäten.
 * Verwaltet alle Datenbankoperationen für Fahrzeuge und Fahrzeugtypen.
 */
public class FahrzeugDao implements GenericDao<Fahrzeug> {

    private final DatabaseConfig config;

    public FahrzeugDao(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Fahrzeug create(Fahrzeug fahrzeug) throws SQLException {
        String sql = "INSERT INTO Fahrzeug (Kennzeichen, AktuellerKilometerstand, Zustand, " +
                     "TuevDatum, Fahrzeugtyp_ID) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, fahrzeug.getKennzeichen());
            stmt.setInt(2, fahrzeug.getAktuellerKilometerstand());
            stmt.setString(3, fahrzeug.getZustand().name());
            stmt.setDate(4, fahrzeug.getTuevDatum() != null ? Date.valueOf(fahrzeug.getTuevDatum()) : null);
            stmt.setInt(5, fahrzeug.getFahrzeugtyp() != null ? fahrzeug.getFahrzeugtyp().getId() : 0);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    fahrzeug.setId(rs.getInt(1));
                }
            }
            
            return fahrzeug;
        }
    }

    @Override
    public Optional<Fahrzeug> findById(int id) throws SQLException {
        String sql = "SELECT f.*, ft.* FROM Fahrzeug f " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "WHERE f.ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFahrzeug(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet ein Fahrzeug anhand des Kennzeichens.
     * 
     * @param kennzeichen Das Kennzeichen
     * @return Optional mit dem Fahrzeug, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public Optional<Fahrzeug> findByKennzeichen(String kennzeichen) throws SQLException {
        String sql = "SELECT f.*, ft.* FROM Fahrzeug f " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "WHERE f.Kennzeichen = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, kennzeichen);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFahrzeug(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet alle verfügbaren Fahrzeuge.
     * 
     * @return Liste der verfügbaren Fahrzeuge
     * @throws SQLException Bei Datenbankfehlern
     */
    public List<Fahrzeug> findVerfuegbare() throws SQLException {
        String sql = "SELECT f.*, ft.* FROM Fahrzeug f " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "WHERE f.Zustand = 'VERFUEGBAR'";
        
        List<Fahrzeug> fahrzeuge = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                fahrzeuge.add(mapResultSetToFahrzeug(rs));
            }
        }
        
        return fahrzeuge;
    }

    @Override
    public List<Fahrzeug> findAll() throws SQLException {
        String sql = "SELECT f.*, ft.* FROM Fahrzeug f " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID";
        
        List<Fahrzeug> fahrzeuge = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                fahrzeuge.add(mapResultSetToFahrzeug(rs));
            }
        }
        
        return fahrzeuge;
    }

    @Override
    public boolean update(Fahrzeug fahrzeug) throws SQLException {
        String sql = "UPDATE Fahrzeug SET Kennzeichen = ?, AktuellerKilometerstand = ?, " +
                     "Zustand = ?, TuevDatum = ?, Fahrzeugtyp_ID = ? WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, fahrzeug.getKennzeichen());
            stmt.setInt(2, fahrzeug.getAktuellerKilometerstand());
            stmt.setString(3, fahrzeug.getZustand().name());
            stmt.setDate(4, fahrzeug.getTuevDatum() != null ? Date.valueOf(fahrzeug.getTuevDatum()) : null);
            stmt.setInt(5, fahrzeug.getFahrzeugtyp() != null ? fahrzeug.getFahrzeugtyp().getId() : 0);
            stmt.setInt(6, fahrzeug.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Fahrzeug WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ========== Fahrzeugtyp-Operationen ==========

    /**
     * Erstellt einen neuen Fahrzeugtyp.
     * 
     * @param typ Der zu erstellende Fahrzeugtyp
     * @return Der erstellte Fahrzeugtyp mit generierter ID
     * @throws SQLException Bei Datenbankfehlern
     */
    public Fahrzeugtyp createFahrzeugtyp(Fahrzeugtyp typ) throws SQLException {
        String sql = "INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, " +
                     "StandardTagesPreis, Sitzplaetze, Antriebsart, ReichweiteKm, Beschreibung) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, typ.getHersteller());
            stmt.setString(2, typ.getModellBezeichnung());
            stmt.setString(3, typ.getKategorie());
            stmt.setDouble(4, typ.getStandardTagesPreis());
            stmt.setInt(5, typ.getSitzplaetze());
            stmt.setString(6, typ.getAntriebsart() != null ? typ.getAntriebsart().name() : null);
            stmt.setInt(7, typ.getReichweiteKm());
            stmt.setString(8, typ.getBeschreibung());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    typ.setId(rs.getInt(1));
                }
            }
            
            return typ;
        }
    }

    /**
     * Findet einen Fahrzeugtyp anhand der ID.
     * 
     * @param id Die ID des Fahrzeugtyps
     * @return Optional mit dem Fahrzeugtyp, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public Optional<Fahrzeugtyp> findFahrzeugtypById(int id) throws SQLException {
        String sql = "SELECT * FROM Fahrzeugtyp WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFahrzeugtyp(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Gibt alle Fahrzeugtypen zurück.
     * 
     * @return Liste aller Fahrzeugtypen
     * @throws SQLException Bei Datenbankfehlern
     */
    public List<Fahrzeugtyp> findAllFahrzeugtypen() throws SQLException {
        String sql = "SELECT * FROM Fahrzeugtyp";
        List<Fahrzeugtyp> typen = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                typen.add(mapResultSetToFahrzeugtyp(rs));
            }
        }
        
        return typen;
    }

    /**
     * Hilfsmethode zum Mappen eines ResultSet auf ein Fahrzeug-Objekt.
     */
    private Fahrzeug mapResultSetToFahrzeug(ResultSet rs) throws SQLException {
        Fahrzeug fahrzeug = new Fahrzeug();
        fahrzeug.setId(rs.getInt("ID"));
        fahrzeug.setKennzeichen(rs.getString("Kennzeichen"));
        fahrzeug.setAktuellerKilometerstand(rs.getInt("AktuellerKilometerstand"));
        
        String zustandStr = rs.getString("Zustand");
        if (zustandStr != null) {
            fahrzeug.setZustand(FahrzeugZustand.valueOf(zustandStr));
        }
        
        Date tuevDatum = rs.getDate("TuevDatum");
        if (tuevDatum != null) {
            fahrzeug.setTuevDatum(tuevDatum.toLocalDate());
        }
        
        // Fahrzeugtyp aus JOIN-Daten laden (falls vorhanden)
        try {
            if (rs.getObject("Fahrzeugtyp_ID") != null && !rs.wasNull()) {
                Fahrzeugtyp typ = mapResultSetToFahrzeugtyp(rs);
                fahrzeug.setFahrzeugtyp(typ);
            }
        } catch (SQLException e) {
            // Falls kein JOIN gemacht wurde, ignorieren
        }
        
        return fahrzeug;
    }

    /**
     * Hilfsmethode zum Mappen eines ResultSet auf ein Fahrzeugtyp-Objekt.
     */
    private Fahrzeugtyp mapResultSetToFahrzeugtyp(ResultSet rs) throws SQLException {
        Fahrzeugtyp typ = new Fahrzeugtyp();
        
        // Prüfen ob die Spalten vom Fahrzeugtyp vorhanden sind
        try {
            typ.setId(rs.getInt("Fahrzeugtyp.ID"));
        } catch (SQLException e) {
            // Fallback wenn ohne Alias
            typ.setId(rs.getInt("ID"));
        }
        
        typ.setHersteller(rs.getString("Hersteller"));
        typ.setModellBezeichnung(rs.getString("ModellBezeichnung"));
        typ.setKategorie(rs.getString("Kategorie"));
        typ.setStandardTagesPreis(rs.getDouble("StandardTagesPreis"));
        typ.setSitzplaetze(rs.getInt("Sitzplaetze"));
        
        String antriebsartStr = rs.getString("Antriebsart");
        if (antriebsartStr != null) {
            typ.setAntriebsart(Antriebsart.valueOf(antriebsartStr));
        }
        
        typ.setReichweiteKm(rs.getInt("ReichweiteKm"));
        typ.setBeschreibung(rs.getString("Beschreibung"));
        
        return typ;
    }

    /**
     * Speichert ein neues Fahrzeug (Alias für create).
     * 
     * @param fahrzeug Das zu speichernde Fahrzeug
     * @return Das gespeicherte Fahrzeug mit generierter ID
     * @throws SQLException Bei Datenbankfehlern
     */
    public Fahrzeug save(Fahrzeug fahrzeug) throws SQLException {
        return create(fahrzeug);
    }

    /**
     * Speichert einen neuen Fahrzeugtyp (Alias für createFahrzeugtyp).
     * 
     * @param typ Der zu speichernde Fahrzeugtyp
     * @return Der gespeicherte Fahrzeugtyp mit generierter ID
     * @throws SQLException Bei Datenbankfehlern
     */
    public Fahrzeugtyp saveFahrzeugtyp(Fahrzeugtyp typ) throws SQLException {
        return createFahrzeugtyp(typ);
    }

    /**
     * Löscht einen Fahrzeugtyp anhand der ID.
     * 
     * @param id Die ID des zu löschenden Fahrzeugtyps
     * @return true wenn erfolgreich gelöscht
     * @throws SQLException Bei Datenbankfehlern
     */
    public boolean deleteFahrzeugtyp(Long id) throws SQLException {
        String sql = "DELETE FROM Fahrzeugtyp WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
