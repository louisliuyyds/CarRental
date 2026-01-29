# Db2 JDBC-Treiber

Dieses Projekt hängt vom IBM Db2 JDBC-Treiber ab, der aufgrund von Lizenzbestimmungen nicht in diesem Repository verteilt werden darf.

## Schritte
1. Laden Sie den Treiber von IBM herunter (z. B. `db2jcc4.jar` Version 11.5.9.0) mit Ihrem IBM-Konto.
2. Platzieren Sie die JAR-Datei in diesem Ordner mit dem Dateinamen `db2jcc4.jar`.
3. Bauen Sie das Projekt normal (die Maven-System Dependency in `pom.xml` verweist auf diesen Pfad).

Wenn Sie einen anderen Dateinamen bevorzugen, aktualisieren Sie `db2.driver.path` in `pom.xml` entsprechend.
