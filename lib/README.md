# Db2 JDBC Driver

This project depends on the IBM Db2 JDBC driver, which cannot be redistributed in this repository due to licensing.

## Steps
1. Download the driver from IBM (e.g., `db2jcc4.jar` version 11.5.9.0) using your IBM account.
2. Place the jar in this folder with the filename `db2jcc4.jar`.
3. Build the project normally (the Maven `system` dependency in `pom.xml` points to this path).

If you prefer to use a different filename, update `db2.driver.path` in `pom.xml` to match.
