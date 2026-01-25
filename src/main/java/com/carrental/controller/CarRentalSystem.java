package com.carrental.controller;

import com.carrental.dao.*;
import com.carrental.model.*;
import com.carrental.util.DatabaseConfig;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Zentrale Singleton-Klasse zur Steuerung des Autovermietungssystems.
 * Verwaltet den Zugriff auf DAOs und bietet zentrale Geschäftslogik.
 * 
 * Implementiert das Singleton-Pattern für globalen Zugriff.
 */
public class CarRentalSystem {

    private static CarRentalSystem instance;
    private static final Object lock = new Object();
    
    // DAOs für Datenbankzugriff
    private final KundeDao kundeDao;
    private final FahrzeugDao fahrzeugDao;
    private final MietvertragDao mietvertragDao;
    private final ZusatzoptionDao zusatzoptionDao;
    
    // In-Memory-Listen für schnellen Zugriff (optional, je nach Implementierung)
    private List<Kunde> kundenListe;
    private List<Fahrzeug> fahrzeugListe;
    private List<Mietvertrag> mietvertraege;

    /**
     * Privater Konstruktor für Singleton-Pattern.
     * Initialisiert die DAOs mit der Datenbankkonfiguration.
     */
    private CarRentalSystem() throws IOException {
        DatabaseConfig config = DatabaseConfig.load("config.properties");
        
        this.kundeDao = new KundeDao(config);
        this.fahrzeugDao = new FahrzeugDao(config);
        this.mietvertragDao = new MietvertragDao(config);
        this.zusatzoptionDao = new ZusatzoptionDao(config);
        
        // Listen initialisieren
        this.kundenListe = new ArrayList<>();
        this.fahrzeugListe = new ArrayList<>();
        this.mietvertraege = new ArrayList<>();
        
        // Optional: Daten aus DB laden
        loadInitialData();
    }

    /**
     * Gibt die Singleton-Instanz des CarRentalSystem zurück.
     * Thread-sicher durch doppelte Überprüfung mit Lock.
     * 
     * @return Die einzige Instanz des Systems
     * @throws RuntimeException wenn Initialisierung fehlschlägt
     */
    public static CarRentalSystem getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    try {
                        instance = new CarRentalSystem();
                    } catch (IOException e) {
                        throw new RuntimeException("Fehler beim Initialisieren des CarRentalSystem: " + e.getMessage(), e);
                    }
                }
            }
        }
        return instance;
    }

    /**
     * Lädt initiale Daten aus der Datenbank (optional).
     */
    private void loadInitialData() {
        try {
            // Test-Kunden laden
            var allCustomers = kundeDao.findAll();
            System.out.println("✓ " + allCustomers.size() + " Kunden geladen.");
        } catch (Exception e) {
            System.err.println("⚠ Warnung: Initiale Daten konnten nicht geladen werden: " + e.getMessage());
            // Nicht fatal – App läuft trotzdem
        }
    }

    /**
     * Sucht einen Kunden anhand der Kundennummer.
     * 
     * @param nr Die Kundennummer
     * @return Der gefundene Kunde oder null
     */
    public Kunde getKundeByNr(int nr) {
        try {
            Optional<Kunde> kunde = kundeDao.findByKundennummer(nr);
            return kunde.orElse(null);
        } catch (SQLException e) {
            System.err.println("Fehler beim Abrufen des Kunden: " + e.getMessage());
            return null;
        }
    }

    /**
     * Sucht ein Fahrzeug anhand des Kennzeichens.
     * 
     * @param kennzeichen Das Kennzeichen
     * @return Das gefundene Fahrzeug oder null
     */
    public Fahrzeug getFahrzeugByKennzeichen(String kennzeichen) {
        try {
            Optional<Fahrzeug> fahrzeug = fahrzeugDao.findByKennzeichen(kennzeichen);
            return fahrzeug.orElse(null);
        } catch (SQLException e) {
            System.err.println("Fehler beim Abrufen des Fahrzeugs: " + e.getMessage());
            return null;
        }
    }

    /**
     * Erstellt einen neuen Mietvertrag.
     * 
     * @param kunde Der Kunde
     * @param fahrzeug Das Fahrzeug
     * @param startDatum Startdatum
     * @param endDatum Enddatum
     * @return Der erstellte Mietvertrag oder null bei Fehler
     */
    public Mietvertrag createMietvertrag(Kunde kunde, Fahrzeug fahrzeug, 
                                         java.time.LocalDate startDatum, 
                                         java.time.LocalDate endDatum) {
        try {
            Mietvertrag vertrag = new Mietvertrag(kunde, fahrzeug, startDatum, endDatum);
            vertrag = mietvertragDao.create(vertrag);
            mietvertraege.add(vertrag);
            return vertrag;
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen des Mietvertrags: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gibt alle verfügbaren Fahrzeuge zurück.
     * 
     * @return Liste der verfügbaren Fahrzeuge
     */
    public List<Fahrzeug> getVerfuegbareFahrzeuge() {
        try {
            return fahrzeugDao.findVerfuegbare();
        } catch (SQLException e) {
            System.err.println("Fehler beim Abrufen verfügbarer Fahrzeuge: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gibt alle Kunden zurück.
     * 
     * @return Liste aller Kunden
     */
    public List<Kunde> getAlleKunden() {
        return new ArrayList<>(kundenListe);
    }

    /**
     * Gibt alle Fahrzeuge zurück.
     * 
     * @return Liste aller Fahrzeuge
     */
    public List<Fahrzeug> getAlleFahrzeuge() {
        return new ArrayList<>(fahrzeugListe);
    }

    /**
     * Gibt alle Mietverträge zurück.
     * 
     * @return Liste aller Mietverträge
     */
    public List<Mietvertrag> getAlleMietvertraege() {
        return new ArrayList<>(mietvertraege);
    }

    // Getter für DAOs (für fortgeschrittene Nutzung)
    
    public KundeDao getKundeDao() {
        return kundeDao;
    }

    public FahrzeugDao getFahrzeugDao() {
        return fahrzeugDao;
    }

    public MietvertragDao getMietvertragDao() {
        return mietvertragDao;
    }

    public ZusatzoptionDao getZusatzoptionDao() {
        return zusatzoptionDao;
    }

    /**
     * Lädt die Daten neu aus der Datenbank.
     */
    public void reloadData() {
        loadInitialData();
    }
}
