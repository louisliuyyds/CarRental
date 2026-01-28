# 🚗 CarRental - Autovermietungssystem

> **Version:** 1.1.0  
> **Letzte Aktualisierung:** Januar 2026

Ein vollständiges **Java-basiertes Autovermietungssystem** mit grafischer Benutzeroberfläche (Swing), Datenbankanbindung (IBM Db2) und MVC-Architektur.

---

## 📋 Inhaltsverzeichnis

- [Features](#-features)
- [系统要求](#-系统要求)
- [快速开始](#-快速开始)
- [详细安装指南](#-详细安装指南)
- [项目结构](#-项目结构)
- [使用的技术](#-使用的技术)
- [更新历史](#-更新历史)
- [许可证](#-许可证)

---

## ✨ Features

### Kundenfunktionen
- ✅ Benutzerregistrierung und Login
- ✅ Verfügbare Fahrzeuge durchsuchen
- ✅ 车辆类别过滤（Kategorie-basiert）
- ✅ Fahrzeuge mit Datumsauswahl reservieren
- ✅ Zusatzoptionen hinzufügen (z.B. Kindersitz)
- ✅ 合同草稿功能（保存为ANGELEGT状态）
- ✅ 草稿继续预订（随时完成预订）
- ✅ 密码修改功能（新密码 + 确认密码验证）
- ✅ Reservierungshistorie anzeigen
- ✅ Vertragsdetails完整显示（所有车辆信息、单日价格）
- ✅ Reservierungen stornieren
- ✅ 个人信息滚动浏览（Meine Daten选项卡）

### Mitarbeiterfunktionen
- ✅ Fahrzeuge verwalten (Hinzufügen, Ändern, Löschen)
- ✅ **按状态过滤车辆**（VERFUEGBAR, VERMIETET, WARTUNG, IN_REPARATUR）
- ✅ Fahrzeugtypen konfigurieren
- ✅ Mietverträge einsehen und verwalten
- ✅ **Kunden信息管理**（Nutzerverwaltung选项卡）
- ✅ 系统statistiken anzeigen
- ✅ **统计卡片点击跳转**（集成导航）
- ✅ Verfügbarkeitsmanagement
- ✅ **车辆详细信息显示**（完整的Fahrzeugtyp数据）

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
git clone https://github.com/louisliuyyds/CarRental.git
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
git clone https://github.com/louisliuyyds/CarRental.git
cd CarRental

# 2. Datenbankkonfiguration erstellen
cp config/config.properties.template src/main/resources/config.properties
# Bearbeite src/main/resources/config.properties

# 3. Projekt bauen
./mvnw clean package

# 4. Anwendung starten
java -jar target/car-rental-system-1.0-SNAPSHOT.jar
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

## 📁 项目结构

```
CarRental/
├── src/
│   ├── main/
│   │   ├── java/com/carrental/
│   │   │   ├── controller/          # Business Logic Layer
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── BookingController.java
│   │   │   │   ├── CarRentalSystem.java (Singleton)
│   │   │   │   └── ContractStatusUpdater.java (自动更新)
│   │   │   ├── dao/                 # Data Access Layer
│   │   │   │   ├── GenericDao.java (Interface)
│   │   │   │   ├── KundeDao.java
│   │   │   │   ├── FahrzeugDao.java
│   │   │   │   ├── MietvertragDao.java
│   │   │   │   ├── MitarbeiterDao.java
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
│   │   │   │   ├── KundeDashboard.java (3 Tabs)
│   │   │   │   ├── MitarbeiterDashboard.java (4 Tabs)
│   │   │   │   ├── BookingDialog.java (草稿支持)
│   │   │   │   ├── FahrzeugPanel.java (过滤功能)
│   │   │   │   ├── CalendarPanel.java (自定义日历)
│   │   │   │   └── CalendarDateChooser.java
│   │   │   ├── util/                # Utilities
│   │   │   │   ├── DatabaseConfig.java
│   │   │   │   └── DatabaseConnection.java
│   │   │   └── Main.java            # Application Entry Point
│   │   └── resources/
│   │       └── config.properties    # Datenbankkonfiguration
├── config/
│   ├── config.properties            # Laufzeit-Konfiguration
│   └── config.properties.template   # Template für neue Instanzen
├── docs/
│   ├── OOA.md                       # Analyse-Dokument (不可改动)
│   ├── OOD.md                       # Design-Dokument (不可改动)
│   ├── Pflichtenheft.md             # Pflichtenheft (不可改动)
│   ├── IMPLEMENTATION_REPORT.md     # Implementierungsbericht
│   ├── TEST_GUIDE.md                # Testanleitungen
│   ├── CHANGELOG.md                 # 更新历史
│   └── database/
│       └── schema.sql               # Datenbankschema für Db2
├── lib/
│   └── db2jcc4.jar                  # Db2 JDBC-Treiber (optional, wird via Maven geladen)
├── pom.xml                          # Maven-Konfiguration
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── .gitignore                       # Git-Ignore-Regeln
└── README.md                        # Diese Datei
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
| **构建工具** | Maven | 3.8+ (Wrapper) |
| **架构** | MVC | 自定义 |
| **模式** | Singleton, Factory, DAO, Observer | - |

---

## 📊 项目统计

### 代码统计
- **总代码行数**: 8,913 行
- **Java文件数**: 32 个
- **类总数**: 28 个 (10 Model, 7 DAO, 4 Controller, 8 View, 2 Util, 1 Main, 3 枚举)
- **文档文件**: 6 个 Markdown文件
- **配置文件**: 3 个 (pom.xml, .gitignore, 配置模板)

### 功能统计
- **总功能数**: 25+
- **客户功能**: 15+
- **员工功能**: 10+
- **系统功能**: 5+

### 开发统计
- **Bug修复**: 10+
- **版本更新**: v1.1 (2026年1月)

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

## 📝 更新历史

### v1.1 (Januar 2026)

#### 新增功能
- **顾客界面增强**
  - 车辆类别过滤功能（Kategorie-basiert）
  - 合同草稿系统（保存为ANGELEGT状态）
  - 合同继续预订功能
  - 密码修改功能（新密码+确认验证）
  - Vertragsdetails完整显示（包含所有车辆信息和单日价格）
  - Meine Daten滚动支持
  - UI改进（标签重命名、按钮文字优化）

- **员工界面增强**
  - Nutzerverwaltung选项卡（Kunden完整信息管理）
  - 车辆状态过滤功能
  - 统计卡片点击跳转集成导航
  - Vertragsdetails与顾客界面相同显示

- **系统增强**
  - ContractStatusUpdater（合同状态自动更新）
  - CalendarPanel和CalendarDateChooser（自定义日历组件）
  - 调试和日志增强
  - Bug修复：Fahrzeugtyp数据加载、数据库JOIN、密码对话框布局

#### Bug修复
- 修复Vertragsdetails只显示车牌号的问题（Fahrzeugtyp未正确加载）
- 修复密码对话框标签被挤压的问题
- 修复车辆类型显示错误的问题
- 修复过滤下拉框重复选项的问题
- 修复UI元素被遮挡的布局问题

#### 改进
- 老年人友好的颜色方案（高对比度、大字体）
- 改进的错误提示（德语）
- 密码安全（不允许空密码、密码不匹配提示）
- 数据库调试输出增强

---

## 💡 常见问题

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
