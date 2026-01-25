package com.carrental.model;

/**
 * Abstrakte Basisklasse für alle Benutzer des Systems (Kunde und Mitarbeiter).
 * Enthält gemeinsame Attribute und Methoden für Login und Passwortverwaltung.
 */
public abstract class Benutzer {

    private String accountName;
    private String passwort;
    private String vorname;
    private String nachname;
    private String email;

    /**
     * Konstruktor für Benutzer.
     */
    protected Benutzer(String accountName, String passwort, String vorname, String nachname, String email) {
        this.accountName = accountName;
        this.passwort = passwort;
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
    }

    /**
     * Überprüft, ob das eingegebene Passwort korrekt ist.
     * 
     * @param pw Das zu prüfende Passwort
     * @return true wenn das Passwort korrekt ist, sonst false
     */
    public boolean login(String pw) {
        return this.passwort != null && this.passwort.equals(pw);
    }

    /**
     * Ändert das Passwort des Benutzers.
     * 
     * @param neu Das neue Passwort
     */
    public void setPasswort(String neu) {
        this.passwort = neu;
    }

    // Getter und Setter
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPasswort() {
        return passwort;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
