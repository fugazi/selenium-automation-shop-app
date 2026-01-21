package org.fugazi.pages;

import java.util.List;

import io.qameta.allure.Step;

import org.fugazi.pages.components.FooterComponent;
import org.fugazi.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing the Home Page of the Music Tech Shop.
 */
public class HomePage extends BasePage {

    // Components
    private final HeaderComponent header;
    private final FooterComponent footer;

    // Locators
    private static final By HERO_SECTION = By.cssSelector("section.relative.overflow-hidden");
    private static final By FEATURED_PRODUCTS = By.cssSelector("[data-testid^='product-card-']");
    private static final By MAIN_CONTENT = By.cssSelector("main.flex-1");
    private static final By FEATURED_SECTION = By.xpath("//h2[contains(text(),'Featured Products')]/ancestor::section");
    private static final By PRODUCT_IMAGE_LINK = By.cssSelector("[data-testid^='product-image-link-']");
    private static final By PRODUCT_TITLE_LINK = By.cssSelector("[data-testid^='product-title-link-']");
    private static final By PRODUCT_TITLE_TEXT = By.cssSelector("[data-testid^='product-title-link-'] h3, h3");

    public HomePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
        this.footer = new FooterComponent(driver);
    }

    /**
     * Get the header component.
     *
     * @return HeaderComponent instance
     */
    public HeaderComponent header() {
        return header;
    }

    /**
     * Get the footer component.
     *
     * @return FooterComponent instance
     */
    public FooterComponent footer() {
        return footer;
    }

    @Override
    @Step("Verify home page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if home page is loaded");
        waitForPageLoad();
        // Home page is loaded if main content is present and hero section or featured products are visible
        return isDisplayed(MAIN_CONTENT) && (isDisplayed(HERO_SECTION) || isDisplayed(FEATURED_SECTION));
    }

    /**
     * Get the list of featured product elements.
     * Note: Products may be animated, so scroll to section first.
     *
     * @return list of product WebElements
     */
    @Step("Get featured products")
    public List<WebElement> getFeaturedProducts() {
        log.debug("Getting featured products");

        // First scroll to featured section to trigger animations
        if (isElementPresent(FEATURED_SECTION)) {
            scrollToElement(FEATURED_SECTION);
            // Wait for animations to complete instead of Thread.sleep
            waitForAnimationsToComplete();
        }

        // Wait for products to be visible with increased timeout
        try {
            waitForVisibility(FEATURED_PRODUCTS);
        } catch (Exception e) {
            log.warn("Featured products not immediately visible, trying to scroll more");
            scrollToBottom();
            // Wait for any elements to appear
            waitForMinimumElements(FEATURED_PRODUCTS, 1);
        }

        return getElements(FEATURED_PRODUCTS);
    }

    /**
     * Get the count of featured products.
     * Note: Products may be animated, so scroll to section first.
     *
     * @return number of featured products
     */
    @Step("Get featured products count")
    public int getFeaturedProductsCount() {
        // First scroll to featured section to trigger animations
        if (isElementPresent(FEATURED_SECTION)) {
            scrollToElement(FEATURED_SECTION);
            // Wait for animations to complete instead of Thread.sleep
            waitForAnimationsToComplete();
        }

        var count = getElementCount(FEATURED_PRODUCTS);
        log.info("Found {} featured products", count);
        return count;
    }

    /**
     * Get product names from featured products.
     *
     * @return list of product names
     */
    @Step("Get product names")
    public List<String> getProductNames() {
        var products = getFeaturedProducts();
        return products.stream().map(this::getProductNameFromElement).filter(name -> !name.isEmpty()).toList();
    }

    private String getProductNameFromElement(WebElement productElement) {
        try {
            // Try finding the h3 inside the product title link
            var titleElement = productElement.findElement(PRODUCT_TITLE_TEXT);
            return titleElement.getText().trim();
        } catch (Exception e) {
            log.debug("Could not find title for product: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Click on the first product to navigate to product detail page.
     */
    @Step("Click on first product")
    public void clickFirstProduct() {
        log.info("Clicking on first product");
        var products = getFeaturedProducts();
        if (!products.isEmpty()) {
            var firstProduct = products.getFirst();
            var currentUrl = getCurrentUrl();
            scrollToElement(FEATURED_PRODUCTS);

            // Try multiple approaches to click
            try {
                // First try: click on the image link using JavaScript
                var imageLink = firstProduct.findElement(PRODUCT_IMAGE_LINK);
                executeScript("arguments[0].click();", imageLink);
            } catch (Exception e1) {
                try {
                    // Second try: click on title link
                    var titleLink = firstProduct.findElement(PRODUCT_TITLE_LINK);
                    executeScript("arguments[0].click();", titleLink);
                } catch (Exception e2) {
                    // Final fallback: click the product card
                    executeScript("arguments[0].click();", firstProduct);
                }
            }
            waitForPageLoad();

            // Wait for URL to change (navigation to product detail)
            waitForUrlChange(currentUrl);
        }
    }

    /**
     * Click on a product by index (0-based) to navigate to product detail page.
     *
     * @param index the index of the product to click
     */
    @Step("Click on product at index: {index}")
    public void clickProductByIndex(int index) {
        log.info("Clicking on product at index: {}", index);
        var products = getFeaturedProducts();
        if (index >= 0 && index < products.size()) {
            var product = products.get(index);
            var currentUrl = getCurrentUrl();
            scrollToElement(FEATURED_PRODUCTS);

            // Try multiple approaches to click (same as clickFirstProduct)
            try {
                // First try: click on the image link using JavaScript
                var imageLink = product.findElement(PRODUCT_IMAGE_LINK);
                executeScript("arguments[0].click();", imageLink);
            } catch (Exception e1) {
                log.debug("JS click on image link failed: {}", e1.getMessage());
                try {
                    // Second try: click on title link
                    var titleLink = product.findElement(PRODUCT_TITLE_LINK);
                    executeScript("arguments[0].click();", titleLink);
                } catch (Exception e2) {
                    log.debug("JS click on title link failed: {}", e2.getMessage());
                    // Final fallback: click the product card
                    executeScript("arguments[0].click();", product);
                }
            }
            waitForPageLoad();

            // Wait for URL to change (navigation to product detail)
            waitForUrlChange(currentUrl);
        } else {
            throw new IndexOutOfBoundsException(
                    "Product index " + index + " is out of bounds. Total products: " + products.size());
        }
    }

    /**
     * Search for a product using the header search.
     *
     * @param searchTerm the term to search for
     */
    @Step("Search for: {searchTerm}")
    public void searchProduct(String searchTerm) {
        header.searchProduct(searchTerm);
    }

    /**
     * Get current cart item count from header.
     *
     * @return cart item count
     */
    public int getCartItemCount() {
        return header.getCartItemCount();
    }
}
