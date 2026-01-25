# Phase 1: Das Statische Modell

## Identifikation fachlicher Klassen (Kandidaten)

Basierend auf der Substantivanalyse des Pflichtenhefts wurden folgende zentrale Geschäftsobjekte identifiziert:

* **Benutzer:** Ein allgemeiner Nutzer des Systems (Abstraktion).  
* **Kunde:** Eine Person, die Fahrzeuge bucht und Verträge abschließt.  
* **Mitarbeiter:** Eine Person, die den Fuhrpark verwaltet und Buchungen bearbeitet.  
* **Fahrzeug:** Das physische Objekt, das vermietet wird (identifiziert durch Kennzeichen).  
* **Mietvertrag:** Die rechtliche Vereinbarung zwischen Kunde und Unternehmen.  
* **Zusatzoption:** Buchbare Extras (z. B. Kindersitz, Versicherung).

## **Anwendung von Analysemustern** 

Um eine redundanzfreie und wartbare Struktur zu gewährleisten, wurden im Analysemodell folgende Patterns angewendet:

* **Exemplartyp-Muster (Item-Description-Pattern):** Zur Trennung von generischen Fahrzeuginformationen (z. B. Modell, Marke, technische Daten) und konkreten physischen Exemplaren wurde die Klasse `Fahrzeug` in `Fahrzeugtyp` (Beschreibung) und `Fahrzeug` (physisches Objekt) aufgeteilt. Dies vermeidet Datenredundanz bei baugleichen Fahrzeugen.  
* **Rollen-Muster / Generalisierung:** Da sowohl Kunden als auch Mitarbeiter grundlegende Benutzerdaten (Login, Name) teilen, wurde eine Generalisierungshierarchie mit der abstrakten Oberklasse `Benutzer` eingeführt.

## Fachliches Klassendiagramm

![][image1]  
classDiagram  
    direction TB

    %% \--- 1\. 枚举定义 \---  
    class VertragsStatus {  
        \<\<enumeration\>\>  
        ANGELEGT  
        BESTAETIGT  
        LAUFEND  
        ABGESCHLOSSEN  
        STORNIERT  
    }

    class FahrzeugZustand {  
        \<\<enumeration\>\>  
        VERFUEGBAR  
        VERMIETET  
        WARTUNG  
    }

    class Antriebsart {  
        \<\<enumeration\>\>  
        VERBRENNER  
        ELEKTRO  
    }

    %% \--- 2\. 核心类定义 \---  
      
    %% 抽象父类  
    class Benutzer {  
        \<\<abstract\>\>  
        accountName : String  
        passwort : String  
        vorname : String  
        nachname : String  
        email : String  
    }

    class Kunde {  
        kundennummer : Integer  
        strasse : String  
        hausnummer : String  
        plz : String  
        ort : String  
        geburtstag : Date  
        fuehrerscheinNummer : String  
        istAktiv : Boolean  
        registrieren()  
        kontoLoeschen()  
    }

    class Mitarbeiter {  
        personalnummer : String  
        berechtigungsStufe : Integer  
    }

    class Mietvertrag {  
        mietnummer : String  
        startDatum : Date  
        endDatum : Date  
        /mietdauerTage : Integer  
        /gesamtPreis : Waehrung  
        status : VertragsStatus  
        preisBerechnen()  
        stornieren()  
        abschliessen()  
    }

    class Fahrzeugtyp {  
        hersteller : String  
        modellBezeichnung : String  
        kategorie : String  
        standardTagesPreis : Waehrung  
        sitzplaetze : Integer  
        antriebsart : Antriebsart  
        reichweiteKm : Integer  
        beschreibung : String  
    }

    class Fahrzeug {  
        kennzeichen : String  
        aktuellerKilometerstand : Integer  
        zustand : FahrzeugZustand  
        tuevDatum : Date  
    }

    class Zusatzoption {  
        bezeichnung : String  
        aufpreis : Waehrung  
        beschreibung : String  
    }

    %% \--- 3\. 关系定义 \---

    %% 继承关系 (Inheritance)  
    Benutzer \<|-- Kunde  
    Benutzer \<|-- Mitarbeiter

    %% 核心关联  
    %% 客户持有合同 (1对多)  
    Kunde "1" \--\> "0..\*" Mietvertrag : erteilt

    %% 员工处理合同 (可选)  
    Mitarbeiter "0..1" \--\> "0..\*" Mietvertrag : bearbeitet

    %% 合同引用车辆 (单向关联)  
    Mietvertrag "0..\*" \--\> "1" Fahrzeug : bucht

    %% 合同包含选项 (多对多)  
    Mietvertrag "0..\*" \--\> "0..\*" Zusatzoption : beinhaltet

    %% 车辆属于类型 (Exemplartyp模式)  
    Fahrzeugtyp "1" \-- "0..\*" Fahrzeug : beschreibt

## 3\. Dynamisches Konzept (Interaktionsdiagramme)

Um die komplexen Abläufe der Anwendungsfälle und das Zusammenspiel der Geschäftsobjekte zu visualisieren, wurden Sequenzdiagramme erstellt.

**Szenario: Fahrzeugbuchung (Happy Path & Validierung)**

Das folgende Sequenzdiagramm modelliert den Kernprozess der Fahrzeugvermietung. Es verdeutlicht, wie das System sicherstellt, dass nur verfügbare Fahrzeuge gebucht werden und wie der Gesamtpreis zustande kommt.

![][image2]

* **Akteure & Beteiligte:** Der `Kunde` interagiert mit einer steuernden Einheit (`:System`), welche die Kommunikation zwischen den Fachklassen (`Fahrzeugtyp`, `Fahrzeug`, `Mietvertrag`) koordiniert.  
* **Schritt 2 (Validierung):** Bevor die Datenbankabfrage startet, wird fachlich geprüft, ob der Mietzeitraum logisch ist (z. B. *Enddatum \> Startdatum*). Dies reduziert unnötige Systemlast.  
* **Schritt 3-4 (Exemplartyp-Muster):** Hier wird das in der statischen Analyse definierte Pattern angewendet. Die Anfrage richtet sich zunächst an den `Fahrzeugtyp` (z. B. "Golf Klasse"). Dieser iteriert über seine konkreten `Fahrzeug`\-Exemplare, um eines zu finden, das im gewünschten Zeitraum nicht belegt ist.  
* **Schritt 6 (Zusatzoptionen):** Da Extras (z. B. Kindersitz) optional sind und den Preis beeinflussen, erfolgt deren Auswahl iterativ vor dem Vertragsabschluss.  
* **Schritt 10 (Konsistenz):** Erst wenn alle Prüfungen erfolgreich waren, wird das Objekt `Mietvertrag` instanziiert. Dies garantiert, dass keine ungültigen Verträge im System existieren.

*(Hinweis: Die Prüfung der Volljährigkeit erfolgt bereits vorgelagert im Use Case "Registrierung" und wird hier vorausgesetzt.)*

sequenceDiagram  
    autonumber  
      
    actor User as :Kunde  
    participant System as :System  
    participant F\_Typ as :Fahrzeugtyp  
    participant F\_Konkret as :Fahrzeug  
    participant Contract as :Mietvertrag

    note over User, Contract: Use Case: Fahrzeug buchen

    %% 1\. 启动预订  
    User-\>\>System: 1\. buchungStarten(fahrzeugTyp, start, end)  
    activate System  
      
    %% 2\. 日期验证  
    System-\>\>System: 2\. validiereZeitraum(start, end)  
      
    alt \[Zeitraum ungültig (z.B. End \< Start)\]  
        System--\>\>User: Fehlermeldung: "Ungültiger Zeitraum"  
    else \[Zeitraum gültig\]  
          
        %% 3\. 查找可用车辆 (Exemplartyp Pattern)  
        note right of System: Prüfen, ob ein physisches Auto frei ist  
          
        System-\>\>F\_Typ: 3\. getVerfuegbareFahrzeuge(start, end)  
        activate F\_Typ  
          
        loop Über alle Fahrzeuge dieses Typs  
            F\_Typ-\>\>F\_Konkret: 4\. istVerfuegbar(start, end)?  
            activate F\_Konkret  
            F\_Konkret--\>\>F\_Typ: boolean  
            deactivate F\_Konkret  
        end  
          
        F\_Typ--\>\>System: listeVerfuegbarerFahrzeuge  
        deactivate F\_Typ

        opt \[Kein Fahrzeug verfügbar\]  
            System--\>\>User: Fehlermeldung: "Kein Fahrzeug dieses Typs verfügbar"  
        end

        %% 4\. 选择附加选项 (循环/多次选择)  
        System--\>\>User: 5\. zeigeVerfuegbareOptionen()  
          
        loop Extras hinzufügen  
            User-\>\>System: 6\. addZusatzoption(option)  
        end

        %% 5\. 价格预览与确认  
        System-\>\>System: 7\. berechneVorschauPreis()  
        System--\>\>User: 8\. zeigePreisUndBestaetigung()

        User-\>\>System: 9\. buchungAbschliessen()

        %% 6\. 创建合同并锁定车辆  
        System-\>\>Contract: 10\. create(kunde, fahrzeug, optionen)  
        activate Contract  
        Contract--\>\>System: vertrag (Status: ANGELEGT)  
        deactivate Contract

        System--\>\>User: 11\. Buchungsbestätigung anzeigen  
    end  
      
    deactivate System  


