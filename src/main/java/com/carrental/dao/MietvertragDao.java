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
                
                int kundeId = resolveKundeId(conn, vertrag.getKunde());
                int fahrzeugId = resolveFahrzeugId(conn, vertrag.getFahrzeug());
                Integer mitarbeiterId = resolveMitarbeiterId(conn, vertrag.getMitarbeiter());

                stmt.setInt(6, kundeId);
                stmt.setInt(7, fahrzeugId);
                if (mitarbeiterId != null) {
                    stmt.setInt(8, mitarbeiterId);
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

                int kundeId = resolveKundeId(conn, vertrag.getKunde());
                int fahrzeugId = resolveFahrzeugId(conn, vertrag.getFahrzeug());
                Integer mitarbeiterId = resolveMitarbeiterId(conn, vertrag.getMitarbeiter());

                stmt.setInt(6, kundeId);
                stmt.setInt(7, fahrzeugId);
                if (mitarbeiterId != null) {
                    stmt.setInt(8, mitarbeiterId);
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

    private int resolveKundeId(Connection conn, Kunde kunde) throws SQLException {
        if (kunde == null) {
            throw new SQLException("Kunde darf beim Mietvertrag nicht fehlen.");
        }
        if (kunde.getId() > 0) {
            return kunde.getId();
        }

        String sql = "SELECT ID FROM Kunde WHERE Kundennummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, kunde.getKundennummer());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID");
                    kunde.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Kein Kunde mit Kundennummer " + kunde.getKundennummer() + " gefunden.");
    }

    private int resolveFahrzeugId(Connection conn, Fahrzeug fahrzeug) throws SQLException {
        if (fahrzeug == null) {
            throw new SQLException("Fahrzeug darf beim Mietvertrag nicht fehlen.");
        }
        if (fahrzeug.getId() > 0) {
            return fahrzeug.getId();
        }

        String sql = "SELECT ID FROM Fahrzeug WHERE Kennzeichen = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, fahrzeug.getKennzeichen());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID");
                    fahrzeug.setId(id);
                    return id;
                }
            }
        }
        throw new SQLException("Kein Fahrzeug mit Kennzeichen " + fahrzeug.getKennzeichen() + " gefunden.");
    }

    private Integer resolveMitarbeiterId(Connection conn, Mitarbeiter mitarbeiter) throws SQLException {
        if (mitarbeiter == null) {
            return null;
        }

        // Mitarbeiter besitzen derzeit keinen persistenten Datensatz.
        // Versuche dennoch, eine ID anhand der Personalnummer zu finden.
        String sql = "SELECT ID FROM Mitarbeiter WHERE Personalnummer = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mitarbeiter.getPersonalnummer());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        }

        return null;
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
                kunde.setId(rs.getInt("Kunde_ID"));
                kunde.setStrasse(rs.getString("Strasse"));
                kunde.setHausnummer(rs.getString("Hausnummer"));
                kunde.setPlz(rs.getString("PLZ"));
                kunde.setOrt(rs.getString("Ort"));
                Date kundeGeburtstag = rs.getDate("Geburtstag");
                if (kundeGeburtstag != null) {
                    kunde.setGeburtstag(kundeGeburtstag.toLocalDate());
                }
                kunde.setFuehrerscheinNummer(rs.getString("FuehrerscheinNummer"));
                kunde.setIstAktiv(rs.getInt("IstAktiv") == 1);
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
