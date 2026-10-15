package org.fugazi.listeners;

import java.util.Optional;

import io.qameta.allure.Attachment;

import org.fugazi.utils.ScreenshotUtils;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit 5 TestWatcher and AfterTestExecutionCallback implementation for Allure reporting.
 * Captures screenshots and page source in afterTestExecution (before @AfterEach quits WebDriver).
 */
public class AllureTestListener implements TestWatcher, AfterTestExecutionCallback {

    private static final Logger log = LoggerFactory.getLogger(AllureTestListener.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void clearDriver() {
        driverThreadLocal.remove();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            var testName = getTestName(context);
            var driver = driverThreadLocal.get();
            if (driver != null) {
                saveScreenshotOnFailure(driver, testName);
                savePageSource(driver, testName);
            } else {
                log.warn("Cannot capture screenshot: WebDriver is not available");
            }
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        log.info("✅ Test PASSED: {}", getTestName(context));
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        var testName = getTestName(context);
        log.error("❌ Test FAILED: {} - Reason: {}", testName, cause.getMessage());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        log.warn("⚠️ Test ABORTED: {} - Reason: {}", getTestName(context), cause.getMessage());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        log.info("⏭️ Test DISABLED: {} - Reason: {}", getTestName(context), reason.orElse("No reason provided"));
    }

    @Attachment(value = "Screenshot on Failure: {testName}", type = "image/png")
    public byte[] saveScreenshotOnFailure(WebDriver driver, String testName) {
        log.debug("Capturing screenshot for failed test: {}", testName);
        ScreenshotUtils.takeScreenshotToFile(driver, testName);
        return ScreenshotUtils.takeScreenshot(driver);
    }

    @Attachment(value = "Page Source: {testName}", type = "text/html")
    public String savePageSource(WebDriver driver, String testName) {
        log.debug("Capturing page source for: {}", testName);
        try {
            return driver.getPageSource();
        } catch (Exception e) {
            log.error("Failed to capture page source: {}", e.getMessage());
            return "";
        }
    }

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
