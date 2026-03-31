# 🚗 CarRental - Autovermietungssystem

> **Version:** 1.1.0  
> **Letzte Aktualisierung:** März 2026

Ein vollständiges **Java-basiertes Autovermietungssystem** mit grafischer Benutzeroberfläche (Swing), Datenbankanbindung (IBM Db2) und MVC-Architektur.

---

## 📋 Inhaltsverzeichnis

- [Features](#-features)
- [Systemanforderungen](#-systemanforderungen)
- [Schnellstart](#-schnellstart)
- [Detaillierte Installationsanleitung](#-detaillierte-installationsanleitung)
- [Projektstruktur](#-projektstruktur)
- [Verwendete Technologien](#-verwendete-technologien)
- [Aktualisierungshistorie](#-aktualisierungshistorie)
- [Lizenz](#-lizenz)

---

## ✨ Features

### Kundenfunktionen
- ✅ Benutzerregistrierung und Login
- ✅ Verfügbare Fahrzeuge durchsuchen
- ✅ Fahrzeugkategoriefilter (kategoriebasiert)
- ✅ Fahrzeuge mit Datumsauswahl reservieren
- ✅ Zusatzoptionen hinzufügen (z.B. Kindersitz)
- ✅ Vertragsentwurfsfunktion (Speichern im Status ANGELEGT)
- ✅ Entwurfsfortsetzung (Buchung jederzeit abschließen)
- ✅ Passwortänderungsfunktion (neues Passwort + Bestätigungsprüfung)
- ✅ Reservierungshistorie anzeigen
- ✅ Vollständige Vertragsdetails (alle Fahrzeuginformationen, Tagespreis)
- ✅ Reservierungen stornieren
- ✅ Scrollbare persönliche Daten (Tab "Meine Daten")

### Mitarbeiterfunktionen
- ✅ Fahrzeuge verwalten (Hinzufügen, Ändern, Löschen)
- ✅ Fahrzeugstatusfilter (VERFUEGBAR, VERMIETET, WARTUNG, IN_REPARATUR)
- ✅ Fahrzeugtypen konfigurieren
- ✅ Mietverträge einsehen und verwalten
- ✅ Kundeninformationsmanagement (Tab "Nutzerverwaltung")
- ✅ Systemstatistiken anzeigen
- ✅ Statistikkarten mit Navigationslinks
- ✅ Verfügbarkeitsmanagement
- ✅ Detaillierte Fahrzeuginformationen (vollständige Fahrzeugtypdaten)

### Geschäftslogik
- 📊 **Intelligente Preisberechnung**: Tagesmiete + Zusatzoptionen + Staffelrabatte
- 🔒 **Konfliktdetektierung**: Automatische Überprüfung auf doppelte Reservierungen
- 💳 **Benutzervalidierung**: Altersverifikation, Kontoaktivität, Fahrerlaubnisstatus
- 📅 **Zeitfenstervalidierung**: Keine Buchungen in der Vergangenheit oder > 90 Tage

---

## 🖥️ Systemanforderungen

### Notwendig
- **Java Development Kit (JDK) 21+**
[Download Temurin JDK 21](https://adoptium.net/temurin/releases/?version=21)
- **IBM Db2 Datenbank**
(Cloud-Instanz oder lokal)
- **Gradle 8.14.3+** (wird automatisch via Gradle Wrapper bereitgestellt)

### Optional
- Git (für Versionskontrolle)
- IDE (Visual Studio Code, IntelliJ IDEA, Eclipse)

---

## 🚀 Schnellstart

### Windows (PowerShell)

```powershell
# 1. Repository klonen
git clone https://github.com/louisliuyyds/CarRental.git
cd CarRental

# 2. Datenbankkonfiguration erstellen
cp config/config.properties.template src/main/resources/config.properties
# Bearbeite src/main/resources/config.properties mit deinen Db2-Zugangsdaten

# 3. Projekt bauen
.\gradlew.bat build

# 4. Anwendung starten
java -jar build/libs/car-rental-system-1.0-SNAPSHOT.jar
```

### Linux / macOS

```bash
# 1. Repository klonen
git clone https://github.com/louisliuyyds/CarRental.git
cd CarRental

# 2. Datenbankkonfiguration erstellen
cp config/config.properties.template src/main/resources/config.properties
# Bearbeite src/main/resources/config.properties

# 3. Projekt bauen
./gradlew build

# 4. Anwendung starten
java -jar build/libs/car-rental-system-1.0-SNAPSHOT.jar
```

**Die GUI sollte sich automatisch öffnen!**

---

## 📖 Detaillierte Installationsanleitung

### Schritt 1: Java 21 installieren

**Windows (PowerShell als Admin):**
```powershell
winget install --id EclipseAdoptium.Temurin.21.JDK -e
# Terminal neu öffnen
java -version
```

**macOS:**
```bash
brew install temurin@21
java -version
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install temurin-21-jdk
java -version
```

### Schritt 2: Projekt vorbereiten

```powershell
# Repository klonen (oder als ZIP herunterladen)
git clone https://github.com/louisliuyyds/CarRental.git
cd CarRental

# Datenbankkonfiguration einrichten
Copy-Item config/config.properties.template src/main/resources/config.properties
```

### Schritt 3: config.properties konfigurieren

Öffne `src/main/resources/config.properties` und füge deine Db2-Zugangsdaten ein:

```properties
# IBM Db2 Verbindungseinstellungen
db.url=jdbc:db2://dein-host:dein-port/deine-datenbank
db.user=dein-benutzername
db.password=dein-passwort
db.ssl=true
```

**Beispiel für IBM Cloud Db2:**
```properties
db.url=jdbc:db2://0c77d6f2-5da9-48a9-81f8-86b520b87518.bs2io90l08kqb1od8lcg.databases.appdomain.cloud:31198/bludb
db.user=klc40279
db.password=foPpp9NUngeOFwa2
db.ssl=true
```

### Schritt 4: Datenbank initialisieren

```powershell
# Stellt sicher, dass die Db2-Verbindung funktioniert
# und das Schema existiert (siehe Schritt 5)
```

**Oder direkt in Db2 ausführen:**

```sql
-- docs/database/schema.sql in deine Db2-Instanz importieren
-- Dies erstellt alle notwendigen Tabellen
```

### Schritt 5: Projekt bauen

```powershell
cd CarRental

# Gradle Wrapper startet automatisch (kein zusätzlicher Gradle-Install nötig)
.\gradlew.bat build

# Linux/macOS:
./gradlew build

# Nach erfolgreichem Build:
# ✅ build/libs/car-rental-system-1.0-SNAPSHOT.jar (Fat-JAR mit allen Dependencies)
```

### Schritt 6: Anwendung ausführen

```powershell
# Starte mit der Fat-JAR (inkl. Db2-Treiber)
java -jar build/libs/car-rental-system-1.0-SNAPSHOT.jar

# Erfolgreich? Konsoleausgabe:
# ✓ Autovermietungssystem gestartet.
# ✓ GUI öffnet sich
```

---

## 🧪 Testkennwörter

Nach dem Datenbankschema-Import (docs/database/schema.sql) sind folgende Test-Konten verfügbar:

### Kunde
- **Kontoname:** kunde1
- **Passwort:** password123

### Mitarbeiter
- **Kontoname:** mitarbeiter1
- **Passwort:** password123

---

## 📁 Projektstruktur

```
CarRental/
├── src/
│ ├── main/
│ │ ├── java/com/carrental/
│ │ │ ├── controller/ # Business Logic Layer
│ │ │ │ ├── AuthController.java
│ │ │ │ ├── BookingController.java
│ │ │ │ ├── CarRentalSystem.java (Singleton)
│ │ │ │ └── ContractStatusUpdater.java (自动更新)
│ │ │ ├── dao/ # Data Access Layer
│ │ │ │ ├── GenericDao.java (Interface)
│ │ │ │ ├── KundeDao.java
│ │ │ │ ├── FahrzeugDao.java
│ │ │ │ ├── MietvertragDao.java
│ │ │ │ ├── MitarbeiterDao.java
│ │ │ │ └── ZusatzoptionDao.java
│ │ │ ├── model/ # Domain Model
│ │ │ │ ├── Benutzer.java (abstract)
│ │ │ │ ├── Kunde.java
│ │ │ │ ├── Mitarbeiter.java
│ │ │ │ ├── Fahrzeug.java
│ │ │ │ ├── Fahrzeugtyp.java
│ │ │ │ ├── Mietvertrag.java
│ │ │ │ ├── Zusatzoption.java
│ │ │ │ ├── VertragsStatus.java (Enum)
│ │ │ │ ├── FahrzeugZustand.java (Enum)
│ │ │ │ └── Antriebsart.java (Enum)
│ │ │ ├── view/ # Presentation Layer (Swing)
│ │ │ │ ├── MainFrame.java
│ │ │ │ ├── LoginPanel.java
│ │ │ │ ├── RegisterPanel.java
│ │ │ │ ├── KundeDashboard.java (3 Tabs)
│ │ │ │ ├── MitarbeiterDashboard.java (4 Tabs)
│ │ │ │ ├── BookingDialog.java (草稿支持)
│ │ │ │ ├── FahrzeugPanel.java (过滤功能)
│ │ │ │ ├── CalendarPanel.java (自定义日历)
│ │ │ │ └── CalendarDateChooser.java
│ │ │ ├── util/ # Utilities
│ │ │ │ ├── DatabaseConfig.java
│ │ │ │ └── DatabaseConnection.java
│ │ │ └── Main.java # Application Entry Point
│ │ └── resources/
│ │ └── config.properties # Datenbankkonfiguration
├── config/
│ ├── config.properties # Laufzeit-Konfiguration
│ └── config.properties.template # Template für neue Instanzen
├── docs/
│ ├── OOA.md # Analyse-Dokument (不可改动)
│ ├── OOD.md # Design-Dokument (不可改动)
│ ├── Pflichtenheft.md # Pflichtenheft (不可改动)
│ ├── IMPLEMENTATION_REPORT.md # Implementierungsbericht
│ ├── TEST_GUIDE.md # Testanleitungen
│ ├── CHANGELOG.md # 更新历史
│ └── database/
│ └── schema.sql # Datenbankschema für Db2
├── lib/
│ └── db2jcc4.jar # Db2 JDBC-Treiber (optional, wird via Gradle geladen)
├── build.gradle # Gradle-Konfiguration
├── gradlew / gradlew.bat # Gradle Wrapper
├── .gitignore # Git-Ignore-Regeln
└── README.md # Diese Datei
```
CarRental/
├── src/
│   ├── main/
│   │   ├── java/com/carrental/
│   │   │   ├── controller/          # Business Logic Layer
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BookingController.java
│   │   │   │   └── CarRentalSystem.java (Singleton)
│   │   │   ├── dao/                 # Data Access Layer
│   │   │   │   ├── GenericDao.java (Interface)
│   │   │   │   ├── KundeDao.java
│   │   │   │   ├── FahrzeugDao.java
│   │   │   │   ├── MietvertragDao.java
│   │   │   │   └── ZusatzoptionDao.java
│   │   │   ├── model/               # Domain Model
│   │   │   │   ├── Benutzer.java (abstract)
│   │   │   │   ├── Kunde.java
│   │   │   │   ├── Mitarbeiter.java
│   │   │   │   ├── Fahrzeug.java
│   │   │   │   ├── Fahrzeugtyp.java
│   │   │   │   ├── Mietvertrag.java
│   │   │   │   ├── Zusatzoption.java
│   │   │   │   ├── VertragsStatus.java (Enum)
│   │   │   │   ├── FahrzeugZustand.java (Enum)
│   │   │   │   └── Antriebsart.java (Enum)
│   │   │   ├── view/                # Presentation Layer (Swing)
│   │   │   │   ├── MainFrame.java
│   │   │   │   ├── LoginPanel.java
│   │   │   │   ├── RegisterPanel.java
│   │   │   │   ├── KundeDashboard.java
│   │   │   │   ├── BookingDialog.java
│   │   │   │   ├── MitarbeiterDashboard.java
│   │   │   │   └── FahrzeugPanel.java
│   │   │   ├── util/                # Utilities
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   └── DatabaseConnection.java
│   │   │   └── Main.java            # Application Entry Point
│   │   └── resources/
│   │       └── config.properties    # Datenbankkonfiguration
│   └── test/                        # Unit Tests (optional)
├── config/
│   ├── config.properties            # Laufzeit-Konfiguration
│   └── config.properties.template   # Template für neue Instanzen
├── docs/
│   ├── OOD.md                       # Objektorientierten Design
│   ├── OOA.md                       # Analyse-Dokument
│   ├── Pflichtenheft.md             # Anforderungsspezifikation
│   ├── IMPLEMENTATION_REPORT.md     # Implementierungsbericht
│   ├── TEST_GUIDE.md                # Testanleitungen
│   └── database/
│       └── schema.sql               # Datenbankschema für Db2
├── lib/
│   └── db2jcc4.jar                  # Db2 JDBC-Treiber (optional, wird via Maven geladen)
├── pom.xml                          # Maven-Konfiguration
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── .gitignore                       # Git-Ignore-Regeln
└── README.md                        # Diese Datei
```

---

## 🛠️ Verwendete Technologien

| Layer | Technologie | Version |
|-------|------------|---------|
| **语言** | Java | 17+ |
| **UI框架** | Swing | JDK内置 |
| **数据库** | IBM Db2 | 11.5.x |
| **JDBC驱动** | com.ibm.db2:jcc | 11.5.9.0 |
| **构建工具** | Gradle | 8.5+ (Wrapper) |
| **架构** | MVC | 自定义 |
| **模式** | Singleton, Factory, DAO, Observer | - |

---

## 📊 Projektstatistiken

### Code-Statistiken
- **Gesamtzahl Codezeilen**: 8.913 Zeilen
- **Anzahl Java-Dateien**: 32
- **Gesamtklassenanzahl**: 28 (10 Model, 7 DAO, 4 Controller, 8 View, 2 Util, 1 Main, 3 Enums)
- **Dokumentationsdateien**: 6 Markdown-Dateien
- **Konfigurationsdateien**: 3 (build.gradle, .gitignore, Konfigurationsvorlage)

### Funktionsstatistiken
- **Gesamtfunktionen**: 25+
- **Kundenfunktionen**: 15+
- **Mitarbeiterfunktionen**: 10+
- **Systemfunktionen**: 5+

### Entwicklungsstatistiken
- **Bugfixes**: 10+
- **Versionsupdates**: v1.1 (Januar 2026)

---

## 🔧 Troubleshooting

### Problem: "No suitable driver found for jdbc:db2://..."

**Lösung:**
```powershell
# Stelle sicher, dass die Fat-JAR verwendet wird:
java -jar build/libs/car-rental-system-1.0-SNAPSHOT.jar

# Oder explizit den Klassenpfad setzen:
java -cp "build/libs/car-rental-system-1.0-SNAPSHOT.jar;lib/db2jcc4.jar" com.carrental.Main
```

### Problem: "invalid target release: 17"

**Lösung:**
```powershell
# Prüfe Java-Version
java -version

# Setze JAVA_HOME auf JDK 17
$env:JAVA_HOME = "C:\\Program Files\\Eclipse Adoptium\\jdk-17.0.11"
$env:Path = "$env:JAVA_HOME\\bin;$env:Path"

# Neuer Build
.\gradlew.bat build
```

### Problem: "Initiale Daten konnten nicht geladen werden"

**Lösung:**
- Prüfe, ob die Db2-Datenbank online und erreichbar ist
- Verifiziere config.properties (URL, User, Password)
- Führe `docs/database/schema.sql` aus, um Tabellen zu erstellen

### Problem: "BUILD FAILURE - dependencies.dependency.systemPath"

**Lösung:**
Diese Warnung ist erwartbar. Gradle wird die Db2-JAR automatisch vom IBM-Repository herunterladen. Der Build funktioniert trotzdem.

---

## 📚 Weitere Dokumentation

- **OOD.md** – Detailliertes Klassendiagramm und Designdokumentation
- **TEST_GUIDE.md** – Testszenarien und Testprotokolle
- **IMPLEMENTATION_REPORT.md** – Vollständiger Implementierungsbericht
- **schema.sql** – Datenbank-DDL-Statements

---

## 👥 Entwickler

- **Projekt:** Objektorientiertete Systemanalyse und -entwurf (3. Semester)
- **Hochschule:** HWR Berlin - Wirtschaftsinformatik

---

## 📄 Lizenz

Dieses Projekt dient zu Bildungszwecken.

---

## 📝 Aktualisierungshistorie

### v1.1 (Januar 2026)

#### Neue Funktionen
- **Kundenoberflächenverbesserungen**
  - Fahrzeugkategoriefilter (kategoriebasiert)
  - Vertragsentwurfssystem (Speichern im Status ANGELEGT)
  - Entwurfsfortsetzungsfunktion
  - Passwortänderungsfunktion (neues Passwort + Bestätigungsprüfung)
  - Vollständige Vertragsdetails (alle Fahrzeuginformationen und Tagespreis)
  - Scrollunterstützung für "Meine Daten"
  - UI-Verbesserungen (Tab-Umbenennungen, Schaltflächentext-Optimierung)

- **Mitarbeiteroberflächenverbesserungen**
  - Tab "Nutzerverwaltung" (vollständige Kundeninformationsverwaltung)
  - Fahrzeugstatusfilterfunktion
  - Navigationsintegration für Statistikkarten
  - Vertragsdetails identisch zur Kundenoberfläche

- **Systemverbesserungen**
  - ContractStatusUpdater (automatische Vertragsstatusaktualisierung)
  - CalendarPanel und CalendarDateChooser (benutzerdefinierte Kalenderkomponenten)
  - Erweiterte Debugging- und Protokollierungsfunktionen
  - Bugfixes: Fahrzeugtyp-Datenladen, Datenbank-JOINs, Passwortdialog-Layout

#### Bugfixes
- Fix: Vertragsdetails zeigen nur das Kennzeichen an (Fahrzeugtyp nicht korrekt geladen)
- Fix: Passwortdialog-Labels werden zusammengedrückt
- Fix: Fahrzeugtyp wird falsch angezeigt
- Fix: Filter-Dropdown zeigt doppelte Optionen
- Fix: UI-Elemente werden verdeckt durch Layoutprobleme

#### Verbesserungen
- Seniorenfreundliches Farbschema (hoher Kontrast, große Schrift)
- Verbesserte Fehlermeldungen (Deutsch)
- Passwortsicherheit (keine leeren Passwörter, Passwort-Abgleich-Hinweise)
- Erweiterte Datenbank-Debug-Ausgaben

---

## 💡 Häufige Fragen

**F: Kann ich das Projekt auch ohne Db2 laufen lassen?**  
A: Nein, die Architektur ist auf Db2 ausgelegt. Für andere Datenbanken müssten die DAOs angepasst werden.

**F: Wie lange dauert der erste Build?**
A: Beim ersten Mal ~2-5 Minuten (Gradle lädt ~100 MB Dependencies). Danach ~30 Sekunden.

**F: Kann ich das Projekt in der IDE debuggen?**
A: Ja! Importiere das Projekt als Gradle Project in IntelliJ IDEA oder Eclipse.

**F: Wie erstelle ich einen neuen Benutzer?**  
A: Klicke auf "Registrieren" in der Login-GUI.

---

**Viel Erfolg! 🚗✨**
