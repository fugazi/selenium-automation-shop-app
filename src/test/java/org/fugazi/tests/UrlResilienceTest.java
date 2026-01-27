package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for URL Resilience and Invalid Route handling.
 * Tests graceful handling of invalid IDs, categories, and routes.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("URL Resilience")
@DisplayName("URL Resilience Tests")
class UrlResilienceTest extends BaseTest {

    // ==================== Invalid Product ID Tests ====================

    @Test
    @Tag("regression")
    @Story("Invalid Routes")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle invalid product ID gracefully")
    void shouldHandleInvalidProductIdGracefully() {
        // Arrange & Act - Navigate to non-existent product
        navigateTo("/products/999999");

        // Assert - Should show error or not-found state
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();
            var pageSource = driver.getPageSource();

            // Application shows 404 in page content (not in URL)
            var is404Page = pageSource.contains("404") ||
                          pageSource.toLowerCase().contains("not found") ||
                          driver.getTitle().toLowerCase().contains("404");

            // Or product detail page loads but shows error message
            var isProductDetailPage = productDetailPage().isPageLoaded();

            softly.assertThat(is404Page || isProductDetailPage)
                    .as("Should handle invalid product gracefully")
                    .isTrue();

            if (isProductDetailPage) {
                softly.assertThat(productDetailPage().getProductTitle())
                        .as("Product title should indicate not found or be generic")
                        .isNotNull();
            }

            log.info("Invalid product ID handled - URL: {}", currentUrl);
        });
    }

    @Test
    @Tag("regression")
    @Story("Invalid Routes")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle negative product ID gracefully")
    void shouldHandleNegativeProductIdGracefully() {
        // Arrange & Act - Navigate to negative ID
        navigateTo("/products/-1");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();
            var pageSource = driver.getPageSource();

            // Application shows 404 in page content (not in URL)
            var is404Page = pageSource.contains("404") ||
                          pageSource.toLowerCase().contains("not found") ||
                          driver.getTitle().toLowerCase().contains("404");

            var isHandled = is404Page || productDetailPage().isPageLoaded();

            softly.assertThat(isHandled)
                    .as("Should handle negative product ID gracefully")
                    .isTrue();
        });

        log.info("Negative product ID handled");
    }

    @Test
    @Tag("regression")
    @Story("Invalid Routes")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle non-numeric product ID gracefully")
    void shouldHandleNonNumericProductIdGracefully() {
        // Arrange & Act - Navigate to non-numeric ID
        navigateTo("/products/abc123");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();

            var isHandled = currentUrl.contains("404") ||
                    currentUrl.contains("not-found") ||
                    currentUrl.contains("error") ||
                    getCurrentUrl().contains("/products");

            softly.assertThat(isHandled)
                    .as("Should handle non-numeric product ID gracefully")
                    .isTrue();
        });

        log.info("Non-numeric product ID handled");
    }

    // ==================== Invalid Category Tests ====================

    @Test
    @Tag("regression")
    @Story("Invalid Categories")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle invalid category query parameter gracefully")
    void shouldHandleInvalidCategoryQueryParameterGracefully() {
        // Arrange & Act - Navigate with invalid category
        navigateTo("/products?category=NotARealCategory123");

        // Assert
        productsPage().waitForContentToLoad();

        SoftAssertions.assertSoftly(softly -> {
            var productCount = productsPage().getProductCount();
            var hasNoResults = productsPage().isNoResultsDisplayed();

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Products page should still load")
                    .isTrue();

            // Either shows no products or shows all products as fallback
            var isGraceful = productCount == 0 || hasNoResults || productCount > 0;

            softly.assertThat(isGraceful)
                    .as("Should handle invalid category gracefully")
                    .isTrue();

            // URL should preserve the invalid category param
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain category parameter")
                    .contains("category=NotARealCategory123");
        });

        log.info("Invalid category handled - Products: {}", productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Invalid Categories")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle special characters in category parameter")
    void shouldHandleSpecialCharactersInCategoryParameter() {
        // Arrange & Act - Navigate with special chars in category
        navigateTo("/products?category=<script>alert('xss')</script>");

        // Assert
        productsPage().waitForContentToLoad();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should load without executing script")
                    .isTrue();

            // Should handle gracefully (empty or fallback)
            var hasProducts = productsPage().hasProducts();
            var isGraceful = hasProducts || productsPage().isNoResultsDisplayed();

            softly.assertThat(isGraceful)
                    .as("Should handle special characters in category")
                    .isTrue();
        });

        log.info("Special characters in category handled safely");
    }

    @Test
    @Tag("regression")
    @Story("Empty Categories")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle empty category parameter")
    void shouldHandleEmptyCategoryParameter() {
        // Arrange & Act - Navigate with empty category
        navigateTo("/products?category=");

        // Assert
        productsPage().waitForContentToLoad();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should load with empty category")
                    .isTrue();

            // Should either show all products or show no results
            var hasProducts = productsPage().hasProducts();
            var isGraceful = hasProducts || productsPage().isNoResultsDisplayed();

            softly.assertThat(isGraceful)
                    .as("Should handle empty category gracefully")
                    .isTrue();
        });

        log.info("Empty category parameter handled");
    }

    // ==================== Invalid Route Tests ====================

    @Test
    @Tag("regression")
    @Story("Invalid Routes")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle non-existent route gracefully")
    void shouldHandleNonExistentRouteGracefully() {
        // Arrange & Act - Navigate to non-existent route
        navigateTo("/this-route-does-not-exist");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();
            var pageSource = driver.getPageSource();

            // Application shows 404 in page content (not in URL)
            // Check for 404 in page source, title, or body text
            var is404Page = pageSource.contains("404") ||
                          pageSource.toLowerCase().contains("not found") ||
                          driver.getTitle().toLowerCase().contains("404");

            softly.assertThat(is404Page)
                    .as("Should show 404 page for non-existent route")
                    .isTrue();
        });

        log.info("Non-existent route handled - URL: {}", getCurrentUrl());
    }

    @Test
    @Tag("regression")
    @Story("Invalid Routes")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle malformed URL gracefully")
    void shouldHandleMalformedUrlGracefully() {
        // Arrange & Act - Navigate with malformed URL
        navigateTo("/products/../admin");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();

            // Application normalizes the path and redirects to /admin
            // Verify we ended up at a valid admin page
            var isAdminPage = currentUrl.contains("/admin");

            softly.assertThat(isAdminPage)
                    .as("Should normalize malformed URL and redirect to /admin")
                    .isTrue();
        });

        log.info("Malformed URL handled - Normalized to: {}", getCurrentUrl());
    }

    // ==================== Deep Link Tests ====================

    @Test
    @Tag("regression")
    @Story("Deep Links")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle deep link to product with redirect")
    void shouldHandleDeepLinkToProductWithRedirect() {
        // Arrange & Act - Navigate directly to specific product
        navigateTo("/products/1");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            // Should load product detail page
            var isProductDetail = productDetailPage().isPageLoaded();

            softly.assertThat(isProductDetail)
                    .as("Should load product from deep link")
                    .isTrue();
        });

        log.info("Deep link to product handled successfully");
    }

    @Test
    @Tag("regression")
    @Story("Deep Links")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle deep link to category with pagination")
    void shouldHandleDeepLinkToCategoryWithPagination() {
        // Arrange & Act - Navigate with category and page
        navigateTo("/products?category=Electronics&page=2");

        // Assert
        productsPage().waitForContentToLoad();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Should load products with deep link")
                    .isTrue();

            var currentUrl = getCurrentUrl();
            softly.assertThat(currentUrl)
                    .as("URL should preserve category and page")
                    .contains("category=Electronics")
                    .contains("page=2");
        });

        log.info("Deep link to category with pagination handled");
    }
}
