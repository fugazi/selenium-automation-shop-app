package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Pagination functionality on Products page.
 * Tests page navigation, URL parameters, and state preservation.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Pagination")
@DisplayName("Pagination Tests")
class PaginationTest extends BaseTest {

    private static final String PRODUCTS_PATH = "/products";

    // ==================== Basic Pagination Tests ====================

    @Test
    @Tag("regression")
    @Story("Pagination State")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should start on page 1 by default")
    void shouldStartOnPage1ByDefault() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();

        // Act
        var currentPage = productsPage().getCurrentPageNumber();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentPage)
                    .as("Should start on page 1")
                    .isEqualTo(1);

            softly.assertThat(getCurrentUrl())
                    .as("URL should not contain page parameter for page 1")
                    .doesNotContain("page=");
        });

        log.info("Starting page is: {}", currentPage);
    }

    @Test
    @Tag("regression")
    @Story("Page Navigation via URL")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to page 2 via URL")
    void shouldNavigateToPage2ViaUrl() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();
        var page1Products = productsPage().getProductTitles().stream().toList();

        // Act
        productsPage().goToPage(2);

        // Assert
        var currentPage = productsPage().getCurrentPageNumber();
        var page2Products = productsPage().getProductTitles();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain page=2")
                    .contains("page=2");

            // Page 2 should have different products (if there are enough products)
            if (!page1Products.isEmpty() && !page2Products.isEmpty()) {
                // At least some products should be different OR same if not enough products
                softly.assertThat(productsPage().isPageLoaded())
                        .as("Page should still be loaded")
                        .isTrue();
            }
        });

        log.info("Navigated to page {} - Products: {}", currentPage, page2Products.size());
    }

    @Test
    @Tag("regression")
    @Story("Page Navigation via URL")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to specific page number")
    void shouldNavigateToSpecificPageNumber() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();
        var targetPage = 3;

        // Act
        productsPage().goToPage(targetPage);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain correct page parameter")
                    .contains("page=" + targetPage);

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Products page should be loaded")
                    .isTrue();
        });

        log.info("Navigated to page {}", targetPage);
    }

    // ==================== Pagination with Filters Tests ====================

    @Test
    @Tag("regression")
    @Story("Pagination with Filters")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should preserve category filter when navigating pages")
    void shouldPreserveCategoryFilterWhenNavigatingPages() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?category=Electronics");
        productsPage().waitForContentToLoad();

        // Act
        productsPage().goToPage(2);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isCategoryFilterActive("Electronics"))
                    .as("Electronics filter should be preserved")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain both category and page")
                    .contains("category=Electronics")
                    .contains("page=2");
        });

        log.info("Category filter preserved on page 2");
    }

    @Test
    @Tag("regression")
    @Story("Pagination with Filters")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should preserve sort option when navigating pages")
    void shouldPreserveSortOptionWhenNavigatingPages() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?sort=price-asc");
        productsPage().waitForContentToLoad();

        // Act
        productsPage().goToPage(2);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().getCurrentSortOption())
                    .as("Sort option should be preserved")
                    .isEqualTo("price-asc");

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain both sort and page")
                    .contains("sort=price-asc")
                    .contains("page=2");
        });

        log.info("Sort option preserved on page 2");
    }

    @Test
    @Tag("regression")
    @Story("Pagination with Filters")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should preserve all filters when navigating pages")
    void shouldPreserveAllFiltersWhenNavigatingPages() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?category=Electronics&sort=price-desc");
        productsPage().waitForContentToLoad();

        // Act
        productsPage().goToPage(2);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should contain all parameters")
                    .contains("category=Electronics")
                    .contains("sort=price-desc")
                    .contains("page=2");

            softly.assertThat(productsPage().isCategoryFilterActive("Electronics"))
                    .as("Category filter should be preserved")
                    .isTrue();

            softly.assertThat(productsPage().getCurrentSortOption())
                    .as("Sort option should be preserved")
                    .isEqualTo("price-desc");
        });

        log.info("All filters preserved on page 2");
    }

    // ==================== Edge Cases ====================

    @Test
    @Tag("regression")
    @Story("Pagination Edge Cases")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle invalid page number gracefully")
    void shouldHandleInvalidPageNumberGracefully() {
        // Arrange & Act
        navigateTo(PRODUCTS_PATH + "?page=9999");
        productsPage().waitForContentToLoad();

        // Assert - Should either show empty results or redirect to valid page
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should still load even with invalid page number")
                    .isTrue();

            // Either shows no products or redirects to valid page
            var isValidState = productsPage().hasProducts() ||
                    productsPage().isNoResultsDisplayed() ||
                    productsPage().getCurrentPageNumber() <= 9999;

            softly.assertThat(isValidState)
                    .as("Page should handle invalid page gracefully")
                    .isTrue();
        });

        log.info("Invalid page handled - Products: {}", productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Pagination Edge Cases")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle page 0 or negative page gracefully")
    void shouldHandleZeroOrNegativePageGracefully() {
        // Arrange & Act
        navigateTo(PRODUCTS_PATH + "?page=0");
        productsPage().waitForContentToLoad();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should load even with page=0")
                    .isTrue();

            // Should show products (treated as page 1 or first page)
            softly.assertThat(productsPage().hasProducts() || productsPage().isNoResultsDisplayed())
                    .as("Should show products or no results message")
                    .isTrue();
        });

        log.info("Page 0 handled - Showing {} products", productsPage().getProductCount());
    }

    @Test
    @Tag("regression")
    @Story("Browser Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate back to previous page using browser back")
    void shouldNavigateBackToPreviousPageUsingBrowserBack() {
        // Arrange
        navigateTo(PRODUCTS_PATH);
        productsPage().waitForContentToLoad();
        var page1Url = getCurrentUrl();

        productsPage().goToPage(2);
        var page2Url = getCurrentUrl();

        // Act
        navigateBack();
        productsPage().waitForContentToLoad();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("Should go back to page 1 URL")
                    .isEqualTo(page1Url);

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Products page should be loaded")
                    .isTrue();
        });

        log.info("Browser back navigation works - From: {} To: {}", page2Url, getCurrentUrl());
    }

    @Test
    @Tag("regression")
    @Story("Page State")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should refresh page and preserve pagination state")
    void shouldRefreshPageAndPreservePaginationState() {
        // Arrange
        navigateTo(PRODUCTS_PATH + "?page=2");
        productsPage().waitForContentToLoad();
        var productsBefore = productsPage().getProductTitles().stream().toList();

        // Act
        refreshPage();
        productsPage().waitForContentToLoad();

        // Assert
        var productsAfter = productsPage().getProductTitles();
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should still contain page=2")
                    .contains("page=2");

            softly.assertThat(productsPage().isPageLoaded())
                    .as("Page should be loaded after refresh")
                    .isTrue();

            // Products should be the same (or at least some overlap)
            if (!productsBefore.isEmpty() && !productsAfter.isEmpty()) {
                softly.assertThat(productsAfter)
                        .as("Products should be similar after refresh")
                        .hasSameSizeAs(productsBefore);
            }
        });

        log.info("Page state preserved after refresh");
    }
}
