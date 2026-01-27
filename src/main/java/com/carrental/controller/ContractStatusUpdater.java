package com.carrental.controller;

import com.carrental.dao.FahrzeugDao;
import com.carrental.dao.MietvertragDao;
import com.carrental.model.Fahrzeug;
import com.carrental.model.FahrzeugZustand;
import com.carrental.model.Mietvertrag;
import com.carrental.model.VertragsStatus;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * Automatisch aktualisiert Vertrags- und Fahrzeugstatus basierend auf dem Datum.
 * 
 * Checkt regelmäßig:
 * - BESTAETIGT -> LAUFEND, wenn Startdatum erreicht
 * - LAUFEND -> ABGESCHLOSSEN, wenn Enddatum überschritten
 * - Fahrzeugstatus, wenn keine aktiven Verträge mehr existieren
 */
public class ContractStatusUpdater {
    
    private static final Logger LOGGER = Logger.getLogger(ContractStatusUpdater.class.getName());
    
    private final CarRentalSystem system;
    private final MietvertragDao mietvertragDao;
    private final FahrzeugDao fahrzeugDao;
    private final Timer updateTimer;
    
    public ContractStatusUpdater(CarRentalSystem system) {
        this.system = system;
        this.mietvertragDao = system.getMietvertragDao();
        this.fahrzeugDao = system.getFahrzeugDao();
        this.updateTimer = new Timer("ContractStatusUpdater", true);
    }
    
    public int updateAllStatuses() {
        int updatedCount = 0;
        LocalDate heute = LocalDate.now();
        
        LOGGER.info("Statusaktualisierung gestartet. Heutiges Datum: " + heute);
        
        try {
            List<Mietvertrag> allVertraege = mietvertragDao.findAll();
            LOGGER.info("Verarbeite " + allVertraege.size() + " Verträge");
            
            for (Mietvertrag vertrag : allVertraege) {
                boolean updated = false;
                
                LOGGER.info("Prüfe Vertrag " + vertrag.getMietnummer() + ": Status=" + vertrag.getStatus() + 
                           ", Start=" + vertrag.getStartDatum() + ", End=" + vertrag.getEndDatum());
                
                // BESTAETIGT -> LAUFEND
                if (vertrag.getStatus() == VertragsStatus.BESTAETIGT) {
                    if (!vertrag.getStartDatum().isAfter(heute)) {
                        vertrag.setStatus(VertragsStatus.LAUFEND);
                        mietvertragDao.updateStatus(vertrag.getId(), vertrag.getStatus().name());
                        LOGGER.info("✓ Vertrag " + vertrag.getMietnummer() + " von BESTAETIGT zu LAUFEND aktualisiert (Startdatum erreicht)");
                        updated = true;
                    } else {
                        LOGGER.info("- Vertrag " + vertrag.getMietnummer() + " bleibt BESTAETIGT (Startdatum noch nicht erreicht)");
                    }
                }
                
                // LAUFEND -> ABGESCHLOSSEN
                if (vertrag.getStatus() == VertragsStatus.LAUFEND) {
                    if (vertrag.getEndDatum().isBefore(heute)) {
                        vertrag.setStatus(VertragsStatus.ABGESCHLOSSEN);
                        mietvertragDao.updateStatus(vertrag.getId(), vertrag.getStatus().name());
                        
                        Fahrzeug fahrzeug = vertrag.getFahrzeug();
                        if (fahrzeug != null && !hasAktiveVertraegeForFahrzeug(fahrzeug, vertrag.getId())) {
                            fahrzeug.setZustand(FahrzeugZustand.VERFUEGBAR);
                            fahrzeugDao.updateStatusAndKilometerstand(fahrzeug);
                            LOGGER.info("✓ Fahrzeug " + fahrzeug.getKennzeichen() + " zu VERFUEGBAR aktualisiert");
                        } else if (fahrzeug != null) {
                            LOGGER.info("- Fahrzeug " + fahrzeug.getKennzeichen() + " bleibt VERMIETET (hat noch andere aktive Verträge)");
                        }
                        
                        LOGGER.info("✓ Vertrag " + vertrag.getMietnummer() + " von LAUFEND zu ABGESCHLOSSEN aktualisiert (Enddatum überschritten)");
                        updated = true;
                    } else {
                        LOGGER.info("- Vertrag " + vertrag.getMietnummer() + " bleibt LAUFEND noch aktiv");
                    }
                }
                
                if (updated) {
                    updatedCount++;
                }
            }
            
            LOGGER.info("Statusaktualisierung abgeschlossen: " + updatedCount + " Verträge aktualisiert");
            
        } catch (SQLException e) {
            LOGGER.severe("Fehler bei Statusaktualisierung: " + e.getMessage());
            e.printStackTrace();
        }
        
        return updatedCount;
    }
    
    /**
     * Prüft, ob ein Fahrzeug noch aktive Verträge hat (außer dem angegebenen Vertrag).
     */
    private boolean hasAktiveVertraegeForFahrzeug(Fahrzeug fahrzeug, int excludeVertragId) {
        try {
            List<Mietvertrag> allVertraege = mietvertragDao.findAll();
            for (Mietvertrag v : allVertraege) {
                if (v.getId() == excludeVertragId) {
                    continue;
                }
                if (v.getFahrzeug() != null && v.getFahrzeug().getId() == fahrzeug.getId() 
                    && isAktiverVertrag(v, LocalDate.now())) {
                    return true;
                }
            }
        } catch (SQLException e) {
            LOGGER.warning("Fehler bei Fahrzeug-Vertragsprüfung: " + e.getMessage());
            return true;
        }
        return false;
    }
    
    /**
     * Prüft, ob ein Vertrag zu einem bestimmten Datum aktiv ist.
     */
    private boolean isAktiverVertrag(Mietvertrag vertrag, LocalDate datum) {
        VertragsStatus status = vertrag.getStatus();
        if (status == VertragsStatus.STORNIERT || status == VertragsStatus.ABGESCHLOSSEN) {
            return false;
        }
        
        return !datum.isBefore(vertrag.getStartDatum()) && !datum.isAfter(vertrag.getEndDatum());
    }
    
    public void startUpdater(int checkIntervalMinutes) {
        LOGGER.info("ContractStatusUpdater wird gestartet. Intervall: " + checkIntervalMinutes + " Minuten");
        LOGGER.info("Erster Check in 10 Sekunden, danach alle " + checkIntervalMinutes + " Minuten");
        
        updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                LOGGER.info("=== Automatische Statusaktualisierung gestartet ===");
                updateAllStatuses();
            }
        }, 10000L, (long) checkIntervalMinutes * 60 * 1000);
        
        LOGGER.info("✓ ContractStatusUpdater erfolgreich gestartet");
    }
    
    public void startUpdater() {
        startUpdater(60);
    }
    
    public void stopUpdater() {
        LOGGER.info("ContractStatusUpdater wird gestoppt");
        updateTimer.cancel();
        updateTimer.purge();
        LOGGER.info("✓ ContractStatusUpdater gestoppt");
    }
}
