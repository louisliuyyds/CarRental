# CarRental - Autovermietungssystem

Ein Java-basiertes Autovermietungssystem mit Swing GUI und IBM Db2 Datenbank.

## 🎯 Projektziele

Dieses Projekt demonstriert ein vollständiges MVC-Pattern-basiertes Autovermietungssystem mit:
- Swing Desktop-GUI für Kunden und Mitarbeiter
- IBM Db2 Datenbankintegration
- Authentifizierung und Benutzerverwaltung
- Fahrzeugverwaltung und Buchungssystem
- Vollständige CRUD-Operationen

## 📋 Voraussetzungen

- **Java 17** (aktuelle Compiler-Version in `pom.xml`)
- **Maven 3.8+** (oder Maven Wrapper)
- **IBM Db2 JDBC Driver** (Version 11.5.9.0)
- **Db2 Datenbankinstanz** (Schema in `docs/database/schema.sql`)

## 🚀 Schnellstart

### 1. Db2 JDBC Treiber einrichten

Der IBM Db2 JDBC Treiber kann nicht mit dem Repository verteilt werden und muss lokal installiert werden:

1. Laden Sie `db2jcc4.jar` Version 11.5.9.0 von IBM herunter
2. Platzieren Sie die Datei in `lib/db2jcc4.jar`
3. Die `pom.xml` nutzt einen `system`-scoped Dependency auf diesen Pfad

**Hinweis:** Details zur Treiberinstallation finden Sie in `lib/README.md`

### 2. Datenbank konfigurieren

1. Führen Sie das Schema aus: `docs/database/schema.sql`
2. Kopieren Sie `src/main/resources/config.properties.template` nach `src/main/resources/config.properties`
3. Bearbeiten Sie `config.properties` mit Ihren Datenbankzugangsdaten:

```properties
db.url=jdbc:db2://your-host:50000/your-database
db.username=your-username
db.password=your-password
db.ssl=false
```

### 3. Projekt bauen und starten

```bash
# Projekt kompilieren
mvn clean compile

# JAR erstellen
mvn clean package

# Anwendung starten
java -jar target/car-rental-system-1.0-SNAPSHOT.jar
```

**Alternative:** Direkt aus IDE ausführen mit Hauptklasse `com.carrental.Main`

## 📁 Projektstruktur

```
CarRental/
├── src/main/java/com/carrental/
│   ├── Main.java                    # Einstiegspunkt
│   ├── controller/                  # Business Logic Layer
│   │   ├── CarRentalSystem.java     # Singleton System
│   │   ├── AuthController.java      # Authentifizierung
│   │   └── BookingController.java   # Buchungslogik
│   ├── dao/                         # Data Access Layer
│   │   ├── GenericDao.java          # DAO Interface
│   │   ├── KundeDao.java
│   │   ├── FahrzeugDao.java
│   │   ├── MietvertragDao.java
│   │   └── ZusatzoptionDao.java
│   ├── model/                       # Domain Models
│   │   ├── Benutzer.java           # Abstract User
│   │   ├── Kunde.java
│   │   ├── Mitarbeiter.java
│   │   ├── Fahrzeug.java
│   │   ├── Fahrzeugtyp.java
│   │   ├── Mietvertrag.java
│   │   └── Zusatzoption.java
│   ├── util/                        # Utility Classes
│   │   ├── DatabaseConfig.java
│   │   └── DatabaseConnection.java
│   └── view/                        # GUI Components
│       ├── MainFrame.java
│       ├── LoginPanel.java
│       ├── RegisterPanel.java
│       ├── KundeDashboard.java
│       ├── MitarbeiterDashboard.java
│       ├── BookingDialog.java
│       └── FahrzeugPanel.java
├── docs/
│   ├── OOA.md                       # Object-Oriented Analysis
│   ├── OOD.md                       # Object-Oriented Design
│   ├── Pflichtenheft.md             # Requirements Specification
│   └── database/schema.sql          # Database Schema
└── config/config.properties          # Configuration Template

```

## 🔧 Konfiguration

### Datenbankkonfiguration

Die Anwendung liest Datenbankeinstellungen aus `config.properties`:

- **db.url**: JDBC URL zur Db2 Datenbank
- **db.username**: Datenbankbenutzer
- **db.password**: Datenbankpasswort
- **db.ssl**: SSL-Verbindung (true/false)
- **db.ssl.certificate**: Pfad zum SSL-Zertifikat (optional)

### Offline-Build

Für Builds ohne Internetverbindung:

```bash
# Einmalig mit Internet: Dependencies cachen
mvn dependency:go-offline

# Lokales Repository verwenden
mvn -Dmaven.repo.local=.m2repo clean package
```

## 🧪 Features

### Für Kunden:
- ✅ Registrierung und Login
- ✅ Verfügbare Fahrzeuge durchsuchen
- ✅ Fahrzeuge buchen mit Zusatzoptionen
- ✅ Buchungshistorie einsehen
- ✅ Buchungen stornieren

### Für Mitarbeiter:
- ✅ Fahrzeuge und Fahrzeugtypen verwalten
- ✅ Alle Mietverträge einsehen
- ✅ Fahrzeugzustände aktualisieren
- ✅ Statistiken anzeigen

## 📊 Datenbankschema

Das vollständige Schema finden Sie in `docs/database/schema.sql`. Haupttabellen:

- **Benutzer** - Basis für Kunde und Mitarbeiter
- **Kunde** - Kundeninformationen
- **Mitarbeiter** - Mitarbeiterinformationen
- **Fahrzeugtyp** - Fahrzeugkategorien
- **Fahrzeug** - Einzelne Fahrzeuge
- **Mietvertrag** - Buchungen
- **Zusatzoption** - Zusatzleistungen
- **Mietvertrag_Zusatzoption** - N:M Beziehung

## 🛠️ Entwicklung

### Verwendete Technologien

- **Java 17** - Programmiersprache
- **Maven** - Build Management
- **Swing** - GUI Framework
- **IBM Db2** - Datenbank
- **JDBC** - Datenbankzugriff

### Design Pattern

- **MVC** (Model-View-Controller)
- **DAO** (Data Access Object)
- **Singleton** (CarRentalSystem)
- **Factory** (DatabaseConnection)

### Code-Konventionen

- Deutsche Kommentare und Variablennamen (gemäß Anforderung)
- JavaDoc für alle öffentlichen Methoden
- PreparedStatements für alle DB-Operationen
- Java 17 Features (Records, Pattern Matching, etc.)

## 📖 Dokumentation

Detaillierte Dokumentation finden Sie in:

- **docs/OOD.md** - Klassendiagramme und Design
- **docs/OOA.md** - Anforderungsanalyse
- **docs/Pflichtenheft.md** - Spezifikation

## 🐛 Fehlerbehandlung

Die Anwendung implementiert mehrere Ebenen der Fehlerbehandlung:

1. **Global Exception Handler** - Fängt unbehandelte Exceptions ab
2. **Controller-Ebene** - Validierung und Business Logic Errors
3. **DAO-Ebene** - SQLException Handling
4. **GUI-Ebene** - Benutzerfreundliche Fehlerdialoge

## ⚠️ Bekannte Einschränkungen

- IBM Db2 Treiber muss manuell installiert werden (Lizenzgründe)
- SSL-Zertifikate für Db2-Cloud müssen separat konfiguriert werden
- Keine automatische Datenbankschema-Migration

## 📝 Lizenz

Dieses Projekt ist ein Studienprojekt für die HWR Berlin.

## 👥 Autoren

Entwickelt als Projektarbeit im Kurs "Objektorientierte Systemanalyse und -Entwurf".

---

**Hinweis:** Stellen Sie sicher, dass `config.properties` mit gültigen Zugangsdaten konfiguriert ist, bevor Sie die Anwendung starten.
