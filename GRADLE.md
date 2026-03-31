# Gradle Build System Documentation

## Overview

This project uses **Gradle** as its build automation tool. Gradle is a modern, flexible build system that provides better performance, cleaner syntax, and more powerful features compared to traditional XML-based build tools like Maven.

## Project Structure

```
CarRental/
├── build.gradle              # Main build configuration file
├── settings.gradle           # Project settings and name
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar        # Gradle wrapper executable
│       └── gradle-wrapper.properties # Wrapper configuration (Gradle 8.14.3)
├── gradlew                   # Unix/Linux/Mac wrapper script
├── gradlew.bat               # Windows wrapper script
└── src/
    ├── main/
    │   ├── java/             # Source code
    │   └── resources/        # Configuration files and resources
    └── test/                 # Test code (if present)
```

## How Gradle Works in This Project

### 1. Build Configuration (`build.gradle`)

The `build.gradle` file defines:

- **Plugins**: 
  - `java` - Java compilation and packaging
  - `application` - Enables running the application directly

- **Project Metadata**:
  - Group: `com.carrental`
  - Version: `1.0-SNAPSHOT`
  - Project Name: `car-rental-system`

- **Repositories**:
  - Maven Central Repository (default)
  - IBM Db2 Repository (for Db2 JDBC driver)

- **Dependencies**:
  - IBM Db2 JDBC Driver (`com.ibm.db2:jcc:11.5.9.0`)

- **Java Configuration**:
  - Source Compatibility: Java 21
  - Target Compatibility: Java 21
  - Encoding: UTF-8

- **Application Configuration**:
  - Main Class: `com.carrental.Main`

### 2. Dependency Management

Gradle manages external libraries automatically:

```groovy
dependencies {
    implementation 'com.ibm.db2:jcc:11.5.9.0'
}
```

**Dependency Configurations**:
- `implementation` - Required at compile time and runtime
- `compileOnly` - Required only at compile time
- `runtimeOnly` - Required only at runtime
- `testImplementation` - Required for testing

### 3. Build Lifecycle

Gradle executes builds in three phases:

1. **Initialization**: Determines which projects participate in the build
2. **Configuration**: Creates and configures tasks
3. **Execution**: Runs selected tasks in dependency order

### 4. Key Tasks

| Task | Description | Command |
|------|-------------|---------|
| `build` | Compiles code, runs tests, creates JAR | `./gradlew build` |
| `clean` | Deletes build directory | `./gradlew clean` |
| `run` | Executes the application | `./gradlew run` |
| `jar` | Creates JAR file | `./gradlew jar` |
| `compileJava` | Compiles Java source code | `./gradlew compileJava` |
| `tasks` | Lists all available tasks | `./gradlew tasks` |
| `dependencies` | Displays project dependencies | `./gradlew dependencies` |

### 5. Fat JAR Creation

The project creates a "fat JAR" (uber-JAR) that contains all dependencies:

```groovy
jar {
    manifest {
        attributes 'Main-Class': 'com.carrental.Main'
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
```

This ensures the JAR can run standalone without external classpath configuration.

## Common Commands

### Building the Project

```bash
# Windows
gradlew build

# Unix/Linux/Mac
./gradlew build
```

### Running the Application

```bash
# Windows
gradlew run

# Unix/Linux/Mac
./gradlew run
```

### Creating Executable JAR

```bash
# Windows
gradlew jar

# Output: build/libs/car-rental-system-1.0-SNAPSHOT.jar
```

### Running the JAR

```bash
java -jar build/libs/car-rental-system-1.0-SNAPSHOT.jar
```

### Cleaning Build Artifacts

```bash
gradlew clean
```

### Viewing All Tasks

```bash
gradlew tasks
```

### Viewing Dependencies

```bash
gradlew dependencies
```

## Gradle Wrapper

The Gradle Wrapper ensures consistent Gradle version across all environments:

- **No installation required**: Gradle is automatically downloaded
- **Version consistency**: All developers use the same Gradle version (8.5)
- **CI/CD ready**: Works in automated build environments

**Wrapper Files**:
- `gradlew` - Unix/Linux/Mac executable
- `gradlew.bat` - Windows executable
- `gradle/wrapper/gradle-wrapper.jar` - Wrapper implementation
- `gradle/wrapper/gradle-wrapper.properties` - Configuration

## Gradle vs Maven Comparison

| Feature | Gradle | Maven |
|---------|--------|-------|
| Configuration Language | Groovy/Kotlin DSL | XML |
| Build Speed | Faster (incremental builds, daemon) | Slower |
| Flexibility | Highly flexible, scriptable | Convention-based, rigid |
| Dependency Management | Dynamic versions, conflict resolution | Static dependency tree |
| Learning Curve | Moderate | Lower |
| IDE Support | Excellent | Excellent |

### Migration Benefits

1. **Performance**: Gradle daemon keeps build state in memory for faster subsequent builds
2. **Incremental Builds**: Only rebuilds changed files
3. **Cleaner Syntax**: Groovy DSL is more readable than XML
4. **Flexibility**: Easy to add custom build logic
5. **Application Plugin**: Direct execution with `gradle run`

## Project-Specific Configuration

### Database Connection

The application uses IBM Db2 database. The JDBC driver is managed by Gradle:

- **Repository**: IBM Public Maven Repository
- **Driver**: `com.ibm.db2:jcc:11.5.9.0`

Configuration files are located in:
- `src/main/resources/config.properties` (Main configuration)
- `config/config.properties` (Alternative configuration)

### Java 17 Features

The project is configured for Java 17, enabling:
- Records
- Sealed classes
- Pattern matching
- Text blocks
- Enhanced switch expressions

## Troubleshooting

### Issue: "gradlew: command not found"

**Solution**: Make the wrapper executable (Unix/Linux/Mac):
```bash
chmod +x gradlew
```

### Issue: "Could not resolve dependencies"

**Solution**: 
1. Check internet connection
2. Clear Gradle cache: `gradlew cleanBuildCache`
3. Refresh dependencies: `gradlew build --refresh-dependencies`

### Issue: "Java version mismatch"

**Solution**: Ensure Java 17 is installed:
```bash
java -version
```

### Issue: "Build failed with exception"

**Solution**: Run with stacktrace for detailed error:
```bash
gradlew build --stacktrace
```

## Best Practices

1. **Use the Wrapper**: Always use `gradlew`/`gradlew.bat` instead of system Gradle
2. **Clean Periodically**: Run `gradlew clean` before major releases
3. **Check Dependencies**: Regularly run `gradlew dependencies` to review dependency tree
4. **Use Daemon**: Gradle daemon is enabled by default for faster builds
5. **Commit Wrapper Files**: Always commit wrapper files to version control

## Additional Resources

- [Gradle Official Documentation](https://docs.gradle.org/)
- [Gradle User Guide](https://docs.gradle.org/current/userguide/userguide.html)
- [Gradle Java Plugin](https://docs.gradle.org/current/userguide/java_plugin.html)
- [Gradle Application Plugin](https://docs.gradle.org/current/userguide/application_plugin.html)

## Author

Car Rental System Project - HWR Berlin
