package org.fugazi.pages;

import java.util.Objects;

import io.qameta.allure.Step;

import org.fugazi.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object representing the Product Detail Page.
 */
public class ProductDetailPage extends BasePage {

    // Components
    private final HeaderComponent header;

    // Locators
    private static final By PRODUCT_TITLE = By.cssSelector("[data-testid='product-title']");
    private static final By PRODUCT_PRICE = By.cssSelector("[data-testid='product-price']");
    private static final By PRODUCT_DESCRIPTION = By.cssSelector("[data-testid='product-description']");
    private static final By PRODUCT_IMAGE = By.cssSelector("[data-testid='gallery-main-image'] img");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector("[data-testid='add-to-cart-button']");
    private static final By PRODUCT_STOCK = By.cssSelector("[data-testid='product-stock-status']");
    private static final By SUCCESS_MESSAGE = By.cssSelector(
            "[data-testid='success-message'], .success, [role='alert']");

    public ProductDetailPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
    }

    /**
     * Get the header component.
     *
     * @return HeaderComponent instance
     */
    public HeaderComponent header() {
        return header;
    }

    @Override
    @Step("Verify product detail page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if product detail page is loaded");
        waitForPageLoad();
        return isDisplayed(PRODUCT_TITLE) && isDisplayed(PRODUCT_PRICE);
    }

    /**
     * Get the product title.
     *
     * @return product title text
     */
    @Step("Get product title")
    public String getProductTitle() {
        log.debug("Getting product title");
        return getText(PRODUCT_TITLE);
    }

    /**
     * Get the product price as text.
     *
     * @return product price text
     */
    @Step("Get product price")
    public String getProductPrice() {
        log.debug("Getting product price");
        return getText(PRODUCT_PRICE);
    }

    /**
     * Get the product price as a numeric value.
     *
     * @return product price as double
     */
    @Step("Get product price value")
    public double getProductPriceValue() {
        var priceText = getProductPrice();
        // Remove currency symbol and whitespace
        var numericPrice = priceText.replaceAll("[^0-9.,]", "");
        // Remove a thousand separators (commas in US format)
        numericPrice = numericPrice.replace(",", "");
        return Double.parseDouble(numericPrice);
    }

    /**
     * Get the product description.
     *
     * @return product description text
     */
    @Step("Get product description")
    public String getProductDescription() {
        log.debug("Getting product description");
        if (isDisplayed(PRODUCT_DESCRIPTION)) {
            return getText(PRODUCT_DESCRIPTION);
        }
        return "";
    }

    /**
     * Check if product image is displayed.
     *
     * @return true if image is visible
     */
    @Step("Check if product image is displayed")
    public boolean isProductImageDisplayed() {
        return isDisplayed(PRODUCT_IMAGE);
    }

    /**
     * Get the product image source URL.
     *
     * @return image src attribute
     */
    public String getProductImageSrc() {
        return getAttribute(PRODUCT_IMAGE, "src");
    }

    /**
     * Click add to cart button.
     */
    @Step("Click add to cart button")
    public void clickAddToCart() {
        log.info("Clicking add to cart button");
        scrollToElement(ADD_TO_CART_BUTTON);
        click(ADD_TO_CART_BUTTON);
    }

    /**
     * Click add to cart button and wait for action to complete.
     * Waits for either a success message, cart update, or page change.
     */
    @Step("Click add to cart and wait for confirmation")
    public void clickAddToCartAndWait() {
        log.info("Clicking add to cart button and waiting for confirmation");
        var currentUrl = getCurrentUrl();
        scrollToElement(ADD_TO_CART_BUTTON);
        click(ADD_TO_CART_BUTTON);

        // Wait for any of: success message, URL change, or button state change
        wait.until(driver -> {
            // Check if URL changed (redirect to log in or cart)
            if (!Objects.requireNonNull(driver.getCurrentUrl()).equals(currentUrl)) {
                return true;
            }
            // Check if success message appeared
            if (isDisplayed(SUCCESS_MESSAGE)) {
                return true;
            }
            // Check if button is still clickable (action completed)
            return isDisplayed(ADD_TO_CART_BUTTON);
        });

        log.info("Add to cart action completed");
    }

    /**
     * Check if add to cart button is displayed.
     *
     * @return true if button is visible
     */
    public boolean isAddToCartButtonDisplayed() {
        return isDisplayed(ADD_TO_CART_BUTTON);
    }

    /**
     * Check if add to cart button is enabled.
     *
     * @return true if button is enabled
     */
    public boolean isAddToCartButtonEnabled() {
        return isEnabled(ADD_TO_CART_BUTTON);
    }

    /**
     * Check if product is in stock.
     *
     * @return true if product is in stock
     */
    @Step("Check if product is in stock")
    public boolean isInStock() {
        if (isDisplayed(PRODUCT_STOCK)) {
            var stockText = getText(PRODUCT_STOCK).toLowerCase();
            log.debug("Stock status text: {}", stockText);
            return stockText.contains("in stock") || stockText.contains("available") || !stockText.contains(
                    "out of stock");
        }
        // If no stock element, check if Add to Cart is enabled
        return isAddToCartButtonEnabled();
    }

    /**
     * Navigate to cart.
     */
    @Step("Go to cart")
    public void goToCart() {
        header.clickCart();
    }
}

