package org.fugazi.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton class for managing application configuration.
 * Loads properties from config.properties file and provides access to configuration values.
 */
public class ConfigurationManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationManager.class);
    private static final String CONFIG_FILE = "config.properties";
    private static volatile ConfigurationManager instance;

    private final String baseUrl;
    private final BrowserType browserType;
    private final int timeout;
    private final boolean headless;
    private final int implicitWait;
    private final int explicitWait;

    private ConfigurationManager() {
        var properties = loadProperties();

        this.baseUrl = getProperty(properties, "base.url", "https://music-tech-shop.vercel.app");
        this.browserType = BrowserType.fromString(getProperty(properties, "browser", "edge"));
        this.timeout = Integer.parseInt(getProperty(properties, "timeout.seconds", "30"));
        this.headless = Boolean.parseBoolean(getProperty(properties, "headless", "false"));
        this.implicitWait = Integer.parseInt(getProperty(properties, "implicit.wait.seconds", "5"));
        this.explicitWait = Integer.parseInt(getProperty(properties, "explicit.wait.seconds", "10"));

        log.info("Configuration loaded - URL: {}, Browser: {}, Headless: {}", baseUrl, browserType, headless);
    }

    /**
     * Get the singleton instance of ConfigurationManager.
     * Thread-safe implementation using double-checked locking.
     *
     * @return the singleton instance
     */
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            synchronized (ConfigurationManager.class) {
                if (instance == null) {
                    instance = new ConfigurationManager();
                }
            }
        }
        return instance;
    }

    /**
     * Load properties from the config file.
     *
     * @return loaded Properties object
     */
    private Properties loadProperties() {
        var properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                log.warn("Unable to find {}. Using default values.", CONFIG_FILE);
                return properties;
            }
            properties.load(input);
            log.debug("Configuration file {} loaded successfully", CONFIG_FILE);
        } catch (IOException e) {
            log.error("Error loading configuration file: {}", e.getMessage());
        }

        return properties;
    }

    /**
     * Get property value with system property override support.
     * Priority: System Property > Config File > Default Value
     *
     * @param properties   the properties object
     * @param key          the property key
     * @param defaultValue the default value if not found
     * @return the property value
     */
    private String getProperty(Properties properties, String key, String defaultValue) {
        // First check system properties (allows command-line overrides)
        var systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            log.debug("Using system property for {}: {}", key, systemValue);
            return systemValue;
        }

        // Then check config file
        var configValue = properties.getProperty(key);
        if (configValue != null && !configValue.isBlank()) {
            return configValue;
        }

        // Finally use default
        log.debug("Using default value for {}: {}", key, defaultValue);
        return defaultValue;
    }

    /**
     * Check if running in headless mode.
     * Checks both config and system property.
     *
     * @return true if headless mode is enabled
     */
    public boolean isHeadless() {
        var systemHeadless = System.getProperty("headless");
        if (systemHeadless != null) {
            return Boolean.parseBoolean(systemHeadless);
        }
        return headless;
    }

    /**
     * Get the browser type from config or system property.
     *
     * @return the BrowserType to use
     */
    public BrowserType getBrowserType() {
        var systemBrowser = System.getProperty("browser");
        if (systemBrowser != null && !systemBrowser.isBlank()) {
            return BrowserType.fromString(systemBrowser);
        }
        return browserType;
    }

    /**
     * Get the base URL from config or system property.
     *
     * @return the base URL
     */
    public String getBaseUrl() {
        var systemBaseUrl = System.getProperty("base.url");
        if (systemBaseUrl != null && !systemBaseUrl.isBlank()) {
            return systemBaseUrl;
        }
        return baseUrl;
    }

    /**
     * Get the timeout in seconds from config or system property.
     *
     * @return the timeout in seconds
     */
    public int getTimeout() {
        var systemTimeout = System.getProperty("timeout.seconds");
        if (systemTimeout != null && !systemTimeout.isBlank()) {
            return Integer.parseInt(systemTimeout);
        }
        return timeout;
    }

    /**
     * Get the implicit wait in seconds from config or system property.
     *
     * @return the implicit wait in seconds
     */
    public int getImplicitWait() {
        var systemImplicitWait = System.getProperty("implicit.wait.seconds");
        if (systemImplicitWait != null && !systemImplicitWait.isBlank()) {
            return Integer.parseInt(systemImplicitWait);
        }
        return implicitWait;
    }

    /**
     * Get the explicit wait in seconds from config or system property.
     *
     * @return the explicit wait in seconds
     */
    public int getExplicitWait() {
        var systemExplicitWait = System.getProperty("explicit.wait.seconds");
        if (systemExplicitWait != null && !systemExplicitWait.isBlank()) {
            return Integer.parseInt(systemExplicitWait);
        }
        return explicitWait;
    }
}

