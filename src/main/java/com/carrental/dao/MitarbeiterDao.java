package com.carrental.dao;

import com.carrental.model.Mitarbeiter;
import com.carrental.util.DatabaseConfig;
import com.carrental.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MitarbeiterDao implements GenericDao<Mitarbeiter> {

    private final DatabaseConfig config;

    public MitarbeiterDao(DatabaseConfig config) {
        this.config = config;
    }

    @Override
    public Mitarbeiter create(Mitarbeiter mitarbeiter) throws SQLException {
        String sql = "INSERT INTO Mitarbeiter (AccountName, Passwort, Vorname, Nachname, Email, Personalnummer, BerechtigungsStufe) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, mitarbeiter.getAccountName());
            stmt.setString(2, mitarbeiter.getPasswort());
            stmt.setString(3, mitarbeiter.getVorname());
            stmt.setString(4, mitarbeiter.getNachname());
            stmt.setString(5, mitarbeiter.getEmail());
            stmt.setString(6, mitarbeiter.getPersonalnummer());
            stmt.setInt(7, mitarbeiter.getBerechtigungsStufe());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    mitarbeiter.setId(rs.getInt(1));
                }
            }

            return mitarbeiter;
        }
    }

    @Override
    public Optional<Mitarbeiter> findById(int id) throws SQLException {
        String sql = "SELECT * FROM Mitarbeiter WHERE ID = ?";
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMitarbeiter(rs));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Mitarbeiter> findByAccountName(String accountName) throws SQLException {
        String sql = "SELECT * FROM Mitarbeiter WHERE AccountName = ?";
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMitarbeiter(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Mitarbeiter> findAll() throws SQLException {
        String sql = "SELECT * FROM Mitarbeiter";
        List<Mitarbeiter> list = new ArrayList<>();

        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToMitarbeiter(rs));
            }
        }

        return list;
    }

    @Override
    public boolean update(Mitarbeiter mitarbeiter) throws SQLException {
        String sql = "UPDATE Mitarbeiter SET AccountName = ?, Passwort = ?, Vorname = ?, Nachname = ?, " +
                     "Email = ?, Personalnummer = ?, BerechtigungsStufe = ? WHERE ID = ?";
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, mitarbeiter.getAccountName());
            stmt.setString(2, mitarbeiter.getPasswort());
            stmt.setString(3, mitarbeiter.getVorname());
            stmt.setString(4, mitarbeiter.getNachname());
            stmt.setString(5, mitarbeiter.getEmail());
            stmt.setString(6, mitarbeiter.getPersonalnummer());
            stmt.setInt(7, mitarbeiter.getBerechtigungsStufe());
            stmt.setInt(8, mitarbeiter.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Mitarbeiter WHERE ID = ?";
        try (Connection conn = DatabaseConnection.create(config);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Mitarbeiter mapResultSetToMitarbeiter(ResultSet rs) throws SQLException {
        Mitarbeiter mitarbeiter = new Mitarbeiter(
            rs.getString("Personalnummer"),
            rs.getString("AccountName"),
            rs.getString("Passwort"),
            rs.getString("Vorname"),
            rs.getString("Nachname"),
            rs.getString("Email")
        );
        mitarbeiter.setId(rs.getInt("ID"));
        mitarbeiter.setBerechtigungsStufe(rs.getInt("BerechtigungsStufe"));
        return mitarbeiter;
    }
}