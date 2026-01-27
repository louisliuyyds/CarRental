package com.carrental.controller;

import com.carrental.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Controller für die Buchungslogik.
 * Behandelt die Erstellung von Mietverträgen, Verfügbarkeitsprüfung,
 * Konfliktdetektion und Preisberechnung.
 */
public class BookingController {

    private final CarRentalSystem system;

    /**
     * Konstruktor für BookingController.
     * 
     * @param system Die CarRentalSystem-Instanz
     */
    public BookingController(CarRentalSystem system) {
        this.system = system;
    }

    /**
     * Erstellt einen neuen Mietvertrag nach Prüfung aller Voraussetzungen.
     * 
     * @param kunde Der Kunde
     * @param fahrzeug Das zu mietende Fahrzeug
     * @param startDatum Startdatum der Miete
     * @param endDatum Enddatum der Miete
     * @param zusatzoptionen Liste der gewünschten Zusatzoptionen
     * @return Der erstellte Mietvertrag oder null bei Fehler
     */
    public Mietvertrag buchungErstellen(Kunde kunde, Fahrzeug fahrzeug, 
                                       LocalDate startDatum, LocalDate endDatum,
                                       List<Zusatzoption> zusatzoptionen) {
        
        // Validierung der Eingabeparameter
        if (kunde == null || fahrzeug == null || startDatum == null || endDatum == null) {
            System.err.println("Ungültige Parameter für Buchung.");
            return null;
        }

        // 1. Datumsvalidierung
        if (!validateDates(startDatum, endDatum)) {
            return null;
        }

        // 2. Kundenvalidierung
        if (!validateKunde(kunde)) {
            return null;
        }

        // 3. Verfügbarkeitsprüfung mit Konfliktdetektion
        if (!isFahrzeugVerfuegbar(fahrzeug, startDatum, endDatum)) {
            System.err.println("Fahrzeug ist im gewählten Zeitraum nicht verfügbar.");
            return null;
        }

        // 4. Kunden-Konfliktprüfung: Kunde darf nicht parallel mehrere Buchungen haben
        if (hasKundeOverlap(kunde, startDatum, endDatum)) {
            System.err.println("Kunde hat bereits eine Buchung im gewählten Zeitraum.");
            return null;
        }

        try {
            // Mietvertrag erstellen
            Mietvertrag vertrag = new Mietvertrag(kunde, fahrzeug, startDatum, endDatum);
            
            // Eindeutige Mietnummer generieren
            vertrag.setMietnummer(generateMietnummer());
            
            // Zusatzoptionen hinzufügen
            if (zusatzoptionen != null && !zusatzoptionen.isEmpty()) {
                for (Zusatzoption option : zusatzoptionen) {
                    vertrag.addZusatzoption(option);
                }
            }
            
            // Preis berechnen
            double preis = calculateGesamtpreis(vertrag);
            vertrag.setGesamtPreis(preis);
            
            // In Datenbank speichern
            vertrag = system.getMietvertragDao().create(vertrag);

            // Fahrzeugstatus aktualisieren - verwendet spezialisierte Methode ohne Fahrzeugtyp
            fahrzeug.setZustand(FahrzeugZustand.VERMIETET);
            system.getFahrzeugDao().updateStatusAndKilometerstand(fahrzeug);

            // Vertragsstatus aktualisieren
            vertrag.setStatus(VertragsStatus.BESTAETIGT);
            system.getMietvertragDao().update(vertrag);
            
            System.out.println("Buchung erfolgreich erstellt: " + vertrag.getMietnummer());
            return vertrag;
            
        } catch (SQLException e) {
            System.err.println("Fehler beim Erstellen der Buchung: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            e.printStackTrace(System.err);
            return null;
        }
    }

    /**
     * Prüft ob ein Fahrzeug in einem bestimmten Zeitraum verfügbar ist.
     * Implementiert Konfliktdetektion durch Prüfung überlappender Mietverträge.
     * 
     * @param fahrzeug Das zu prüfende Fahrzeug
     * @param startDatum Startdatum
     * @param endDatum Enddatum
     * @return true wenn verfügbar, false bei Konflikt
     */
    public boolean isFahrzeugVerfuegbar(Fahrzeug fahrzeug, LocalDate startDatum, LocalDate endDatum) {
        if (fahrzeug == null || startDatum == null || endDatum == null) {
            return false;
        }

        // 1. Grundstatus prüfen
        if (fahrzeug.getZustand() != FahrzeugZustand.VERFUEGBAR) {
            // Nur wenn Status WARTUNG ist, definitiv nicht verfügbar
            if (fahrzeug.getZustand() == FahrzeugZustand.WARTUNG) {
                return false;
            }
            // Bei VERMIETET: weiter prüfen ob Zeitraum frei
        }

        // 2. Konfliktdetektion: Prüfe alle existierenden Mietverträge für dieses Fahrzeug
        try {
            List<Mietvertrag> allVertraege = system.getMietvertragDao().findAll();
            
            for (Mietvertrag vertrag : allVertraege) {
                // Nur aktive Verträge prüfen
                if (vertrag.getFahrzeug() != null && 
                    vertrag.getFahrzeug().getId() == fahrzeug.getId() &&
                    isAktiverVertrag(vertrag)) {
                    
                    // Prüfe auf Zeitraum-Überlappung
                    if (hasDateOverlap(startDatum, endDatum, 
                                      vertrag.getStartDatum(), vertrag.getEndDatum())) {
                        return false; // Konflikt gefunden
                    }
                }
            }
            
            return true; // Kein Konflikt gefunden
            
        } catch (SQLException e) {
            System.err.println("Fehler bei Verfügbarkeitsprüfung: " + e.getMessage());
            return false; // Im Fehlerfall konservativ: nicht verfügbar
        }
    }

    /**
     * Prüft ob zwei Zeiträume sich überlappen.
     * 
     * @param start1 Start des ersten Zeitraums
     * @param end1 Ende des ersten Zeitraums
     * @param start2 Start des zweiten Zeitraums
     * @param end2 Ende des zweiten Zeitraums
     * @return true wenn Überlappung existiert
     */
    private boolean hasDateOverlap(LocalDate start1, LocalDate end1, 
                                   LocalDate start2, LocalDate end2) {
        // Zeiträume überlappen wenn:
        // start1 <= end2 UND end1 >= start2
        return !start1.isAfter(end2) && !end1.isBefore(start2);
    }

    /**
     * Prüft ob ein Vertrag aktiv ist (nicht storniert oder abgeschlossen).
     * 
     * @param vertrag Der zu prüfende Vertrag
     * @return true wenn aktiv
     */
    private boolean isAktiverVertrag(Mietvertrag vertrag) {
        VertragsStatus status = vertrag.getStatus();
        return status != VertragsStatus.STORNIERT && 
               status != VertragsStatus.ABGESCHLOSSEN;
    }

    /**
     * Berechnet den Gesamtpreis für einen Mietvertrag.
     * Berücksichtigt Fahrzeugtyp-Tagespreis, Mietdauer und Zusatzoptionen.
     * 
     * @param vertrag Der Mietvertrag
     * @return Gesamtpreis
     */
    public double calculateGesamtpreis(Mietvertrag vertrag) {
        if (vertrag == null || vertrag.getFahrzeug() == null || 
            vertrag.getFahrzeug().getFahrzeugtyp() == null) {
            return 0.0;
        }

        // Basis: Tagespreis * Anzahl Tage
        int tage = vertrag.getMietdauerTage();
        double basisPreis = tage * vertrag.getFahrzeug().getFahrzeugtyp().getStandardTagesPreis();

        // Zusatzoptionen addieren (pro Tag)
        double zusatzkosten = 0.0;
        if (vertrag.getZusatzoptionen() != null) {
            for (Zusatzoption option : vertrag.getZusatzoptionen()) {
                zusatzkosten += option.getAufpreis() * tage;
            }
        }

        double gesamtpreis = basisPreis + zusatzkosten;

        // Optional: Rabatte für längere Mietdauer
        gesamtpreis = applyDiscounts(gesamtpreis, tage);

        return Math.round(gesamtpreis * 100.0) / 100.0; // Auf 2 Nachkommastellen runden
    }

    /**
     * Wendet Rabatte basierend auf der Mietdauer an.
     * 
     * @param preis Ursprünglicher Preis
     * @param tage Anzahl der Miettage
     * @return Rabattierter Preis
     */
    private double applyDiscounts(double preis, int tage) {
        // Beispiel-Rabattstaffel:
        // 7-13 Tage: 5% Rabatt
        // 14-29 Tage: 10% Rabatt
        // 30+ Tage: 15% Rabatt
        
        if (tage >= 30) {
            return preis * 0.85; // 15% Rabatt
        } else if (tage >= 14) {
            return preis * 0.90; // 10% Rabatt
        } else if (tage >= 7) {
            return preis * 0.95; // 5% Rabatt
        }
        
        return preis; // Kein Rabatt
    }

    /**
     * Storniert einen Mietvertrag.
     * 
     * @param vertrag Der zu stornierende Vertrag
     * @return true bei Erfolg
     */
    public boolean buchungStornieren(Mietvertrag vertrag) {
        if (vertrag == null) {
            return false;
        }

        try {
            // Vertrag stornieren
            vertrag.stornieren();
            
            // Nur den Status aktualisieren (vermeidet Foreign-Key-Probleme)
            system.getMietvertragDao().updateStatus(vertrag.getId(), vertrag.getStatus().name());

            // Fahrzeugstatus ggf. aktualisieren
            if (vertrag.getFahrzeug() != null) {
                Fahrzeug fahrzeug = vertrag.getFahrzeug();
                if (!hasAktiveVertraegeForFahrzeug(fahrzeug, vertrag.getId())) {
                    fahrzeug.setZustand(FahrzeugZustand.VERFUEGBAR);
                    system.getFahrzeugDao().updateStatusAndKilometerstand(fahrzeug);
                }
            }
            
            System.out.println("Buchung storniert: " + vertrag.getMietnummer());
            return true;
            
        } catch (SQLException e) {
            System.err.println("Fehler beim Stornieren: " + e.getMessage());
            return false;
        }
    }

    /**
     * Schließt einen Mietvertrag ab (nach Fahrzeugrückgabe).
     * 
     * @param vertrag Der abzuschließende Vertrag
     * @param kilometerstand Aktueller Kilometerstand bei Rückgabe
     * @return true bei Erfolg
     */
    public boolean buchungAbschliessen(Mietvertrag vertrag, int kilometerstand) {
        if (vertrag == null) {
            return false;
        }

        try {
            // Vertrag abschließen
            vertrag.abschliessen();
             
            // Kilometerstand und Status aktualisieren
            if (vertrag.getFahrzeug() != null) {
                vertrag.getFahrzeug().setAktuellerKilometerstand(kilometerstand);
                vertrag.getFahrzeug().setZustand(FahrzeugZustand.VERFUEGBAR);
                system.getFahrzeugDao().updateStatusAndKilometerstand(vertrag.getFahrzeug());
            }
            
            // Vertrag in DB aktualisieren
            system.getMietvertragDao().update(vertrag);
            
            System.out.println("Buchung abgeschlossen: " + vertrag.getMietnummer());
            return true;
            
        } catch (SQLException e) {
            System.err.println("Fehler beim Abschließen: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gibt alle verfügbaren Fahrzeuge für einen bestimmten Zeitraum zurück.
     * 
     * @param startDatum Startdatum
     * @param endDatum Enddatum
     * @return Liste verfügbarer Fahrzeuge
     */
    public List<Fahrzeug> getVerfuegbareFahrzeugeInZeitraum(LocalDate startDatum, LocalDate endDatum) {
        List<Fahrzeug> verfuegbare = new ArrayList<>();
        
        try {
            List<Fahrzeug> alleFahrzeuge = system.getFahrzeugDao().findAll();
            
            for (Fahrzeug fahrzeug : alleFahrzeuge) {
                if (isFahrzeugVerfuegbar(fahrzeug, startDatum, endDatum)) {
                    verfuegbare.add(fahrzeug);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Fehler beim Abrufen verfügbarer Fahrzeuge: " + e.getMessage());
        }
        
        return verfuegbare;
    }

    /**
     * Validiert die Datumsangaben für eine Buchung.
     */
    private boolean validateDates(LocalDate startDatum, LocalDate endDatum) {
        LocalDate heute = LocalDate.now();
        
        // Startdatum darf nicht in der Vergangenheit liegen
        if (startDatum.isBefore(heute)) {
            System.err.println("Startdatum darf nicht in der Vergangenheit liegen.");
            return false;
        }
        
        // Enddatum muss nach Startdatum liegen
        if (!endDatum.isAfter(startDatum)) {
            System.err.println("Enddatum muss nach dem Startdatum liegen.");
            return false;
        }
        
        // Mindestmietdauer: 1 Tag (bereits durch obige Prüfung erfüllt)
        // Maximale Mietdauer: z.B. 90 Tage
        long tage = ChronoUnit.DAYS.between(startDatum, endDatum);
        if (tage > 90) {
            System.err.println("Maximale Mietdauer von 90 Tagen überschritten.");
            return false;
        }
        
        return true;
    }

    /**
     * Validiert den Kunden für eine Buchung.
     */
    private boolean validateKunde(Kunde kunde) {
        // Kunde muss aktiv sein
        if (!kunde.isIstAktiv()) {
            System.err.println("Kundenkonto ist nicht aktiv.");
            return false;
        }
        
        // Kunde muss volljährig sein
        if (!kunde.istVolljaehrig()) {
            System.err.println("Kunde muss volljährig sein (mindestens 18 Jahre).");
            return false;
        }
        
        // Führerschein muss vorhanden sein
        if (kunde.getFuehrerscheinNummer() == null || kunde.getFuehrerscheinNummer().isBlank()) {
            System.err.println("Kunde benötigt eine gültige Führerscheinnummer.");
            return false;
        }
        
        return true;
    }

    /**
     * Prüft, ob der Kunde bereits eine überlappende Buchung hat.
     */
    private boolean hasKundeOverlap(Kunde kunde, LocalDate startDatum, LocalDate endDatum) {
        try {
            List<Mietvertrag> vertraege = system.getMietvertragDao().findByKunde(kunde.getKundennummer());
            for (Mietvertrag v : vertraege) {
                if (isAktiverVertrag(v) &&
                    hasDateOverlap(startDatum, endDatum, v.getStartDatum(), v.getEndDatum())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei Kunden-Konfliktprüfung: " + e.getMessage());
            return true; // im Fehlerfall konservativ
        }
        return false;
    }

    /**
     * Prüft, ob für das Fahrzeug noch aktive Verträge existieren (außer dem angegebenen Vertrag).
     */
    private boolean hasAktiveVertraegeForFahrzeug(Fahrzeug fahrzeug, int excludeVertragId) {
        try {
            List<Mietvertrag> allVertraege = system.getMietvertragDao().findAll();
            for (Mietvertrag v : allVertraege) {
                if (v.getId() == excludeVertragId) {
                    continue;
                }
                if (v.getFahrzeug() != null && v.getFahrzeug().getId() == fahrzeug.getId() && isAktiverVertrag(v)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Fehler bei Fahrzeug-Vertragsprüfung: " + e.getMessage());
            return true; // konservativ
        }
        return false;
    }

    /**
     * Generiert eine eindeutige Mietnummer.
     * 
     * @return Eindeutige Mietnummer
     */
    private String generateMietnummer() {
        // Format: MV-YYYYMMDD-XXXX
        LocalDate heute = LocalDate.now();
        String datumPart = String.format("%04d%02d%02d", 
            heute.getYear(), heute.getMonthValue(), heute.getDayOfMonth());
        String randomPart = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        
        return "MV-" + datumPart + "-" + randomPart;
    }
}
