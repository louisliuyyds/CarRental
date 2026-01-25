-- ==========================================================
-- 1. CLEANUP (清理旧表)
-- 如果表不存在报错 -204，请忽略，继续执行 CREATE 部分
-- ==========================================================
DROP TABLE Mietvertrag_Zusatzoption IF EXISTS;
DROP TABLE Zusatzoption IF EXISTS;
DROP TABLE Mietvertrag IF EXISTS;
DROP TABLE Fahrzeug IF EXISTS;
DROP TABLE Fahrzeugtyp IF EXISTS;
DROP TABLE Mitarbeiter IF EXISTS;
DROP TABLE Kunde IF EXISTS;

-- ==========================================================
-- 2. TABELLEN ERSTELLEN (建表)
-- ==========================================================

-- --- Tabelle: Kunde ---
CREATE TABLE Kunde (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    -- Benutzer 属性
    AccountName VARCHAR(50) NOT NULL UNIQUE, -- 必须是 NOT NULL 才能 UNIQUE
    Passwort VARCHAR(100) NOT NULL,
    Vorname VARCHAR(50),
    Nachname VARCHAR(50),
    Email VARCHAR(100),
    -- Kunde 属性
    Kundennummer INT NOT NULL UNIQUE,        -- 必须是 NOT NULL 才能 UNIQUE
    Strasse VARCHAR(100),
    Hausnummer VARCHAR(10),
    PLZ VARCHAR(10),
    Ort VARCHAR(50),
    Geburtstag DATE,
    FuehrerscheinNummer VARCHAR(50),
    IstAktiv SMALLINT DEFAULT 1, 
    PRIMARY KEY (ID)
);

-- --- Tabelle: Mitarbeiter ---
CREATE TABLE Mitarbeiter (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    -- Benutzer 属性
    AccountName VARCHAR(50) NOT NULL UNIQUE, -- 必须是 NOT NULL 才能 UNIQUE
    Passwort VARCHAR(100) NOT NULL,
    Vorname VARCHAR(50),
    Nachname VARCHAR(50),
    Email VARCHAR(100),
    -- Mitarbeiter 属性
    Personalnummer VARCHAR(20) NOT NULL,
    BerechtigungsStufe INT,
    PRIMARY KEY (ID)
);

-- --- Tabelle: Fahrzeugtyp ---
CREATE TABLE Fahrzeugtyp (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    Hersteller VARCHAR(50),
    ModellBezeichnung VARCHAR(50),
    Kategorie VARCHAR(50),
    StandardTagesPreis DECIMAL(10, 2),
    Sitzplaetze INT,
    Antriebsart VARCHAR(20),
    ReichweiteKm INT,
    Beschreibung VARCHAR(255),
    PRIMARY KEY (ID)
);

-- --- Tabelle: Fahrzeug ---
CREATE TABLE Fahrzeug (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    Kennzeichen VARCHAR(15) NOT NULL UNIQUE, -- 必须是 NOT NULL 才能 UNIQUE
    AktuellerKilometerstand INT,
    Zustand VARCHAR(20),
    TuevDatum DATE,
    Fahrzeugtyp_ID INT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (Fahrzeugtyp_ID) REFERENCES Fahrzeugtyp(ID)
);

-- --- Tabelle: Zusatzoption ---
CREATE TABLE Zusatzoption (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    Bezeichnung VARCHAR(50),
    Aufpreis DECIMAL(10, 2),
    Beschreibung VARCHAR(255),
    PRIMARY KEY (ID)
);

-- --- Tabelle: Mietvertrag (这里是报错的地方，已修正) ---
CREATE TABLE Mietvertrag (
    ID INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1),
    -- 修正: 增加了 NOT NULL
    Mietnummer VARCHAR(20) NOT NULL UNIQUE, 
    StartDatum DATE NOT NULL,
    EndDatum DATE NOT NULL,
    Status VARCHAR(20),
    GesamtPreis DECIMAL(10, 2),
    -- 外键
    Kunde_ID INT NOT NULL,
    Fahrzeug_ID INT NOT NULL,
    Mitarbeiter_ID INT,
    PRIMARY KEY (ID),
    FOREIGN KEY (Kunde_ID) REFERENCES Kunde(ID),
    FOREIGN KEY (Fahrzeug_ID) REFERENCES Fahrzeug(ID),
    FOREIGN KEY (Mitarbeiter_ID) REFERENCES Mitarbeiter(ID)
);

-- --- Tabelle: Mietvertrag_Zusatzoption ---
CREATE TABLE Mietvertrag_Zusatzoption (
    Mietvertrag_ID INT NOT NULL,
    Zusatzoption_ID INT NOT NULL,
    PRIMARY KEY (Mietvertrag_ID, Zusatzoption_ID),
    FOREIGN KEY (Mietvertrag_ID) REFERENCES Mietvertrag(ID),
    FOREIGN KEY (Zusatzoption_ID) REFERENCES Zusatzoption(ID)
);

-- ==========================================================
-- 3. TESTDATEN (测试数据)
-- ==========================================================

INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, StandardTagesPreis, Antriebsart) 
VALUES ('VW', 'Golf 8', 'Kompakt', 45.00, 'VERBRENNER');

INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, StandardTagesPreis, Antriebsart) 
VALUES ('Tesla', 'Model 3', 'Limousine', 90.00, 'ELEKTRO');

INSERT INTO Fahrzeug (Kennzeichen, AktuellerKilometerstand, Zustand, TuevDatum, Fahrzeugtyp_ID)
VALUES ('B-XY 123', 15000, 'VERFUEGBAR', '2026-12-31', 1);

INSERT INTO Kunde (AccountName, Passwort, Vorname, Nachname, Kundennummer, Geburtstag)
VALUES ('maxmustermann', 'secret123', 'Max', 'Mustermann', 1001, '1990-05-20');

INSERT INTO Zusatzoption (Bezeichnung, Aufpreis) VALUES ('Kindersitz', 10.00);