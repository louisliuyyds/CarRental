package com.carrental;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 构建验证测试 - 确保项目基本结构正确
 * 用于Jenkins CI/CD演示
 */
public class BuildVerificationTest {

    @Test
    @DisplayName("验证项目结构 - 主类存在")
    public void testMainClassExists() {
        // 验证Main类可以被加载
        assertDoesNotThrow(() -> {
            Class.forName("com.carrental.Main");
        }, "Main类应该存在");
    }

    @Test
    @DisplayName("验证项目结构 - Controller类存在")
    public void testControllerClassesExist() {
        assertDoesNotThrow(() -> {
            Class.forName("com.carrental.controller.CarRentalSystem");
            Class.forName("com.carrental.controller.AuthController");
        }, "Controller类应该存在");
    }

    @Test
    @DisplayName("验证项目结构 - DAO类存在")
    public void testDaoClassesExist() {
        assertDoesNotThrow(() -> {
            Class.forName("com.carrental.dao.FahrzeugDao");
            Class.forName("com.carrental.dao.KundeDao");
            Class.forName("com.carrental.dao.GenericDao");
        }, "DAO类应该存在");
    }

    @Test
    @DisplayName("验证项目结构 - 模型类存在")
    public void testModelClassesExist() {
        assertDoesNotThrow(() -> {
            Class.forName("com.carrental.model.Fahrzeug");
            Class.forName("com.carrental.model.Kunde");
            Class.forName("com.carrental.model.Mietvertrag");
            Class.forName("com.carrental.model.Benutzer");
        }, "模型类应该存在");
    }

    @Test
    @DisplayName("验证Java版本 - 需要17或更高")
    public void testJavaVersion() {
        String version = System.getProperty("java.version");
        int majorVersion = Integer.parseInt(version.split("\\.")[0]);
        assertTrue(majorVersion >= 17,
            "Java版本应该是17或更高，当前是: " + version);
    }

    @Test
    @DisplayName("验证系统属性")
    public void testSystemProperties() {
        assertNotNull(System.getProperty("user.dir"), "user.dir应该存在");
        assertNotNull(System.getProperty("os.name"), "os.name应该存在");
        assertNotNull(System.getProperty("user.home"), "user.home应该存在");
    }

    @Test
    @DisplayName("验证文件分隔符")
    public void testFileSeparator() {
        String separator = System.getProperty("file.separator");
        assertTrue(separator.equals("/") || separator.equals("\\"),
            "文件分隔符应该是/或\\");
    }

    @Test
    @DisplayName("验证项目名称")
    public void testProjectName() {
        String userDir = System.getProperty("user.dir");
        assertTrue(userDir.contains("CarRental"),
            "项目目录应包含CarRental");
    }

    @Test
    @DisplayName("验证内存可用")
    public void testMemoryAvailable() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        assertTrue(maxMemory > 0, "最大内存应该大于0");

        long freeMemory = runtime.freeMemory();
        assertTrue(freeMemory > 0, "可用内存应该大于0");
    }

    @Test
    @DisplayName("验证处理器数量")
    public void testProcessorsAvailable() {
        int processors = Runtime.getRuntime().availableProcessors();
        assertTrue(processors >= 1, "至少应该有1个处理器");
    }
}
