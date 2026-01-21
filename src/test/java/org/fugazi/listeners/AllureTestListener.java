package org.fugazi.listeners;

import java.util.Optional;

import io.qameta.allure.Attachment;

import org.fugazi.utils.ScreenshotUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit 5 TestWatcher implementation for Allure reporting.
 */
public class AllureTestListener implements TestWatcher {

    private static final Logger log = LoggerFactory.getLogger(AllureTestListener.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Set the WebDriver instance for the current thread.
     * Must be called in test setup to enable screenshot capture on failure.
     *
     * @param driver the WebDriver instance
     */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    /**
     * Get the WebDriver instance for the current thread.
     *
     * @return the WebDriver instance or null
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Clear the WebDriver instance for the current thread.
     * Should be called in test teardown.
     */
    public static void clearDriver() {
        driverThreadLocal.remove();
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("✅ Test PASSED: {}", getTestName(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        var testName = getTestName(context);
        log.error("❌ Test FAILED: {} - Reason: {}", testName, cause.getMessage());

        // Capture screenshot on failure
        var driver = driverThreadLocal.get();
        if (driver != null) {
            saveScreenshotOnFailure(driver, testName);
            savePageSource(driver, testName);
        } else {
            log.warn("Cannot capture screenshot: WebDriver is not available");
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log.warn("⚠️ Test ABORTED: {} - Reason: {}", getTestName(context), cause.getMessage());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log.info("⏭️ Test DISABLED: {} - Reason: {}", getTestName(context), reason.orElse("No reason provided"));
    }

    /**
     * Capture and attach screenshot to Allure report.
     *
     * @param driver   the WebDriver instance
     * @param testName the name of the test
     */
    @Attachment(value = "Screenshot on Failure: {testName}", type = "image/png")
    public void saveScreenshotOnFailure(WebDriver driver, String testName) {
        log.debug("Capturing screenshot for failed test: {}", testName);

        // Also save to file for local debugging
        ScreenshotUtils.takeScreenshotToFile(driver, testName);

    }

    /**
     * Capture and attach page source to Allure report.
     *
     * @param driver   the WebDriver instance
     * @param testName the name of the test
     */
    @Attachment(value = "Page Source: {testName}", type = "text/html")
    public void savePageSource(WebDriver driver, String testName) {
        log.debug("Capturing page source for: {}", testName);
        try {
            driver.getPageSource();
        } catch (Exception e) {
            log.error("Failed to capture page source: {}", e.getMessage());
        }
    }

    /**
     * Get the full test name from context.
     *
     * @param context the ExtensionContext
     * @return formatted test name
     */
    private String getTestName(ExtensionContext context) {
        var className = context.getTestClass()
                .map(Class::getSimpleName)
                .orElse("UnknownClass");
        var methodName = context.getTestMethod()
                .map(java.lang.reflect.Method::getName)
                .orElse("unknownMethod");
        return className + "." + methodName;
    }
}

