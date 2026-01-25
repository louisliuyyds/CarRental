package com.carrental.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Generische DAO-Schnittstelle für CRUD-Operationen.
 * Definiert die Standard-Datenbankoperationen für alle Entitäten.
 * 
 * @param <T> Der Entitätstyp
 */
public interface GenericDao<T> {

    /**
     * Fügt eine neue Entität in die Datenbank ein.
     * 
     * @param entity Die einzufügende Entität
     * @return Die eingefügte Entität mit generierter ID
     * @throws SQLException Bei Datenbankfehlern
     */
    T create(T entity) throws SQLException;

    /**
     * Sucht eine Entität anhand ihrer ID.
     * 
     * @param id Die ID der Entität
     * @return Optional mit der Entität, falls gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    Optional<T> findById(int id) throws SQLException;

    /**
     * Gibt alle Entitäten zurück.
     * 
     * @return Liste aller Entitäten
     * @throws SQLException Bei Datenbankfehlern
     */
    List<T> findAll() throws SQLException;

    /**
     * Aktualisiert eine bestehende Entität.
     * 
     * @param entity Die zu aktualisierende Entität
     * @return true bei Erfolg, false wenn nicht gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    boolean update(T entity) throws SQLException;

    /**
     * Löscht eine Entität anhand ihrer ID.
     * 
     * @param id Die ID der zu löschenden Entität
     * @return true bei Erfolg, false wenn nicht gefunden
     * @throws SQLException Bei Datenbankfehlern
     */
    boolean delete(int id) throws SQLException;
}
