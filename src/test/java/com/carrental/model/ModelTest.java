package com.carrental.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 单元测试 - 测试模型类的基本功能
 * 用于Jenkins CI/CD演示
 */
public class ModelTest {

    // ===== Antriebsart枚举测试 =====

    @Test
    @DisplayName("测试Antriebsart枚举值存在")
    public void testAntriebsartValues() {
        Antriebsart[] drives = Antriebsart.values();
        assertTrue(drives.length > 0, "Antriebsart应该有值");
        assertEquals(2, drives.length, "Antriebsart应该有2个值");
    }

    @Test
    @DisplayName("测试Antriebsart枚举值内容")
    public void testAntriebsartContent() {
        assertNotNull(Antriebsart.valueOf("VERBRENNER"));
        assertNotNull(Antriebsart.valueOf("ELEKTRO"));
    }

    // ===== VertragsStatus枚举测试 =====

    @Test
    @DisplayName("测试VertragsStatus枚举值存在")
    public void testVertragsStatusValues() {
        VertragsStatus[] statuses = VertragsStatus.values();
        assertTrue(statuses.length > 0, "VertragsStatus应该有值");
        assertEquals(5, statuses.length, "VertragsStatus应该有5个值");
    }

    @Test
    @DisplayName("测试VertragsStatus枚举值内容")
    public void testVertragsStatusContent() {
        assertNotNull(VertragsStatus.valueOf("ANGELEGT"));
        assertNotNull(VertragsStatus.valueOf("BESTAETIGT"));
        assertNotNull(VertragsStatus.valueOf("LAUFEND"));
        assertNotNull(VertragsStatus.valueOf("ABGESCHLOSSEN"));
        assertNotNull(VertragsStatus.valueOf("STORNIERT"));
    }

    // ===== FahrzeugZustand枚举测试 =====

    @Test
    @DisplayName("测试FahrzeugZustand枚举值存在")
    public void testFahrzeugZustandValues() {
        FahrzeugZustand[] states = FahrzeugZustand.values();
        assertTrue(states.length > 0, "FahrzeugZustand应该有值");
        assertEquals(3, states.length, "FahrzeugZustand应该有3个值");
    }

    @Test
    @DisplayName("测试FahrzeugZustand枚举值内容")
    public void testFahrzeugZustandContent() {
        assertNotNull(FahrzeugZustand.valueOf("VERFUEGBAR"));
        assertNotNull(FahrzeugZustand.valueOf("VERMIETET"));
        assertNotNull(FahrzeugZustand.valueOf("WARTUNG"));
    }

    // ===== Fahrzeugtyp类测试（不是枚举）=====

    @Test
    @DisplayName("测试Fahrzeugtyp类实例化")
    public void testFahrzeugtypInstantiation() {
        Fahrzeugtyp typ = new Fahrzeugtyp();
        assertNotNull(typ, "Fahrzeugtyp应该可以实例化");
    }

    @Test
    @DisplayName("测试Fahrzeugtyp类构造函数")
    public void testFahrzeugtypConstructor() {
        Fahrzeugtyp typ = new Fahrzeugtyp("VW", "Golf", 50.0);
        assertEquals("VW", typ.getHersteller());
        assertEquals("Golf", typ.getModellBezeichnung());
        assertEquals(50.0, typ.getStandardTagesPreis());
    }

    @Test
    @DisplayName("测试Fahrzeugtyp类Setter和Getter")
    public void testFahrzeugtypSetterGetter() {
        Fahrzeugtyp typ = new Fahrzeugtyp();
        typ.setHersteller("Tesla");
        typ.setModellBezeichnung("Model 3");
        typ.setStandardTagesPreis(100.0);
        typ.setSitzplaetze(5);
        typ.setAntriebsart(Antriebsart.ELEKTRO);

        assertEquals("Tesla", typ.getHersteller());
        assertEquals("Model 3", typ.getModellBezeichnung());
        assertEquals(100.0, typ.getStandardTagesPreis());
        assertEquals(5, typ.getSitzplaetze());
        assertEquals(Antriebsart.ELEKTRO, typ.getAntriebsart());
    }
}
