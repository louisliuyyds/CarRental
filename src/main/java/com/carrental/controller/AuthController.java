package com.carrental.controller;

import com.carrental.model.Benutzer;
import com.carrental.model.Kunde;
import com.carrental.model.Mitarbeiter;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller für Authentifizierungs- und Benutzerverwaltungslogik.
 * Behandelt Login, Registrierung und Kontoverwaltung.
 */
public class AuthController {

    private final CarRentalSystem system;
    private Benutzer currentUser; // Aktuell angemeldeter Benutzer
    private final Map<String, Mitarbeiter> mitarbeiterAccounts;

    /**
     * Konstruktor für AuthController.
     * 
     * @param system Die CarRentalSystem-Instanz
     */
    public AuthController(CarRentalSystem system) {
        this.system = system;
        this.currentUser = null;
        this.mitarbeiterAccounts = new HashMap<>();
        seedDefaultMitarbeiter();
    }

    /**
     * Meldet einen Benutzer anhand des Account-Namens und Passworts an.
     * 
     * @param accountName Der Account-Name
     * @param passwort Das Passwort
     * @return true wenn Login erfolgreich, sonst false
     */
    public boolean login(String accountName, String passwort) {
        return login(accountName, passwort, false);
    }

    public boolean login(String accountName, String passwort, boolean alsMitarbeiter) {
        if (accountName == null || passwort == null || accountName.isBlank() || passwort.isBlank()) {
            return false;
        }

        if (alsMitarbeiter) {
            Mitarbeiter m = mitarbeiterAccounts.get(accountName);
            if (m != null && m.login(passwort)) {
                this.currentUser = m;
                return true;
            }
            return false;
        }

        try {
            Optional<Kunde> kunde = system.getKundeDao().findByAccountName(accountName);
            if (kunde.isPresent()) {
                if (kunde.get().login(passwort)) {
                    if (kunde.get().isIstAktiv()) {
                        this.currentUser = kunde.get();
                        return true;
                    } else {
                        System.err.println("Konto ist deaktiviert.");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Datenbankfehler beim Login: " + e.getMessage());
        }

        return false;
    }

    /**
     * Meldet den aktuellen Benutzer ab.
     */
    public void logout() {
        this.currentUser = null;
    }

    /**
     * Registriert einen neuen Kunden.
     * 
     * @param accountName Account-Name (muss eindeutig sein)
     * @param passwort Passwort
     * @param vorname Vorname
     * @param nachname Nachname
     * @param email E-Mail-Adresse
     * @param geburtstag Geburtsdatum
     * @param fuehrerscheinNummer Führerscheinnummer
     * @return Der registrierte Kunde oder null bei Fehler
     */
    public Kunde registrieren(String accountName, String passwort, String vorname, 
                             String nachname, String email, LocalDate geburtstag, 
                             String fuehrerscheinNummer) {
        
        // Validierung
        if (accountName == null || accountName.isBlank()) {
            System.err.println("Account-Name darf nicht leer sein.");
            return null;
        }
        
        if (passwort == null || passwort.length() < 6) {
            System.err.println("Passwort muss mindestens 6 Zeichen lang sein.");
            return null;
        }

        try {
            // Prüfen ob Account-Name bereits existiert
            Optional<Kunde> existing = system.getKundeDao().findByAccountName(accountName);
            if (existing.isPresent()) {
                System.err.println("Account-Name bereits vergeben.");
                return null;
            }

            // Neue Kundennummer generieren (einfache Implementierung)
            int neueKundennummer = generateKundennummer();

            // Kunde erstellen
            Kunde kunde = new Kunde(neueKundennummer, accountName, passwort, vorname, nachname, email);
            kunde.setGeburtstag(geburtstag);
            kunde.setFuehrerscheinNummer(fuehrerscheinNummer);

            // Volljährigkeit prüfen
            if (!kunde.istVolljaehrig()) {
                System.err.println("Kunde muss volljährig sein (mindestens 18 Jahre).");
                return null;
            }

            // In Datenbank speichern
            kunde = system.getKundeDao().create(kunde);
            kunde.registrieren(); // Status auf aktiv setzen

            System.out.println("Kunde erfolgreich registriert: " + accountName);
            return kunde;

        } catch (SQLException e) {
            System.err.println("Fehler bei der Registrierung: " + e.getMessage());
            return null;
        }
    }

    /**
     * Ändert das Passwort des aktuell angemeldeten Benutzers.
     * 
     * @param altesPasswort Das alte Passwort zur Verifizierung
     * @param neuesPasswort Das neue Passwort
     * @return true bei Erfolg, false bei Fehler
     */
    public boolean passwortAendern(String altesPasswort, String neuesPasswort) {
        if (currentUser == null) {
            System.err.println("Kein Benutzer angemeldet.");
            return false;
        }

        if (!currentUser.login(altesPasswort)) {
            System.err.println("Altes Passwort ist falsch.");
            return false;
        }

        if (neuesPasswort == null || neuesPasswort.length() < 6) {
            System.err.println("Neues Passwort muss mindestens 6 Zeichen lang sein.");
            return false;
        }

        try {
            currentUser.setPasswort(neuesPasswort);

            // In Datenbank aktualisieren
            if (currentUser instanceof Kunde) {
                return system.getKundeDao().update((Kunde) currentUser);
            }
            // TODO: Mitarbeiter-Update implementieren

        } catch (SQLException e) {
            System.err.println("Fehler beim Passwort ändern: " + e.getMessage());
        }

        return false;
    }

    /**
     * Löscht das Konto des aktuell angemeldeten Kunden (Soft-Delete).
     * 
     * @return true bei Erfolg, false bei Fehler
     */
    public boolean kontoLoeschen() {
        if (currentUser == null || !(currentUser instanceof Kunde)) {
            System.err.println("Nur Kunden können ihr Konto löschen.");
            return false;
        }

        try {
            Kunde kunde = (Kunde) currentUser;
            kunde.kontoLoeschen();
            
            boolean erfolg = system.getKundeDao().softDeleteByKundennummer(kunde.getKundennummer());
            
            if (erfolg) {
                logout(); // Benutzer abmelden
                System.out.println("Konto erfolgreich gelöscht.");
            }
            
            return erfolg;

        } catch (SQLException e) {
            System.err.println("Fehler beim Löschen des Kontos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gibt den aktuell angemeldeten Benutzer zurück.
     * 
     * @return Der angemeldete Benutzer oder null
     */
    public Benutzer getCurrentUser() {
        return currentUser;
    }

    /**
     * Prüft ob ein Benutzer angemeldet ist.
     * 
     * @return true wenn ein Benutzer angemeldet ist
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Prüft ob der angemeldete Benutzer ein Kunde ist.
     * 
     * @return true wenn der Benutzer ein Kunde ist
     */
    public boolean isKunde() {
        return currentUser instanceof Kunde;
    }

    /**
     * Prüft ob der angemeldete Benutzer ein Mitarbeiter ist.
     * 
     * @return true wenn der Benutzer ein Mitarbeiter ist
     */
    public boolean isMitarbeiter() {
        return currentUser instanceof Mitarbeiter;
    }

    /**
     * Gibt den aktuell angemeldeten Kunden zurück.
     * 
     * @return Der Kunde oder null
     */
    public Kunde getCurrentKunde() {
        if (currentUser instanceof Kunde) {
            return (Kunde) currentUser;
        }
        return null;
    }

    /**
     * Gibt den aktuell angemeldeten Mitarbeiter zurück.
     * 
     * @return Der Mitarbeiter oder null
     */
    public Mitarbeiter getCurrentMitarbeiter() {
        if (currentUser instanceof Mitarbeiter) {
            return (Mitarbeiter) currentUser;
        }
        return null;
    }

    private void seedDefaultMitarbeiter() {
        // Minimal vordefinierte Mitarbeiter-Accounts (Username/Passwort)
        Mitarbeiter admin = new Mitarbeiter("P-1001", "admin", "admin123", "Admin", "User", "admin@carrental.local");
        mitarbeiterAccounts.put(admin.getAccountName(), admin);

        Mitarbeiter service = new Mitarbeiter("P-1002", "service", "service123", "Service", "Team", "service@carrental.local");
        mitarbeiterAccounts.put(service.getAccountName(), service);
    }

    /**
     * Generiert eine neue eindeutige Kundennummer.
     * Einfache Implementierung: findet die höchste existierende Nummer und addiert 1.
     * 
     * @return Neue Kundennummer
     */
    private int generateKundennummer() {
        try {
            var alleKunden = system.getKundeDao().findAll();
            int maxNr = 1000; // Startwert
            
            for (Kunde k : alleKunden) {
                if (k.getKundennummer() > maxNr) {
                    maxNr = k.getKundennummer();
                }
            }
            
            return maxNr + 1;
            
        } catch (SQLException e) {
            // Bei Fehler eine zufällige Nummer im Bereich 1000-9999 zurückgeben
            return 1000 + (int) (Math.random() * 8999);
        }
    }
}
