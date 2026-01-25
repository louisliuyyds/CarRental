package com.carrental.model;

import java.time.LocalDate;
import java.time.Period;

/**
 * Repräsentiert einen Kunden im Autovermietungssystem.
 * Erbt von Benutzer und erweitert um kundenspezifische Attribute.
 */
public class Kunde extends Benutzer {

    private int kundennummer;
    private String strasse;
    private String hausnummer;
    private String plz;
    private String ort;
    private LocalDate geburtstag;
    private String fuehrerscheinNummer;
    private boolean istAktiv;

    /**
     * Konstruktor für Kunde.
     * 
     * @param nr Kundennummer
     * @param accountName Account-Name
     * @param passwort Passwort
     * @param vorname Vorname
     * @param nachname Nachname
     * @param email E-Mail-Adresse
     */
    public Kunde(int nr, String accountName, String passwort, String vorname, String nachname, String email) {
        super(accountName, passwort, vorname, nachname, email);
        this.kundennummer = nr;
        this.istAktiv = true; // Neuer Kunde ist standardmäßig aktiv
    }

    /**
     * Registriert den Kunden im System.
     * Setzt den Status auf aktiv.
     */
    public void registrieren() {
        this.istAktiv = true;
    }

    /**
     * Löscht das Kundenkonto (Soft-Delete durch Deaktivierung).
     */
    public void kontoLoeschen() {
        this.istAktiv = false;
    }

    /**
     * Prüft, ob der Kunde volljährig ist (mindestens 18 Jahre alt).
     * 
     * @return true wenn der Kunde volljährig ist, sonst false
     */
    public boolean istVolljaehrig() {
        if (geburtstag == null) {
            return false;
        }
        return Period.between(geburtstag, LocalDate.now()).getYears() >= 18;
    }

    // Getter und Setter
    public int getKundennummer() {
        return kundennummer;
    }

    public void setKundennummer(int kundennummer) {
        this.kundennummer = kundennummer;
    }

    public String getStrasse() {
        return strasse;
    }

    public void setStrasse(String strasse) {
        this.strasse = strasse;
    }

    public String getHausnummer() {
        return hausnummer;
    }

    public void setHausnummer(String hausnummer) {
        this.hausnummer = hausnummer;
    }

    public String getPlz() {
        return plz;
    }

    public void setPlz(String plz) {
        this.plz = plz;
    }

    public String getOrt() {
        return ort;
    }

    public void setOrt(String ort) {
        this.ort = ort;
    }

    public LocalDate getGeburtstag() {
        return geburtstag;
    }

    public void setGeburtstag(LocalDate geburtstag) {
        this.geburtstag = geburtstag;
    }

    public String getFuehrerscheinNummer() {
        return fuehrerscheinNummer;
    }

    public void setFuehrerscheinNummer(String fuehrerscheinNummer) {
        this.fuehrerscheinNummer = fuehrerscheinNummer;
    }

    public boolean isIstAktiv() {
        return istAktiv;
    }

    public void setIstAktiv(boolean istAktiv) {
        this.istAktiv = istAktiv;
    }
}
