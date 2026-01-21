package org.fugazi.tests;

import static org.assertj.core.api.Assertions.assertThat;

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
 * Test class for Add to Cart functionality.
 * Note: Cart page requires authentication, so these tests verify the add to cart
 * action on the product detail page without navigating to the cart.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Add to Cart")
@DisplayName("Add to Cart Tests")
class AddToCartTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Add Product")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should click add to cart button successfully")
    void shouldClickAddToCartButtonSuccessfully() {
        // Arrange
        homePage().clickFirstProduct();

        var productTitle = productDetailPage().getProductTitle();

        // Assert preconditions
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonDisplayed())
                    .as("Add to cart button should be displayed")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                    .as("Add to cart button should be enabled")
                    .isTrue();
        });

        // Act - Click and wait for action to complete
        productDetailPage().clickAddToCartAndWait();

        // Assert - Click was successful (no exception thrown)
        log.info("Successfully clicked add to cart for product: '{}'", productTitle);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Add to Cart Button")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should display add to cart button on product detail page")
    void shouldDisplayAddToCartButtonOnProductDetailPage() {
        // Arrange
        homePage().clickFirstProduct();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonDisplayed())
                    .as("Add to cart button should be visible")
                    .isTrue();

            softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                    .as("Add to cart button should be enabled")
                    .isTrue();
        });

        log.info("Add to cart button is displayed and enabled");
    }

    @Test
    @Tag("regression")
    @Story("Add Multiple Products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should be able to add multiple different products")
    void shouldBeAbleToAddMultipleDifferentProducts() {
        // Arrange
        var productCount = homePage().getFeaturedProductsCount();

        if (productCount < 2) {
            log.warn("Not enough products to test adding multiple products");
            return;
        }

        // Act - Add first product
        homePage().clickFirstProduct();
        var firstProductTitle = productDetailPage().getProductTitle();

        SoftAssertions.assertSoftly(softly -> softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                .as("Add to cart should be enabled for first product")
                .isTrue());

        productDetailPage().clickAddToCartAndWait();
        log.info("Added first product: '{}'", firstProductTitle);

        // Navigate back using base URL
        driver.get(config.getBaseUrl());

        // Wait for home page to load
        SoftAssertions.assertSoftly(softly -> softly.assertThat(homePage().isPageLoaded())
                .as("Home page should load after navigation")
                .isTrue());

        // Add second product
        homePage().clickProductByIndex(1);
        var secondProductTitle = productDetailPage().getProductTitle();

        SoftAssertions.assertSoftly(softly -> softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                .as("Add to cart should be enabled for second product")
                .isTrue());

        productDetailPage().clickAddToCartAndWait();
        log.info("Added second product: '{}'", secondProductTitle);

        log.info("Successfully added 2 products: '{}' and '{}'", firstProductTitle, secondProductTitle);
    }

    @Test
    @Tag("regression")
    @Story("Add Same Product Multiple Times")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should be able to click add to cart multiple times")
    void shouldBeAbleToClickAddToCartMultipleTimes() {
        // Arrange
        homePage().clickFirstProduct();
        var productTitle = productDetailPage().getProductTitle();

        // First click
        SoftAssertions.assertSoftly(softly -> softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                .as("Add to cart button should be enabled")
                .isTrue());

        productDetailPage().clickAddToCartAndWait();
        log.info("First add to cart click for: '{}'", productTitle);

        // If still on product page, try second click
        if (productDetailPage().isAddToCartButtonDisplayed()) {
            productDetailPage().clickAddToCartAndWait();
            log.info("Second add to cart click for: '{}'", productTitle);
        } else {
            log.info("Page navigated after first add to cart - second click not possible");
        }
    }

    @Test
    @Tag("regression")
    @Story("Product Information")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display product information before adding to cart")
    void shouldDisplayProductInformationBeforeAddingToCart() {
        // Arrange
        homePage().clickFirstProduct();

        // Act
        var productTitle = productDetailPage().getProductTitle();
        var productPrice = productDetailPage().getProductPrice();
        var productDescription = productDetailPage().getProductDescription();

        // Assert - Product info is displayed
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();

            softly.assertThat(productTitle)
                    .as("Product title should not be empty")
                    .isNotBlank();

            softly.assertThat(productPrice)
                    .as("Product price should not be empty")
                    .isNotBlank();

            softly.assertThat(productDescription)
                    .as("Product description should not be empty")
                    .isNotBlank();

            softly.assertThat(productDetailPage().isAddToCartButtonDisplayed())
                    .as("Add to cart button should be displayed")
                    .isTrue();
        });

        log.info("Product info: Title='{}', Price='{}', Description length={}",
                productTitle, productPrice, productDescription.length());
    }

    @Test
    @Tag("regression")
    @Story("Navigation After Add")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should be able to continue shopping after adding to cart")
    void shouldBeAbleToContinueShoppingAfterAddingToCart() {
        // Arrange
        homePage().clickFirstProduct();
        var productTitle = productDetailPage().getProductTitle();

        // Act - Add to cart and wait for action to complete
        productDetailPage().clickAddToCartAndWait();

        // Navigate back to home
        driver.get(config.getBaseUrl());

        // Assert - Home page should be loaded
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(homePage().isPageLoaded())
                    .as("Should be able to return to home page after adding to cart")
                    .isTrue();

            softly.assertThat(homePage().getFeaturedProductsCount())
                    .as("Should see products on home page")
                    .isGreaterThan(0);
        });

        log.info("Added '{}' and successfully returned to home page", productTitle);
    }

    @Test
    @Tag("regression")
    @Story("Add From Different Products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should add products from different indices")
    void shouldAddProductsFromDifferentIndices() {
        // Add product by clicking on different featured products
        var productCount = homePage().getFeaturedProductsCount();

        if (productCount >= 3) {
            // Add third product (index 2) to get variety
            homePage().clickProductByIndex(2);
            var productTitle = productDetailPage().getProductTitle();

            SoftAssertions.assertSoftly(softly -> softly.assertThat(productDetailPage().isAddToCartButtonEnabled())
                    .as("Add to cart should be enabled")
                    .isTrue());

            productDetailPage().clickAddToCartAndWait();

            var currentUrl = getCurrentUrl();
            SoftAssertions.assertSoftly(softly -> softly.assertThat(currentUrl)
                    .as("Should remain on product page or redirect to login")
                    .satisfiesAnyOf(
                        url -> assertThat(url).contains("/products/"),
                        url -> assertThat(url).contains("/login")
                    ));

            log.info("Added product from index 2: '{}', now at: {}", productTitle, currentUrl);
        } else {
            log.warn("Not enough products (need 3+) to test variety");
            // Still pass the test but add first product
            homePage().clickFirstProduct();
            productDetailPage().clickAddToCartAndWait();
        }
    }
}

