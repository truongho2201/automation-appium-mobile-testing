package com.automation.core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DriverManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void createDriver() {
        if (DRIVER.get() != null) {
            throw new IllegalStateException("An Appium driver already exists on this test thread.");
        }

        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(ConfigReader.getRequired("platform.name"))
                .setAutomationName(ConfigReader.getRequired("automation.name"))
                .setDeviceName(ConfigReader.getRequired("device.name"))
                .setApp(ConfigReader.requireAppPath().toString())
                .setAutoGrantPermissions(Boolean.parseBoolean(ConfigReader.get("auto.grant.permissions")));

        String udid = ConfigReader.get("device.udid");
        if (!udid.isBlank()) {
            options.setUdid(udid);
        }

        String appPackage = ConfigReader.get("app.package");
        if (!appPackage.isBlank()) {
            options.setAppPackage(appPackage);
        }

        String appActivity = ConfigReader.get("app.activity");
        if (!appActivity.isBlank()) {
            options.setAppActivity(appActivity);
        }

        try {
            URL serverUrl = URI.create(ConfigReader.getRequired("appium.server.url")).toURL();
            LOGGER.info("Creating Appium session at {}", serverUrl);
            DRIVER.set(new AndroidDriver(serverUrl, options));
        } catch (IllegalArgumentException | MalformedURLException exception) {
            throw new IllegalStateException(
                    "Invalid appium.server.url: " + ConfigReader.get("appium.server.url"), exception);
        } catch (RuntimeException exception) {
            DRIVER.remove();
            throw exception;
        }
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "No Appium driver exists for this scenario. Check that the @Before hook completed.");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        try {
            if (driver != null) {
                LOGGER.info("Closing Appium session");
                driver.quit();
            }
        } catch (RuntimeException exception) {
            LOGGER.warn("Appium session cleanup reported an error", exception);
        } finally {
            DRIVER.remove();
        }
    }
}
