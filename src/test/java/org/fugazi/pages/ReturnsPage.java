package org.fugazi.pages;

import io.qameta.allure.Step;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the Returns & Warranty page.
 */
public class ReturnsPage extends BasePage {

    // Page structure
    private static final By PAGE_CONTAINER = By.cssSelector("[data-testid='returns-page']");
    private static final By PAGE_HEADING = By.cssSelector("[data-testid='returns-title']");
    private static final By PAGE_CONTENT = By.tagName("main");

    // Return policy card
    private static final By RETURN_POLICY_CARD = By.cssSelector("[data-testid='return-policy-card']");

    // Return process
    private static final By RETURN_STEPS = By.cssSelector("[data-testid='return-steps']");

    // Warranty section
    private static final By WARRANTY_OPTIONS = By.cssSelector("[data-testid='warranty-options']");

    // DHL locations
    private static final By DHL_LOCATIONS = By.cssSelector("[data-testid='dhl-locations']");

    // Return conditions
    private static final By RETURN_CONDITIONS = By.cssSelector("[data-testid='return-conditions']");

    // Help buttons
    private static final By VIEW_ORDERS_BUTTON = By.cssSelector("[data-testid='view-orders-button']");
    private static final By CONTACT_SUPPORT_BUTTON = By.cssSelector("[data-testid='contact-support-button']");

    public ReturnsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    @Step("Verify Returns page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if Returns page is loaded");
        waitForPageLoad();

        var currentUrl = getCurrentUrl();
        log.info("Returns page - Current URL: {}", currentUrl);

        // Check if URL contains /returns
        if (currentUrl != null && currentUrl.contains("/returns")) {
            boolean hasContainer = isDisplayed(PAGE_CONTAINER);
            boolean hasHeading = isDisplayed(PAGE_HEADING);
            log.info("Returns page elements - Container: {}, Heading: {}", hasContainer, hasHeading);
            return hasContainer || hasHeading || isDisplayed(PAGE_CONTENT);
        }

        log.warn("URL does not contain /returns: {}", currentUrl);
        return false;
    }

    /**
     * Get the page heading text.
     *
     * @return page heading text
     */
    @Step("Get Returns page heading")
    public String getPageHeading() {
        waitForVisibility(PAGE_HEADING);
        return getText(PAGE_HEADING);
    }

    /**
     * Check if return policy card is displayed.
     *
     * @return true if return policy card is visible
     */
    @Step("Check if return policy card is displayed")
    public boolean isReturnPolicyCardDisplayed() {
        return isDisplayed(RETURN_POLICY_CARD);
    }

    /**
     * Check if return process steps are displayed.
     *
     * @return true if return steps are visible
     */
    @Step("Check if return steps are displayed")
    public boolean areReturnStepsDisplayed() {
        return isDisplayed(RETURN_STEPS);
    }

    /**
     * Check if warranty options are displayed.
     *
     * @return true if warranty options are visible
     */
    @Step("Check if warranty options are displayed")
    public boolean areWarrantyOptionsDisplayed() {
        return isDisplayed(WARRANTY_OPTIONS);
    }

    /**
     * Check if DHL locations are displayed.
     *
     * @return true if DHL locations are visible
     */
    @Step("Check if DHL locations are displayed")
    public boolean areDHLLocationsDisplayed() {
        return isDisplayed(DHL_LOCATIONS);
    }

    /**
     * Check if return conditions are displayed.
     *
     * @return true if return conditions are visible
     */
    @Step("Check if return conditions are displayed")
    public boolean areReturnConditionsDisplayed() {
        return isDisplayed(RETURN_CONDITIONS);
    }

    /**
     * Check if help buttons are displayed.
     *
     * @return true if both help buttons are visible
     */
    @Step("Check if help buttons are displayed")
    public boolean areHelpButtonsDisplayed() {
        return isDisplayed(VIEW_ORDERS_BUTTON) && isDisplayed(CONTACT_SUPPORT_BUTTON);
    }

    /**
     * Verify page contains expected content keywords.
     *
     * @return true if page contains relevant keywords
     */
    @Step("Verify Returns page contains expected content")
    public boolean hasExpectedContent() {
        var pageContent = getText(PAGE_CONTENT).toLowerCase();

        // Check for keywords from actual implementation
        return pageContent.contains("return") ||
                pageContent.contains("warranty") ||
                pageContent.contains("refund") ||
                pageContent.contains("30") ||
                pageContent.contains("dhl");
    }
}
