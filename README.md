# 🚗 CarRental - Autovermietungssystem

Ein vollständiges **Java-basiertes Autovermietungssystem** mit grafischer Benutzeroberfläche (Swing), Datenbankanbindung (IBM Db2) und MVC-Architektur.

## 📋 Inhaltsverzeichnis

- [Features](#-features)
- [Systemanforderungen](#-systemanforderungen)
- [Schnellstart](#-schnellstart)
- [Detaillierte Installationsanleitung](#-detaillierte-installationsanleitung)
- [Projektstruktur](#-projektstruktur)
- [Verwendete Technologien](#-verwendete-technologien)
- [Lizenz](#-lizenz)

---

## ✨ Features

### Kundenfunktionen
- ✅ Benutzerregistrierung und Login
- ✅ Verfügbare Fahrzeuge durchsuchen
- ✅ Fahrzeuge mit Datumsauswahl reservieren
- ✅ Zusatzoptionen hinzufügen (z.B. Kindersitz)
- ✅ Reservierungshistorie anzeigen
- ✅ Reservierungen stornieren

### Mitarbeiterfunktionen
- ✅ Fahrzeuge verwalten (Hinzufügen, Ändern, Löschen)
- ✅ Fahrzeugtypen konfigurieren
- ✅ Mietverträge einsehen und verwalten
- ✅ Systemstatistiken anzeigen
- ✅ Verfügbarkeitsmanagement

### Geschäftslogik
- 📊 **Intelligente Preisberechnung**: Tagesmiete + Zusatzoptionen + Staffelrabatte
- 🔒 **Konfliktdetektierung**: Automatische Überprüfung auf doppelte Reservierungen
- 💳 **Benutzervalidierung**: Altersverifikation, Kontoaktivität, Fahrerlaubnisstatus
- 📅 **Zeitfenstervalidierung**: Keine Buchungen in der Vergangenheit oder > 90 Tage

---

## 🖥️ Systemanforderungen

### Notwendig
- **Java Development Kit (JDK) 17+**  
  [Download Temurin JDK 17](https://adoptium.net/temurin/releases/?version=17)
- **IBM Db2 Datenbank**  
  (Cloud-Instanz oder lokal)
- **Maven 3.8+** (wird automatisch via Maven Wrapper bereitgestellt)

### Optional
- Git (für Versionskontrolle)
- IDE (Visual Studio Code, IntelliJ IDEA, Eclipse)

---

## 🚀 Schnellstart

### Windows (PowerShell)

```powershell
# 1. Repository klonen
git clone https://github.com/dein-benutzer/CarRental.git
cd CarRental

# 2. Datenbankkonfiguration erstellen
cp config/config.properties.template src/main/resources/config.properties
# Bearbeite src/main/resources/config.properties mit deinen Db2-Zugangsdaten

# 3. Projekt bauen
.\mvnw.cmd clean package

# 4. Anwendung starten
java -jar target/car-rental-system-jar-with-dependencies.jar
```

### Linux / macOS

```bash
# 1. Repository klonen
git clone https://github.com/dein-benutzer/CarRental.git
cd CarRental

# 2. Datenbankkonfiguration erstellen
cp config/config.properties.template src/main/resources/config.properties
# Bearbeite src/main/resources/config.properties

# 3. Projekt bauen
./mvnw clean package

# 4. Anwendung starten
java -jar target/car-rental-system-jar-with-dependencies.jar
```

**Die GUI sollte sich automatisch öffnen!**

---

## 📖 Detaillierte Installationsanleitung

### Schritt 1: Java 17 installieren

**Windows (PowerShell als Admin):**
```powershell
winget install --id EclipseAdoptium.Temurin.17.JDK -e
# Terminal neu öffnen
java -version
```

**macOS:**
```bash
brew install temurin@17
java -version
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install temurin-17-jdk
java -version
```

### Schritt 2: Projekt vorbereiten

```powershell
# Repository klonen (oder als ZIP herunterladen)
git clone https://github.com/dein-benutzer/CarRental.git
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

# Maven Wrapper startet automatisch (kein zusätzlicher Maven-Install nötig)
.\mvnw.cmd clean package

# Linux/macOS:
./mvnw clean package

# Nach erfolgreichem Build:
# ✅ target/car-rental-system-jar-with-dependencies.jar (JAR mit allen Dependencies)
# ✅ target/car-rental-system-1.0-SNAPSHOT.jar (JAR ohne Dependencies)
```

### Schritt 6: Anwendung ausführen

```powershell
# Starte mit der Fat-JAR (inkl. Db2-Treiber)
java -jar target/car-rental-system-jar-with-dependencies.jar

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

| Schicht | Technologie | Version |
|---------|-------------|---------|
| **Sprache** | Java | 17+ |
| **UI Framework** | Swing | JDK-Built-in |
| **Datenbank** | IBM Db2 | 11.5.x |
| **JDBC Driver** | com.ibm.db2:jcc | 11.5.9.0 |
| **Build Tool** | Maven | 3.8+ (Wrapper) |
| **Architektur** | MVC | Custom |
| **Pattern** | Singleton, Factory, DAO | - |

---

## 🔧 Troubleshooting

### Problem: "No suitable driver found for jdbc:db2://..."

**Lösung:**
```powershell
# Stelle sicher, dass die Fat-JAR verwendet wird:
java -jar target/car-rental-system-jar-with-dependencies.jar

# Oder explizit den Klassenpfad setzen:
java -cp "target/car-rental-system-1.0-SNAPSHOT.jar;lib/db2jcc4.jar" com.carrental.Main
```

### Problem: "invalid target release: 17"

**Lösung:**
```powershell
# Prüfe Java-Version
java -version

# Setze JAVA_HOME auf JDK 17
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.11"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

# Neuer Build
.\mvnw.cmd clean package
```

### Problem: "Initiale Daten konnten nicht geladen werden"

**Lösung:**
- Prüfe, ob die Db2-Datenbank online und erreichbar ist
- Verifiziere config.properties (URL, User, Password)
- Führe `docs/database/schema.sql` aus, um Tabellen zu erstellen

### Problem: "BUILD FAILURE - dependencies.dependency.systemPath"

**Lösung:**
Diese Warnung ist erwartbar. Maven wird die Db2-JAR automatisch vom IBM-Repository herunterladen. Der Build funktioniert trotzdem.

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

## 💡 Häufig gestellte Fragen

**F: Kann ich das Projekt auch ohne Db2 laufen lassen?**  
A: Nein, die Architektur ist auf Db2 ausgelegt. Für andere Datenbanken müssten die DAOs angepasst werden.

**F: Wie lange dauert der erste Build?**  
A: Beim ersten Mal ~2-5 Minuten (Maven lädt ~100 MB Dependencies). Danach ~30 Sekunden.

**F: Kann ich das Projekt in der IDE debuggen?**  
A: Ja! Importiere das Projekt als Maven Project in IntelliJ IDEA oder Eclipse.

**F: Wie erstelle ich einen neuen Benutzer?**  
A: Klicke auf "Registrieren" in der Login-GUI.

---

**Viel Erfolg! 🚗✨**
