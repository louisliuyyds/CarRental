package com.carrental.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 工具类测试 - 字符串和通用工具函数
 * 用于Jenkins CI/CD演示
 */
public class StringUtilTest {

    @Test
    @DisplayName("测试空字符串检查 - null输入")
    public void testIsEmptyNull() {
        assertTrue(isEmpty(null), "null应该是空");
    }

    @Test
    @DisplayName("测试空字符串检查 - 空字符串")
    public void testIsEmptyEmptyString() {
        assertTrue(isEmpty(""), "空字符串应该是空");
    }

    @Test
    @DisplayName("测试空字符串检查 - 空白字符串")
    public void testIsEmptyWhitespace() {
        assertTrue(isEmpty("   "), "空白字符串应该是空");
    }

    @Test
    @DisplayName("测试空字符串检查 - 非空字符串")
    public void testIsEmptyNonEmpty() {
        assertFalse(isEmpty("test"), "非空字符串不应该为空");
    }

    @Test
    @DisplayName("测试空字符串检查 - 带空格的非空字符串")
    public void testIsEmptyWithSpaces() {
        assertFalse(isEmpty(" test "), "带空格的字符串不应该为空");
    }

    @Test
    @DisplayName("测试字符串截断 - 无需截断")
    public void testTruncateNoNeed() {
        assertEquals("test", truncate("test", 10), "短字符串不应截断");
    }

    @Test
    @DisplayName("测试字符串截断 - 需要截断")
    public void testTruncateNeeded() {
        String result = truncate("testlongstring", 5);
        assertTrue(result.endsWith("..."), "截断后应以...结尾");
        assertEquals(5, result.length(), "截断后长度应为指定长度");
    }

    @Test
    @DisplayName("测试字符串截断 - 空输入")
    public void testTruncateEmpty() {
        assertEquals("", truncate("", 5), "空字符串应返回空");
    }

    @Test
    @DisplayName("测试字符串截断 - null输入")
    public void testTruncateNull() {
        assertEquals("", truncate(null, 5), "null应返回空字符串");
    }

    @Test
    @DisplayName("测试字符串长度验证")
    public void testStringLength() {
        String test = "CarRental";
        assertEquals(9, test.length(), "CarRental长度应为9");
    }

    @Test
    @DisplayName("测试字符串大写转换")
    public void testToUpperCase() {
        assertEquals("CARRENTAL", "CarRental".toUpperCase());
    }

    @Test
    @DisplayName("测试字符串小写转换")
    public void testToLowerCase() {
        assertEquals("carrental", "CarRental".toLowerCase());
    }

    @Test
    @DisplayName("测试字符串包含")
    public void testContains() {
        String full = "CarRental System";
        assertTrue(full.contains("Rental"));
        assertFalse(full.contains("Java"));
    }

    // ===== 辅助方法=====

    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String truncate(String str, int maxLength) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}
