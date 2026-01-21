package org.fugazi.pages;

import io.qameta.allure.Step;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the Terms and Conditions page.
 */
public class TermsPage extends BasePage {

    // Page structure
    private static final By PAGE_CONTAINER = By.cssSelector("[data-testid='terms-page']");
    private static final By PAGE_HEADING = By.xpath("//h1[contains(text(), 'Terms')]");
    private static final By PAGE_CONTENT = By.tagName("main");
    private static final By TERMS_SECTION = By.cssSelector("[data-testid='terms-section']");

    // Individual sections
    private static final By SECTION_AGREEMENT = By.cssSelector("[data-testid='terms-agreement']");
    private static final By SECTION_USE_LICENSE = By.cssSelector("[data-testid='terms-use-license']");
    private static final By SECTION_DISCLAIMER = By.cssSelector("[data-testid='terms-disclaimer']");
    private static final By SECTION_LIMITATIONS = By.cssSelector("[data-testid='terms-limitations']");
    private static final By SECTION_ACCURACY = By.cssSelector("[data-testid='terms-accuracy']");
    private static final By SECTION_LINKS = By.cssSelector("[data-testid='terms-links']");
    private static final By SECTION_MODIFICATIONS = By.cssSelector("[data-testid='terms-modifications']");
    private static final By SECTION_GOVERNING_LAW = By.cssSelector("[data-testid='terms-governing-law']");
    private static final By SECTION_RETURNS = By.cssSelector("[data-testid='terms-returns']");
    private static final By SECTION_ACCOUNTS = By.cssSelector("[data-testid='terms-accounts']");

    // Last updated
    private static final By LAST_UPDATED = By.cssSelector("[data-testid='terms-updated']");

    public TermsPage(WebDriver driver) {
        super(driver);
    }

    @Override
    @Step("Verify Terms page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if Terms page is loaded");
        waitForPageLoad();

        var currentUrl = getCurrentUrl();
        log.info("Terms page - Current URL: {}", currentUrl);

        // Check if URL contains /terms
        if (currentUrl != null && currentUrl.contains("/terms")) {
            boolean hasContainer = isDisplayed(PAGE_CONTAINER);
            boolean hasSection = isDisplayed(TERMS_SECTION);
            log.info("Terms page elements - Container: {}, Section: {}", hasContainer, hasSection);
            return hasContainer || hasSection || isDisplayed(PAGE_CONTENT);
        }

        log.warn("URL does not contain /terms: {}", currentUrl);
        return false;
    }

    /**
     * Get the page heading text.
     *
     * @return page heading text
     */
    @Step("Get Terms page heading")
    public String getPageHeading() {
        waitForVisibility(PAGE_HEADING);
        return getText(PAGE_HEADING);
    }

    /**
     * Check if terms section container is displayed.
     *
     * @return true if terms section is visible
     */
    @Step("Check if terms section is displayed")
    public boolean isTermsSectionDisplayed() {
        return isDisplayed(TERMS_SECTION);
    }

    /**
     * Check if all 10 terms sections are displayed.
     *
     * @return true if all sections are visible
     */
    @Step("Check if all terms sections are displayed")
    public boolean areAllSectionsDisplayed() {
        return isDisplayed(SECTION_AGREEMENT) &&
                isDisplayed(SECTION_USE_LICENSE) &&
                isDisplayed(SECTION_DISCLAIMER) &&
                isDisplayed(SECTION_LIMITATIONS) &&
                isDisplayed(SECTION_ACCURACY) &&
                isDisplayed(SECTION_LINKS) &&
                isDisplayed(SECTION_MODIFICATIONS) &&
                isDisplayed(SECTION_GOVERNING_LAW) &&
                isDisplayed(SECTION_RETURNS) &&
                isDisplayed(SECTION_ACCOUNTS);
    }

    /**
     * Check if last updated date is displayed.
     *
     * @return true if last updated date is visible
     */
    @Step("Check if last updated date is displayed")
    public boolean isLastUpdatedDisplayed() {
        return isDisplayed(LAST_UPDATED);
    }

    /**
     * Get last updated date text.
     *
     * @return last updated date text
     */
    @Step("Get last updated date")
    public String getLastUpdatedDate() {
        if (isDisplayed(LAST_UPDATED)) {
            return getText(LAST_UPDATED);
        }
        return "";
    }

    /**
     * Verify page contains expected content keywords.
     *
     * @return true if page contains relevant keywords
     */
    @Step("Verify Terms page contains expected content")
    public boolean hasExpectedContent() {
        var pageContent = getText(PAGE_CONTENT).toLowerCase();

        // Check for keywords from actual implementation
        return pageContent.contains("terms") ||
                pageContent.contains("conditions") ||
                pageContent.contains("agreement") ||
                pageContent.contains("license") ||
                pageContent.contains("colombia");
    }
}
