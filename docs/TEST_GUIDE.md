# Test-Leitfaden für CarRental System

## 📋 Vorbereitung

### 1. Datenbank vorbereiten

Führen Sie zuerst das Schema aus:
```sql
-- Siehe docs/database/schema.sql
```

### 2. Testdaten einfügen (Optional)

```sql
-- Beispiel: Fahrzeugtyp hinzufügen
INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, StandardTagesPreis, Sitzplaetze, Antriebsart)
VALUES ('Volkswagen', 'Golf', 'Kompaktklasse', 45.00, 5, 'VERBRENNER');

INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, StandardTagesPreis, Sitzplaetze, Antriebsart)
VALUES ('Tesla', 'Model 3', 'Mittelklasse', 89.00, 5, 'ELEKTRO');

-- Beispiel: Fahrzeuge hinzufügen
INSERT INTO Fahrzeug (Kennzeichen, AktuellerKilometerstand, Zustand, Fahrzeugtyp_ID)
VALUES ('B-AB 1234', 25000, 'VERFUEGBAR', 1);

INSERT INTO Fahrzeug (Kennzeichen, AktuellerKilometerstand, Zustand, Fahrzeugtyp_ID)
VALUES ('B-CD 5678', 12000, 'VERFUEGBAR', 2);

-- Beispiel: Zusatzoptionen
INSERT INTO Zusatzoption (Bezeichnung, Beschreibung, Aufpreis)
VALUES ('GPS Navigation', 'Navigationssystem mit Echtzeit-Verkehrsdaten', 5.00);

INSERT INTO Zusatzoption (Bezeichnung, Beschreibung, Aufpreis)
VALUES ('Kindersitz', 'Kindersitz für Kinder von 9-36 kg', 3.00);

INSERT INTO Zusatzoption (Bezeichnung, Beschreibung, Aufpreis)
VALUES ('Zusatzfahrer', 'Zweiter Fahrer erlaubt', 10.00);

-- Beispiel: Testmitarbeiter (Passwort: "test123")
INSERT INTO Benutzer (Account_Name, Passwort_Hash, Vorname, Nachname, Email)
VALUES ('admin', 'test123', 'Max', 'Mustermann', 'admin@carrental.de');

INSERT INTO Mitarbeiter (Benutzer_ID, Mitarbeiternummer, Abteilung, istAktiv)
VALUES (1, 1001, 'Vermietung', TRUE);
```

## 🧪 Testszenarien

### Test 1: Kundenregistrierung

1. **Anwendung starten**
   ```bash
   java -jar target/car-rental-system-1.0-SNAPSHOT.jar
   ```

2. **Auf "Registrieren" klicken**

3. **Testdaten eingeben:**
   - Account-Name: `test_kunde`
   - Passwort: `test123456`
   - Passwort wiederholen: `test123456`
   - Vorname: `Anna`
   - Nachname: `Schmidt`
   - E-Mail: `anna.schmidt@test.de`
   - Geburtstag: `1990-05-15`
   - Führerscheinnummer: `D1234567890`

4. **Erwartetes Ergebnis:**
   - ✅ Erfolgsmeldung
   - ✅ Automatischer Login
   - ✅ Kunden-Dashboard wird angezeigt

### Test 2: Kundenlogin

1. **Auf "Zurück zum Login" klicken** (falls registriert)

2. **Anmeldedaten eingeben:**
   - Account-Name: `test_kunde`
   - Passwort: `test123456`

3. **Auf "Anmelden" klicken**

4. **Erwartetes Ergebnis:**
   - ✅ Login erfolgreich
   - ✅ Kunden-Dashboard wird angezeigt
   - ✅ Verfügbare Fahrzeuge werden geladen

### Test 3: Fahrzeuge durchsuchen

1. **Im Kunden-Dashboard**

2. **Tab "Verfügbare Fahrzeuge" öffnen**

3. **Erwartetes Ergebnis:**
   - ✅ Liste aller verfügbaren Fahrzeuge wird angezeigt
   - ✅ Spalten: ID, Kennzeichen, Hersteller, Modell, Kategorie, Tagespreis, Zustand
   - ✅ Nur Fahrzeuge mit Zustand "VERFUEGBAR" werden angezeigt

### Test 4: Fahrzeug buchen

1. **Fahrzeug in der Liste auswählen**

2. **Auf "Buchen" klicken**

3. **Im Buchungsdialog:**
   - Startdatum: Morgen (automatisch vorbelegt)
   - Enddatum: In 7 Tagen (automatisch vorbelegt)
   - Optional: Zusatzoptionen auswählen (GPS, Kindersitz, etc.)

4. **Auf "Preis berechnen" klicken**

5. **Erwartetes Ergebnis:**
   - ✅ Gesamtpreis wird berechnet und angezeigt
   - ✅ Preisberechnung berücksichtigt:
     - Tagespreis × Anzahl Tage
     - Zusatzoptionen × Anzahl Tage
     - Mengenrabatt (5%/10%/15% bei 7/14/30+ Tagen)

6. **Auf "Jetzt buchen" klicken**

7. **Erwartetes Ergebnis:**
   - ✅ Erfolgsmeldung mit Mietnummer
   - ✅ Dialog schließt sich
   - ✅ Buchung erscheint im Tab "Meine Buchungen"

### Test 5: Buchungshistorie ansehen

1. **Tab "Meine Buchungen" öffnen**

2. **Erwartetes Ergebnis:**
   - ✅ Alle Buchungen des Kunden werden angezeigt
   - ✅ Spalten: Mietnummer, Fahrzeug, Startdatum, Enddatum, Preis, Status
   - ✅ Aktuelle Buchung ist sichtbar

### Test 6: Buchung stornieren

1. **Buchung in der Liste auswählen**

2. **Auf "Stornieren" klicken**

3. **Bestätigung im Dialog**

4. **Erwartetes Ergebnis:**
   - ✅ Status ändert sich zu "STORNIERT"
   - ✅ Erfolgsmeldung
   - ✅ Fahrzeug wird wieder verfügbar

### Test 7: Mitarbeiter-Login (falls Testmitarbeiter existiert)

1. **Abmelden**

2. **Mit Mitarbeiter-Account anmelden:**
   - Account-Name: `admin`
   - Passwort: `test123`

3. **Erwartetes Ergebnis:**
   - ✅ Mitarbeiter-Dashboard wird angezeigt
   - ✅ Tabs: Fahrzeugverwaltung, Vertragsverwaltung, Statistiken

### Test 8: Fahrzeug hinzufügen (Mitarbeiter)

1. **Tab "Fahrzeugverwaltung" öffnen**

2. **Untergeordneter Tab "Fahrzeuge"**

3. **Auf "Hinzufügen" klicken**

4. **Testdaten eingeben:**
   - Kennzeichen: `B-TEST 999`
   - Fahrzeugtyp: Aus Liste wählen
   - Zustand: `VERFUEGBAR`

5. **Auf "Speichern" klicken**

6. **Erwartetes Ergebnis:**
   - ✅ Fahrzeug wird in Datenbank gespeichert
   - ✅ Erfolgsmeldung
   - ✅ Fahrzeug erscheint in der Liste

### Test 9: Fahrzeugtyp hinzufügen (Mitarbeiter)

1. **Tab "Fahrzeugtypen" öffnen**

2. **Auf "Hinzufügen" klicken**

3. **Testdaten eingeben:**
   - Hersteller: `BMW`
   - Modell: `3er`
   - Kategorie: `Mittelklasse`
   - Antriebsart: `VERBRENNER`
   - Sitzplätze: `5`
   - Tagespreis: `75.00`

4. **Auf "Speichern" klicken**

5. **Erwartetes Ergebnis:**
   - ✅ Fahrzeugtyp wird gespeichert
   - ✅ Erfolgsmeldung
   - ✅ Typ erscheint in der Liste

### Test 10: Vertragsverwaltung (Mitarbeiter)

1. **Tab "Vertragsverwaltung" öffnen**

2. **Auf "Aktualisieren" klicken**

3. **Erwartetes Ergebnis:**
   - ✅ Alle Mietverträge werden angezeigt
   - ✅ Verträge aller Kunden sind sichtbar

4. **Vertrag auswählen und "Details anzeigen" klicken**

5. **Erwartetes Ergebnis:**
   - ✅ Detailinformationen werden angezeigt:
     - Mietnummer
     - Kundenname
     - Fahrzeug
     - Zeitraum
     - Gesamtpreis
     - Status
     - Zusatzoptionen

### Test 11: Statistiken anzeigen (Mitarbeiter)

1. **Tab "Statistiken" öffnen**

2. **Auf "Statistiken aktualisieren" klicken**

3. **Erwartetes Ergebnis:**
   - ✅ Anzahl aller Fahrzeuge
   - ✅ Anzahl aktiver Verträge
   - ✅ Anzahl verfügbarer Fahrzeuge
   - ✅ Anzahl registrierter Kunden

## 🐛 Fehlerszenarien testen

### Test E1: Ungültige Login-Daten

1. **Falsches Passwort eingeben**
2. **Erwartetes Ergebnis:** ❌ Fehlermeldung "Ungültige Anmeldedaten"

### Test E2: Doppelte Registrierung

1. **Bereits existierenden Account-Namen verwenden**
2. **Erwartetes Ergebnis:** ❌ Fehlermeldung "Account-Name bereits vergeben"

### Test E3: Ungültige Buchungsdaten

1. **Enddatum vor Startdatum wählen**
2. **Erwartetes Ergebnis:** ❌ Warnung "Enddatum muss nach dem Startdatum liegen"

### Test E4: Buchung ohne Preisberechnung

1. **Direkt auf "Jetzt buchen" klicken ohne Preisberechnung**
2. **Erwartetes Ergebnis:** ❌ Warnung "Bitte berechnen Sie zuerst den Preis"

### Test E5: Datenbankverbindungsfehler

1. **Falsche Datenbank-Konfiguration in `config.properties`**
2. **Anwendung starten**
3. **Erwartetes Ergebnis:** ❌ Startfehler-Dialog mit Hinweis auf Konfigurationsproblem

## ✅ Checkliste für vollständigen Test

- [ ] Kundenregistrierung funktioniert
- [ ] Kundenlogin funktioniert
- [ ] Fahrzeugliste wird korrekt angezeigt
- [ ] Buchungsdialog öffnet sich
- [ ] Preisberechnung ist korrekt
- [ ] Buchung wird gespeichert
- [ ] Buchungshistorie wird angezeigt
- [ ] Buchung kann storniert werden
- [ ] Mitarbeiter-Login funktioniert
- [ ] Fahrzeugverwaltung funktioniert
- [ ] Fahrzeugtypen können erstellt werden
- [ ] Vertragsliste wird angezeigt
- [ ] Vertragsdetails werden angezeigt
- [ ] Statistiken werden berechnet
- [ ] Fehlerbehandlung funktioniert korrekt
- [ ] GUI reagiert flüssig
- [ ] Keine Exceptions in der Konsole

## 📝 Testprotokoll

Nach jedem Test dokumentieren Sie:

```
Test: [Testnummer und Name]
Datum: [TT.MM.JJJJ]
Tester: [Name]
Ergebnis: ✅ Bestanden / ❌ Fehlgeschlagen
Bemerkungen: [Besonderheiten, Fehler, Verbesserungsvorschläge]
```

## 🔍 Performance-Tests

1. **Ladezeit:** GUI sollte in < 2 Sekunden starten
2. **Datenbankabfragen:** Fahrzeugliste sollte in < 1 Sekunde laden
3. **Buchungsvorgang:** Komplett in < 3 Sekunden

## 🎯 Erfolgsmetriken

Das System gilt als erfolgreich getestet, wenn:
- ✅ Alle Hauptfunktionen ohne Fehler ausführbar sind
- ✅ Keine unkontrollierten Exceptions auftreten
- ✅ Fehlerbehandlung benutzerfreundlich ist
- ✅ Datenbankintegrität erhalten bleibt
- ✅ GUI reagiert konsistent und intuitiv
