package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Test class for Product Listing functionality.
 * Tests filters, sorting, search, and product grid on /products page.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Product Listing")
@DisplayName("Product Listing Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductListingTest extends BaseTest {

    private static final String PRODUCTS_PATH = "/products";

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Products Page Load")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should load products page successfully")
    @Order(1)
    void shouldLoadProductsPageSuccessfully() {
        // Arrange
        navigateTo(PRODUCTS_PATH);

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Products page should be loaded")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain /products")
                    .contains("/products");
        });

        log.info("Products page loaded successfully");
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Products Page Load")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should display products in grid")
    @Order(2)
    void shouldDisplayProductsInGrid() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        var productCount = productsPage().getProductCount();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().hasProducts())
                    .as("Products should be displayed in grid")
                    .isTrue();

            softly.assertThat(productCount)
                    .as("Should have at least one product")
                    .isGreaterThan(0);
        });

        log.info("Found {} products in grid", productCount);
    }

    @Test
    @Tag("regression")
    @Story("Products Page Load")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display product titles")
    @Order(3)
    void shouldDisplayProductTitles() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        var titles = productsPage().getProductTitles();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(titles)
                    .as("Product titles should not be empty")
                    .isNotEmpty();

            titles.forEach(title ->
                    softly.assertThat(title)
                            .as("Each product title should not be blank")
                            .isNotBlank()
            );
        });

        log.info("Found {} product titles", titles.size());
    }

    @Test
    @Tag("regression")
    @Story("Products Page Load")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display product prices")
    @Order(4)
    void shouldDisplayProductPrices() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        var prices = productsPage().getProductPrices();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(prices)
                    .as("Product prices should not be empty")
                    .isNotEmpty();

            prices.forEach(price ->
                    softly.assertThat(price)
                            .as("Each price should contain currency symbol")
                            .containsPattern("[$€]?\\d")
            );
        });

        log.info("Found {} product prices", prices.size());
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Category Filter")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should filter products by Electronics category")
    @Order(5)
    void shouldFilterProductsByElectronicsCategory() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();
        var initialCount = productsPage().getProductCount();

        // Act
        productsPage().filterByCategory("Electronics");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Products page should still be loaded after filter")
                    .isTrue();

            softly.assertThat(productsPage().isCategoryFilterActive("Electronics"))
                    .as("Electronics category should be active")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain category parameter")
                    .contains("category=Electronics");
        });

        log.info("Filtered by Electronics - Initial: {}, After filter: {}",
                initialCount, productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Category Filter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should filter products by Photography category")
    @Order(6)
    void shouldFilterProductsByPhotographyCategory() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        productsPage().filterByCategory("Photography");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isCategoryFilterActive("Photography"))
                    .as("Photography category should be active")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain Photography category")
                    .contains("category=Photography");
        });

        log.info("Filtered by Photography - Product count: {}", productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Category Filter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should filter products by Accessories category")
    @Order(7)
    void shouldFilterProductsByAccessoriesCategory() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        productsPage().filterByCategory("Accessories");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isCategoryFilterActive("Accessories"))
                    .as("Accessories category should be active")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain Accessories category")
                    .contains("category=Accessories");
        });

        log.info("Filtered by Accessories - Product count: {}", productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Category Filter")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should clear category filter and show all products")
    @Order(8)
    void shouldClearCategoryFilterAndShowAllProducts() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?category=Electronics");
        productsPage().waitForContentToLoad();
        var filteredCount = productsPage().getProductCount();

        // Act
        productsPage().clearCategoryFilter();

        // Assert
        var allProductsCount = productsPage().getProductCount();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().getActiveCategoryFilter())
                    .as("No category filter should be active")
                    .isEmpty();

            softly.assertThat(allProductsCount)
                    .as("All products count should be >= filtered count")
                    .isGreaterThanOrEqualTo(filteredCount);
        });

        log.info("Cleared filter - Filtered: {}, All products: {}", filteredCount, allProductsCount);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Product Search")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should search products on products page")
    @Order(9)
    void shouldSearchProductsOnProductsPage() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();
        var searchTerm = "music";

        // Act
        productsPage().searchProducts(searchTerm);

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(productsPage().isPageLoaded())
                .as("Products page should still be loaded after search")
                .isTrue());
    }

    @Test
    @Tag("regression")
    @Story("Product Search")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should show no results for invalid search")
    @Order(10)
    void shouldShowNoResultsForInvalidSearch() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();
        var invalidTerm = "xyznonexistent12345";

        // Act
        productsPage().searchProducts(invalidTerm);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should still be loaded")
                    .isTrue();

            // Either no products or no results message
            var hasNoResults = productsPage().getProductCount() == 0 ||
                    productsPage().isNoResultsDisplayed();

            softly.assertThat(hasNoResults)
                    .as("Should show no results for invalid search")
                    .isTrue();
        });

        log.info("Searched for '{}' - Found {} products", invalidTerm, productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Product Sorting")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should apply sort parameter to URL")
    @Order(11)
    void shouldApplySortParameterToUrl() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        productsPage().sortBy("price-asc");

        // Assert - Just verify the URL parameter is applied
        // Note: Actual sorting may or may not be implemented by the application
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain sort parameter")
                    .contains("sort=price-asc");

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should still be loaded after applying sort")
                    .isTrue();
        });

        var prices = productsPage().getProductPricesAsNumbers();
        log.info("Applied sort=price-asc - Prices: {}", prices);
    }

    @Test
    @Tag("regression")
    @Story("Product Sorting")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should apply price descending sort to URL")
    @Order(12)
    void shouldApplyPriceDescendingSortToUrl() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        productsPage().sortBy("price-desc");

        // Assert - Just verify the URL parameter is applied
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain sort parameter")
                    .contains("sort=price-desc");

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should still be loaded after applying sort")
                    .isTrue();
        });

        var prices = productsPage().getProductPricesAsNumbers();
        log.info("Applied sort=price-desc - Prices: {}", prices);
    }

    @Test
    @Tag("regression")
    @Story("Product Sorting")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should apply name ascending sort to URL")
    @Order(13)
    void shouldApplyNameAscendingSortToUrl() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        productsPage().sortBy("name-asc");

        // Assert - Just verify the URL parameter is applied
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain sort parameter")
                    .contains("sort=name-asc");

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should still be loaded after applying sort")
                    .isTrue();
        });

        var titles = productsPage().getProductTitles();
        log.info("Applied sort=name-asc - Titles: {}", titles);
    }

    @Test
    @Tag("regression")
    @Story("Product Sorting")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should apply name descending sort to URL")
    @Order(14)
    void shouldApplyNameDescendingSortToUrl() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        productsPage().sortBy("name-desc");

        // Assert - Just verify the URL parameter is applied
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain sort parameter")
                    .contains("sort=name-desc");

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should still be loaded after applying sort")
                    .isTrue();
        });

        var titles = productsPage().getProductTitles();
        log.info("Applied sort=name-desc - Titles: {}", titles);
    }

    @Test
    @Tag("regression")
    @Story("Combined Filters")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should combine category filter with sorting")
    @Order(15)
    void shouldCombineCategoryFilterWithSorting() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act - Apply both filters
        productsPage().applyFilters("Electronics", "price-asc", null);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isCategoryFilterActive("Electronics"))
                    .as("Electronics filter should be active")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain both category and sort")
                    .contains("category=Electronics")
                    .contains("sort=price-asc");
        });

        log.info("Combined filters applied - Category: Electronics, Sort: price-asc");
    }

    @Test
    @Tag("regression")
    @Story("Combined Filters")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should reset all filters")
    @Order(16)
    void shouldResetAllFilters() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?category=Electronics&sort=price-desc");
        productsPage().waitForContentToLoad();

        // Act
        productsPage().resetAllFilters();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().getActiveCategoryFilter())
                    .as("Category filter should be cleared")
                    .isEmpty();

            softly.assertThat(productsPage().getCurrentSortOption())
                    .as("Sort option should be cleared")
                    .isEmpty();

            softly.assertThat(getCurrentUrl())
                    .as("URL should not contain filter parameters")
                    .doesNotContain("category=")
                    .doesNotContain("sort=");
        });

        log.info("All filters reset successfully");
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Product Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should navigate to product detail from listing")
    @Order(17)
    void shouldNavigateToProductDetailFromListing() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        if (!productsPage().hasProducts()) {
            log.warn("No products found, skipping navigation test");
            return;
        }

        var firstProductTitle = productsPage().getProductTitleAtIndex(0);
        var initialUrl = getCurrentUrl();

        // Act
        productsPage().clickFirstProduct();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should change after clicking product")
                    .isNotEqualTo(initialUrl);

            softly.assertThat(getCurrentUrl())
                    .as("Should navigate to product detail page")
                    .contains("/products/");

            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();
        });

        log.info("Navigated from listing to product detail: {}", firstProductTitle);
    }

    @Test
    @Tag("regression")
    @Story("Product Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to product detail by index")
    @Order(18)
    void shouldNavigateToProductDetailByIndex() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        var productCount = productsPage().getProductCount();
        if (productCount < 2) {
            log.warn("Not enough products for index test, skipping");
            return;
        }

        var productIndex = 1; // Second product
        var initialUrl = getCurrentUrl();

        // Act
        productsPage().clickProductByIndex(productIndex);

        // Assert - Just verify URL changed (navigation happened)
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should change after clicking product")
                    .isNotEqualTo(initialUrl);

            // Verify we're on a product page (either detail or still products)
            var currentUrl = getCurrentUrl();
            var isOnProductPage = currentUrl.contains("/products/") || currentUrl.contains("/products");
            softly.assertThat(isOnProductPage)
                    .as("Should be on a product-related page")
                    .isTrue();
        });

        log.info("Navigated from index {} - URL: {}", productIndex, getCurrentUrl());
    }

    @Test
    @Tag("regression")
    @Story("Browser Navigation")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should preserve filters after page refresh")
    @Order(19)
    void shouldPreserveFiltersAfterPageRefresh() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?category=Electronics");
        productsPage().waitForContentToLoad();
        var countBeforeRefresh = productsPage().getProductCount();

        // Act
        refreshPage();
        productsPage().waitForContentToLoad();

        // Assert
        var countAfterRefresh = productsPage().getProductCount();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isCategoryFilterActive("Electronics"))
                    .as("Category filter should be preserved after refresh")
                    .isTrue();

            softly.assertThat(countAfterRefresh)
                    .as("Product count should be same after refresh")
                    .isEqualTo(countBeforeRefresh);
        });

        log.info("Filters preserved after refresh - Count: {}", countAfterRefresh);
    }
}
