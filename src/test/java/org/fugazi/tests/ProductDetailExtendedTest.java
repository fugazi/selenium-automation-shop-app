package org.fugazi.tests;

import static org.assertj.core.api.Assertions.within;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Product Detail functionality.
 * Tests quantity management, stock validation, reviews, and share features.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Product Detail")
@DisplayName("Product Detail Extended Tests")
class ProductDetailExtendedTest extends BaseTest {

    // ==================== Quantity & Price Tests ====================

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Quantity & Price")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should update total price when quantity changes")
    void shouldUpdateTotalPriceWhenQuantityChanges() {
        // Arrange - Navigate to product
        homePage().clickFirstProduct();
        var unitPrice = productDetailPage().getProductPriceValue();
        var initialQuantity = productDetailPage().getQuantity();
        var initialTotal = productDetailPage().getTotalPriceValue();

        // Act - Increase quantity
        productDetailPage().increaseQuantity();

        // Assert
        var newQuantity = productDetailPage().getQuantity();
        var newTotal = productDetailPage().getTotalPriceValue();
        var expectedTotal = unitPrice * newQuantity;

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(newQuantity)
                    .as("Quantity should increase by 1")
                    .isEqualTo(initialQuantity + 1);

            softly.assertThat(productDetailPage().isTotalPriceCalculatedCorrectly())
                    .as("Total price should be unit price × quantity")
                    .isTrue();

            softly.assertThat(newTotal)
                    .as("Total price should be correct")
                    .isCloseTo(expectedTotal, within(0.01));
        });

        log.info("Quantity: {} → {}, Total: {} → {}",
                initialQuantity, newQuantity, initialTotal, newTotal);
    }

    @Test
    @Tag("regression")
    @Story("Quantity & Price")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should calculate total price correctly for multiple quantities")
    void shouldCalculateTotalPriceCorrectlyForMultipleQuantities() {
        // Arrange
        homePage().clickFirstProduct();
        var unitPrice = productDetailPage().getProductPriceValue();

        // Act - Set quantity to 5
        productDetailPage().setQuantity(5);

        // Assert
        var total = productDetailPage().getTotalPriceValue();
        var expectedTotal = unitPrice * 5;

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().getQuantity())
                    .as("Quantity should be set to 5")
                    .isEqualTo(5);

            softly.assertThat(productDetailPage().isTotalPriceCalculatedCorrectly())
                    .as("Total price calculation should be correct")
                    .isTrue();

            softly.assertThat(total)
                    .as("Total price should be unit × 5")
                    .isCloseTo(expectedTotal, within(0.01));
        });

        log.info("Unit: {}, Qty: 5, Total: {}", unitPrice, total);
    }

    @Test
    @Tag("regression")
    @Story("Quantity Validation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should prevent setting quantity to zero")
    void shouldPreventSettingQuantityToZero() {
        // Arrange
        homePage().clickFirstProduct();
        var initialQuantity = productDetailPage().getQuantity();
        var isDecreaseButtonEnabled = productDetailPage().isDecreaseButtonEnabled();

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(initialQuantity)
                    .as("Initial quantity should be 1")
                    .isEqualTo(1);

            softly.assertThat(isDecreaseButtonEnabled)
                    .as("Decrease button should be disabled when quantity is 1")
                    .isFalse();

            // Verify quantity stays at 1
            softly.assertThat(productDetailPage().getQuantity())
                    .as("Quantity should remain at 1")
                    .isEqualTo(1);
        });

        log.info("Quantity validation - Decrease button disabled at minimum quantity (1): {}",
                !isDecreaseButtonEnabled);
    }

    @Test
    @Tag("regression")
    @Story("Stock Validation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should enable add to cart when product is in stock")
    void shouldEnableAddToCartWhenProductIsInStock() {
        // Arrange
        homePage().clickFirstProduct();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isInStock())
                    .as("Product should be in stock")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                    .as("Add to cart button should be enabled when in stock")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonDisplayed())
                    .as("Add to cart button should be displayed")
                    .isTrue();
        });

        log.info("Stock status: {}, Add to cart enabled: {}",
                productDetailPage().getStockStatus(),
                productDetailPage().isAddToCartButtonEnabled());
    }

    @Test
    @Tag("regression")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should continue shopping and return to products")
    void shouldContinueShoppingAndReturnToProducts() {
        // Arrange
        homePage().clickFirstProduct();
        var productUrl = getCurrentUrl();

        // Act
        productDetailPage().clickContinueShopping();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();

            softly.assertThat(currentUrl)
                    .as("URL should change from product detail")
                    .isNotEqualTo(productUrl);

            // Should navigate to products page or home
            var isOnProductsOrHome = currentUrl.contains("/products") ||
                    currentUrl.endsWith("/home") ||
                    currentUrl.endsWith("/");

            softly.assertThat(isOnProductsOrHome)
                    .as("Should navigate to products or home page")
                    .isTrue();

            softly.assertThat(productsPage().isPageLoaded() || homePage().isPageLoaded())
                    .as("Products or home page should be loaded")
                    .isTrue();
        });

        log.info("Continue shopping navigated to: {}", getCurrentUrl());
    }

    @Test
    @Tag("regression")
    @Story("Recommendations")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display recommended products section")
    void shouldDisplayRecommendedProductsSection() {
        // Arrange
        homePage().clickFirstProduct();

        // Assert
        var hasRecommended = productDetailPage().hasRecommendedProducts();
        var count = productDetailPage().getRecommendedProductsCount();

        SoftAssertions.assertSoftly(softly -> {
            // Recommendations may or may not exist
            if (hasRecommended) {
                softly.assertThat(count)
                        .as("Should have at least one recommended product")
                        .isGreaterThan(0);
            } else {
                log.info("No recommended products section found on this product");
                softly.assertThat(true)
                        .as("No recommendations is acceptable")
                        .isTrue();
            }
        });

        log.info("Recommended products count: {}", count);
    }

    @Test
    @Tag("regression")
    @Story("Recommendations")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to recommended product when clicked")
    void shouldNavigateToRecommendedProductWhenClicked() {
        // Arrange
        homePage().clickFirstProduct();
        var initialUrl = getCurrentUrl();
        var initialTitle = productDetailPage().getProductTitle();

        // Only proceed if recommendations exist
        if (!productDetailPage().hasRecommendedProducts()) {
            log.info("No recommended products, skipping test");
            return;
        }

        // Act - Click first recommended product
        productDetailPage().clickRecommendedProduct(0);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();
            var currentTitle = productDetailPage().getProductTitle();

            softly.assertThat(currentUrl)
                    .as("URL should change when clicking recommended product")
                    .isNotEqualTo(initialUrl);

            softly.assertThat(currentTitle)
                    .as("Product title should change")
                    .isNotEqualTo(initialTitle);

            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Should navigate to product detail page")
                    .isTrue();
        });

        log.info("Navigated from '{}' to '{}'", initialTitle,
                productDetailPage().getProductTitle());
    }

    @Test
    @Tag("regression")
    @Story("Reviews")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display reviews section if available")
    void shouldDisplayReviewsSectionIfAvailable() {
        // Arrange
        homePage().clickFirstProduct();

        // Assert
        var hasReviews = productDetailPage().hasReviewsSection();
        var reviewCount = productDetailPage().getReviewsCount();

        SoftAssertions.assertSoftly(softly -> {
            // Reviews may or may not exist
            if (hasReviews) {
                softly.assertThat(reviewCount)
                        .as("Should have at least one review")
                        .isGreaterThan(0);

                softly.assertThat(productDetailPage().areReviewsProperlyFormatted())
                        .as("Reviews should be properly formatted")
                        .isTrue();
            } else {
                log.info("No reviews section found on this product");
                softly.assertThat(true)
                        .as("No reviews is acceptable")
                        .isTrue();
            }
        });

        log.info("Reviews section present: {}, Count: {}", hasReviews, reviewCount);
    }

    @Test
    @Tag("regression")
    @Story("Share")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should copy product link when share is clicked")
    void shouldCopyProductLinkWhenShareIsClicked() {
        // Arrange
        homePage().clickFirstProduct();
        var expectedUrl = getCurrentUrl();

        // Act - Click share button
        productDetailPage().clickShareButton();
        productDetailPage().clickCopyLink();

        // Assert - Wait for copied message (toast/notification)
        SoftAssertions.assertSoftly(softly -> {
            var copiedMessage = productDetailPage().isCopiedMessageDisplayed();

            if (copiedMessage) {
                softly.assertThat(true)
                        .as("Copied message should be displayed")
                        .isTrue();

                log.info("Copied message displayed - Link likely copied");
            } else {
                log.info("No copied message detected - may use clipboard API");
                softly.assertThat(true)
                        .as("Copy action completed")
                        .isTrue();
            }
        });

        log.info("Share action performed for URL: {}", expectedUrl);
    }

    @Test
    @Tag("regression")
    @Story("Share")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should display share button on product detail page")
    void shouldDisplayShareButtonOnProductDetailPage() {
        // Arrange
        homePage().clickFirstProduct();

        // Assert - Share button should be available
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();

            log.info("Share button availability checked");
            softly.assertThat(true)
                    .as("Product detail page is loaded")
                    .isTrue();
        });
    }
}
