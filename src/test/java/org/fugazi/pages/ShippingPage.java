package org.fugazi.pages;

import io.qameta.allure.Step;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the Shipping Policy page.
 */
public class ShippingPage extends BasePage {

    // Page structure
    private static final By PAGE_CONTAINER = By.cssSelector("[data-testid='shipping-page']");
    private static final By PAGE_HEADING = By.cssSelector("[data-testid='shipping-title']");
    private static final By PAGE_CONTENT = By.tagName("main");

    // Delivery calculator
    private static final By DELIVERY_CALCULATOR = By.cssSelector("[data-testid='delivery-calculator']");
    private static final By ZIP_CODE_INPUT = By.cssSelector("[data-testid='zip-code-input']");

    // Shipping options
    private static final By SHIPPING_STANDARD = By.cssSelector("[data-testid='shipping-option-standard']");
    private static final By SHIPPING_EXPRESS = By.cssSelector("[data-testid='shipping-option-express']");
    private static final By SHIPPING_OVERNIGHT = By.cssSelector("[data-testid='shipping-option-overnight']");
    private static final By POPULAR_BADGE = By.cssSelector("[data-testid='popular-badge']");

    // FAQ section
    private static final By FAQ_SECTION = By.cssSelector("[data-testid='shipping-faq']");

    public ShippingPage(WebDriver driver) {
        super(driver);
    }

    @Override
    @Step("Verify Shipping page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if Shipping page is loaded");
        waitForPageLoad();

        var currentUrl = getCurrentUrl();
        log.info("Shipping page - Current URL: {}", currentUrl);

        // Check if URL contains /shipping
        if (currentUrl != null && currentUrl.contains("/shipping")) {
            boolean hasContainer = isDisplayed(PAGE_CONTAINER);
            boolean hasHeading = isDisplayed(PAGE_HEADING);
            log.info("Shipping page elements - Container: {}, Heading: {}", hasContainer, hasHeading);
            return hasContainer || hasHeading || isDisplayed(PAGE_CONTENT);
        }

        log.warn("URL does not contain /shipping: {}", currentUrl);
        return false;
    }

    /**
     * Get the page heading text.
     *
     * @return page heading text
     */
    @Step("Get Shipping page heading")
    public String getPageHeading() {
        waitForVisibility(PAGE_HEADING);
        return getText(PAGE_HEADING);
    }

    /**
     * Check if delivery calculator is displayed.
     *
     * @return true if delivery calculator is visible
     */
    @Step("Check if delivery calculator is displayed")
    public boolean isDeliveryCalculatorDisplayed() {
        return isDisplayed(DELIVERY_CALCULATOR);
    }

    /**
     * Check if all shipping options are displayed.
     *
     * @return true if all 3 shipping options are visible
     */
    @Step("Check if all shipping options are displayed")
    public boolean areAllShippingOptionsDisplayed() {
        return isDisplayed(SHIPPING_STANDARD) &&
                isDisplayed(SHIPPING_EXPRESS) &&
                isDisplayed(SHIPPING_OVERNIGHT);
    }

    /**
     * Check if FAQ section is displayed.
     *
     * @return true if FAQ section is visible
     */
    @Step("Check if FAQ section is displayed")
    public boolean isFAQSectionDisplayed() {
        return isDisplayed(FAQ_SECTION);
    }

    /**
     * Enter ZIP code in calculator.
     *
     * @param zipCode ZIP code to enter
     */
    @Step("Enter ZIP code: {zipCode}")
    public void enterZipCode(String zipCode) {
        type(ZIP_CODE_INPUT, zipCode);
    }

    /**
     * Check if "Popular" badge is displayed on Express shipping.
     *
     * @return true if popular badge is visible
     */
    @Step("Check if popular badge is displayed")
    public boolean isPopularBadgeDisplayed() {
        return isDisplayed(POPULAR_BADGE);
    }

    /**
     * Verify page contains expected content keywords.
     *
     * @return true if page contains relevant keywords
     */
    @Step("Verify Shipping page contains expected content")
    public boolean hasExpectedContent() {
        var pageContent = getText(PAGE_CONTENT).toLowerCase();

        // Check for keywords from actual implementation
        return pageContent.contains("shipping") ||
                pageContent.contains("delivery") ||
                pageContent.contains("standard") ||
                pageContent.contains("express") ||
                pageContent.contains("overnight");
    }
}
