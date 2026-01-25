package com.carrental.model;

/**
 * Repräsentiert einen Fahrzeugtyp (z.B. VW Golf, Tesla Model 3).
 * Definiert die Eigenschaften und Preise für eine Fahrzeugkategorie.
 */
public class Fahrzeugtyp {

    private int id;
    private String hersteller;
    private String modellBezeichnung;
    private String kategorie;
    private double standardTagesPreis;
    private int sitzplaetze;
    private Antriebsart antriebsart;
    private int reichweiteKm;
    private String beschreibung;

    /**
     * Konstruktor für Fahrzeugtyp.
     */
    public Fahrzeugtyp() {
        // Leerer Konstruktor für flexible Initialisierung
    }

    /**
     * Konstruktor mit wichtigsten Parametern.
     * 
     * @param hersteller Hersteller des Fahrzeugs
     * @param modellBezeichnung Modellbezeichnung
     * @param standardTagesPreis Standard-Tagespreis
     */
    public Fahrzeugtyp(String hersteller, String modellBezeichnung, double standardTagesPreis) {
        this.hersteller = hersteller;
        this.modellBezeichnung = modellBezeichnung;
        this.standardTagesPreis = standardTagesPreis;
    }

    /**
     * Konstruktor mit allen wichtigen Parametern.
     * 
     * @param hersteller Hersteller des Fahrzeugs
     * @param modellBezeichnung Modellbezeichnung
     * @param kategorie Fahrzeugkategorie
     * @param antriebsart Antriebsart
     * @param sitzplaetze Anzahl Sitzplätze
     * @param standardTagesPreis Standard-Tagespreis
     */
    public Fahrzeugtyp(String hersteller, String modellBezeichnung, String kategorie, 
                       Antriebsart antriebsart, int sitzplaetze, double standardTagesPreis) {
        this.hersteller = hersteller;
        this.modellBezeichnung = modellBezeichnung;
        this.kategorie = kategorie;
        this.antriebsart = antriebsart;
        this.sitzplaetze = sitzplaetze;
        this.standardTagesPreis = standardTagesPreis;
    }

    /**
     * Gibt den Standard-Tagespreis zurück.
     * 
     * @return Standard-Tagespreis
     */
    public double getStandardTagesPreis() {
        return standardTagesPreis;
    }

    /**
     * Gibt die Beschreibung des Fahrzeugtyps zurück.
     * 
     * @return Beschreibung
     */
    public String getBeschreibung() {
        return beschreibung;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHersteller() {
        return hersteller;
    }

    public void setHersteller(String hersteller) {
        this.hersteller = hersteller;
    }

    public String getModellBezeichnung() {
        return modellBezeichnung;
    }

    public void setModellBezeichnung(String modellBezeichnung) {
        this.modellBezeichnung = modellBezeichnung;
    }

    public String getKategorie() {
        return kategorie;
    }

    public void setKategorie(String kategorie) {
        this.kategorie = kategorie;
    }

    public void setStandardTagesPreis(double standardTagesPreis) {
        this.standardTagesPreis = standardTagesPreis;
    }

    public int getSitzplaetze() {
        return sitzplaetze;
    }

    public void setSitzplaetze(int sitzplaetze) {
        this.sitzplaetze = sitzplaetze;
    }

    public Antriebsart getAntriebsart() {
        return antriebsart;
    }

    public void setAntriebsart(Antriebsart antriebsart) {
        this.antriebsart = antriebsart;
    }

    public int getReichweiteKm() {
        return reichweiteKm;
    }

    public void setReichweiteKm(int reichweiteKm) {
        this.reichweiteKm = reichweiteKm;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}
