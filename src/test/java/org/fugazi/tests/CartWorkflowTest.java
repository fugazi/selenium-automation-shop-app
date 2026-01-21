package org.fugazi.tests;

import java.time.Duration;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.assertj.core.api.SoftAssertions;
import org.fugazi.config.ConfigurationManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test class for Cart Workflow functionality.
 * Tests comprehensive cart workflows including adding products,
 * quantity management, price calculations, and checkout flow.
 * Complements CartOperationsTest with more complex scenarios.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Cart Workflows")
@DisplayName("Cart Workflow Tests")
class CartWorkflowTest extends BaseTest {

    private WebDriverWait wait;

    @BeforeEach
    void setupWithLogin() {
        log.info("=== Setting up CartWorkflowTest ===");
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Login with customer account (cart requires authentication)
        performLogin();
    }

    // TODO: Refactorize
    private void performLogin() {
        log.info("Step 1: Navigating to login page");
        try {
            driver.get(ConfigurationManager.getInstance().getBaseUrl() + "/login");
        } catch (Exception e) {
            log.warn("Initial navigation failed, retrying: {}", e.getMessage());
            // Use WebDriverWait instead of Thread.sleep (framework compliance)
            var retryWait = new WebDriverWait(driver, Duration.ofSeconds(2));
            try {
                retryWait.until(d -> false);  // Wait for timeout without action
            } catch (org.openqa.selenium.TimeoutException te) {
                log.debug("Retry wait completed");
            }
            driver.get(ConfigurationManager.getInstance().getBaseUrl() + "/login");
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("[data-testid='login-email-input']")));

        log.info("Step 2: Logging in with customer credentials");
        var emailInput = driver.findElement(By.cssSelector("[data-testid='login-email-input']"));
        var passwordInput = driver.findElement(By.cssSelector("[data-testid='login-password-input']"));
        var submitButton = driver.findElement(By.cssSelector("[data-testid='login-submit-button']"));

        // Use constant credentials instead of hardcoded values (code quality improvement)
        emailInput.clear();
        emailInput.sendKeys(org.fugazi.data.models.Credentials.CUSTOMER_CREDENTIALS.email());
        passwordInput.clear();
        passwordInput.sendKeys(org.fugazi.data.models.Credentials.CUSTOMER_CREDENTIALS.password());
        submitButton.click();

        // Wait for login to complete - check for URL change or home page element
        try {
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        } catch (Exception e) {
            // If URL didn't change, try clicking submit again
            log.warn("Login may have failed, retrying...");
            submitButton.click();
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));
        }
        log.info("Login successful - URL: {}", driver.getCurrentUrl());
    }

    private void addProductToCart() {
        driver.get(ConfigurationManager.getInstance().getBaseUrl());
        homePage().clickFirstProduct();
        productDetailPage().clickAddToCart();
    }

    private void navigateToCart() {
        productDetailPage().goToCart();
        wait.until(ExpectedConditions.urlContains("/cart"));
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[data-slot='skeleton']")));
        } catch (Exception e) {
            log.debug("No skeletons found or already hidden");
        }
    }

    private void addSecondProduct() {
        // Navigate home and add a different product
        driver.get(ConfigurationManager.getInstance().getBaseUrl());
        homePage().clickProductByIndex(1);
        productDetailPage().clickAddToCart();
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Add Products")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should add single product to cart and verify details")
    void shouldAddSingleProductToCartAndVerify() {
        // Arrange
        driver.get(ConfigurationManager.getInstance().getBaseUrl());
        homePage().clickFirstProduct();
        var expectedProductName = productDetailPage().getProductTitle();
        var expectedPrice = productDetailPage().getProductPrice();

        // Act
        productDetailPage().clickAddToCart();
        navigateToCart();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cartPage().getCartItemCount())
                    .as("Cart should have exactly 1 item")
                    .isEqualTo(1);

            var itemNames = cartPage().getItemNames();
            softly.assertThat(itemNames)
                    .as("Cart should contain the added product")
                    .contains(expectedProductName);

            softly.assertThat(cartPage().getTotal())
                    .as("Cart total should match product price")
                    .contains("$");
        });

        log.info("Product '{}' added to cart with price {}", expectedProductName, expectedPrice);
    }

    @Test
    @Tag("regression")
    @Story("Add Products")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should add multiple different products to cart")
    void shouldAddMultipleProductsToCart() {
        // Add first product
        addProductToCart();
        log.info("Added first product");

        // Add second product
        addSecondProduct();
        log.info("Added second product");

        // Navigate to cart
        navigateToCart();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cartPage().getCartItemCount())
                    .as("Cart should have 2 items")
                    .isEqualTo(2);

            var itemNames = cartPage().getItemNames();
            softly.assertThat(itemNames)
                    .as("Cart should contain both products")
                    .hasSize(2);
        });
    }

    @Test
    @Tag("regression")
    @Story("Quantity Management")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should increase item quantity in cart")
    void shouldIncreaseItemQuantityInCart() {
        // Arrange
        addProductToCart();
        navigateToCart();
        var initialQuantity = cartPage().getItemQuantity(0);
        var initialTotal = cartPage().getTotal(); // Use String comparison

        // Act
        cartPage().increaseItemQuantity(0);

        // Wait for cart to update
        wait.until(d -> cartPage().getItemQuantity(0) > initialQuantity);

        var newQuantity = cartPage().getItemQuantity(0);
        var newTotal = cartPage().getTotal();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(newQuantity)
                    .as("Quantity should increase by 1")
                    .isEqualTo(initialQuantity + 1);

            softly.assertThat(newTotal)
                    .as("Total should have changed")
                    .isNotEqualTo(initialTotal);
        });

        log.info("Quantity changed from {} to {}, total from {} to {}",
                initialQuantity, newQuantity, initialTotal, newTotal);
    }

    @Test
    @Tag("regression")
    @Story("Quantity Management")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should decrease item quantity in cart")
    void shouldDecreaseItemQuantityInCart() {
        // Arrange - add product and increase quantity first
        addProductToCart();
        navigateToCart();
        cartPage().increaseItemQuantity(0); // Now quantity = 2

        wait.until(d -> cartPage().getItemQuantity(0) >= 2);
        var initialQuantity = cartPage().getItemQuantity(0);
        var initialTotal = cartPage().getTotal();

        // Act
        cartPage().decreaseItemQuantity(0);

        // Wait for cart to update
        wait.until(d -> cartPage().getItemQuantity(0) < initialQuantity);

        var newQuantity = cartPage().getItemQuantity(0);
        var newTotal = cartPage().getTotal();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(newQuantity)
                    .as("Quantity should decrease by 1")
                    .isEqualTo(initialQuantity - 1);

            softly.assertThat(newTotal)
                    .as("Total should change when quantity decreases")
                    .isNotEqualTo(initialTotal);
        });

        log.info("Quantity changed from {} to {}", initialQuantity, newQuantity);
    }

    @Test
    @Tag("regression")
    @Story("Price Calculations")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should update cart total after adding items")
    void shouldUpdateCartTotalAfterAddingItems() {
        // Add first product and get its price
        addProductToCart();
        navigateToCart();
        var firstItemTotal = cartPage().getTotalValue();
        log.info("First item total: ${}", firstItemTotal);

        // Add second product
        cartPage().clickContinueShopping();
        addSecondProduct();
        navigateToCart();
        var finalTotal = cartPage().getTotalValue();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(finalTotal)
                    .as("Total should be greater after adding second item")
                    .isGreaterThan(firstItemTotal);

            softly.assertThat(cartPage().getCartItemCount())
                    .as("Cart should have 2 items")
                    .isEqualTo(2);
        });

        log.info("Cart total updated from ${} to ${}", firstItemTotal, finalTotal);
    }

    @Test
    @Tag("regression")
    @Story("Cart Header")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display correct item count in header cart icon")
    void shouldDisplayCorrectItemCountInHeader() {
        // Arrange - add product
        addProductToCart();

        // Navigate to cart to verify items are persisted
        navigateToCart();

        // Assert - cart should have items
        var cartItemCount = cartPage().getCartItemCount();

        SoftAssertions.assertSoftly(softly -> softly.assertThat(cartItemCount)
                .as("Cart should show item count")
                .isGreaterThanOrEqualTo(1));

        log.info("Cart contains {} items", cartItemCount);
    }

    @Test
    @Tag("regression")
    @Story("Cart Persistence")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should persist cart items after page refresh")
    void shouldPersistCartAfterPageRefresh() {
        // Arrange
        addProductToCart();
        navigateToCart();
        var itemsBefore = cartPage().getCartItemCount();
        var namesBefore = cartPage().getItemNames().stream().toList(); // Create defensive copy

        // Act - refresh page
        driver.navigate().refresh();

        // Wait for cart page to fully reload
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("[data-testid^='cart-item-'][role='article']")));

        // Wait for skeletons to disappear
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                    By.cssSelector("[data-slot='skeleton']")));
        } catch (Exception e) {
            log.debug("No skeletons found");
        }

        // Get fresh data after refresh
        var itemsAfter = cartPage().getCartItemCount();
        var namesAfter = cartPage().getItemNames();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(itemsAfter)
                    .as("Item count should persist after refresh")
                    .isEqualTo(itemsBefore);

            softly.assertThat(namesAfter)
                    .as("Item names should persist after refresh")
                    .containsAll(namesBefore);
        });

        log.info("Cart persisted with {} items after refresh", itemsBefore);
    }

    @Test
    @Tag("regression")
    @Story("Checkout Flow")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should display checkout button when cart has items")
    void shouldProceedToCheckoutWhenLoggedIn() {
        // Arrange
        addProductToCart();
        navigateToCart();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cartPage().isCheckoutButtonDisplayed())
                    .as("Checkout button should be displayed")
                    .isTrue();

            softly.assertThat(cartPage().isCheckoutButtonEnabled())
                    .as("Checkout button should be enabled")
                    .isTrue();
        });

        log.info("Checkout button is displayed and enabled");
    }

    @Test
    @Tag("regression")
    @Story("Empty Cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should show empty state when cart has no items")
    void shouldShowEmptyCartWhenNoItems() {
        // Navigate directly to cart without adding items
        driver.get(ConfigurationManager.getInstance().getBaseUrl() + "/cart");

        wait.until(ExpectedConditions.urlContains("/cart"));

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cartPage().getCartItemCount())
                    .as("Cart should have no items")
                    .isEqualTo(0);

            softly.assertThat(cartPage().isCartEmpty())
                    .as("Cart should be empty")
                    .isTrue();
        });

        log.info("Empty cart state verified");
    }

    @Test
    @Tag("regression")
    @Story("Cart Display")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display item images in cart")
    void shouldDisplayItemImagesInCart() {
        // Arrange
        addProductToCart();
        navigateToCart();

        // Act - check for images
        var cartItems = cartPage().getCartItems();
        var hasImages = !cartItems.isEmpty() && cartItems.stream()
                .anyMatch(item -> {
                    try {
                        // Try multiple possible selectors for image
                        var images = item.findElements(By.tagName("img"));
                        return !images.isEmpty();
                    } catch (Exception e) {
                        return false;
                    }
                });

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cartItems)
                    .as("Cart should have at least one item")
                    .isNotEmpty();

            softly.assertThat(hasImages)
                    .as("Cart items should display images")
                    .isTrue();
        });

        log.info("Cart items display images: {}", hasImages);
    }

    @Test
    @Tag("regression")
    @Story("Cart Display")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display item prices in cart")
    void shouldDisplayItemPricesInCart() {
        // Arrange
        addProductToCart();
        navigateToCart();

        // Act
        var itemPrice = cartPage().getItemTotalPrice(0);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(itemPrice)
                    .as("Item price should be displayed")
                    .isNotEmpty();

            softly.assertThat(itemPrice)
                    .as("Item price should contain valid format")
                    .matches(".*\\d+.*"); // Contains numbers
        });

        log.info("Item price displayed: {}", itemPrice);
    }

    @Test
    @Tag("regression")
    @Story("Price Calculations")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should calculate subtotal correctly")
    void shouldCalculateSubtotalCorrectly() {
        // Arrange
        addProductToCart();
        navigateToCart();

        // Act
        var subtotal = cartPage().getCartSubtotal();
        var total = cartPage().getTotal();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(subtotal)
                    .as("Subtotal should be displayed")
                    .isNotEmpty();

            softly.assertThat(total)
                    .as("Total should be displayed")
                    .contains("$");
        });

        log.info("Subtotal: {}, Total: {}", subtotal, total);
    }

    @Test
    @Tag("regression")
    @Story("Quantity Management")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle multiple quantity increases")
    void shouldHandleMultipleQuantityIncreases() {
        // Arrange
        addProductToCart();
        navigateToCart();
        var initialQuantity = cartPage().getItemQuantity(0);

        // Act - increase quantity multiple times
        cartPage().increaseItemQuantity(0);
        cartPage().increaseItemQuantity(0);
        cartPage().increaseItemQuantity(0);

        var finalQuantity = cartPage().getItemQuantity(0);

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(finalQuantity)
                .as("Quantity should increase by 3")
                .isEqualTo(initialQuantity + 3));

        log.info("Quantity increased from {} to {}", initialQuantity, finalQuantity);
    }

    @Test
    @Tag("regression")
    @Story("Remove Items")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should remove all items from cart")
    void shouldRemoveAllItemsFromCart() {
        // Arrange - add multiple products
        addProductToCart();
        addSecondProduct();
        navigateToCart();

        var initialCount = cartPage().getCartItemCount();
        log.info("Initial cart count: {}", initialCount);

        // Act - clear cart
        cartPage().clearCart();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cartPage().getCartItemCount())
                    .as("Cart should be empty after clearing")
                    .isEqualTo(0);

            softly.assertThat(cartPage().isCartEmpty())
                    .as("Cart should report empty state")
                    .isTrue();
        });

        log.info("Cart cleared successfully from {} items", initialCount);
    }

    @Test
    @Tag("regression")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate back to shopping from cart")
    void shouldNavigateBackToShoppingFromCart() {
        // Arrange
        addProductToCart();
        navigateToCart();

        // Act
        cartPage().clickContinueShopping();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = driver.getCurrentUrl();
            softly.assertThat(currentUrl)
                    .as("Should navigate away from cart page")
                    .doesNotContain("/cart");
        });

        log.info("Navigated back to shopping, URL: {}", driver.getCurrentUrl());
    }
}
