package org.fugazi.tests;

import io.qameta.allure.Step;

import org.assertj.core.api.SoftAssertions;
import org.fugazi.config.ConfigurationManager;
import org.fugazi.factory.WebDriverFactory;
import org.fugazi.listeners.AllureTestListener;
import org.fugazi.pages.CartPage;
import org.fugazi.pages.HomePage;
import org.fugazi.pages.LoginPage;
import org.fugazi.pages.ProductDetailPage;
import org.fugazi.pages.ProductsPage;
import org.fugazi.pages.SearchResultsPage;
import org.fugazi.pages.components.FooterComponent;
import org.fugazi.pages.components.HeaderComponent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base test class providing common setup and teardown functionality.
 * All test classes should extend this class.
 */
@ExtendWith(AllureTestListener.class)
public abstract class BaseTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected WebDriver driver;
    protected ConfigurationManager config;

    // Page Objects - initialized lazily
    private HomePage homePage;
    private ProductDetailPage productDetailPage;
    private CartPage cartPage;
    private SearchResultsPage searchResultsPage;
    private LoginPage loginPage;
    private ProductsPage productsPage;

    // Components - initialized lazily
    private FooterComponent footerComponent;
    private HeaderComponent headerComponent;

    @BeforeEach
    @Step("Initialize WebDriver and navigate to base URL")
    void setUp() {
        log.info("=== Setting up test ===");
        config = ConfigurationManager.getInstance();

        // Create WebDriver instance
        driver = WebDriverFactory.createDriver();
        AllureTestListener.setDriver(driver);

        // Navigate to base URL
        navigateToBaseUrl();

        log.info("Test setup completed - Browser: {}, URL: {}",
                config.getBrowserType(), config.getBaseUrl());
    }

    @AfterEach
    @Step("Clean up WebDriver")
    void tearDown() {
        log.info("=== Tearing down test ===");

        if (driver != null) {
            try {
                driver.quit();
                log.debug("WebDriver closed successfully");
            } catch (Exception e) {
                log.error("Error closing WebDriver: {}", e.getMessage());
            } finally {
                AllureTestListener.clearDriver();
            }
        }

        // Reset page objects
        homePage = null;
        productDetailPage = null;
        cartPage = null;
        searchResultsPage = null;
        loginPage = null;
        productsPage = null;

        log.info("Test teardown completed");
    }

    /**
     * Navigate to the base URL with retry logic.
     * Uses WebDriverWait instead of Thread.sleep for better reliability.
     */
    @Step("Navigate to base URL")
    protected void navigateToBaseUrl() {
        log.debug("Navigating to base URL: {}", config.getBaseUrl());
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                driver.get(config.getBaseUrl());

                // Wait for page load instead of Thread.sleep
                var pageLoadWait = new WebDriverWait(
                        driver, java.time.Duration.ofSeconds(10)
                );
                pageLoadWait.until(d -> {
                    var js = (JavascriptExecutor) d;
                    return "complete".equals(js.executeScript("return document.readyState"));
                });

                log.info("Successfully navigated to base URL");
                return;

            } catch (Exception e) {
                log.warn("Navigation attempt {} of {} failed: {}",
                        attempt, maxAttempts, e.getMessage());

                if (attempt == maxAttempts) {
                    log.error("Failed to navigate after {} attempts", maxAttempts);
                    throw new RuntimeException(
                            "Unable to navigate to base URL after " + maxAttempts + " attempts", e
                    );
                }

                // Wait with exponential backoff using WebDriverWait
                var backoffSeconds = attempt * 2; // 2s, 4s
                try {
                    var backoffWait = new WebDriverWait(
                            driver, java.time.Duration.ofSeconds(backoffSeconds)
                    );
                    backoffWait.until(d -> false); // Just wait
                } catch (TimeoutException te) {
                    // Expected - continue to next attempt
                    log.debug("Backoff wait completed, retrying...");
                }
            }
        }
    }

    /**
     * Navigate to a specific path relative to base URL.
     *
     * @param path the path to navigate to
     */
    @Step("Navigate to path: {path}")
    protected void navigateTo(String path) {
        var url = config.getBaseUrl() + path;
        log.debug("Navigating to: {}", url);
        driver.get(url);
    }

    // ==================== SoftAssertions Helper ====================

    /**
     * Get a SoftAssertions instance for assertions.
     * Provides centralized access to AssertJ soft assertions.
     *
     * @return SoftAssertions instance
     */
    protected SoftAssertions softly() {
        return new SoftAssertions();
    }

    // ==================== Page Object Getters (Lazy Initialization) ====================

    /**
     * Get the HomePage instance.
     *
     * @return HomePage object
     */
    protected HomePage homePage() {
        if (homePage == null) {
            homePage = new HomePage(driver);
        }
        return homePage;
    }

    /**
     * Get the ProductDetailPage instance.
     *
     * @return ProductDetailPage object
     */
    protected ProductDetailPage productDetailPage() {
        if (productDetailPage == null) {
            productDetailPage = new ProductDetailPage(driver);
        }
        return productDetailPage;
    }

    /**
     * Get the CartPage instance.
     *
     * @return CartPage object
     */
    protected CartPage cartPage() {
        if (cartPage == null) {
            cartPage = new CartPage(driver);
        }
        return cartPage;
    }

    /**
     * Get the SearchResultsPage instance.
     *
     * @return SearchResultsPage object
     */
    protected SearchResultsPage searchResultsPage() {
        if (searchResultsPage == null) {
            searchResultsPage = new SearchResultsPage(driver);
        }
        return searchResultsPage;
    }

    /**
     * Get the LoginPage instance.
     *
     * @return LoginPage object
     */
    protected LoginPage loginPage() {
        if (loginPage == null) {
            loginPage = new LoginPage(driver);
        }
        return loginPage;
    }

    /**
     * Get the ProductsPage instance.
     *
     * @return ProductsPage object
     */
    protected ProductsPage productsPage() {
        if (productsPage == null) {
            productsPage = new ProductsPage(driver);
        }
        return productsPage;
    }

    // ==================== Component Getters (Lazy Initialization) ====================

    /**
     * Get the FooterComponent instance.
     *
     * @return FooterComponent object
     */
    protected FooterComponent getFooterComponent() {
        if (footerComponent == null) {
            footerComponent = new FooterComponent(driver);
        }
        return footerComponent;
    }

    /**
     * Get the HeaderComponent instance.
     *
     * @return HeaderComponent object
     */
    protected HeaderComponent getHeaderComponent() {
        if (headerComponent == null) {
            headerComponent = new HeaderComponent(driver);
        }
        return headerComponent;
    }

    // ==================== Utility Methods ====================

    /**
     * Get the current page URL.
     *
     * @return current URL
     */
    protected String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get the current page title.
     *
     * @return page title
     */
    protected String getPageTitle() {
        return driver.getTitle();
    }

    /**
     * Refresh the current page.
     */
    @Step("Refresh page")
    protected void refreshPage() {
        log.debug("Refreshing page");
        driver.navigate().refresh();
    }

    /**
     * Navigate back in browser history.
     */
    @Step("Navigate back")
    protected void navigateBack() {
        log.debug("Navigating back");
        driver.navigate().back();
    }

}

