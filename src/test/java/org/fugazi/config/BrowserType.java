package org.fugazi.config;

import lombok.Getter;

/**
 * Enum representing supported browser types for WebDriver.
 * Currently, EdgeDriver is the default, with support for Chrome and Firefox.
 */
@Getter public enum BrowserType {
    EDGE("edge"),
    CHROME("chrome"),
    FIREFOX("firefox");

    private final String browserName;

    BrowserType(String browserName) {
        this.browserName = browserName;
    }

    /**
     * Get BrowserType from string value (case-insensitive).
     *
     * @param browser the browser name as string
     * @return the corresponding BrowserType, defaults to EDGE if not found
     */
    public static BrowserType fromString(String browser) {
        if (browser == null || browser.isBlank()) {
            return EDGE;
        }

        return switch (browser.toLowerCase().trim()) {
            case "chrome" -> CHROME;
            case "firefox" -> FIREFOX;
            default -> EDGE;
        };
    }
}

