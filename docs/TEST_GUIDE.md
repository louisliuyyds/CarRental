# Test-Leitfaden für CarRental System

## 📋 Vorbereitung

### 1. Datenbank vorbereiten

Führen Sie zuerst das Schema aus:
```sql
-- Siehe docs/database/schema.sql
```

### 2. Testdaten einfügen (Optional)

```sql
-- Beispiel: Fahrzeugtyp hinzufügen
INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, StandardTagesPreis, Sitzplaetze, Antriebsart)
VALUES ('Volkswagen', 'Golf', 'Kompaktklasse', 45.00, 5, 'VERBRENNER');

INSERT INTO Fahrzeugtyp (Hersteller, ModellBezeichnung, Kategorie, StandardTagesPreis, Sitzplaetze, Antriebsart)
VALUES ('Tesla', 'Model 3', 'Mittelklasse', 89.00, 5, 'ELEKTRO');

-- Beispiel: Fahrzeuge hinzufügen
INSERT INTO Fahrzeug (Kennzeichen, AktuellerKilometerstand, Zustand, Fahrzeugtyp_ID)
VALUES ('B-AB 1234', 25000, 'VERFUEGBAR', 1);

INSERT INTO Fahrzeug (Kennzeichen, AktuellerKilometerstand, Zustand, Fahrzeugtyp_ID)
VALUES ('B-CD 5678', 12000, 'VERFUEGBAR', 2);

-- Beispiel: Zusatzoptionen
INSERT INTO Zusatzoption (Bezeichnung, Beschreibung, Aufpreis)
VALUES ('GPS Navigation', 'Navigationssystem mit Echtzeit-Verkehrsdaten', 5.00);

INSERT INTO Zusatzoption (Bezeichnung, Beschreibung, Aufpreis)
VALUES ('Kindersitz', 'Kindersitz für Kinder von 9-36 kg', 3.00);

INSERT INTO Zusatzoption (Bezeichnung, Beschreibung, Aufpreis)
VALUES ('Zusatzfahrer', 'Zweiter Fahrer erlaubt', 10.00);

-- Beispiel: Testmitarbeiter (Passwort: "test123")
INSERT INTO Benutzer (Account_Name, Passwort_Hash, Vorname, Nachname, Email)
VALUES ('admin', 'test123', 'Max', 'Mustermann', 'admin@carrental.de');

INSERT INTO Mitarbeiter (Benutzer_ID, Mitarbeiternummer, Abteilung, istAktiv)
VALUES (1, 1001, 'Vermietung', TRUE);
```

## 🧪 Testszenarien

### Test 1: Kundenregistrierung

1. **Anwendung starten**
   ```bash
   java -jar target/car-rental-system-1.0-SNAPSHOT.jar
   ```

2. **Auf "Registrieren" klicken**

3. **Testdaten eingeben:**
   - Account-Name: `test_kunde`
   - Passwort: `test123456`
   - Passwort wiederholen: `test123456`
   - Vorname: `Anna`
   - Nachname: `Schmidt`
   - E-Mail: `anna.schmidt@test.de`
   - Geburtstag: `1990-05-15`
   - Führerscheinnummer: `D1234567890`

4. **Erwartetes Ergebnis:**
   - ✅ Erfolgsmeldung
   - ✅ Automatischer Login
   - ✅ Kunden-Dashboard wird angezeigt

### Test 2: Kundenlogin

1. **Auf "Zurück zum Login" klicken** (falls registriert)

2. **Anmeldedaten eingeben:**
   - Account-Name: `test_kunde`
   - Passwort: `test123456`

3. **Auf "Anmelden" klicken**

4. **Erwartetes Ergebnis:**
   - ✅ Login erfolgreich
   - ✅ Kunden-Dashboard wird angezeigt
   - ✅ Verfügbare Fahrzeuge werden geladen

### Test 3: Fahrzeuge durchsuchen

1. **Im Kunden-Dashboard**

2. **Tab "Verfügbare Fahrzeuge" öffnen**

3. **Erwartetes Ergebnis:**
   - ✅ Liste aller verfügbaren Fahrzeuge wird angezeigt
   - ✅ Spalten: ID, Kennzeichen, Hersteller, Modell, Kategorie, Tagespreis, Zustand
   - ✅ Nur Fahrzeuge mit Zustand "VERFUEGBAR" werden angezeigt

### Test 4: Fahrzeug buchen

1. **Fahrzeug in der Liste auswählen**

2. **Auf "Buchen" klicken**

3. **Im Buchungsdialog:**
   - Startdatum: Morgen (automatisch vorbelegt)
   - Enddatum: In 7 Tagen (automatisch vorbelegt)
   - Optional: Zusatzoptionen auswählen (GPS, Kindersitz, etc.)

4. **Auf "Preis berechnen" klicken**

5. **Erwartetes Ergebnis:**
   - ✅ Gesamtpreis wird berechnet und angezeigt
   - ✅ Preisberechnung berücksichtigt:
     - Tagespreis × Anzahl Tage
     - Zusatzoptionen × Anzahl Tage
     - Mengenrabatt (5%/10%/15% bei 7/14/30+ Tagen)

6. **Auf "Jetzt buchen" klicken**

7. **Erwartetes Ergebnis:**
   - ✅ Erfolgsmeldung mit Mietnummer
   - ✅ Dialog schließt sich
   - ✅ Buchung erscheint im Tab "Meine Buchungen"

### Test 5: Buchungshistorie ansehen

1. **Tab "Meine Buchungen" öffnen**

2. **Erwartetes Ergebnis:**
   - ✅ Alle Buchungen des Kunden werden angezeigt
   - ✅ Spalten: Mietnummer, Fahrzeug, Startdatum, Enddatum, Preis, Status
   - ✅ Aktuelle Buchung ist sichtbar

### Test 6: Buchung stornieren

1. **Buchung in der Liste auswählen**

2. **Auf "Stornieren" klicken**

3. **Bestätigung im Dialog**

4. **Erwartetes Ergebnis:**
   - ✅ Status ändert sich zu "STORNIERT"
   - ✅ Erfolgsmeldung
   - ✅ Fahrzeug wird wieder verfügbar

### Test 7: Mitarbeiter-Login (falls Testmitarbeiter existiert)

1. **Abmelden**

2. **Mit Mitarbeiter-Account anmelden:**
   - Account-Name: `admin`
   - Passwort: `test123`

3. **Erwartetes Ergebnis:**
   - ✅ Mitarbeiter-Dashboard wird angezeigt
   - ✅ Tabs: Fahrzeugverwaltung, Vertragsverwaltung, Statistiken

### Test 8: Fahrzeug hinzufügen (Mitarbeiter)

1. **Tab "Fahrzeugverwaltung" öffnen**

2. **Untergeordneter Tab "Fahrzeuge"**

3. **Auf "Hinzufügen" klicken**

4. **Testdaten eingeben:**
   - Kennzeichen: `B-TEST 999`
   - Fahrzeugtyp: Aus Liste wählen
   - Zustand: `VERFUEGBAR`

5. **Auf "Speichern" klicken**

6. **Erwartetes Ergebnis:**
   - ✅ Fahrzeug wird in Datenbank gespeichert
   - ✅ Erfolgsmeldung
   - ✅ Fahrzeug erscheint in der Liste

### Test 9: Fahrzeugtyp hinzufügen (Mitarbeiter)

1. **Tab "Fahrzeugtypen" öffnen**

2. **Auf "Hinzufügen" klicken**

3. **Testdaten eingeben:**
   - Hersteller: `BMW`
   - Modell: `3er`
   - Kategorie: `Mittelklasse`
   - Antriebsart: `VERBRENNER`
   - Sitzplätze: `5`
   - Tagespreis: `75.00`

4. **Auf "Speichern" klicken**

5. **Erwartetes Ergebnis:**
   - ✅ Fahrzeugtyp wird gespeichert
   - ✅ Erfolgsmeldung
   - ✅ Typ erscheint in der Liste

### Test 10: Vertragsverwaltung (Mitarbeiter)

1. **Tab "Vertragsverwaltung" öffnen**

2. **Auf "Aktualisieren" klicken**

3. **Erwartetes Ergebnis:**
   - ✅ Alle Mietverträge werden angezeigt
   - ✅ Verträge aller Kunden sind sichtbar

4. **Vertrag auswählen und "Details anzeigen" klicken**

5. **Erwartetes Ergebnis:**
   - ✅ Detailinformationen werden angezeigt:
     - Mietnummer
     - Kundenname
     - Fahrzeug
     - Zeitraum
     - Gesamtpreis
     - Status
     - Zusatzoptionen

### Test 11: Statistiken anzeigen (Mitarbeiter)

1. **Tab "Statistiken" öffnen**

2. **Auf "Statistiken aktualisieren" klicken**

3. **Erwartetes Ergebnis:**
   - ✅ Anzahl aller Fahrzeuge
   - ✅ Anzahl aktiver Verträge
   - ✅ Anzahl verfügbarer Fahrzeuge
   - ✅ Anzahl registrierter Kunden

## 🐛 Fehlerszenarien testen

### Test E1: Ungültige Login-Daten

1. **Falsches Passwort eingeben**
2. **Erwartetes Ergebnis:** ❌ Fehlermeldung "Ungültige Anmeldedaten"

### Test E2: Doppelte Registrierung

1. **Bereits existierenden Account-Namen verwenden**
2. **Erwartetes Ergebnis:** ❌ Fehlermeldung "Account-Name bereits vergeben"

### Test E3: Ungültige Buchungsdaten

1. **Enddatum vor Startdatum wählen**
2. **Erwartetes Ergebnis:** ❌ Warnung "Enddatum muss nach dem Startdatum liegen"

### Test E4: Buchung ohne Preisberechnung

1. **Direkt auf "Jetzt buchen" klicken ohne Preisberechnung**
2. **Erwartetes Ergebnis:** ❌ Warnung "Bitte berechnen Sie zuerst den Preis"

### Test E5: Datenbankverbindungsfehler

1. **Falsche Datenbank-Konfiguration in `config.properties`**
2. **Anwendung starten**
3. **Erwartetes Ergebnis:** ❌ Startfehler-Dialog mit Hinweis auf Konfigurationsproblem

## ✅ Checkliste für vollständigen Test

- [ ] Kundenregistrierung funktioniert
- [ ] Kundenlogin funktioniert
- [ ] Fahrzeugliste wird korrekt angezeigt
- [ ] Buchungsdialog öffnet sich
- [ ] Preisberechnung ist korrekt
- [ ] Buchung wird gespeichert
- [ ] Buchungshistorie wird angezeigt
- [ ] Buchung kann storniert werden
- [ ] Mitarbeiter-Login funktioniert
- [ ] Fahrzeugverwaltung funktioniert
- [ ] Fahrzeugtypen können erstellt werden
- [ ] Vertragsliste wird angezeigt
- [ ] Vertragsdetails werden angezeigt
- [ ] Statistiken werden berechnet
- [ ] Fehlerbehandlung funktioniert korrekt
- [ ] GUI reagiert flüssig
- [ ] Keine Exceptions in der Konsole

## 🎯 Erfolgsmetriken

Das System gilt als erfolgreich getestet, wenn:
- ✅ Alle Hauptfunktionen ohne Fehler ausführbar sind
- ✅ Keine unkontrollierten Exceptions auftreten
- ✅ Fehlerbehandlung benutzerfreundlich ist
- ✅ Datenbankintegrität erhalten bleibt
- ✅ GUI reagiert konsistent und intuitiv

---

## 🔬 高级功能测试 (v1.1新增)

### Test 12: 顾客密码修改

1. **在顾客Dashboard点击 "Meine Daten"**

2. **点击 "Passwort ändern" 按钮**

3. **输入测试数据：**
   - Neues Passwort: `NewPass123!`
   - Passwort bestätigen: `NewPass123!`

4. **点击 "OK"**

5. **验证结果：**
   - ✅ 密码成功修改
   - ✅ 显示成功消息 "Passwort erfolgreich geändert."
   - ✅ 下次登录需要新密码

6. **测试错误场景：**
   - 密码不匹配 → 显示错误提示 "Die Passwörter stimmen nicht überein."
   - 空密码 → 显示错误提示 "Das Passwort darf nicht leer sein."
   - 点击"Abbrechen" → 对话框关闭，密码不变

### Test 13: 合同草稿功能

1. **在顾客Dashboard → "Autos suchen"**

2. **选择一辆可用车辆并设置日期**

3. **在BookingDialog选择日期和附加选项**

4. **点击 "Als Entwurf speichern" 按钮**

5. **验证结果：**
   - ✅ 合同以ANGELEGT状态保存
   - ✅ 显示草稿成功消息
   - ✅ 车辆状态保持VERFUEGBAR（不变为VERMIETET）
   - ✅ Dialog关闭

6. **在 "Meine Buchungen" 查看**
   - ✅ 合同状态显示为"ANGELEGT"
   - ✅ 车辆状态未改变

7. **选择angelegt状态的合同，点击"fortsetzen"按钮**

8. **在打开的Dialog中可以修改日期和选项**

9. **点击 "Buchung abschließen"**

10. **验证结果：**
    - ✅ 合同状态变为BESTAETIGT（如果日期是今天或过去）或LAUFEND（如果日期是今天）
    - ✅ 车辆状态变为VERMIETET
    - ✅ 显示成功消息
    - ✅ Dialog关闭

11. **测试场景：在angelegt状态下取消合同**
    - 选择angelegt状态的合同
    - 点击"Stornieren"
    - 应该允许取消并显示成功消息（草稿可以自由取消）

### Test 14: 车辆类别过滤功能

1. **在顾客Dashboard → "Autos suchen"**

2. **设置开始和结束日期**

3. **点击 "Autos anzeigen"**

4. **查看显示的车辆列表**

5. **在"Kategorie:"下拉框选择不同类别**

6. **验证结果：**
   - ✅ 车辆列表自动筛选为所选类别
   - ✅ 列表只显示该类别的车辆
   - ✅ 选择"Alle"后显示所有可用车辆
   - ✅ 过滤后仍可正常预订车辆

### Test 15: 员工Nutzerverwaltung跳转

1. **员工登录 → 进入"Statistiken"选项卡**

2. **点击 "Registrierte Kunden"统计卡片**

3. **验证结果：**
   - ✅ 自动跳转到"Nutzerverwaltung"选项卡
   - ✅ 激活"Registrierte Kunden"选项卡显示
   - ✅ 客户列表完整显示

### Test 16: 员工车辆状态过滤

1. **员工登录 → 进入"Fahrzeugverwaltung" → "Fahrzeuge"选项卡**

2. **点击 "Filter" 按钮**

3. **在过滤对话框中选择不同的状态**

4. **验证结果：**
   - ✅ 车辆列表按所选状态筛选
   - ✅ 支持的状态：Alle, VERFUEGBAR, VERMIETET, WARTUNG, IN_REPARATUR
   - ✅ 过滤对话框关闭后过滤保持
   - ✅ 可以重置为"Alle"

### Test 17: 员工跳转导航

1. **员工登录 → 进入"Statistiken"选项卡**

2. **测试各个统计卡片点击跳转：**
   - Gesamte Fahrzeuge → Fahrzeugverwaltung
   - Aktive Verträge → Vertragsverwaltung（应用LAUFEND过滤）
   - Verfügbare Fahrzeuge → Fahrzeugverwaltung（应用VERFUEGBAR过滤）

3. **验证结果：**
   - ✅ 跳转到正确的选项卡
   - ✅ 激活相应的子选项卡
   - ✅ 自动应用正确的过滤
   - ✅ 数据正确加载

### Test 18: Vertragsdetails完整显示验证

1. **员工登录 → "Vertragsverwaltung"选项卡**

2. **选择任意合同，点击 "Details anzeigen"**

3. **验证弹窗中显示的完整信息：**
   - ✅ 最顶部显示合同号码（Vertragsnummer）
   - ✅ Kunde区块：Kunden-ID, 姓名, Email
   - ✅ Fahrzeug区块：
     - Kennzeichen
     - Hersteller
     - Modell
     - Kategorie
     - Antriebsart
     - Sitzplätze
     - **Tagespreis**（绿色高亮显示）
   - ✅ Mietzeitraum区块：Startdatum, Enddatum
   - ✅ Zusatzoptionen区块（完整列表）
   - ✅ Status区块
   - ✅ Gesamtpreis（底部，红色高亮显示）

4. **验证控制台输出（DEBUG消息）：**
   ```
   DEBUG Vertragsdetails:
     Mietnummer: MV-...
     Fahrzeug Kennzeichen: B-ABC 123
     Fahrzeugtyp: NOT NULL
       Hersteller: BMW
       Modell: 3er
       Kategorie: Limousine
       Antriebsart: VERBRENNER
       Sitzplätze: 5
       Tagespreis: 80.0
   ```

### Test 19: 老年人友好性验证

1. **打开Vertragsdetails弹窗**

2. **验证字体大小和颜色对比度：**
   - ✅ 一级标题（如"Vertragsnummer"）：24pt, 加粗，黑色
   - ✅ 二级标题（如"Kunde"、"Fahrzeug"）：18pt, 加粗，深蓝色（#003366）
   - ✅ 详细信息行：14pt, 普通字体，黑色
   - ✅ Tagespreis：16pt, 加粗, 绿色（#006633）
   - ✅ Gesamtpreis：24pt, 加粗, 红色（#CC0000）

3. **验证可读性：**
   - ✅ 字体足够大，老年人易于阅读
   - ✅ 颜色对比度高（黑、深蓝、绿、红）
   - ✅ 信息层次清晰（标题 > 区块标题 > 详情）
   - ✅ 总价醒目突出

### Test 20: 密码对话框UI优化

1. **顾客登录 → "Meine Daten" → "Passwort ändern"**

2. **验证对话框尺寸和布局：**
   - ✅ 对话框宽度：500px
   - ✅ 对话框高度：280px
   - ✅ "Passwort bestätigen:"标签完整显示（不被挤压）
   - ✅ 输入框宽度：280px
   - ✅ 标签和输入框对齐

### Test 21: Meine Daten滚动功能

1. **顾客登录 → "Meine Daten"**

2. **尝试滚动表单内容**

3. **验证结果：**
   - ✅ 表单可以垂直滚动
   - ✅ 滚动速度适中
   - ✅ 所有字段都可访问（包括用户名和生日）
   - ✅ 水平方向不滚动（不必要）

### Test 22: 合同草稿数据完整性

1. **创建草稿合同并保存**

2. **验证数据库数据：**
   ```sql
   SELECT * FROM Mietvertrag WHERE Status = 'ANGELEGT';
   ```

3. **验证：**
   - ✅ 合同状态为ANGELEGT
   - ✅ Fahrzeug_ID关联正确
   - Fahrzeug_ID对应的Fahrzeugtyp_ID仍然指向该车型
   - ✅ 车辆的Zustand保持VERFUEGBAR
   - ✅ 所有选定的Zusatzoptionen正确关联

---

## 🐛 Bug修复测试 (v1.1新增)

### Test B1: Vertragsdetails车辆信息缺失修复

1. **重现问题：**
   - 之前版本只显示车牌号

2. **验证修复：**
   - ✅ 完整的车辆信息显示
   - ✅ 控制台显示Fahrzeugtyp为NOT NULL
   - ✅ 所有Fahrzeugtyp字段正确显示

### Test B2: 密码对话框标签被挤压修复

1. **重现问题：**
   - "Passwort bestätigen:"被截断为"Passwort bestät"

2. **验证修复：**
   - ✅ "Passwort bestätigen:"完整显示
   - ✅ 输入框宽度适中
   - ✅ 对话框布局平衡

### Test B3: 车辆类型信息错误显示修复

1. **重现问题：**
   - 创建的Familienauto类型仍显示为"kompakt"

2. **验证修复：**
   - ✅ 下拉框显示格式："型号 (Kategorie)"
   - ✅ 显示正确的类别信息
   - ✅ 数据库中Fahrzeugtyp_ID正确

### Test B4: 过滤下拉框重复选项修复

1. **重现问题：**
   - 选择不同类型时，选项重复（如"Alle, Tesla, Alle, Tesla"）

2. **验证修复：**
   - ✅ 只显示Kategorie过滤
   - ✅ 选项不重复
   - ✅ 默认显示"Alle"

### Test B5: 顾客取消进行中的合同

1. **重新安装v1.1版本**

2. **尝试取消LAUFEND或BESTAETIGT状态的合同**

3. **验证行为：**
   - ❌ 显示错误提示："Buchung kann nicht storniert werden. Bitte wenden Sie sich für Änderungen an den Mitarbeiter."
   - ✅ ANGELEGT状态的合同可以自由取消

---

## 📝 测试协议

Nach jedem Test dokumentieren Sie:

```
Test: [Testnummer und Name]
Datum: [TT.MM.JJJJ]
Tester: [Name]
Ergebnis: ✅ Bestanden / ❌ Fehlgeschlagen
Bemerkungen: [Besonderheiten, Fehler, Verbesserungsvorschläge]
```

## 🔍 Performance-Tests

1. **Ladezeit:** GUI sollte in < 2 Sekunden starten
2. **Datenbankabfragen:** Fahrzeugliste sollte in < 1 Sekunde laden
3. **Buchungsvorgang:** Komplett in < 3 Sekunden

## 🎯 Erfolgsmetriken

Das System gilt als erfolgreich getestet, wenn:
- ✅ Alle Hauptfunktionen ohne Fehler ausführbar sind
- ✅ Keine unkontrollierten Exceptions auftreten
- ✅ Fehlerbehandlung benutzerfreundlich ist
- ✅ Datenbankintegrität erhalten bleibt
- ✅ GUI reagiert konsistent und intuitiv
