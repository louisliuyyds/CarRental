package com.carrental.model;

/**
 * Repräsentiert einen Mitarbeiter im Autovermietungssystem.
 * Erbt von Benutzer und erweitert um mitarbeiterspezifische Attribute.
 */
public class Mitarbeiter extends Benutzer {

    private int id;
    private String personalnummer;
    private int berechtigungsStufe;

    /**
     * Konstruktor für Mitarbeiter.
     * 
     * @param pNr Personalnummer
     * @param accountName Account-Name
     * @param passwort Passwort
     * @param vorname Vorname
     * @param nachname Nachname
     * @param email E-Mail-Adresse
     */
    public Mitarbeiter(String pNr, String accountName, String passwort, String vorname, String nachname, String email) {
        super(accountName, passwort, vorname, nachname, email);
        this.personalnummer = pNr;
        this.berechtigungsStufe = 1; // Standard-Berechtigungsstufe
    }

    /**
     * Erstellt einen Bericht (Platzhalter-Implementierung).
     * Kann später erweitert werden, um tatsächliche Reports zu generieren.
     */
    public void reportErstellen() {
        // TODO: Implementierung der Report-Erstellung
        System.out.println("Report wird erstellt von Mitarbeiter: " + personalnummer);
    }

    // Getter und Setter
    public String getPersonalnummer() {
        return personalnummer;
    }

    public void setPersonalnummer(String personalnummer) {
        this.personalnummer = personalnummer;
    }

    public int getBerechtigungsStufe() {
        return berechtigungsStufe;
    }

    public void setBerechtigungsStufe(int berechtigungsStufe) {
        this.berechtigungsStufe = berechtigungsStufe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
