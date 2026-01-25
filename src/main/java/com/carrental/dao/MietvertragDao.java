package com.carrental.dao;

import com.carrental.model.*;
import com.carrental.util.DatabaseConfig;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * DAO-Implementierung für Mietvertrag-Entitäten.
 * Verwaltet alle Datenbankoperationen für Mietverträge inklusive Zusatzoptionen.
 */
public class MietvertragDao implements GenericDao<Mietvertrag> {

    private final DatabaseConfig config;

    public MietvertragDao(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Mietvertrag create(Mietvertrag vertrag) throws SQLException {
        String sql = "INSERT INTO Mietvertrag (Mietnummer, StartDatum, EndDatum, Status, " +
                     "GesamtPreis, Kunde_ID, Fahrzeug_ID, Mitarbeiter_ID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.create(config);
            conn.setAutoCommit(false); // Transaktion starten
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, vertrag.getMietnummer());
                stmt.setDate(2, Date.valueOf(vertrag.getStartDatum()));
                stmt.setDate(3, Date.valueOf(vertrag.getEndDatum()));
                stmt.setString(4, vertrag.getStatus().name());
                stmt.setDouble(5, vertrag.getGesamtPreis());
                
                // Hinweis: Kunde und Fahrzeug müssen eine ID-Eigenschaft haben
                // In der aktuellen Implementierung fehlt diese - hier Platzhalter
                stmt.setInt(6, vertrag.getKunde() != null ? vertrag.getKunde().getKundennummer() : 0);
                stmt.setInt(7, vertrag.getFahrzeug() != null ? vertrag.getFahrzeug().getId() : 0);
                
                if (vertrag.getMitarbeiter() != null) {
                    // Mitarbeiter hat ebenfalls keine ID in unserem Model
                    stmt.setNull(8, Types.INTEGER);
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        vertrag.setId(rs.getInt(1));
                    }
                }
                
                // Zusatzoptionen speichern
                if (vertrag.getZusatzoptionen() != null && !vertrag.getZusatzoptionen().isEmpty()) {
                    addZusatzoptionenToVertrag(conn, vertrag.getId(), vertrag.getZusatzoptionen());
                }
                
                conn.commit(); // Transaktion abschließen
            } catch (SQLException e) {
                conn.rollback(); // Bei Fehler zurückrollen
                throw e;
            }
            
            return vertrag;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public Optional<Mietvertrag> findById(int id) throws SQLException {
        String sql = "SELECT m.*, k.*, f.*, ft.*, mit.* " +
                     "FROM Mietvertrag m " +
                     "LEFT JOIN Kunde k ON m.Kunde_ID = k.ID " +
                     "LEFT JOIN Fahrzeug f ON m.Fahrzeug_ID = f.ID " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "LEFT JOIN Mitarbeiter mit ON m.Mitarbeiter_ID = mit.ID " +
                     "WHERE m.ID = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mietvertrag vertrag = mapResultSetToMietvertrag(rs);
                    // Zusatzoptionen laden
                    vertrag.setZusatzoptionen(loadZusatzoptionen(conn, id));
                    return Optional.of(vertrag);
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet einen Mietvertrag anhand der Mietnummer.
     * 
     * @param mietnummer Die Mietnummer
     * @return Optional mit dem Mietvertrag, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    public Optional<Mietvertrag> findByMietnummer(String mietnummer) throws SQLException {
        String sql = "SELECT m.*, k.*, f.*, ft.*, mit.* " +
                     "FROM Mietvertrag m " +
                     "LEFT JOIN Kunde k ON m.Kunde_ID = k.ID " +
                     "LEFT JOIN Fahrzeug f ON m.Fahrzeug_ID = f.ID " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "LEFT JOIN Mitarbeiter mit ON m.Mitarbeiter_ID = mit.ID " +
                     "WHERE m.Mietnummer = ?";
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, mietnummer);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Mietvertrag vertrag = mapResultSetToMietvertrag(rs);
                    vertrag.setZusatzoptionen(loadZusatzoptionen(conn, vertrag.getId()));
                    return Optional.of(vertrag);
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Findet alle Mietverträge eines Kunden.
     * 
     * @param kundennummer Die Kundennummer
     * @return Liste der Mietverträge
     * @throws SQLException Bei Datenbankfehlern
     */
    public List<Mietvertrag> findByKunde(int kundennummer) throws SQLException {
        String sql = "SELECT m.*, k.*, f.*, ft.*, mit.* " +
                     "FROM Mietvertrag m " +
                     "LEFT JOIN Kunde k ON m.Kunde_ID = k.ID " +
                     "LEFT JOIN Fahrzeug f ON m.Fahrzeug_ID = f.ID " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "LEFT JOIN Mitarbeiter mit ON m.Mitarbeiter_ID = mit.ID " +
                     "WHERE k.Kundennummer = ?";
        
        List<Mietvertrag> vertraege = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kundennummer);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Mietvertrag vertrag = mapResultSetToMietvertrag(rs);
                    vertrag.setZusatzoptionen(loadZusatzoptionen(conn, vertrag.getId()));
                    vertraege.add(vertrag);
                }
            }
        }
        
        return vertraege;
    }

    @Override
    public List<Mietvertrag> findAll() throws SQLException {
        String sql = "SELECT m.*, k.*, f.*, ft.*, mit.* " +
                     "FROM Mietvertrag m " +
                     "LEFT JOIN Kunde k ON m.Kunde_ID = k.ID " +
                     "LEFT JOIN Fahrzeug f ON m.Fahrzeug_ID = f.ID " +
                     "LEFT JOIN Fahrzeugtyp ft ON f.Fahrzeugtyp_ID = ft.ID " +
                     "LEFT JOIN Mitarbeiter mit ON m.Mitarbeiter_ID = mit.ID";
        
        List<Mietvertrag> vertraege = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Mietvertrag vertrag = mapResultSetToMietvertrag(rs);
                vertrag.setZusatzoptionen(loadZusatzoptionen(conn, vertrag.getId()));
                vertraege.add(vertrag);
            }
        }
        
        return vertraege;
    }

    @Override
    public boolean update(Mietvertrag vertrag) throws SQLException {
        String sql = "UPDATE Mietvertrag SET Mietnummer = ?, StartDatum = ?, EndDatum = ?, " +
                     "Status = ?, GesamtPreis = ?, Kunde_ID = ?, Fahrzeug_ID = ?, " +
                     "Mitarbeiter_ID = ? WHERE ID = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.create(config);
            conn.setAutoCommit(false);
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, vertrag.getMietnummer());
                stmt.setDate(2, Date.valueOf(vertrag.getStartDatum()));
                stmt.setDate(3, Date.valueOf(vertrag.getEndDatum()));
                stmt.setString(4, vertrag.getStatus().name());
                stmt.setDouble(5, vertrag.getGesamtPreis());
                stmt.setInt(6, vertrag.getKunde() != null ? vertrag.getKunde().getKundennummer() : 0);
                stmt.setInt(7, vertrag.getFahrzeug() != null ? vertrag.getFahrzeug().getId() : 0);
                
                if (vertrag.getMitarbeiter() != null) {
                    stmt.setNull(8, Types.INTEGER);
                } else {
                    stmt.setNull(8, Types.INTEGER);
                }
                
                stmt.setInt(9, vertrag.getId());
                
                boolean updated = stmt.executeUpdate() > 0;
                
                if (updated) {
                    // Zusatzoptionen aktualisieren (erst löschen, dann neu einfügen)
                    deleteZusatzoptionenFromVertrag(conn, vertrag.getId());
                    if (vertrag.getZusatzoptionen() != null && !vertrag.getZusatzoptionen().isEmpty()) {
                        addZusatzoptionenToVertrag(conn, vertrag.getId(), vertrag.getZusatzoptionen());
                    }
                }
                
                conn.commit();
                return updated;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.create(config);
            conn.setAutoCommit(false);
            
            // Erst Zusatzoptionen löschen
            deleteZusatzoptionenFromVertrag(conn, id);
            
            // Dann Mietvertrag löschen
            String sql = "DELETE FROM Mietvertrag WHERE ID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                boolean deleted = stmt.executeUpdate() > 0;
                conn.commit();
                return deleted;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Lädt die Zusatzoptionen für einen Mietvertrag.
     */
    private List<Zusatzoption> loadZusatzoptionen(Connection conn, int vertragId) throws SQLException {
        String sql = "SELECT z.* FROM Zusatzoption z " +
                     "INNER JOIN Mietvertrag_Zusatzoption mz ON z.ID = mz.Zusatzoption_ID " +
                     "WHERE mz.Mietvertrag_ID = ?";
        
        List<Zusatzoption> optionen = new ArrayList<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vertragId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    optionen.add(mapResultSetToZusatzoption(rs));
                }
            }
        }
        
        return optionen;
    }

    /**
     * Fügt Zusatzoptionen zu einem Mietvertrag hinzu.
     */
    private void addZusatzoptionenToVertrag(Connection conn, int vertragId, 
                                           List<Zusatzoption> optionen) throws SQLException {
        String sql = "INSERT INTO Mietvertrag_Zusatzoption (Mietvertrag_ID, Zusatzoption_ID) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Zusatzoption option : optionen) {
                stmt.setInt(1, vertragId);
                stmt.setInt(2, option.getId());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    /**
     * Löscht alle Zusatzoptionen eines Mietvertrags.
     */
    private void deleteZusatzoptionenFromVertrag(Connection conn, int vertragId) throws SQLException {
        String sql = "DELETE FROM Mietvertrag_Zusatzoption WHERE Mietvertrag_ID = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vertragId);
            stmt.executeUpdate();
        }
    }

    /**
     * Hilfsmethode zum Mappen eines ResultSet auf ein Mietvertrag-Objekt.
     */
    private Mietvertrag mapResultSetToMietvertrag(ResultSet rs) throws SQLException {
        Mietvertrag vertrag = new Mietvertrag();
        vertrag.setId(rs.getInt("ID"));
        vertrag.setMietnummer(rs.getString("Mietnummer"));
        
        Date startDatum = rs.getDate("StartDatum");
        if (startDatum != null) {
            vertrag.setStartDatum(startDatum.toLocalDate());
        }
        
        Date endDatum = rs.getDate("EndDatum");
        if (endDatum != null) {
            vertrag.setEndDatum(endDatum.toLocalDate());
        }
        
        String statusStr = rs.getString("Status");
        if (statusStr != null) {
            vertrag.setStatus(VertragsStatus.valueOf(statusStr));
        }
        
        vertrag.setGesamtPreis(rs.getDouble("GesamtPreis"));
        
        // Kunde laden (falls vorhanden im JOIN)
        try {
            int kundennummer = rs.getInt("Kundennummer");
            if (!rs.wasNull()) {
                Kunde kunde = new Kunde(
                    kundennummer,
                    rs.getString("AccountName"),
                    rs.getString("Passwort"),
                    rs.getString("Vorname"),
                    rs.getString("Nachname"),
                    rs.getString("Email")
                );
                // Weitere Kunde-Felder setzen...
                vertrag.setKunde(kunde);
            }
        } catch (SQLException e) {
            // Kunde nicht im ResultSet
        }
        
        // Fahrzeug laden (falls vorhanden im JOIN)
        try {
            String kennzeichen = rs.getString("Kennzeichen");
            if (kennzeichen != null) {
                Fahrzeug fahrzeug = new Fahrzeug();
                fahrzeug.setId(rs.getInt("Fahrzeug_ID"));
                fahrzeug.setKennzeichen(kennzeichen);
                // Weitere Fahrzeug-Felder laden...
                vertrag.setFahrzeug(fahrzeug);
            }
        } catch (SQLException e) {
            // Fahrzeug nicht im ResultSet
        }
        
        return vertrag;
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
