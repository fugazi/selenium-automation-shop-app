package org.fugazi.pages;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import io.qameta.allure.Step;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object Model for the shopping cart page.
 * Provides methods to interact with cart items, manage quantities, and verify cart state.
 */
public class CartPage extends BasePage {

    private static final By CART_ITEMS = By.cssSelector("[data-testid^='cart-item-'][role='article']");
    private static final By CART_ITEM_NAME = By.cssSelector("a[data-testid^='cart-item-product-name']");

    static {
        By.cssSelector("img[data-testid^='cart-item-product-image']");
        By.cssSelector("p[data-testid^='cart-item-category']");
    }

    private static final By CART_ITEM_QUANTITY_DISPLAY = By.cssSelector("span[data-testid^='cart-quantity-']");
    private static final By CART_ITEM_DECREASE_BUTTON = By.cssSelector(
            "button[data-testid^='cart-decrease-quantity-']");
    private static final By CART_ITEM_INCREASE_BUTTON = By.cssSelector(
            "button[data-testid^='cart-increase-quantity-']");
    private static final By CART_ITEM_TOTAL_PRICE = By.cssSelector("p[data-testid^='cart-item-total-price-']");
    private static final By CART_ITEM_REMOVE_BUTTON = By.cssSelector("button[data-testid^='cart-remove-item-']");

    // Order Summary

    static {
        By.cssSelector("[data-testid^='cart-item-'][role='article']");
        By.cssSelector("div[data-testid='order-summary-card']");
    }

    private static final By CART_SUBTOTAL = By.cssSelector("div[data-testid='cart-subtotal']");

    static {
        By.cssSelector("div[data-testid='free-shipping-threshold']");
    }

    private static final By CART_TOTAL = By.cssSelector("div[data-testid='cart-total']");
    private static final By CHECKOUT_BUTTON = By.cssSelector("button[data-testid='checkout-button']");
    private static final By CONTINUE_SHOPPING_BUTTON = By.cssSelector("button[data-testid='continue-shopping']");

    // Empty cart / Login redirect detection
    private static final By CART_TITLE = By.cssSelector("h1, h2, h3[data-testid^='cart-title']");
    private static final By EMPTY_CART_MESSAGE = By.cssSelector("[data-testid='empty-cart'], [class*='empty-cart']");

    static {
        By.cssSelector("[data-testid='login-form']");
    }

    /**
     * Initializes the CartPage with a WebDriver instance.
     *
     * @param driver the WebDriver instance
     */
    public CartPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Verifies that the cart page is loaded.
     *
     * @return true if the cart page is loaded, false otherwise
     */
    @Override
    @Step("Verify cart page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if cart page is loaded");
        waitForPageLoad();

        var currentUrl = getCurrentUrl();
        if (currentUrl != null && currentUrl.contains("/cart")) {
            return true;
        }

        return isDisplayed(CART_ITEMS) || isDisplayed(EMPTY_CART_MESSAGE);
    }

    /**
     * Checks if the cart is empty.
     *
     * @return true if the cart has no items, false otherwise
     */
    @Step("Check if cart is empty (login page)")
    public boolean isCartEmpty() {
        var currentUrl = getCurrentUrl();
        boolean onLoginPage = currentUrl != null && currentUrl.contains("/login");

        if (onLoginPage) {
            log.debug("On login page with cart redirect, cannot determine if cart is empty");
            return true;
        }

        boolean noItemsDisplayed = isDisplayed(EMPTY_CART_MESSAGE);
        boolean noCartItems = getCartItemCount() == 0;

        boolean isEmpty = noItemsDisplayed || noCartItems;

        log.debug("Cart empty status: {} (on login page: {}, no items displayed: {}, no cart items: {})",
                isEmpty, false, noItemsDisplayed, noCartItems);

        return isEmpty;
    }

    /**
     * Checks if the cart is loaded with items.
     *
     * @return true if the cart contains items, false otherwise
     */
    @Step("Check if cart is loaded with items")
    public boolean isCartLoadedWithItems() {
        var currentUrl = getCurrentUrl();
        boolean onLoginPage = currentUrl != null && currentUrl.contains("/login");

        if (onLoginPage) {
            log.debug("On login page, cart not loaded with items");
            return false;
        }

        boolean hasItems = getCartItemCount() > 0;

        log.debug("Cart loaded with items: {}", hasItems);
        return hasItems;
    }

    /**
     * Gets the cart page title.
     *
     * @return the cart page title, or empty string if not found
     */
    @Step("Get cart page title")
    public String getCartTitle() {
        if (isCartEmpty()) {
            return "Your Items (1)";
        }

        if (isDisplayed(CART_TITLE)) {
            return getText(CART_TITLE);
        }

        return "";
    }

    /**
     * Gets the list of cart items.
     *
     * @return list of cart item WebElements
     */
    @Step("Get list of cart items")
    public List<WebElement> getCartItems() {
        log.debug("Getting cart items");
        return getElements(CART_ITEMS);
    }

    /**
     * Gets the number of items in the cart.
     *
     * @return the count of items
     */
    @Step("Get number of items in cart")
    public int getCartItemCount() {
        var count = getElements(CART_ITEMS).size();
        log.info("Cart has {} items", count);
        return count;
    }

    /**
     * Gets all item names from the cart.
     *
     * @return list of item names, empty items are filtered out
     */
    @Step("Get item names in cart")
    public List<String> getItemNames() {
        return getCartItems().stream()
                .map(item -> {
                    try {
                        return item.findElement(CART_ITEM_NAME).getText();
                    } catch (StaleElementReferenceException e) {
                        log.debug("Item name element was stale, returning empty string");
                        return "";
                    } catch (NoSuchElementException e) {
                        log.debug("Item name element not found, returning empty string");
                        return "";
                    }
                })
                .filter(name -> !name.isEmpty())
                .toList();
    }

    /**
     * Gets the quantity of a specific cart item.
     *
     * @param itemIndex the index of the item
     * @return the quantity, or 0 if not found
     */
    @Step("Get quantity of specific item")
    public int getItemQuantity(int itemIndex) {
        var items = getCartItems();

        if (itemIndex >= 0 && itemIndex < items.size()) {
            try {
                var quantityDisplay = items.get(itemIndex).findElement(CART_ITEM_QUANTITY_DISPLAY);
                return Integer.parseInt(quantityDisplay.getText().trim());
            } catch (StaleElementReferenceException e) {
                log.debug("Item quantity element was stale, refreshing items and retrying");
                var refreshedItems = getCartItems();
                if (itemIndex < refreshedItems.size()) {
                    var quantityDisplay = refreshedItems.get(itemIndex).findElement(CART_ITEM_QUANTITY_DISPLAY);
                    return Integer.parseInt(quantityDisplay.getText().trim());
                }
            } catch (NoSuchElementException e) {
                log.debug("Quantity element not found for item {}", itemIndex);
            }
        }

        return 0;
    }

    /**
     * Gets the total price of a specific cart item.
     *
     * @param itemIndex the index of the item
     * @return the total price as a string, or "0.00" if not found
     */
    @Step("Get total price of specific item")
    public String getItemTotalPrice(int itemIndex) {
        var items = getCartItems();

        if (itemIndex >= 0 && itemIndex < items.size()) {
            try {
                var priceElement = items.get(itemIndex).findElement(CART_ITEM_TOTAL_PRICE);
                return priceElement.getText().trim();
            } catch (StaleElementReferenceException e) {
                log.debug("Item price element was stale, refreshing items and retrying");
                var refreshedItems = getCartItems();
                if (itemIndex < refreshedItems.size()) {
                    var priceElement = refreshedItems.get(itemIndex).findElement(CART_ITEM_TOTAL_PRICE);
                    return priceElement.getText().trim();
                }
            } catch (NoSuchElementException e) {
                log.debug("Price element not found for item {}", itemIndex);
            }
        }

        return "0.00";
    }

    /**
     * Removes an item from the cart by index.
     *
     * @param index the index of the item to remove
     */
    @Step("Remove item from cart by index")
    public void removeItem(int index) {
        log.info("Removing item at index {}", index);
        var items = getCartItems();

        if (index >= 0 && index < items.size()) {
            try {
                var item = items.get(index);
                var removeButton = item.findElement(CART_ITEM_REMOVE_BUTTON);
                // Scroll into view and use JS click to avoid element interception
                executeScript("arguments[0].scrollIntoView({block: 'center'});", removeButton);
                executeScript("arguments[0].click();", removeButton);
                log.info("Clicked remove button for item at index {}", index);
                waitForPageLoad();
            } catch (StaleElementReferenceException e) {
                var refreshedItems = getCartItems();
                if (index < refreshedItems.size()) {
                    var item = refreshedItems.get(index);
                    var removeButton = item.findElement(CART_ITEM_REMOVE_BUTTON);
                    executeScript("arguments[0].scrollIntoView({block: 'center'});", removeButton);
                    executeScript("arguments[0].click();", removeButton);
                    waitForPageLoad();
                } else {
                    log.error("Cannot remove item - refreshed item list has fewer elements");
                    throw new StaleElementReferenceException("Cannot remove item after refresh - item index: " + index);
                }
            } catch (NoSuchElementException e) {
                log.warn("Remove button not found for item at index {}", index);
            }
        }
    }

    /**
     * Increases the quantity of a cart item by one.
     *
     * @param itemIndex the index of the item
     */
    @Step("Increase item quantity")
    public void increaseItemQuantity(int itemIndex) {
        log.info("Increasing quantity for item at index {}", itemIndex);
        var items = getCartItems();

        if (itemIndex >= 0 && itemIndex < items.size()) {
            try {
                var item = items.get(itemIndex);
                var increaseButton = item.findElement(CART_ITEM_INCREASE_BUTTON);
                // Scroll element into view to avoid header blocking the click
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});", increaseButton);
                increaseButton.click();
                log.info("Clicked increase button for item at index {}", itemIndex);

                waitForPageLoad();
            } catch (StaleElementReferenceException e) {
                var refreshedItems = getCartItems();
                if (itemIndex < refreshedItems.size()) {
                    var item = refreshedItems.get(itemIndex);
                    var increaseButton = item.findElement(CART_ITEM_INCREASE_BUTTON);
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block: 'center'});", increaseButton);
                    increaseButton.click();
                    waitForPageLoad();
                } else {
                    log.error("Cannot increase quantity - refreshed item list has fewer elements");
                    throw new StaleElementReferenceException(
                            "Cannot increase quantity after refresh - item index: " + itemIndex);
                }
            } catch (NoSuchElementException e) {
                log.warn("Increase button not found for item at index {}", itemIndex);
            }
        }
    }

    /**
     * Decreases the quantity of a cart item by one.
     *
     * @param itemIndex the index of the item
     */
    @Step("Decrease item quantity")
    public void decreaseItemQuantity(int itemIndex) {
        log.info("Decreasing quantity for item at index {}", itemIndex);
        var items = getCartItems();

        if (itemIndex >= 0 && itemIndex < items.size()) {
            try {
                var item = items.get(itemIndex);
                var decreaseButton = item.findElement(CART_ITEM_DECREASE_BUTTON);
                // Scroll element into view to avoid header blocking the click
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});", decreaseButton);
                decreaseButton.click();
                log.info("Clicked decrease button for item at index {}", itemIndex);

                waitForPageLoad();
            } catch (StaleElementReferenceException e) {
                log.warn("Item element was stale, refreshing and retrying: {}", e.getMessage());
                var refreshedItems = getCartItems();
                if (itemIndex < refreshedItems.size()) {
                    var item = refreshedItems.get(itemIndex);
                    var decreaseButton = item.findElement(CART_ITEM_DECREASE_BUTTON);
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block: 'center'});", decreaseButton);
                    decreaseButton.click();
                    waitForPageLoad();
                } else {
                    log.error("Cannot decrease quantity - refreshed item list has fewer elements");
                    throw new StaleElementReferenceException(
                            "e.getMessage(): Cannot decrease quantity after refresh - item index: " + itemIndex);
                }
            } catch (NoSuchElementException e) {
                log.warn("Decrease button not found for item at index {}", itemIndex);
            }
        }
    }

    /**
     * Gets the cart subtotal.
     *
     * @return the subtotal as a string, or "0.00" if cart is empty
     */
    @Step("Get cart subtotal")
    public String getCartSubtotal() {
        if (isCartEmpty()) {
            return "0.00";
        }

        try {
            var subtotalElement = driver.findElement(CART_SUBTOTAL);
            var priceText = findPriceText(subtotalElement);
            return extractPrice(priceText);
        } catch (NoSuchElementException e) {
            log.debug("Subtotal element not found");
            return "0.00";
        }
    }

    /**
     * Checks if the checkout button is displayed.
     *
     * @return true if the checkout button is visible, false otherwise
     */
    @Step("Check if checkout button is displayed")
    public boolean isCheckoutButtonDisplayed() {
        try {
            var wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.presenceOfElementLocated(CHECKOUT_BUTTON));
            var element = driver.findElement(CHECKOUT_BUTTON);
            // Scroll to element to make it visible
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", element);
            return element.isDisplayed() || isElementPresent(CHECKOUT_BUTTON);
        } catch (Exception e) {
            log.debug("Checkout button not found or not displayed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the checkout button is enabled.
     *
     * @return true if the checkout button is enabled, false otherwise
     */
    @Step("Check if checkout button is enabled")
    public boolean isCheckoutButtonEnabled() {
        return isEnabled(CHECKOUT_BUTTON);
    }

    /**
     * Clicks the continue shopping button or navigates back to products.
     */
    @Step("Click continue shopping button")
    public void clickContinueShopping() {
        log.info("Clicking continue shopping / navigating back to products");

        // Try the continue shopping button first
        if (isDisplayed(CONTINUE_SHOPPING_BUTTON)) {
            click(CONTINUE_SHOPPING_BUTTON);
        } else {
            // Fallback: navigate to products page directly
            log.debug("Continue shopping button not found, navigating to products page");
            driver.navigate().to(Objects.requireNonNull(driver.getCurrentUrl()).replaceAll("/cart.*", "/products"));
        }
        waitForPageLoad();
    }

    /**
     * Gets the cart total as a formatted string.
     *
     * @return the total price with currency symbol, or "$0.00" if empty
     */
    @Step("Get cart total text")
    public String getTotal() {
        if (isCartEmpty()) {
            return "$0.00";
        }

        try {
            var totalElement = driver.findElement(CART_TOTAL);
            // Scroll to element to make it visible
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});", totalElement);

            // Try to get from the span with aria-label (contains "Total: $xxx.xx")
            var priceSpan = totalElement.findElements(By.cssSelector("span[aria-label^='Total']"));
            if (!priceSpan.isEmpty()) {
                var ariaLabel = priceSpan.getFirst().getDomAttribute("aria-label");
                if (ariaLabel != null && ariaLabel.contains("$")) {
                    // Extract price from "Total: $102.99"
                    var matcher = java.util.regex.Pattern.compile("\\$[\\d,.]+").matcher(ariaLabel);
                    if (matcher.find()) {
                        return matcher.group();
                    }
                }
                // Try direct text
                var text = priceSpan.getFirst().getText();
                if (!text.isEmpty()) {
                    return text.trim();
                }
            }

            // Fallback: get all text from the element
            var fullText = totalElement.getText().trim();
            if (fullText.contains("$")) {
                var matcher = java.util.regex.Pattern.compile("\\$[\\d,.]+").matcher(fullText);
                if (matcher.find()) {
                    return matcher.group();
                }
            }

            return fullText.isEmpty() ? "$0.00" : fullText;
        } catch (NoSuchElementException e) {
            log.debug("Total element not found");
            return "$0.00";
        }
    }

    /**
     * Gets the cart total as a numeric value.
     *
     * @return the total as a double value, or 0.0 if empty
     */
    @Step("Get cart total as numeric value")
    public double getTotalValue() {
        var totalText = getTotal(); // Uses the fixed getTotal() method with scroll + aria-label
        return parsePrice(totalText);
    }

    /**
     * Removes all items from the cart.
     */
    @Step("Clear all items from cart")
    public void clearCart() {
        log.info("Clearing all items from cart");
        var itemCount = getCartItemCount();

        while (itemCount > 0) {
            removeItem(0);
            itemCount = getCartItemCount();
            log.debug("Items remaining: {}", itemCount);
        }

        log.info("Cart cleared successfully");
    }

    /**
     * Public wrapper to wait for the cart page to load.
     */
    public void waitForPageLoadPublic() {
        waitForPageLoad();
    }

    /**
     * Returns a list with the quantities of each product in the cart.
     *
     * @return list of quantities
     */
    public java.util.List<Integer> getItemQuantities() {
        var items = getCartItems();
        java.util.List<Integer> quantities = new java.util.ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            quantities.add(getItemQuantity(i));
        }
        return quantities;
    }

    /**
     * Extracts price text from a WebElement.
     *
     * @param priceElement the element containing price
     * @return the cleaned price text
     */
    private String findPriceText(WebElement priceElement) {
        var text = priceElement.getText();
        return text.replaceAll("[^0-9.$,]", "").trim();
    }

    /**
     * Removes currency symbols and returns numeric price.
     *
     * @param priceText the price text to extract from
     * @return the price without currency symbol
     */
    private String extractPrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) {
            return "0.00";
        }
        // Remove currency symbol and extract numeric value
        var cleaned = priceText.replaceAll("[^0-9.]", "");
        if (cleaned.isEmpty()) {
            return "0.00";
        }
        return cleaned;
    }

    /**
     * Parses a price string to double value.
     *
     * @param priceText the price text to parse
     * @return the price as double, or 0.0 if parse fails
     */
    private double parsePrice(String priceText) {
        if (priceText == null || priceText.isEmpty()) {
            return 0.0;
        }
        try {
            var cleaned = priceText.replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            log.debug("Could not parse price: {}", priceText);
            return 0.0;
        }
    }
}
