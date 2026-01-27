package com.carrental.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Repräsentiert einen Mietvertrag zwischen einem Kunden und einem Fahrzeug.
 * Enthält alle relevanten Informationen zur Miete inklusive Zusatzoptionen.
 */
public class Mietvertrag {

    private int id;
    private String mietnummer;
    private LocalDate startDatum;
    private LocalDate endDatum;
    private VertragsStatus status;
    private double gesamtPreis;
    
    // Beziehungen zu anderen Entitäten
    private Kunde kunde;
    private Fahrzeug fahrzeug;
    private Mitarbeiter mitarbeiter;
    private List<Zusatzoption> zusatzoptionen;

    /**
     * Konstruktor für Mietvertrag.
     * 
     * @param k Kunde
     * @param f Fahrzeug
     * @param start Startdatum
     * @param end Enddatum
     */
    public Mietvertrag(Kunde k, Fahrzeug f, LocalDate start, LocalDate end) {
        this.kunde = k;
        this.fahrzeug = f;
        this.startDatum = start;
        this.endDatum = end;
        this.status = VertragsStatus.ANGELEGT;
        this.zusatzoptionen = new ArrayList<>();
        this.gesamtPreis = preisBerechnen();
    }

    /**
     * Leerer Konstruktor für flexible Initialisierung.
     */
    public Mietvertrag() {
        this.status = VertragsStatus.ANGELEGT;
        this.zusatzoptionen = new ArrayList<>();
    }

    /**
     * Berechnet den Gesamtpreis des Mietvertrags basierend auf:
     * - Mietdauer
     * - Fahrzeugtyp-Tagespreis
     * - Zusatzoptionen
     * 
     * @return Gesamtpreis
     */
    public double preisBerechnen() {
        if (fahrzeug == null || fahrzeug.getFahrzeugtyp() == null || startDatum == null || endDatum == null) {
            return 0.0;
        }
        
        int tage = getMietdauerTage();
        double basisPreis = tage * fahrzeug.getFahrzeugtyp().getStandardTagesPreis();
        
        // Zusatzoptionen addieren
        double zusatzkosten = 0.0;
        if (zusatzoptionen != null) {
            for (Zusatzoption option : zusatzoptionen) {
                zusatzkosten += option.getAufpreis() * tage;
            }
        }
        
        return basisPreis + zusatzkosten;
    }

    /**
     * Storniert den Mietvertrag.
     */
    public void stornieren() {
        this.status = VertragsStatus.STORNIERT;
        // Fahrzeug wieder verfügbar machen
        if (fahrzeug != null) {
            fahrzeug.setZustand(FahrzeugZustand.VERFUEGBAR);
        }
    }

    /**
     * Schließt den Mietvertrag ab (nach Rückgabe des Fahrzeugs).
     */
    public void abschliessen() {
        this.status = VertragsStatus.ABGESCHLOSSEN;
        // Fahrzeug wieder verfügbar machen
        if (fahrzeug != null) {
            fahrzeug.setZustand(FahrzeugZustand.VERFUEGBAR);
        }
    }

    public int getMietdauerTage() {
        if (startDatum == null || endDatum == null) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(startDatum, endDatum);
    }

    /**
     * Fügt eine Zusatzoption zum Vertrag hinzu.
     * 
     * @param option Zusatzoption
     */
    public void addZusatzoption(Zusatzoption option) {
        if (zusatzoptionen == null) {
            zusatzoptionen = new ArrayList<>();
        }
        zusatzoptionen.add(option);
        this.gesamtPreis = preisBerechnen(); // Preis neu berechnen
    }

    // Getter und Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMietnummer() {
        return mietnummer;
    }

    public void setMietnummer(String mietnummer) {
        this.mietnummer = mietnummer;
    }

    public LocalDate getStartDatum() {
        return startDatum;
    }

    public void setStartDatum(LocalDate startDatum) {
        this.startDatum = startDatum;
    }

    public LocalDate getEndDatum() {
        return endDatum;
    }

    public void setEndDatum(LocalDate endDatum) {
        this.endDatum = endDatum;
    }

    public VertragsStatus getStatus() {
        return status;
    }

    public void setStatus(VertragsStatus status) {
        this.status = status;
    }

    public double getGesamtPreis() {
        return gesamtPreis;
    }

    public void setGesamtPreis(double gesamtPreis) {
        this.gesamtPreis = gesamtPreis;
    }

    public Kunde getKunde() {
        return kunde;
    }

    public void setKunde(Kunde kunde) {
        this.kunde = kunde;
    }

    public Fahrzeug getFahrzeug() {
        return fahrzeug;
    }

    public void setFahrzeug(Fahrzeug fahrzeug) {
        this.fahrzeug = fahrzeug;
    }

    public Mitarbeiter getMitarbeiter() {
        return mitarbeiter;
    }

    public void setMitarbeiter(Mitarbeiter mitarbeiter) {
        this.mitarbeiter = mitarbeiter;
    }

    public List<Zusatzoption> getZusatzoptionen() {
        return zusatzoptionen;
    }

    public void setZusatzoptionen(List<Zusatzoption> zusatzoptionen) {
        this.zusatzoptionen = zusatzoptionen;
    }
}
