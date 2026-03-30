# Db2 JDBC-Treiber

Dieses Projekt hängt vom IBM Db2 JDBC-Treiber ab, der aufgrund von Lizenzbestimmungen nicht in diesem Repository verteilt werden darf.

## Schritte

1. Laden Sie den Treiber von IBM herunter (z. B. `db2jcc4.jar` Version 11.5.9.0) mit Ihrem IBM-Konto.
2. Platzieren Sie die JAR-Datei in diesem Ordner mit dem Dateinamen `db2jcc4.jar`.
3. Bauen Sie das Projekt normal (Gradle wird den Treiber automatisch vom IBM-Repository herunterladen).

## Alternative

Wenn Sie keine IBM-Zugangsdaten haben, können Sie den Treiber manuell herunterladen und in diesem Ordner platzieren. Gradle wird den lokalen Treiber verwenden, falls vorhanden.

**Hinweis:** Gradle lädt die Db2-Dependencies automatisch vom IBM Public Maven Repository (https://public.dhe.ibm.com/ibmdl/export/pub/software/websphere/maven/repository/).
