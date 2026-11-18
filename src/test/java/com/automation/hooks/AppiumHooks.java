package com.automation.hooks;

import com.automation.core.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppiumHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppiumHooks.class);

    @Before("@requiresRealApp")
    public void startAppiumSession() {
        DriverManager.createDriver();
    }

    @After(value = "@requiresRealApp", order = 100)
    public void captureFailureScreenshot(Scenario scenario) {
        if (scenario.isFailed() && DriverManager.hasDriver()) {
            try {
                byte[] screenshot = DriverManager.getDriver().getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failure screenshot");
            } catch (RuntimeException exception) {
                LOGGER.warn("Could not capture the failure screenshot", exception);
            }
        }
    }

    @After(value = "@requiresRealApp", order = 0)
    public void stopAppiumSession() {
        DriverManager.quitDriver();
    }
}
