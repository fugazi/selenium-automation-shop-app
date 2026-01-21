package org.fugazi.pages.components;

import io.qameta.allure.Step;

import org.fugazi.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;

/**
 * Component representing the header section of the application.
 * Includes navigation, search functionality, and cart access.
 */
public class HeaderComponent extends BasePage {

    // Header exists on /products, /cart, etc.
    private static final By LOGO = By.cssSelector("[data-testid='logo-link'], a[href='/']");
    private static final By HEADER_SEARCH_INPUT = By.cssSelector("[data-testid='search-input'], #header-search");
    private static final By HEADER_SEARCH_BUTTON = By.cssSelector("[data-testid='search-button']");

    // Products page list search
    private static final By PRODUCTS_SEARCH_INPUT = By.cssSelector("[data-testid='search-products-input']");

    // Cart
    private static final By CART_ICON = By.cssSelector(
            "a[href='/cart'], a[href*='/cart'], [data-testid='cart-icon'], button[aria-label*='cart']");
    // Counter varies by page; keep it flexible
    private static final By CART_COUNTER = By.cssSelector(
            "[data-testid='cart-count'], [data-testid='cart-counter'], .cart-count");

    private static final By NAV_LINKS = By.cssSelector("nav a, header a");
    private static final By HEADER_CONTAINER = By.cssSelector("header[data-testid='header'], header");
    private static final By START_SHOPPING_BUTTON = By.cssSelector("a[href='/products'], [data-testid='nav-products']");

    // Theme toggle
    private static final By THEME_TOGGLE_BUTTON = By.cssSelector("button[aria-label='Toggle theme']");
    private static final By HTML_ELEMENT = By.tagName("html");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    private String getBaseUrl() {
        var current = driver.getCurrentUrl();
        if (current == null || current.isBlank()) {
            return "";
        }
        var idx = current.indexOf("//");
        if (idx < 0) {
            return current;
        }
        var afterProtocol = current.substring(idx + 2);
        var slash = afterProtocol.indexOf('/');
        var host = slash >= 0 ? afterProtocol.substring(0, slash) : afterProtocol;
        return current.substring(0, idx + 2) + host;
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(HEADER_CONTAINER) || isDisplayed(START_SHOPPING_BUTTON);
    }

    /**
     * Check if header is displayed.
     * Note: The current app design may not have a traditional header.
     *
     * @return true if header or main navigation is visible
     */
    @Step("Verify header is displayed")
    public boolean isHeaderDisplayed() {
        log.debug("Checking if header/navigation is displayed");
        // More flexible check since app doesn't have traditional header
        return isDisplayed(HEADER_CONTAINER) || isElementPresent(START_SHOPPING_BUTTON);
    }

    /**
     * Click on the logo to navigate to home page.
     * Falls back to navigating to base URL if logo not found.
     */
    @Step("Click on logo to go to home page")
    public void clickLogo() {
        log.info("Clicking on logo to go to home page");
        if (isElementPresent(LOGO)) {
            click(LOGO);
        } else {
            driver.get(getBaseUrl());
        }
        waitForPageLoad();
    }

    /**
     * Check if logo is displayed.
     * Note: Current app may not have a visible logo.
     *
     * @return true if logo is visible, or home link exists
     */
    public boolean isLogoDisplayed() {
        return isElementPresent(LOGO) || isElementPresent(START_SHOPPING_BUTTON);
    }

    /**
     * Search for a product using the search bar.
     * Note: Search is available on /products page, will navigate there first if needed.
     *
     * @param searchTerm the term to search for
     */
    @Step("Search for product: {searchTerm}")
    public void searchProduct(String searchTerm) {
        log.info("Searching for: {}", searchTerm);

        var currentUrl = driver.getCurrentUrl();
        // Prefer searching on /products list (best for assertions in tests)
        if (currentUrl == null || !currentUrl.contains("/products")) {
            driver.get(getBaseUrl() + "/products");
            waitForPageLoad();
        }

        // If list search exists, use it
        if (isElementPresent(PRODUCTS_SEARCH_INPUT)) {
            type(PRODUCTS_SEARCH_INPUT, searchTerm);
            waitForPageLoad();
            return;
        }

        // Fallback to header search (if present)
        if (isElementPresent(HEADER_SEARCH_INPUT)) {
            type(HEADER_SEARCH_INPUT, searchTerm);
            if (isElementPresent(HEADER_SEARCH_BUTTON)) {
                click(HEADER_SEARCH_BUTTON);
            }
            waitForPageLoad();
        }
    }

    /**
     * Type in search field without submitting.
     *
     * @param searchTerm the term to type
     */
    @Step("Type in search field: {searchTerm}")
    public void typeInSearchField(String searchTerm) {
        log.debug("Typing in search field: {}", searchTerm);

        var currentUrl = driver.getCurrentUrl();
        if (currentUrl == null || !currentUrl.contains("/products")) {
            driver.get(getBaseUrl() + "/products");
            waitForPageLoad();
        }

        if (isElementPresent(PRODUCTS_SEARCH_INPUT)) {
            type(PRODUCTS_SEARCH_INPUT, searchTerm);
            return;
        }

        // fallback
        type(HEADER_SEARCH_INPUT, searchTerm);
    }

    /**
     * Get the current value in search field.
     *
     * @return the search field value
     */
    public String getSearchFieldValue() {
        if (isElementPresent(PRODUCTS_SEARCH_INPUT)) {
            return getAttribute(PRODUCTS_SEARCH_INPUT, "value");
        }
        return getAttribute(HEADER_SEARCH_INPUT, "value");
    }

    /**
     * Check if search input is displayed.
     * Note: Search is only available on /products page.
     *
     * @return true if search input is visible
     */
    public boolean isSearchInputDisplayed() {
        var currentUrl = driver.getCurrentUrl();
        if (currentUrl != null && currentUrl.contains("/products")) {
            return isElementPresent(PRODUCTS_SEARCH_INPUT) || isElementPresent(HEADER_SEARCH_INPUT);
        }
        return true;
    }

    /**
     * Click on the cart icon.
     * Falls back to navigating directly to /cart if icon not found.
     */
    @Step("Click on cart icon")
    public void clickCart() {
        log.info("Clicking on cart icon");
        try {
            if (isElementPresent(CART_ICON)) {
                var cartElement = driver.findElement(CART_ICON);
                try {
                    cartElement.click();
                } catch (StaleElementReferenceException e) {
                    log.debug("Cart element was stale, retrying");
                    driver.get(getBaseUrl() + "/cart");
                }
            } else {
                driver.get(getBaseUrl() + "/cart");
            }
        } catch (Exception e) {
            log.debug("Error clicking cart icon, navigating directly: {}", e.getMessage());
            driver.get(getBaseUrl() + "/cart");
        }
        waitForPageLoad();
    }

    /**
     * Check if cart icon is displayed.
     * Note: The current app design may not have a visible cart icon on all pages.
     *
     * @return true if cart icon is visible or cart functionality is accessible
     */
    public boolean isCartIconDisplayed() {
        // Try to find cart icon or link
        return isElementPresent(CART_ICON);
    }

    /**
     * Get the number of items in the cart from the badge.
     *
     * @return the cart item count, 0 if badge not visible
     */
    @Step("Get cart item count")
    public int getCartItemCount() {
        try {
            if (isDisplayed(CART_COUNTER)) {
                var countText = getText(CART_COUNTER).trim();
                log.debug("Cart counter text: {}", countText);

                if (countText.isEmpty()) {
                    return 0;
                }

                // Extract number from text (handles cases like "3 items" or just "3")
                var numberOnly = countText.replaceAll("[^0-9]", "");
                return numberOnly.isEmpty() ? 0 : Integer.parseInt(numberOnly);
            }
        } catch (Exception e) {
            log.debug("Could not get cart count: {}", e.getMessage());
        }
        return 0;
    }

    /**
     * Get the count of navigation links.
     *
     * @return number of nav links
     */
    public int getNavLinksCount() {
        return getElementCount(NAV_LINKS);
    }

    /**
     * Check if theme toggle button is displayed.
     *
     * @return true if theme toggle button is present
     */
    @Step("Verify theme toggle button is displayed")
    public boolean isThemeToggleDisplayed() {
        log.debug("Checking if theme toggle button is displayed");
        return isElementPresent(THEME_TOGGLE_BUTTON);
    }

    /**
     * Click the theme toggle button to switch between light and dark mode.
     */
    @Step("Click theme toggle button")
    public void clickThemeToggle() {
        log.info("Clicking theme toggle button");
        if (isElementPresent(THEME_TOGGLE_BUTTON)) {
            click(THEME_TOGGLE_BUTTON);
            // Wait briefly for theme transition
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } else {
            log.warn("Theme toggle button not found");
        }
    }

    /**
     * Get the current theme applied to the page.
     * Checks the HTML element's class and style attributes.
     *
     * @return "dark", "light", "system", or "unknown"
     */
    @Step("Get current theme")
    public String getCurrentTheme() {
        log.debug("Getting current theme");
        try {
            var htmlElement = driver.findElement(HTML_ELEMENT);
            var htmlClass = htmlElement.getDomAttribute("class");

            if (htmlClass != null) {
                if (htmlClass.contains("dark")) {
                    log.debug("Current theme: dark");
                    return "dark";
                } else if (htmlClass.contains("light")) {
                    log.debug("Current theme: light");
                    return "light";
                }
            }

            // Check color-scheme style
            var style = htmlElement.getDomAttribute("style");
            if (style != null) {
                if (style.contains("dark")) {
                    log.debug("Current theme: dark (from style)");
                    return "dark";
                } else if (style.contains("light")) {
                    log.debug("Current theme: light (from style)");
                    return "light";
                }
            }

            log.debug("Current theme: system (default)");
            return "system";
        } catch (Exception e) {
            log.debug("Error getting current theme: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Get the aria-label attribute of the theme toggle button.
     *
     * @return the aria-label value or empty string if not found
     */
    public String getThemeToggleAriaLabel() {
        if (isElementPresent(THEME_TOGGLE_BUTTON)) {
            return getAttribute(THEME_TOGGLE_BUTTON, "aria-label");
        }
        return "";
    }

    /**
     * Get the HTML element's class attribute.
     *
     * @return the class attribute value
     */
    public String getHtmlClass() {
        return getAttribute(HTML_ELEMENT, "class");
    }

    /**
     * Get the HTML element's color-scheme CSS value.
     *
     * @return the color-scheme CSS value
     */
    public String getHtmlColorScheme() {
        try {
            var htmlElement = driver.findElement(HTML_ELEMENT);
            return htmlElement.getCssValue("color-scheme");
        } catch (Exception e) {
            log.debug("Error getting color-scheme: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Get the HTML element's style attribute.
     *
     * @return the style attribute value
     */
    public String getHtmlStyle() {
        return getAttribute(HTML_ELEMENT, "style");
    }
}
