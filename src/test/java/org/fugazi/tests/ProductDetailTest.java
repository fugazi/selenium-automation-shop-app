package org.fugazi.tests;

import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Product Detail Page functionality.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Product Detail")
@DisplayName("Product Detail Tests")
class ProductDetailTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Product Information")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should display product details")
    void shouldDisplayProductDetails() {
        // Arrange - Navigate to a product
        homePage().clickFirstProduct();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();

            softly.assertThat(productDetailPage().getProductTitle())
                    .as("Product title should not be empty")
                    .isNotBlank();

            softly.assertThat(productDetailPage().getProductPrice())
                    .as("Product price should not be empty")
                    .isNotBlank();
        });
    }

    @Test
    @Tag("regression")
    @Story("Product Information")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should show correct price format")
    void shouldShowCorrectPriceFormat() {
        // Arrange
        homePage().clickFirstProduct();

        // Act
        var priceText = productDetailPage().getProductPrice();
        var priceValue = productDetailPage().getProductPriceValue();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(priceText)
                    .as("Price should contain currency symbol or number")
                    .containsPattern("[$€£]?\\d+([.,]\\d{2})?");

            softly.assertThat(priceValue)
                    .as("Price value should be greater than 0")
                    .isGreaterThan(0.0);
        });

        log.info("Product price: {} (value: {})", priceText, priceValue);
    }

    @Test
    @Tag("regression")
    @Story("Product Image")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display product image")
    void shouldDisplayProductImage() {
        // Arrange
        homePage().clickFirstProduct();

        // Act
        var imageSrc = productDetailPage().getProductImageSrc();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isProductImageDisplayed())
                    .as("Product image should be displayed")
                    .isTrue();

            softly.assertThat(imageSrc)
                    .as("Image src should not be empty")
                    .isNotBlank();
        });

        log.info("Product image src: {}", imageSrc);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Add to Cart Button")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should display add to cart button")
    void shouldDisplayAddToCartButton() {
        // Arrange
        homePage().clickFirstProduct();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isAddToCartButtonDisplayed())
                    .as("Add to cart button should be displayed")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                    .as("Add to cart button should be enabled")
                    .isTrue();
        });
    }

    @Test
    @Tag("regression")
    @Story("Product Description")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should display product description if available")
    void shouldDisplayProductDescriptionIfAvailable() {
        // Arrange
        homePage().clickFirstProduct();

        // Act
        var description = productDetailPage().getProductDescription();

        // Assert - Description might not always be present
        if (!description.isEmpty()) {
            SoftAssertions.assertSoftly(softly -> softly.assertThat(description)
                    .as("Product description should not be blank when present")
                    .isNotBlank());

            log.info("Product description: {}", description);
        } else {
            log.info("Product description is not available");
        }
    }

    @Test
    @Tag("regression")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should be able to go back to home page")
    void shouldBeAbleToGoBackToHomePage() {
        // Arrange
        homePage().clickFirstProduct();

        SoftAssertions.assertSoftly(softly -> softly.assertThat(productDetailPage().isPageLoaded())
                .as("Product detail page should be loaded initially")
                .isTrue());

        // Act - Click logo to go home
        productDetailPage().header().clickLogo();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(homePage().isPageLoaded())
                .as("Should navigate back to home page")
                .isTrue());
    }

    @Test
    @Tag("regression")
    @Story("Multiple Products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate between different products")
    void shouldNavigateBetweenDifferentProducts() {
        // Arrange - Go to first product
        homePage().clickFirstProduct();
        var firstProductTitle = productDetailPage().getProductTitle();

        // Act - Go back and select second product
        productDetailPage().header().clickLogo();
        var productCount = homePage().getFeaturedProductsCount();

        if (productCount >= 2) {
            homePage().clickProductByIndex(1);
            var secondProductTitle = productDetailPage().getProductTitle();

            // Assert
            SoftAssertions.assertSoftly(softly -> softly.assertThat(secondProductTitle)
                    .as("Second product should have different title")
                    .isNotEqualTo(firstProductTitle));

            log.info("First product: '{}', Second product: '{}'", firstProductTitle, secondProductTitle);
        } else {
            log.warn("Not enough products to test navigation between products");
        }
    }

    @Test
    @Tag("regression")
    @Story("Stock Status")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should show stock status")
    void shouldShowStockStatus() {
        // Arrange
        homePage().clickFirstProduct();

        // Act
        var isInStock = productDetailPage().isInStock();

        // Assert - Just verify we can determine stock status
        log.info("Product in stock: {}", isInStock);

        // If in stock, add to cart should be enabled
        if (isInStock) {
            SoftAssertions.assertSoftly(softly -> softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                    .as("Add to cart should be enabled for in-stock items")
                    .isTrue());
        }
    }
}

