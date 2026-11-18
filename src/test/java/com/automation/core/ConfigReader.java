package com.automation.core;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Properties;

public final class ConfigReader {
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigReader() {
    }

    public static String getRequired(String key) {
        String value = get(key);
        if (value.isBlank()) {
            throw new IllegalStateException("Required configuration value is blank: " + key);
        }
        return value;
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null) {
            return systemValue.trim();
        }
        return PROPERTIES.getProperty(key, "").trim();
    }

    public static Duration getExplicitWait() {
        String rawValue = getRequired("explicit.wait.seconds");
        try {
            long seconds = Long.parseLong(rawValue);
            if (seconds <= 0) {
                throw new NumberFormatException("value must be positive");
            }
            return Duration.ofSeconds(seconds);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "explicit.wait.seconds must be a positive whole number, but was: " + rawValue,
                    exception);
        }
    }

    public static ZoneId getTestZoneId() {
        String rawValue = get("test.timezone");
        if (rawValue.isBlank()) {
            return ZoneId.systemDefault();
        }
        try {
            return ZoneId.of(rawValue);
        } catch (java.time.DateTimeException exception) {
            throw new IllegalStateException(
                    "test.timezone must be a valid IANA timezone, but was: " + rawValue,
                    exception);
        }
    }

    public static Clock getTestClock() {
        return Clock.system(getTestZoneId());
    }

    public static LocalDate today() {
        return LocalDate.now(getTestClock());
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(getTestClock());
    }

    public static Path getAppPath() {
        Path configuredPath = Paths.get(getRequired("app.path"));
        Path resolvedPath = configuredPath.isAbsolute()
                ? configuredPath
                : Paths.get(System.getProperty("user.dir")).resolve(configuredPath);
        return resolvedPath.normalize().toAbsolutePath();
    }

    public static Path requireAppPath() {
        Path appPath = getAppPath();
        if (!Files.isRegularFile(appPath)) {
            throw new IllegalStateException(
                    "APK file was not found: " + appPath
                            + System.lineSeparator()
                            + "Copy the APK there or update app.path in src/test/resources/config.properties.");
        }
        if (!appPath.getFileName().toString().toLowerCase().endsWith(".apk")) {
            throw new IllegalStateException("Configured app.path is not an APK file: " + appPath);
        }
        return appPath;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream input = classLoader.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new IllegalStateException("Could not find " + CONFIG_FILE + " on the test classpath.");
            }
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read " + CONFIG_FILE + ".", exception);
        }
    }
}
