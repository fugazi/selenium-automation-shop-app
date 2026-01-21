package org.fugazi.pages;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.fugazi.config.ConfigurationManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base page class providing common functionality for all page objects.
 * Implements fluent interface pattern for method chaining.
 */
public abstract class BasePage {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    /**
     * Constructor initializing the driver and wait objects.
     *
     * @param driver the WebDriver instance
     */
    protected BasePage(WebDriver driver) {
        this.driver = driver;
        var config = ConfigurationManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
    }

    /**
     * Wait for an element to be visible on the page.
     *
     * @param locator the element locator
     * @return the visible WebElement
     */
    protected WebElement waitForVisibility(By locator) {
        log.debug("Waiting for visibility of element: {}", locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for an element to be clickable.
     *
     * @param locator the element locator
     * @return the clickable WebElement
     */
    protected WebElement waitForClickable(By locator) {
        log.debug("Waiting for element to be clickable: {}", locator);
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for an element to be present in the DOM.
     *
     * @param locator the element locator
     * @return the present WebElement
     */
    protected WebElement waitForPresence(By locator) {
        log.debug("Waiting for presence of element: {}", locator);
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for the page to fully load.
     */
    protected void waitForPageLoad() {
        log.debug("Waiting for page to load completely");
        wait.until(driver -> {
            var js = (JavascriptExecutor) driver;
            return Objects.requireNonNull(js.executeScript("return document.readyState")).equals("complete");
        });
    }

    /**
     * Wait for the URL to change from the current URL.
     *
     * @param currentUrl the current URL before the action
     */
    protected void waitForUrlChange(String currentUrl) {
        log.debug("Waiting for URL to change from: {}", currentUrl);
        wait.until(ExpectedConditions.not(ExpectedConditions.urlToBe(currentUrl)));
    }

    /**
     * Wait for animations to complete by checking for stable DOM.
     * Uses JavaScript to detect when no animations are running.
     */
    protected void waitForAnimationsToComplete() {
        log.debug("Waiting for animations to complete");
        wait.until(driver -> {
            var js = (JavascriptExecutor) driver;
            // Check if there are any running CSS animations or transitions
            var noAnimations = (Boolean) js.executeScript(
                    "return document.getAnimations().length === 0;"
            );
            return Boolean.TRUE.equals(noAnimations);
        });
    }

    /**
     * Wait for at least one element matching the locator to be present.
     * Useful for waiting for dynamic content to load.
     *
     * @param locator  the element locator
     * @param minCount minimum number of elements expected
     */
    protected void waitForMinimumElements(By locator, int minCount) {
        log.debug("Waiting for at least {} elements: {}", minCount, locator);
        try {
            wait.until(driver -> driver.findElements(locator).size() >= minCount);
        } catch (TimeoutException e) {
            log.debug("Timeout waiting for {} elements: {}", minCount, locator);
        }
    }

    /**
     * Click on an element with wait for clickable.
     *
     * @param locator element locator
     */
    protected void click(By locator) {
        log.debug("Clicking element: {}", locator);
        try {
            waitForClickable(locator).click();
        } catch (StaleElementReferenceException e) {
            log.debug("Element was stale, retrying click: {}", e.getMessage());
            waitForClickable(locator).click();
        } catch (TimeoutException e) {
            log.debug("Timeout waiting for element to be clickable: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Type text into an element (clears existing text first).
     *
     * @param locator the element locator
     * @param text    the text to type
     */
    protected void type(By locator, String text) {
        log.debug("Typing '{}' into element: {}", text, locator);
        var element = waitForVisibility(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Clear the text from an element.
     *
     * @param locator the element locator
     */
    protected void clear(By locator) {
        log.debug("Clearing element: {}", locator);
        waitForVisibility(locator).clear();
    }

    /**
     * Get the text content of an element.
     *
     * @param locator the element locator
     * @return the text content
     */
    protected String getText(By locator) {
        log.debug("Getting text from element: {}", locator);
        return waitForVisibility(locator).getText();
    }

    /**
     * Get an attribute value from an element.
     *
     * @param locator   the element locator
     * @param attribute the attribute name
     * @return the attribute value
     */
    protected String getAttribute(By locator, String attribute) {
        log.debug("Getting attribute '{}' from element: {}", attribute, locator);
        return waitForPresence(locator).getDomAttribute(attribute);
    }

    /**
     * Check if an element is displayed.
     *
     * @param locator the element locator
     * @return true if element is displayed
     */
    protected boolean isDisplayed(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            log.debug("Element not displayed: {}", locator);
            return false;
        }
    }

    /**
     * Check if an element is enabled.
     *
     * @param locator the element locator
     * @return true if element is enabled
     */
    protected boolean isEnabled(By locator) {
        try {
            return driver.findElement(locator).isEnabled();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if an element exists in the DOM.
     *
     * @param locator the element locator
     * @return true if element exists
     */
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Scroll to an element using JavaScript.
     *
     * @param locator the element locator
     */
    protected void scrollToElement(By locator) {
        log.debug("Scrolling to element: {}", locator);
        var element = waitForPresence(locator);
        var js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    /**
     * Scroll to the bottom of the page.
     */
    protected void scrollToBottom() {
        log.debug("Scrolling to bottom of page");
        var js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Get the current URL.
     *
     * @return the current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Get the count of elements matching the locator.
     *
     * @param locator the element locator
     * @return the count of matching elements
     */
    protected int getElementCount(By locator) {
        return driver.findElements(locator).size();
    }

    /**
     * Get all elements matching the locator.
     *
     * @param locator the element locator
     * @return list of matching elements
     */
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Execute JavaScript code.
     *
     * @param script the JavaScript code
     * @param args   arguments to pass to the script
     */
    protected void executeScript(String script, Object... args) {
        var js = (JavascriptExecutor) driver;
        js.executeScript(script, args);
    }

    /**
     * Check if the page is loaded (abstract method to be implemented by subclasses).
     *
     * @return true if page is loaded correctly
     */
    public abstract boolean isPageLoaded();
}

