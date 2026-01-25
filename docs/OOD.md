## **1\. Klassenentwurf & Datentypen** 

Das Analysemodell wurde in ein implementierungsnahes Entwurfsmodell überführt. Dabei wurden konkrete Datentypen festgelegt:

* **Zeitangaben:** Verwendung der Java-Klasse `java.time.LocalDate` für eine präzise Datumsverarbeitung (Geburtstage, Mietzeiträume).  
* **Geld:** Verwendung von `double` für Preisberechnungen (im Prototyp).  
* **Listen:** Verwendung von `java.util.List` und `ArrayList` zur dynamischen Verwaltung von Objekten.

## **2\. Architektur** 

Das System folgt einer vereinfachten Schichtenarchitektur:

* **Model:** Die Fachklassen (Kunde, Fahrzeug, Mietvertrag) enthalten die Daten und die direkten Geschäftsregeln.  
* **Controller (CarRentalSystem):** Die Singleton-Klasse steuert die Abläufe und verwaltet die Objekt-Listen (In-Memory-Datenhaltung).  
* Die Anwendungslogik wird durch **Controller-Klassen** von der Benutzeroberfläche getrennt (Separation of Concerns). Dies erleichtert die Wartbarkeit und ermöglicht den Austausch der GUI.  
* Für die zentrale Steuerungsklasse `CarRentalSystem` wird das **Singleton-Pattern** verwendet. Dies stellt sicher, dass zur Laufzeit genau eine Instanz existiert, die den globalen Zugriff auf die Listen der Kunden, Fahrzeuge und Verträge verwaltet.

## 3\. Design-Klassendiagramm

Sichtbarkeit: Attribute werden zu „-“ (privat), Methoden zu „+“ (öffentlich).

Typen: Konkrete Java-Datentypen wie List\<T\>, LocalDate und double wurden hinzugefügt.

Neue Klasse: CarRentalSystem wurde als Verwaltungsklasse (Singleton) hinzugefügt.  
\---  
config:  
  layout: elk  
\---  
**classDiagram**  
    direction TB  
    **class** CarRentalSystem **{**  
        **\-** static instance **:** CarRentalSystem  
        **\-** kundenListe **:** List**\~**Kunde**\~**  
        **\-** fahrzeugListe **:** List**\~**Fahrzeug**\~**  
        **\-** mietvertraege **:** List**\~**Mietvertrag**\~**  
        **\-** CarRentalSystem**()**  
        **\+** static getInstance**()** **:** CarRentalSystem  
        **\+** getKundeByNr**(**nr **:** int**)** **:** Kunde  
        **\+** createMietvertrag**(..**.**)** **:** Mietvertrag  
    **}**  
    **class** VertragsStatus **{**  
        **\<\<**enumeration**\>\>**  
        ANGELEGT  
        BESTAETIGT  
        LAUFEND  
        ABGESCHLOSSEN  
        STORNIERT  
    **}**

    **class** FahrzeugZustand **{**  
        **\<\<**enumeration**\>\>**  
        VERFUEGBAR  
        VERMIETET  
        WARTUNG  
    **}**

    **class** Antriebsart **{**  
        **\<\<**enumeration**\>\>**  
        VERBRENNER  
        ELEKTRO  
    **}**  
    **class** Benutzer **{**  
        **\<\<**abstract**\>\>**  
        **\-** accountName **:** String  
        **\-** passwort **:** String  
        **\-** vorname **:** String  
        **\-** nachname **:** String  
        **\-** email **:** String  
        **\+** login**(**pw **:** String**)** **:** boolean  
        **\+** setPasswort**(**neu **:** String**)** **:** void  
    **}**  
    **class** Kunde **{**  
        **\-** kundennummer **:** int  
        **\-** strasse **:** String  
        **\-** hausnummer **:** String  
        **\-** plz **:** String  
        **\-** ort **:** String  
        **\-** geburtstag **:** LocalDate  
        **\-** fuehrerscheinNummer **:** String  
        **\-** istAktiv **:** boolean  
        **\+** Kunde**(**nr**:** int, name**:** String**)**  
        **\+** registrieren**()** **:** void  
        **\+** kontoLoeschen**()** **:** void  
        **\+** istVolljaehrig**()** **:** boolean  
        **\+** getKundennummer**()** **:** int  
    **}**  
    **class** Mitarbeiter **{**  
        **\-** personalnummer **:** String  
        **\-** berechtigungsStufe **:** int  
        **\+** Mitarbeiter**(**pNr **:** String**)**  
        **\+** reportErstellen**()** **:** void  
    **}**

    **class** Mietvertrag **{**  
        **\-** mietnummer **:** String  
        **\-** startDatum **:** LocalDate  
        **\-** endDatum **:** LocalDate  
        **\-** status **:** VertragsStatus  
        **\-** gesamtPreis **:** double  
         
        **\+** Mietvertrag**(**k, f, start, end**)**  
        **\+** preisBerechnen**()** **:** double  
        **\+** stornieren**()** **:** void  
        **\+** abschliessen**()** **:** void  
        **\+** getMietdauerTage**()** **:** int  
    **}**

    **class** Fahrzeugtyp **{**  
        **\-** hersteller **:** String  
        **\-** modellBezeichnung **:** String  
        **\-** kategorie **:** String  
        **\-** standardTagesPreis **:** double  
        **\-** sitzplaetze **:** int  
        **\-** antriebsart **:** Antriebsart  
        **\-** reichweiteKm **:** int  
        **\-** beschreibung **:** String  
         
        **\+** getStandardTagesPreis**()** **:** double  
        **\+** getBeschreibung**()** **:** String  
    **}**

    **class** Fahrzeug **{**  
        **\-** kennzeichen **:** String  
        **\-** aktuellerKilometerstand **:** int  
        **\-** zustand **:** FahrzeugZustand  
        **\-** tuevDatum **:** LocalDate  
         
        **\+** istVerfuegbar**(**start **:** LocalDate, end **:** LocalDate**)** **:** boolean  
        **\+** setZustand**(**z **:** FahrzeugZustand**)** **:** void  
    **}**

    **class** Zusatzoption **{**  
        **\-** bezeichnung **:** String  
        **\-** aufpreis **:** double  
        **\-** beschreibung **:** String  
        **\+** getAufpreis**()** **:** double  
    **}**  
    Benutzer **\<|--** Kunde  
    Benutzer **\<|--** Mitarbeiter  
    CarRentalSystem o**\--\>** "0..\*" Kunde **:** verwaltet  
    CarRentalSystem o**\--\>** "0..\*" Fahrzeug **:** verwaltet  
    CarRentalSystem o**\--\>** "0..\*" Mietvertrag **:** verwaltet  
     
    Kunde "1" **\<--** "0..\*" Mietvertrag **:** hat  
    Mietvertrag "0..\*" **\--\>** "1" Fahrzeug **:** bucht  
    Mietvertrag "0..\*" **o--\>** "0..\*" Zusatzoption **:** beinhaltet

    Fahrzeugtyp "1" **\--** "0..\*" Fahrzeug **:** definiert  
    Mitarbeiter "0..1" **\--\>** "0..\*" Mietvertrag **:** bearbeitet

