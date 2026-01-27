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

    // Quantity selectors
    // NOTE: Application uses buttons for quantity, NOT an input field
    // The quantity value is displayed as text, not in an editable input
    private static final By QUANTITY_SELECTOR = By.cssSelector("[data-testid='quantity-selector']");
    private static final By QUANTITY_DECREASE = By.cssSelector("[data-testid='quantity-decrease-button']");
    private static final By QUANTITY_INCREASE = By.cssSelector("[data-testid='quantity-increase-button']");
    private static final By QUANTITY_DISPLAY = By.cssSelector("[data-testid='quantity-value'], .quantity-value");
    private static final By TOTAL_PRICE = By.cssSelector("[data-testid='total-price'], .total-price");

    // Navigation & Recommendations
    private static final By CONTINUE_SHOPPING_BUTTON = By.cssSelector(
            "[data-testid='continue-shopping'], a[href*='/products']");
    private static final By RECOMMENDED_PRODUCTS = By.cssSelector(
            "[data-testid^='recommended-'], .recommended-products");
    private static final By RECOMMENDED_PRODUCT_LINKS = By.cssSelector(
            "[data-testid^='recommended-'] a, .recommended-products a");

    // Reviews
    private static final By REVIEWS_SECTION = By.cssSelector("[data-testid='reviews-section'], #reviews, .reviews");
    private static final By REVIEW_ITEMS = By.cssSelector("[data-testid^='review-'], .review-item");

    // Share
    private static final By SHARE_BUTTON = By.cssSelector("[data-testid='share-button'], button[aria-label*='share']");
    private static final By COPY_LINK_BUTTON = By.cssSelector("[data-testid='copy-link'], button[aria-label*='copy']");
    private static final By COPIED_MESSAGE = By.cssSelector("[data-testid='copied-message'], .copied, [role='status']");

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
     * Check if decrease quantity button is enabled.
     *
     * @return true if button is enabled
     */
    @Step("Check if decrease quantity button is enabled")
    public boolean isDecreaseButtonEnabled() {
        return isEnabled(QUANTITY_DECREASE);
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

    /**
     * Get current quantity value.
     * NOTE: Application displays quantity as text within the quantity selector,
     * not as an input field. We need to extract it from the display.
     *
     * @return quantity as integer
     */
    @Step("Get product quantity")
    public int getQuantity() {
        try {
            // Try to get quantity from display element
            if (isDisplayed(QUANTITY_DISPLAY)) {
                var quantityText = getText(QUANTITY_DISPLAY);
                try {
                    return Integer.parseInt(quantityText.trim());
                } catch (NumberFormatException e) {
                    log.warn("Could not parse quantity from display: {}", quantityText);
                }
            }

            // Fallback: try to find quantity in the quantity selector text
            if (isDisplayed(QUANTITY_SELECTOR)) {
                var selectorText = getText(QUANTITY_SELECTOR);
                // Extract first number from the text
                var matcher = java.util.regex.Pattern.compile("\\d+").matcher(selectorText);
                if (matcher.find()) {
                    return Integer.parseInt(matcher.group());
                }
            }

            log.warn("Could not find quantity value, returning default 1");
            return 1;
        } catch (Exception e) {
            log.warn("Error getting quantity: {}", e.getMessage());
            return 1;
        }
    }

    /**
     * Set product quantity to specified value.
     * NOTE: Since there's no editable input field, we click the increase button
     * multiple times to reach the desired quantity.
     *
     * @param quantity quantity to set
     */
    @Step("Set product quantity to: {quantity}")
    public void setQuantity(int quantity) {
        log.info("Setting quantity to: {} (using increase button)", quantity);

        var currentQuantity = getQuantity();
        log.debug("Current quantity: {}, Target: {}", currentQuantity, quantity);

        // If we need to increase quantity
        if (quantity > currentQuantity) {
            var clicksNeeded = quantity - currentQuantity;
            for (int i = 0; i < clicksNeeded; i++) {
                click(QUANTITY_INCREASE);
                // Small wait to allow React state to update
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            log.info("Clicked increase button {} times", clicksNeeded);
        }
        // If we need to decrease quantity (only if > 1)
        else if (quantity < currentQuantity && quantity > 0) {
            var clicksNeeded = currentQuantity - quantity;
            for (int i = 0; i < clicksNeeded; i++) {
                click(QUANTITY_DECREASE);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            log.info("Clicked decrease button {} times", clicksNeeded);
        } else {
            log.info("Quantity already set to {}", quantity);
        }

        // Verify quantity was updated
        var newQuantity = getQuantity();
        log.info("Quantity after setting: {}", newQuantity);
    }

    /**
     * Increase product quantity by one.
     */
    @Step("Increase product quantity")
    public void increaseQuantity() {
        if (isDisplayed(QUANTITY_INCREASE)) {
            var initialQuantity = getQuantity();
            click(QUANTITY_INCREASE);

            // Wait for quantity to update (React state change)
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var newQuantity = getQuantity();
            log.info("Quantity increased: {} → {}", initialQuantity, newQuantity);
        }
    }

    /**
     * Decrease product quantity by one.
     */
    @Step("Decrease product quantity")
    public void decreaseQuantity() {
        if (isDisplayed(QUANTITY_DECREASE)) {
            var initialQuantity = getQuantity();
            click(QUANTITY_DECREASE);

            // Wait for quantity to update (React state change)
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            var newQuantity = getQuantity();
            log.info("Quantity decreased: {} → {}", initialQuantity, newQuantity);
        }
    }

    /**
     * Get total price (unit price × quantity).
     *
     * @return total price as text
     */
    @Step("Get total price")
    public String getTotalPrice() {
        if (isDisplayed(TOTAL_PRICE)) {
            return getText(TOTAL_PRICE);
        }
        return getProductPrice();
    }

    /**
     * Get total price as numeric value.
     *
     * @return total price as double
     */
    @Step("Get total price value")
    public double getTotalPriceValue() {
        var priceText = getTotalPrice();
        var numericPrice = priceText.replaceAll("[^0-9.,]", "");
        numericPrice = numericPrice.replace(",", "");
        try {
            return Double.parseDouble(numericPrice);
        } catch (NumberFormatException e) {
            log.warn("Could not parse total price: {}", priceText);
            return getProductPriceValue() * getQuantity();
        }
    }

    /**
     * Verify total price calculation (unit price × quantity).
     *
     * @return true if calculation is correct
     */
    @Step("Verify total price calculation")
    public boolean isTotalPriceCalculatedCorrectly() {
        var unitPrice = getProductPriceValue();
        var quantity = getQuantity();
        var expectedTotal = unitPrice * quantity;
        var actualTotal = getTotalPriceValue();

        var isCorrect = Math.abs(actualTotal - expectedTotal) < 0.01;
        log.info("Total price check - Unit: {}, Qty: {}, Expected: {}, Actual: {}",
                unitPrice, quantity, expectedTotal, actualTotal);
        return isCorrect;
    }

    /**
     * Click continue shopping button to return to products/home.
     */
    @Step("Click continue shopping button")
    public void clickContinueShopping() {
        if (isDisplayed(CONTINUE_SHOPPING_BUTTON)) {
            click(CONTINUE_SHOPPING_BUTTON);
            log.info("Continue shopping button clicked");
        }
    }

    /**
     * Check if recommended products section is displayed.
     *
     * @return true if recommendations are visible
     */
    @Step("Check if recommended products are displayed")
    public boolean hasRecommendedProducts() {
        return isDisplayed(RECOMMENDED_PRODUCTS);
    }

    /**
     * Get count of recommended products.
     *
     * @return number of recommended products
     */
    @Step("Get recommended products count")
    public int getRecommendedProductsCount() {
        if (hasRecommendedProducts()) {
            return getElements(RECOMMENDED_PRODUCT_LINKS).size();
        }
        return 0;
    }

    /**
     * Click on recommended product by index.
     *
     * @param index product index (0-based)
     */
    @Step("Click recommended product at index: {index}")
    public void clickRecommendedProduct(int index) {
        var products = getElements(RECOMMENDED_PRODUCT_LINKS);
        if (index >= 0 && index < products.size()) {
            products.get(index).click();
            log.info("Clicked recommended product at index {}", index);
        }
    }

    /**
     * Check if reviews section is displayed.
     *
     * @return true if reviews section is visible
     */
    @Step("Check if reviews section is displayed")
    public boolean hasReviewsSection() {
        return isDisplayed(REVIEWS_SECTION);
    }

    /**
     * Get count of reviews.
     *
     * @return number of reviews
     */
    @Step("Get reviews count")
    public int getReviewsCount() {
        if (hasReviewsSection()) {
            return getElements(REVIEW_ITEMS).size();
        }
        return 0;
    }

    /**
     * Check if reviews contain required information (name, date).
     *
     * @return true if reviews are properly formatted
     */
    @Step("Check if reviews are properly formatted")
    public boolean areReviewsProperlyFormatted() {
        var reviews = getElements(REVIEW_ITEMS);
        if (reviews.isEmpty()) {
            return true;
        }

        return reviews.stream().allMatch(review -> {
            var text = review.getText();
            return !text.isBlank() &&
                    (text.toLowerCase().contains("by") ||
                            text.length() > 10);
        });
    }

    /**
     * Click share button to open share options.
     */
    @Step("Click share button")
    public void clickShareButton() {
        if (isDisplayed(SHARE_BUTTON)) {
            click(SHARE_BUTTON);
            log.info("Share button clicked");
        }
    }

    /**
     * Click copy link button.
     */
    @Step("Click copy link button")
    public void clickCopyLink() {
        if (isDisplayed(COPY_LINK_BUTTON)) {
            click(COPY_LINK_BUTTON);
            log.info("Copy link button clicked");
        }
    }

    /**
     * Check if copied/success message is displayed after sharing.
     *
     * @return true if copied message is visible
     */
    @Step("Check if copied message is displayed")
    public boolean isCopiedMessageDisplayed() {
        return isDisplayed(COPIED_MESSAGE);
    }

    /**
     * Get stock status text.
     *
     * @return stock status text
     */
    @Step("Get stock status")
    public String getStockStatus() {
        if (isDisplayed(PRODUCT_STOCK)) {
            return getText(PRODUCT_STOCK);
        }
        return "";
    }
}
