package com.carrental.model;

import java.time.LocalDate;

/**
 * Repräsentiert ein konkretes Fahrzeug im Autovermietungssystem.
 * Jedes Fahrzeug gehört zu einem Fahrzeugtyp.
 */
public class Fahrzeug {

    private int id;
    private String kennzeichen;
    private int aktuellerKilometerstand;
    private FahrzeugZustand zustand;
    private LocalDate tuevDatum;
    private Fahrzeugtyp fahrzeugtyp;

    /**
     * Konstruktor für Fahrzeug.
     */
    public Fahrzeug() {
        this.zustand = FahrzeugZustand.VERFUEGBAR; // Standardzustand
    }

    /**
     * Konstruktor mit wichtigsten Parametern.
     * 
     * @param kennzeichen Kennzeichen des Fahrzeugs
     * @param fahrzeugtyp Fahrzeugtyp
     */
    public Fahrzeug(String kennzeichen, Fahrzeugtyp fahrzeugtyp) {
        this();
        this.kennzeichen = kennzeichen;
        this.fahrzeugtyp = fahrzeugtyp;
    }

    /**
     * Konstruktor mit Kennzeichen, Fahrzeugtyp und Zustand.
     * 
     * @param kennzeichen Kennzeichen des Fahrzeugs
     * @param fahrzeugtyp Fahrzeugtyp
     * @param zustand Initialer Zustand
     */
    public Fahrzeug(String kennzeichen, Fahrzeugtyp fahrzeugtyp, FahrzeugZustand zustand) {
        this.kennzeichen = kennzeichen;
        this.fahrzeugtyp = fahrzeugtyp;
        this.zustand = zustand;
    }

    /**
     * Prüft, ob das Fahrzeug im angegebenen Zeitraum verfügbar ist.
     * 
     * @param start Startdatum
     * @param end Enddatum
     * @return true wenn verfügbar, sonst false
     */
    public boolean istVerfuegbar(LocalDate start, LocalDate end) {
        // Einfache Implementierung: Prüfung des aktuellen Zustands
        // In einer vollständigen Implementierung würde hier die Datenbank
        // nach überlappenden Mietverträgen geprüft
        return this.zustand == FahrzeugZustand.VERFUEGBAR;
    }

    /**
     * Setzt den Zustand des Fahrzeugs.
     * 
     * @param z Neuer Zustand
     */
    public void setZustand(FahrzeugZustand z) {
        this.zustand = z;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKennzeichen() {
        return kennzeichen;
    }

    public void setKennzeichen(String kennzeichen) {
        this.kennzeichen = kennzeichen;
    }

    public int getAktuellerKilometerstand() {
        return aktuellerKilometerstand;
    }

    public void setAktuellerKilometerstand(int aktuellerKilometerstand) {
        this.aktuellerKilometerstand = aktuellerKilometerstand;
    }

    public FahrzeugZustand getZustand() {
        return zustand;
    }

    public LocalDate getTuevDatum() {
        return tuevDatum;
    }

    public void setTuevDatum(LocalDate tuevDatum) {
        this.tuevDatum = tuevDatum;
    }

    public Fahrzeugtyp getFahrzeugtyp() {
        return fahrzeugtyp;
    }

    public void setFahrzeugtyp(Fahrzeugtyp fahrzeugtyp) {
        this.fahrzeugtyp = fahrzeugtyp;
    }
}
