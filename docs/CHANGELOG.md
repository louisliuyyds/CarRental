# CarRental - Aktualisierungshistorie

## [1.1.0] - 2026-01-28

### Neue Funktionen

#### Kundenoberfläche
- **Fahrzeugfilterfunktion**
  - Kategorie-Dropdown hinzugefügt
  - Vereinfachung auf Nur-Kategorie-Filter für bessere Zuverlässigkeit
  - Automatische Filteranwendung ohne zusätzlichen Klick

- **Vertragsentwurfssystem (ANGELEGT-Status)**
  - "Als Entwurf speichern"-Button hinzugefügt
  - Im Entwurfsstatus bleibt der Fahrzeugstatus VERFUEGBAR
  - "Buchung fortsetzen"-Funktion unterstützt

- **Passwortänderungsfunktion**
  - Bestätigung für neues Passwort
  - Leere Passwörter nicht erlaubt
  - Dialoggröße optimiert (500x280)
  - Echtzeit-Datenbankaktualisierung

- **Vollständige Vertragsdetails-Anzeige**
  - Fix: Fahrzeugtyp nicht korrekt geladen
  - Vollständige Fahrzeuginformationen (Hersteller, Modell, Kategorie, Antriebsart, Sitzplätze, Tagespreis)
  - Seniorenfreundliches Farbschema (Schwarz+Dunkelblau+Grün+Rot)
  - Pop-up-Größe optimiert (800x600)

- **UI-Optimierung**
  - "Verfügbare Fahrzeuge" → "Autos suchen"
  - Button-Text "Buchung fortsetzen" optimiert
  - "Stornieren"-Button verhindert Stornierung laufender Verträge (deutsche Fehlermeldung)
  - "Details"-Button hinzugefügt (zeigt Vertragsdetails)
  - Schreibgeschützte Benutzername-Anzeige (grauer Hintergrund)
  - Tab "Meine Daten" mit Scrollunterstützung

#### Mitarbeiteroberfläche
- **Tab "Nutzerverwaltung"**
  - Neuer 4. Tab
  - Vollständige Kundeninformationen (12 Spalten)
  - Kundendetails-Anzeigefunktion
  - Statistikkarten mit Navigation zur Nutzerverwaltung

- **Fahrzeugfilterfunktion**
  - Neuer Statusfilterdialog
  - Unterstützte Filter: VERFUEGBAR, VERMIETET, WARTUNG, IN_REPARATUR
  - Fahrzeugtyp-Dropdown zeigt Kategorie-Information

- **Erweiterte Statistikfunktionen**
  - Gesamte Fahrzeuge → Fahrzeugverwaltung
  - Aktive Verträge → Vertragsverwaltung (automatischer LAUFEND-Filter)
  - Verfügbare Fahrzeuge → Fahrzeugverwaltung (automatischer VERFUEGBAR-Filter)
  - Registrierte Kunden → Nutzerverwaltung

#### Systemfunktionen
- **ContractStatusUpdater**
  - Automatische Vertragsstatusprüfung und -aktualisierung
  - Regelmäßige Aufgabenplanung
  - E-Mail-Benachrichtigungsunterstützung

- **Benutzerdefinierte Kalenderkomponenten**
  - CalendarPanel: Monatsansichts-Kalender
  - CalendarDateChooser: Datumsauswahl
  - Integration in Registrierungsformular und persönliche Datenansicht

- **Datenbankverbesserungen**
  - Fix: MietvertragDao Fahrzeugtyp-Mapping-Problem
  - Vollständige JOIN-Abfragen mit allen Beziehungsdaten
  - Erweiterte Debug-Protokollausgaben

### Bugfixes

1. **Kritischer Bug: Vertragsdetails zeigen nur Kennzeichen**
   - Ursache: MietvertragDao hat Fahrzeugtyp nicht korrekt geladen
   - Lösung: Vollständige Implementierung der Fahrzeugtyp-Mapping-Logik (Hersteller, Modell, Kategorie usw.)
   - Auswirkung: Alle Vertragsdetails-Anzeigen

2. **Bug: Passwortdialog-Labels zusammengedrückt**
   - Ursache: Eingabefeld-preferredSize zu groß (350px) in 500px-Dialog
   - Lösung: Eingabefeldbreite auf 280px angepasst, GridBagConstraints.weightx verwendet
   - Auswirkung: Passwortänderungsdialog

3. **Bug: Fahrzeugtyp-Info falsch angezeigt**
   - Ursache: FahrzeugPanel-Dropdown zeigt keine Kategorie
   - Lösung: Dropdown-Renderer mit "(Kategorie)"-Suffix erweitert
   - Auswirkung: Fahrzeugerstellung und -auswahl

4. **Bug: Filter-Dropdown zeigt doppelte Optionen**
   - Ursache: updateFilterValues()-Methode häufig aufgerufen, fehlende Duplikatprüfung
   - Lösung: Vereinfachung auf Nur-Kategorie-Filter, Hersteller-Filter entfernt
   - Auswirkung: Tab "Autos suchen"

5. **Bug: UI-Elemente von Fahrzeugliste verdeckt**
   - Ursache: filterPanel ohne feste Höhe, Komponenten umbrechen
   - Lösung: filterPanel.setPreferredSize(-1, 100) gesetzt
   - Auswirkung: Tab "Autos suchen"

6. **Bug: Kunde kann laufende Verträge stornieren**
   - Ursache: Fehlende Statusprüfung
   - Lösung: LAUFEND- und BESTAETIGT-Statusprüfung hinzugefügt, deutsche Fehlermeldung
   - Auswirkung: Tab "Meine Buchungen"

7. **Bug: "Meine Daten" Informationen teilweise nicht sichtbar**
   - Ursache: Formular-Panel nicht in JScrollPane
   - Lösung: Formular in JScrollPane mit vertikalem ScrollPolicy gesetzt
   - Auswirkung: Tab "Meine Daten"

8. **Bug: Fahrzeugtyp in Vertragserfüllung nicht geladen**
   - Ursache: MietvertragDao unvollständig
   - Lösung: Vollständiges Laden aller Fahrzeugtyp- und Fahrzeug-Felder
   - Auswirkung: Alle vertragsbezogenen Funktionen

### Verbesserungen

#### Benutzerfreundlichkeit
- Seniorenfreundliches Design (Farben, Schrift, Kontrast)
- Deutsch lokalisierte Fehlermeldungen
- Klarere UI-Labels und Buttons
- Verbesserte Barrierefreiheit

#### Code-Qualität
- Erweiterte Debug-Ausgaben (Konsolenprotokollierung)
- Detaillierte Fehlerbehandlung und Hinweise
- Verbesserte SQL-Abfrage-Performance (JOIN-Optimierung)
- Umfassendere Dokumentationskommentare

#### Performance-Optimierung
- Datenbankverbindungsmanagement optimiert
- Ressourcenfreigabe verbessert (try-with-resources)
- Zeichenkettenverarbeitung optimiert

### Entwicklungsstatistiken
- **Neuer Code**: ca. 1.500 Zeilen
- **Geänderte Dateien**: 6 Dateien
- **Bugfixes**: 8
- **Neue Ansichten**: 2 (CalendarPanel, CalendarDateChooser)
- **Neue Controller**: 1 (ContractStatusUpdater)

### Technische Schulden
- Ausstehend: Passwortverschlüsselung (aktuell Klartextspeicherung)
- Ausstehend: E-Mail-Benachrichtigungsversand
- Ausstehend: Automatisches Testsuite

---

## [1.0.0] - 2025-12-15

### Erste Version

- Vollständige MVC-Architektur implementiert
- Grundlegende Kunden- und Mitarbeiterfunktionen
- Fahrzeug- und Fahrzeugtypverwaltung
- Mietvertragsmanagement
- Swing GUI implementiert
- IBM Db2 Datenbankintegration
- Maven Build-System
