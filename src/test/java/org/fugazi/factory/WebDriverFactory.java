package org.fugazi.factory;

import org.fugazi.config.BrowserType;
import org.fugazi.config.ConfigurationManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Factory class for creating WebDriver instances.
 * Supports Edge (default), Chrome, and Firefox browsers with configurable options.
 */
public class WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final String WINDOW_SIZE = "--window-size=1920,1080";

    private WebDriverFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Create a WebDriver instance using the configured browser type.
     *
     * @return configured WebDriver instance
     */
    public static WebDriver createDriver() {
        var config = ConfigurationManager.getInstance();
        return createDriver(config.getBrowserType());
    }

    /**
     * Create a WebDriver instance for the specified browser type.
     *
     * @param browserType the type of browser to create
     * @return configured WebDriver instance
     */
    public static WebDriver createDriver(BrowserType browserType) {
        var config = ConfigurationManager.getInstance();
        var headless = config.isHeadless();

        log.info("Creating {} driver (headless: {})", browserType, headless);

        var driver = switch (browserType) {
            case CHROME -> createChromeDriver(headless);
            case FIREFOX -> createFirefoxDriver(headless);
            case EDGE -> createEdgeDriver(headless);
        };

        configureDriver(driver, config);
        return driver;
    }

    /**
     * Create EdgeDriver with configured options.
     *
     * @param headless whether to run in headless mode
     * @return configured EdgeDriver instance
     */
    private static WebDriver createEdgeDriver(boolean headless) {
        var options = new EdgeOptions();

        options.addArguments(WINDOW_SIZE);
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-allow-origins=*");

        if (headless) {
            options.addArguments("--headless=new");
            log.debug("Edge running in headless mode");
        }

        return new EdgeDriver(options);
    }

    /**
     * Create ChromeDriver with configured options.
     *
     * @param headless whether to run in headless mode
     * @return configured ChromeDriver instance
     */
    private static WebDriver createChromeDriver(boolean headless) {
        var options = new ChromeOptions();

        options.addArguments(WINDOW_SIZE);
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-infobars");
        options.addArguments("--remote-allow-origins=*");

        if (headless) {
            options.addArguments("--headless=new");
            log.debug("Chrome running in headless mode");
        }

        return new ChromeDriver(options);
    }

    /**
     * Create FirefoxDriver with configured options.
     *
     * @param headless whether to run in headless mode
     * @return configured FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver(boolean headless) {
        var options = new FirefoxOptions();

        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        if (headless) {
            options.addArguments("--headless");
            log.debug("Firefox running in headless mode");
        }

        return new FirefoxDriver(options);
    }

    /**
     * Configure common WebDriver settings.
     *
     * @param driver the WebDriver instance to configure
     * @param config the configuration manager
     */
    private static void configureDriver(WebDriver driver, ConfigurationManager config) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getTimeout()));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getTimeout()));
        driver.manage().window().maximize();

        log.debug("Driver configured with implicit wait: {}s, page load timeout: {}s",
                config.getImplicitWait(), config.getTimeout());
    }
}

