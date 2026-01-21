package org.fugazi.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for capturing and managing screenshots.
 */
public class ScreenshotUtils {

    private static final Logger log = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "target/screenshots";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    private ScreenshotUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Take a screenshot and return as byte array.
     *
     * @param driver the WebDriver instance
     * @return screenshot as byte array
     */
    public static byte[] takeScreenshot(WebDriver driver) {
        if (driver == null) {
            return new byte[0];
        }

        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            log.error("Failed to take screenshot: {}", e.getMessage());
            return new byte[0];
        }
    }

    /**
     * Take a screenshot and save to file.
     *
     * @param driver   the WebDriver instance
     * @param testName the name of the test (used in filename)
     */
    public static void takeScreenshotToFile(WebDriver driver, String testName) {
        if (driver == null) {
            log.warn("Cannot take screenshot: driver is null");
            return;
        }

        try {
            // Create screenshots directory if it doesn't exist
            var screenshotDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
            }

            // Generate filename with timestamp
            var timestamp = LocalDateTime.now().format(FORMATTER);
            var sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9-_]", "_");
            var filename = String.format("%s_%s.png", sanitizedTestName, timestamp);

            // Take and save screenshot
            var screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            var destination = screenshotDir.resolve(filename);
            Files.copy(screenshotFile.toPath(), destination);

            log.info("Screenshot saved: {}", destination);

        } catch (IOException e) {
            log.error("Failed to save screenshot: {}", e.getMessage());
        }
    }

}

