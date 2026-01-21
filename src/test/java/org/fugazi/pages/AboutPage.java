package org.fugazi.pages;

import io.qameta.allure.Step;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the About Us page.
 */
public class AboutPage extends BasePage {

    // Page structure
    private static final By PAGE_HEADING = By.cssSelector("h1");
    private static final By PAGE_CONTENT = By.tagName("main");

    // Mission/Vision/Values cards
    private static final By MISSION_CARD = By.cssSelector("[data-testid='mission-card']");
    private static final By VISION_CARD = By.cssSelector("[data-testid='vision-card']");
    private static final By VALUES_CARD = By.cssSelector("[data-testid='values-card']");

    // Why Choose Us features
    private static final By WHY_AUTHENTIC = By.cssSelector("[data-testid='why-authentic']");
    private static final By WHY_SUPPORT = By.cssSelector("[data-testid='why-support']");
    private static final By WHY_COMPETITIVE = By.cssSelector("[data-testid='why-competitive']");
    private static final By WHY_WARRANTY = By.cssSelector("[data-testid='why-warranty']");
    private static final By WHY_DELIVERY = By.cssSelector("[data-testid='why-delivery']");
    private static final By WHY_RETURNS = By.cssSelector("[data-testid='why-returns']");

    // Statistics section
    private static final By STAT_CUSTOMERS = By.cssSelector("[data-testid='stat-customers']");
    private static final By STAT_PRODUCTS = By.cssSelector("[data-testid='stat-products']");
    private static final By STAT_CITIES = By.cssSelector("[data-testid='stat-cities']");
    private static final By STAT_YEARS = By.cssSelector("[data-testid='stat-years']");

    // CTA buttons
    private static final By CTA_PRODUCTS = By.cssSelector("[data-testid='cta-products']");
    private static final By CTA_CONTACT = By.cssSelector("[data-testid='cta-contact']");

    public AboutPage(WebDriver driver) {
        super(driver);
    }

    @Override
    @Step("Verify About page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if About page is loaded");
        waitForPageLoad();

        var currentUrl = getCurrentUrl();
        log.info("Current URL after navigation: {}", currentUrl);

        // Check if URL contains /about
        if (currentUrl != null && currentUrl.contains("/about")) {
            // Use mission-card as primary indicator (reliable data-testid)
            boolean hasMissionCard = isDisplayed(MISSION_CARD);
            boolean hasHeading = isDisplayed(PAGE_HEADING);
            log.info("About page elements - Mission Card: {}, Heading: {}", hasMissionCard, hasHeading);
            return hasMissionCard || hasHeading;
        }

        log.warn("URL does not contain /about: {}", currentUrl);
        return false;
    }

    /**
     * Get the page heading text.
     *
     * @return page heading text
     */
    @Step("Get About page heading")
    public String getPageHeading() {
        waitForVisibility(PAGE_HEADING);
        return getText(PAGE_HEADING);
    }

    /**
     * Check if mission card is displayed.
     *
     * @return true if mission card is visible
     */
    @Step("Check if mission card is displayed")
    public boolean isMissionCardDisplayed() {
        return isDisplayed(MISSION_CARD);
    }

    /**
     * Check if vision card is displayed.
     *
     * @return true if vision card is visible
     */
    @Step("Check if vision card is displayed")
    public boolean isVisionCardDisplayed() {
        return isDisplayed(VISION_CARD);
    }

    /**
     * Check if values card is displayed.
     *
     * @return true if values card is visible
     */
    @Step("Check if values card is displayed")
    public boolean isValuesCardDisplayed() {
        return isDisplayed(VALUES_CARD);
    }

    /**
     * Check if all "Why Choose Us" features are displayed.
     *
     * @return true if all 6 features are visible
     */
    @Step("Check if all Why Choose Us features are displayed")
    public boolean areAllWhyChooseUsFeaturesDisplayed() {
        return isDisplayed(WHY_AUTHENTIC) &&
                isDisplayed(WHY_SUPPORT) &&
                isDisplayed(WHY_COMPETITIVE) &&
                isDisplayed(WHY_WARRANTY) &&
                isDisplayed(WHY_DELIVERY) &&
                isDisplayed(WHY_RETURNS);
    }

    /**
     * Check if statistics section is displayed.
     *
     * @return true if all 4 statistics are visible
     */
    @Step("Check if statistics are displayed")
    public boolean areStatisticsDisplayed() {
        return isDisplayed(STAT_CUSTOMERS) &&
                isDisplayed(STAT_PRODUCTS) &&
                isDisplayed(STAT_CITIES) &&
                isDisplayed(STAT_YEARS);
    }

    /**
     * Check if CTA buttons are displayed.
     *
     * @return true if both CTA buttons are visible
     */
    @Step("Check if CTA buttons are displayed")
    public boolean areCTAButtonsDisplayed() {
        return isDisplayed(CTA_PRODUCTS) && isDisplayed(CTA_CONTACT);
    }

    /**
     * Verify page contains expected content keywords.
     *
     * @return true if page contains relevant keywords
     */
    @Step("Verify About page contains expected content")
    public boolean hasExpectedContent() {
        var pageContent = getText(PAGE_CONTENT).toLowerCase();

        // Check for keywords from actual implementation
        return pageContent.contains("mission") ||
                pageContent.contains("vision") ||
                pageContent.contains("values") ||
                pageContent.contains("music") ||
                pageContent.contains("colombia");
    }
}
