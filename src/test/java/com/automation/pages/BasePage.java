package com.automation.pages;

import com.automation.core.ConfigReader;
import io.appium.java_client.android.AndroidDriver;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final AndroidDriver driver;
    private final WebDriverWait wait;

    protected BasePage(AndroidDriver driver) {
        this.driver = driver;
        Duration timeout = ConfigReader.getExplicitWait();
        this.wait = new WebDriverWait(driver, timeout);
    }

    protected WebElement waitUntilVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitUntilClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void type(By locator, String value) {
        WebElement element = waitUntilVisible(locator);
        element.clear();
        element.sendKeys(value);
    }

    protected void tap(By locator) {
        waitUntilClickable(locator).click();
    }

    protected boolean isVisible(By locator) {
        return waitUntilVisible(locator).isDisplayed();
    }

    protected void waitUntilInvisible(By locator) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    protected boolean isAbsent(By locator) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }
}
