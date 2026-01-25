package com.carrental.dao;

import com.carrental.model.Zusatzoption;
import com.carrental.util.DatabaseConfig;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-Implementierung für Zusatzoption-Entitäten.
 * Verwaltet alle Datenbankoperationen für Zusatzoptionen wie Kindersitz, Navigationssystem, etc.
 */
public class ZusatzoptionDao implements GenericDao<Zusatzoption> {

    private final DatabaseConfig config;

    public ZusatzoptionDao(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Zusatzoption create(Zusatzoption option) throws SQLException {
        String sql = "INSERT INTO Zusatzoption (Bezeichnung, Aufpreis, Beschreibung) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, option.getBezeichnung());
            stmt.setDouble(2, option.getAufpreis());
            stmt.setString(3, option.getBeschreibung());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    option.setId(rs.getInt(1));
                }
            }
            
            return option;
        }
    }

    @Override
    public Optional<Zusatzoption> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Zusatzoption WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToZusatzoption(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet eine Zusatzoption anhand der Bezeichnung.
     * 
     * @param bezeichnung Die Bezeichnung der Option
     * @return Optional mit der Zusatzoption, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public Optional<Zusatzoption> findByBezeichnung(String bezeichnung) throws SQLException {
        String sql = "SELECT * FROM Zusatzoption WHERE Bezeichnung = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, bezeichnung);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToZusatzoption(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet alle Zusatzoptionen bis zu einem maximalen Aufpreis.
     * 
     * @param maxAufpreis Maximaler Aufpreis
     * @return Liste der Zusatzoptionen
     * @throws SQLException Bei Datenbankfehlern
     */
    public List<Zusatzoption> findByMaxAufpreis(double maxAufpreis) throws SQLException {
        String sql = "SELECT * FROM Zusatzoption WHERE Aufpreis <= ? ORDER BY Aufpreis";
        
        List<Zusatzoption> optionen = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDouble(1, maxAufpreis);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    optionen.add(mapResultSetToZusatzoption(rs));
                }
            }
        }
        
        return optionen;
    }

    @Override
    public List<Zusatzoption> findAll() throws SQLException {
        String sql = "SELECT * FROM Zusatzoption ORDER BY Bezeichnung";
        
        List<Zusatzoption> optionen = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                optionen.add(mapResultSetToZusatzoption(rs));
            }
        }
        
        return optionen;
    }

    @Override
    public boolean update(Zusatzoption option) throws SQLException {
        String sql = "UPDATE Zusatzoption SET Bezeichnung = ?, Aufpreis = ?, Beschreibung = ? WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, option.getBezeichnung());
            stmt.setDouble(2, option.getAufpreis());
            stmt.setString(3, option.getBeschreibung());
            stmt.setInt(4, option.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        // Hinweis: Dies wird fehlschlagen, wenn die Option noch in Mietverträgen verwendet wird
        // aufgrund der Foreign-Key-Constraints
        String sql = "DELETE FROM Zusatzoption WHERE ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Prüft, ob eine Zusatzoption in aktiven Mietverträgen verwendet wird.
     * 
     * @param id Die ID der Zusatzoption
     * @return true wenn die Option verwendet wird, sonst false
     * @throws SQLException Bei Datenbankfehlern
     */
    public boolean isInUse(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Mietvertrag_Zusatzoption WHERE Zusatzoption_ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }

    /**
     * Hilfsmethode zum Mappen eines ResultSet auf ein Zusatzoption-Objekt.
     */
    private Zusatzoption mapResultSetToZusatzoption(ResultSet rs) throws SQLException {
        Zusatzoption option = new Zusatzoption();
        option.setId(rs.getInt("ID"));
        option.setBezeichnung(rs.getString("Bezeichnung"));
        option.setAufpreis(rs.getDouble("Aufpreis"));
        option.setBeschreibung(rs.getString("Beschreibung"));
        return option;
    }
}
