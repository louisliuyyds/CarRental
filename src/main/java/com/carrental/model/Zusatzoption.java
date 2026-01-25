package com.carrental.model;

/**
 * Repräsentiert eine Zusatzoption für einen Mietvertrag (z.B. Kindersitz, Navi).
 * Wird einem Mietvertrag zugeordnet und erhöht den Tagespreis.
 */
public class Zusatzoption {

    private int id;
    private String bezeichnung;
    private double aufpreis;
    private String beschreibung;

    /**
     * Leerer Konstruktor für flexible Initialisierung.
     */
    public Zusatzoption() {
    }

    /**
     * Konstruktor mit wichtigsten Parametern.
     * 
     * @param bezeichnung Bezeichnung der Option
     * @param aufpreis Aufpreis pro Tag
     */
    public Zusatzoption(String bezeichnung, double aufpreis) {
        this.bezeichnung = bezeichnung;
        this.aufpreis = aufpreis;
    }

    /**
     * Gibt den Aufpreis der Zusatzoption zurück.
     * 
     * @return Aufpreis
     */
    public double getAufpreis() {
        return aufpreis;
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public void setAufpreis(double aufpreis) {
        this.aufpreis = aufpreis;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }
}
