# **Pflichtenheft: CarRental-System**

Projekt: Entwicklung eines Autovermietungssystems  
Auftragnehmer: \[Dein Name/Gruppe\]  
Auftraggeber: OSE-Modulverantwortlicher  
Datum: 13.01.2026  
Version: 1.1

## **1\. Zielbestimmung**

Das Ziel des Projekts ist die Entwicklung einer Desktop-Anwendung ("CarRental") zur digitalen Abwicklung von Fahrzeugvermietungen. Das System soll den manuellen Buchungsaufwand reduzieren und durch automatische Prüfungen Doppelbuchungen verhindern.

### **1.1 Muss-Kriterien**

* **Benutzerverwaltung:** Registrierung, Login und Profilbearbeitung für Kunden.  
* **Fahrzeugverwaltung:** Erfassung, Bearbeitung und Löschung von Fahrzeugdaten durch Mitarbeiter.  
* **Vertragsmanagement:** Erstellung von Mietverträgen unter Berücksichtigung von Zeiträumen und Fahrzeugverfügbarkeit.  
* **Automatische Preisberechnung:** Kalkulation des Gesamtpreises basierend auf Tagespreis, Mietdauer und gewählten Zusatzoptionen vor Vertragsabschluss.  
* **Konfliktprüfung:** Systemseitige Verhinderung von Doppelbuchungen (gleiches Auto zur gleichen Zeit) und Mehrfachmieten (ein Kunde leiht \>1 Auto gleichzeitig).  
* **Zusatzoptionen:** Verwaltung und Buchung von Extras (z. B. Kindersitz, Versicherung).  
* **Rückgabe:** Kunden können Fahrzeuge per Klick im System zurückgeben (Statusänderung).  
* **Historie:** Einsicht in vergangene, aktive und zukünftige Buchungen.

### **1.2 Kann-Kriterien**

* **Erweiterte Suche:** Filterung der Fahrzeuge nach Marke, Kategorie oder Verfügbarkeit in einem Zeitraum.  
* **Erweiterte Nutzerverwaltung:** Administratoren können Mitarbeiter-Accounts anlegen.  
* **Statistik:** Dashboard für Mitarbeiter (z. B. "Meistgebuchtes Auto", "Durchschnittliche Mietdauer").  
* **UI-Komfort:** Grafischer Kalender zur Datumsauswahl.

### **1.3 Abgrenzungskriterien**

* **Kein Zahlungssystem:** Es erfolgt keine Anbindung an Banken oder Kreditkartendienste.  
* **Keine externen Schnittstellen:** Keine Prüfung von Führerscheindaten oder Schufa.  
* **Keine mobile App:** Das System ist ausschließlich als Desktop-Anwendung konzipiert.  
* **Kein Wartungsmanagement:** Werkstattaufenthalte oder Schadensmeldungen werden nicht abgebildet.  
* **Keine anderen Mietobjekte:** Nur PKW, keine LKW oder Motorräder.

## **2\. Einsatz**

### **2.1 Anwendungsbereiche**

Das System wird in den Filialen der Autovermietung sowie auf den persönlichen Computern der Kunden (Simulation) eingesetzt. Es dient der Verwaltung des operativen Tagesgeschäfts.

### **2.2 Zielgruppen**

* **Kunden:** Gelegenheitsnutzer ohne Schulung. Fokus: Einfache Buchung und Übersicht.  
* **Mitarbeiter:** Geschulte Nutzer, die den Fahrzeugbestand pflegen und Problemfälle (Stornierungen) lösen.

### **2.3 Betriebsbedingungen**

* **Betriebszeit:** Das System muss theoretisch 24/7 verfügbar sein (Online-Datenbank).  
* **Art der Nutzung:** Interaktiver Dialogbetrieb über GUI (Graphical User Interface).

## **3\. Umgebung**

### **3.1 Software**

* **Betriebssystem:** Plattformunabhängig (Windows, Linux, macOS), sofern eine Java Runtime Environment (JRE) vorhanden ist.  
* **Laufzeitumgebung:** Java SE 17 oder höher.  
* **Datenbank:** Relationale Online-Datenbank (z. B. MySQL, PostgreSQL oder H2 im Server-Modus).  
* **Entwicklungsumgebung:** Java Swing (Standard-Bibliothek).

### **3.2 Hardware**

* Standard-PC oder Laptop mit Internetverbindung (für Datenbankzugriff).  
* Bildschirmauflösung mindestens 1024x768 Pixel.

### **3.3 Orgware**

* **Benutzerhandbuch:** Eine Anleitung für Kunden (Registrierung, Buchung) und Mitarbeiter (Fahrzeugpflege).  
* **Installationsanleitung:** Dokumentation zur Einrichtung der Datenbankverbindung.

## **4\. Funktionalität**

Die Funktionen werden durch typische Arbeitsabläufe (Use Cases) beschrieben:

* **/F10/ Identifikation:**  
  * Anmeldung mittels Benutzername und Passwort.  
  * Unterscheidung der Rechte zwischen Kunde und Mitarbeiter.  
* **/F20/ Fuhrparkpflege (Mitarbeiter):**  
  * Eingabe neuer Fahrzeuge (Marke, Modell, Kennzeichen, Preis).  
  * Statusänderung von Fahrzeugen (z. B. bei Verkauf löschen).  
  * Verwalten der Zusatzoptionen (Name, Preis).  
* **/F30/ Buchungsvorgang (Kunde):**  
  * Auswahl eines verfügbaren Autos.  
  * Festlegung des Zeitraums (Start/Ende).  
  * Auswahl von Extras (Checkbox-Liste).  
  * Anzeige der Zusammenfassung mit Preis.  
  * Verbindliche Buchung (Speicherung in DB).  
* **/F40/ Vertragsüberwachung:**  
  * Kunden sehen Status ihrer Buchungen.  
  * Mitarbeiter können Buchungen einsehen und stornieren.  
  * Rückgabefunktion (Kunde beendet Miete \-\> Auto wird wieder "frei").

## **5\. Daten**

Das System speichert folgende Daten langfristig:

* **Benutzerdaten:** ID, Name, Vorname, Adresse, Login-Daten, Rolle.  
* **Fahrzeugdaten:** ID, Kennzeichen, Marke, Modell, Kategorie, Tagespreis, aktueller Status.  
* **Zusatzoptionen:** ID, Bezeichnung, Aufpreis.  
* **Mietverträge:** ID, Referenz auf Kunde, Referenz auf Auto, Startdatum, Enddatum, Gesamtpreis, Status (Aktiv/Storniert/Abgeschlossen), Liste der gewählten Optionen.

## **6\. Leistung**

* **Reaktionszeit:** Suchanfragen und Buchungen sollen in unter 2 Sekunden verarbeitet werden (bei stabiler Internetverbindung).  
* **Konsistenz:** Die Datenbank muss ACID-Eigenschaften erfüllen, um zu garantieren, dass ein Auto niemals doppelt gebucht wird (Transaktionssicherheit).

## **7\. Benutzungsoberfläche (GUI-Entwurf)**

Die Benutzeroberfläche wird mittels Java Swing realisiert. Sie folgt dem Prinzip "Form follows Function" und trennt strikt zwischen Kunden- und Mitarbeiteransichten.

### **7.1 Login-Screen**

Der Einstiegspunkt für alle Nutzer.

* **Elemente:** Textfelder für Benutzername/Passwort, Buttons für "Login" und "Registrieren".  
* **Verhalten:** Bei erfolgreichem Login Weiterleitung zum jeweiligen Dashboard (Kunde oder Mitarbeiter) basierend auf der Rolle.

\+-------------------------------------------------------+  
|  \[Logo\] CarRental System                              |  
\+-------------------------------------------------------+  
|                                                       |  
|      Benutzername: \[ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_ \]                |  
|      Passwort:     \[ \*\*\*\*\*\*\*\*\*\*\*\*\*\*\* \]                |  
|                                                       |  
|            \[ Einloggen \]   \[ Registrieren \]           |  
|                                                       |  
\+-------------------------------------------------------+

### **7.2 Kunden-Dashboard: Fahrzeugsuche & Buchung**

Die Hauptansicht für den Kunden.

* **Bereich A (Filter):** Auswahl von Start- und Enddatum (Pflichtfelder). Optional Filter nach Kategorie.  
* **Bereich B (Ergebnisliste):** Tabelle aller *verfügbaren* Fahrzeuge im gewählten Zeitraum. Spalten: Marke, Modell, Kategorie, Preis/Tag.  
* **Bereich C (Aktionen):** Button "Auswählen / Buchen".

\+-------------------------------------------------------+  
|  Hallo, \[KundenName\]\!       \[Mein Profil\] \[Logout\]    |  
\+-------------------------------------------------------+  
|  SUCHE                                                |  
|  Von: \[TT.MM.JJJJ\]  Bis: \[TT.MM.JJJJ\]  \[ Suchen \]     |  
|  Kategorie: \[ Alle ▼ \]                                |  
\+-------------------------------------------------------+  
|  VERFÜGBARE FAHRZEUGE                                 |  
|  \+-------+----------+-----------+------------+        |  
|  | Marke | Modell   | Kategorie | Preis/Tag  |        |  
|  \+-------+----------+-----------+------------+        |  
|  | BMW   | 3er      | Limousine | 80.00 €    |        |  
|  | VW    | Golf     | Kleinwagen| 50.00 €    |        |  
|  \+-------+----------+-----------+------------+        |  
|                                                       |  
|                       \[ Auto Buchen \]                 |  
\+-------------------------------------------------------+

### **7.3 Buchungsabschluss (Pop-up oder Detailansicht)**

Erscheint nach Auswahl eines Fahrzeugs.

* **Zweck:** Auswahl von Extras und finale Bestätigung.  
* **Elemente:** Liste der Zusatzoptionen (Checkboxen), Automatische Berechnung des Gesamtpreises.

\+-------------------------------------------------------+  
|  BUCHUNG ABSCHLIESSEN                                 |  
\+-------------------------------------------------------+  
|  Fahrzeug: BMW 3er                                    |  
|  Zeitraum: 01.05. \- 05.05. (4 Tage)                   |  
|                                                       |  
|  ZUSATZOPTIONEN WÄHLEN:                               |  
|  \[x\] Vollkasko (+10€/Tag)                             |  
|  \[ \] Kindersitz (+5€/Tag)                             |  
|                                                       |  
|  \--------------------------------------------------   |  
|  GESAMTPREIS:  360.00 €                               |  
|                                                       |  
|        \[ Abbrechen \]          \[ Kostenpflichtig Buchen \] |  
\+-------------------------------------------------------+

### **7.4 Mitarbeiter-Dashboard: Verwaltung**

Die administrative Sicht (Reiter-basiert).

* **Tab 1: Fuhrpark:** Tabelle aller Autos mit CRUD-Buttons (Neu, Bearbeiten, Löschen).  
* **Tab 2: Buchungen:** Übersicht aller aktiven Verträge. Möglichkeit zur Stornierung.

\+-------------------------------------------------------+  
|  ADMIN-PANEL                \[Logout: Mitarbeiter\]     |  
\+-------------------------------------------------------+  
|  \[TAB: Fuhrpark\]   \[TAB: Buchungen\]  \[TAB: Extras\]    |  
\+-------------------------------------------------------+  
|                                                       |  
|  \[ \+ Neues Auto \]  \[ Bearbeiten \]  \[ Löschen \]        |  
|                                                       |  
|  \+-------+-------+---------+------------+----------+  |  
|  | ID    | Kennz.| Modell  | Status     | Aktionen |  |  
|  \+-------+-------+---------+------------+----------+  |  
|  | 101   | B-XY  | Audi A4 | VERFÜGBAR  | \[Edit\]   |  |  
|  | 102   | M-AB  | VW Polo | VERMIETET  | \[Edit\]   |  |  
|  \+-------+-------+---------+------------+----------+  |  
|                                                       |  
\+-------------------------------------------------------+

### **7.5 Feedback & Fehlerbehandlung**

Das System kommuniziert über Dialogfenster (Pop-ups):

* **Erfolg:** "Buchung \#1023 erfolgreich angelegt\!"  
* **Fehler:** "Zeitraum kollidiert\! Das Fahrzeug ist vom 02.05. bis 03.05. bereits belegt."