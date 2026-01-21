package org.fugazi.pages.components;

import io.qameta.allure.Step;

import org.fugazi.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * Component representing the footer section of the application.
 * Includes links, social media, and copyright information.
 */
public class FooterComponent extends BasePage {

    // Locators
    private static final By FOOTER_CONTAINER = By.cssSelector("footer, [data-testid='footer']");
    private static final By COPYRIGHT = By.cssSelector(
            "footer p, footer .copyright, footer [class*='copyright'], footer [data-testid='footer-about'] p");

    // Footer sections
    private static final By FOOTER_ABOUT = By.cssSelector("[data-testid='footer-about']");
    private static final By FOOTER_PRODUCTS = By.cssSelector("[data-testid='footer-products']");
    private static final By FOOTER_INFORMATION = By.cssSelector("[data-testid='footer-information']");
    private static final By FOOTER_SUPPORT = By.cssSelector("[data-testid='footer-support']");
    private static final By FOOTER_COPYRIGHT = By.cssSelector("[data-testid='footer-copyright']");

    // Social links specific
    private static final By SOCIAL_LINK_ICONS = By.cssSelector(
            "[data-testid='footer-about'] a.rounded-full, [data-testid='footer-about'] a[href='#'], footer a .lucide-instagram, footer a .lucide-twitter, footer a .lucide-github");

    // Footer navigation links
    private static final By FOOTER_LINK_ELECTRONICS = By.cssSelector("[data-testid='footer-link-electronics']");
    private static final By FOOTER_LINK_PHOTOGRAPHY = By.cssSelector("[data-testid='footer-link-photography']");
    private static final By FOOTER_LINK_ACCESSORIES = By.cssSelector("[data-testid='footer-link-accessories']");
    private static final By FOOTER_LINK_ABOUT = By.cssSelector("[data-testid='footer-link-about']");
    private static final By FOOTER_LINK_SHIPPING = By.cssSelector("[data-testid='footer-link-shipping']");
    private static final By FOOTER_LINK_RETURNS = By.cssSelector("[data-testid='footer-link-returns']");
    private static final By FOOTER_LINK_TERMS = By.cssSelector("[data-testid='footer-link-terms']");

    public FooterComponent(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(FOOTER_CONTAINER);
    }

    /**
     * Check if footer is displayed.
     *
     * @return true if footer is visible
     */
    @Step("Verify footer is displayed")
    public boolean isFooterDisplayed() {
        log.debug("Checking if footer is displayed");
        return isDisplayed(FOOTER_CONTAINER);
    }

    /**
     * Scroll to footer section.
     */
    @Step("Scroll to footer")
    public void scrollToFooter() {
        log.debug("Scrolling to footer");
        scrollToElement(FOOTER_CONTAINER);
    }

    /**
     * Get the copyright text.
     *
     * @return the copyright text
     */
    @Step("Get copyright text")
    public String getCopyrightText() {
        log.debug("Getting copyright text");
        scrollToFooter();
        if (isDisplayed(COPYRIGHT)) {
            return getText(COPYRIGHT);
        }
        return "";
    }

    /**
     * Check if footer about section is displayed.
     *
     * @return true if about section is visible
     */
    @Step("Check if footer about section is displayed")
    public boolean isAboutSectionDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_ABOUT);
    }

    /**
     * Check if footer products section is displayed.
     *
     * @return true if products section is visible
     */
    @Step("Check if footer products section is displayed")
    public boolean isProductsSectionDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_PRODUCTS);
    }

    /**
     * Check if footer information section is displayed.
     *
     * @return true if information section is visible
     */
    @Step("Check if footer information section is displayed")
    public boolean isInformationSectionDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_INFORMATION);
    }

    /**
     * Check if footer support section is displayed.
     *
     * @return true if support section is visible
     */
    @Step("Check if footer support section is displayed")
    public boolean isSupportSectionDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_SUPPORT);
    }

    /**
     * Check if footer copyright section is displayed.
     *
     * @return true if copyright section is visible
     */
    @Step("Check if footer copyright section is displayed")
    public boolean isCopyrightSectionDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_COPYRIGHT);
    }

    /**
     * Get count of social link icons.
     *
     * @return number of social link icons
     */
    public int getSocialLinkIconsCount() {
        scrollToFooter();
        return getElementCount(SOCIAL_LINK_ICONS);
    }

    /**
     * Check if electronics footer link is displayed.
     *
     * @return true if electronics link is visible
     */
    public boolean isElectronicsLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_ELECTRONICS);
    }

    /**
     * Click on electronics category link in footer.
     */
    @Step("Click electronics category link in footer")
    public void clickElectronicsLink() {
        log.info("Clicking electronics link in footer");
        scrollToFooter();
        click(FOOTER_LINK_ELECTRONICS);
        waitForPageLoad();
    }

    /**
     * Check if photography footer link is displayed.
     *
     * @return true if photography link is visible
     */
    public boolean isPhotographyLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_PHOTOGRAPHY);
    }

    /**
     * Click on photography category link in footer.
     */
    @Step("Click photography category link in footer")
    public void clickPhotographyLink() {
        log.info("Clicking photography link in footer");
        scrollToFooter();
        click(FOOTER_LINK_PHOTOGRAPHY);
        waitForPageLoad();
    }

    /**
     * Check if accessories footer link is displayed.
     *
     * @return true if accessories link is visible
     */
    public boolean isAccessoriesLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_ACCESSORIES);
    }

    /**
     * Click on accessories category link in footer.
     */
    @Step("Click accessories category link in footer")
    public void clickAccessoriesLink() {
        log.info("Clicking accessories link in footer");
        scrollToFooter();
        click(FOOTER_LINK_ACCESSORIES);
        waitForPageLoad();
    }

    /**
     * Check if about footer link is displayed.
     *
     * @return true if about link is visible
     */
    public boolean isAboutLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_ABOUT);
    }

    /**
     * Check if shipping footer link is displayed.
     *
     * @return true if shipping link is visible
     */
    public boolean isShippingLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_SHIPPING);
    }

    /**
     * Check if returns footer link is displayed.
     *
     * @return true if returns link is visible
     */
    public boolean isReturnsLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_RETURNS);
    }

    /**
     * Check if terms footer link is displayed.
     *
     * @return true if terms link is visible
     */
    public boolean isTermsLinkDisplayed() {
        scrollToFooter();
        return isElementPresent(FOOTER_LINK_TERMS);
    }

    /**
     * Click on About Us link in footer using JavaScript for reliability.
     * Link text: "About Us" - href: /about
     */
    @Step("Click About Us link in footer")
    public void clickAboutLink() {
        log.info("Clicking About Us link in footer");
        scrollToFooter();

        // Wait for element to be present
        waitForClickable(FOOTER_LINK_ABOUT);

        var currentUrl = driver.getCurrentUrl();
        var element = driver.findElement(FOOTER_LINK_ABOUT);
        var href = element.getDomAttribute("href");
        log.info("About link href: {}", href);

        // Use JavaScript click for better reliability
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            log.warn("JavaScript click failed, trying regular click: {}", e.getMessage());
            element.click();
        }

        // Wait for URL to change
        waitForUrlChange(currentUrl);
        waitForPageLoad();
        log.info("After clicking About link, URL is: {}", driver.getCurrentUrl());
    }

    /**
     * Click on Shipping Policy link in footer using JavaScript for reliability.
     * Link text: "Shipping Policy" - href: /shipping
     */
    @Step("Click Shipping Policy link in footer")
    public void clickShippingLink() {
        log.info("Clicking Shipping Policy link in footer");
        scrollToFooter();

        waitForClickable(FOOTER_LINK_SHIPPING);

        var currentUrl = driver.getCurrentUrl();
        var element = driver.findElement(FOOTER_LINK_SHIPPING);
        var href = element.getDomAttribute("href");
        log.info("Shipping link href: {}", href);

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            element.click();
        }

        // Wait for URL to change
        waitForUrlChange(currentUrl);
        waitForPageLoad();
        log.info("After clicking Shipping link, URL is: {}", driver.getCurrentUrl());
    }

    /**
     * Click on Returns & Refunds link in footer using JavaScript for reliability.
     * Link text: "Returns & Refunds" - href: /returns
     */
    @Step("Click Returns & Refunds link in footer")
    public void clickReturnsLink() {
        log.info("Clicking Returns & Refunds link in footer");
        scrollToFooter();

        waitForClickable(FOOTER_LINK_RETURNS);

        var currentUrl = driver.getCurrentUrl();
        var element = driver.findElement(FOOTER_LINK_RETURNS);
        var href = element.getDomAttribute("href");
        log.info("Returns link href: {}", href);

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            element.click();
        }

        // Wait for URL to change
        waitForUrlChange(currentUrl);
        waitForPageLoad();
        log.info("After clicking Returns link, URL is: {}", driver.getCurrentUrl());
    }

    /**
     * Click on Terms of Service link in footer using JavaScript for reliability.
     * Link text: "Terms of Service" - href: /terms
     */
    @Step("Click Terms of Service link in footer")
    public void clickTermsLink() {
        log.info("Clicking Terms of Service link in footer");
        scrollToFooter();

        waitForClickable(FOOTER_LINK_TERMS);

        var currentUrl = driver.getCurrentUrl();
        var element = driver.findElement(FOOTER_LINK_TERMS);
        var href = element.getDomAttribute("href");
        log.info("Terms link href: {}", href);

        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            element.click();
        }

        // Wait for URL to change
        waitForUrlChange(currentUrl);
        waitForPageLoad();
        log.info("After clicking Terms link, URL is: {}", driver.getCurrentUrl());
    }
}
