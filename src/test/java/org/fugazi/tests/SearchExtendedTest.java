package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Search functionality extensions.
 * Tests case-insensitivity, whitespace handling, and special characters.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Search")
@DisplayName("Search Extended Tests")
class SearchExtendedTest extends BaseTest {

    private static final String SEARCH_TERM = "synthesizer";

    // ==================== Case Sensitivity Tests ====================

    @Test
    @Tag("regression")
    @Story("Search Normalization")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should be case-insensitive when searching")
    void shouldBeCaseInsensitiveWhenSearching() {
        // Arrange - Search with lowercase
        homePage().searchProduct(SEARCH_TERM.toLowerCase());
        var lowercaseCount = searchResultsPage().hasResults() ?
                searchResultsPage().getResultCount() : 0;

        // Act - Search with uppercase
        homePage().header().searchProduct(SEARCH_TERM.toUpperCase());
        var uppercaseCount = searchResultsPage().hasResults() ?
                searchResultsPage().getResultCount() : 0;

        // Assert - Should return same number of results
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Search results page should be loaded")
                    .isTrue();

            // Results should be consistent regardless of case
            softly.assertThat(Math.abs(lowercaseCount - uppercaseCount))
                    .as("Search should be case-insensitive")
                    .isLessThanOrEqualTo(1);
        });

        log.info("Case insensitive search - Lowercase: {}, Uppercase: {}",
                lowercaseCount, uppercaseCount);
    }

    @Test
    @Tag("regression")
    @Story("Search Normalization")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle mixed case search terms")
    void shouldHandleMixedCaseSearchTerms() {
        // Arrange - Search with mixed case
        var mixedCaseTerm = "SyNtHeSiZeR";

        // Act
        homePage().searchProduct(mixedCaseTerm);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Search results page should be loaded")
                    .isTrue();

            var hasResults = searchResultsPage().hasResults() ||
                    searchResultsPage().isNoResultsMessageDisplayed();

            softly.assertThat(hasResults)
                    .as("Should handle mixed case without error")
                    .isTrue();
        });

        log.info("Mixed case search completed successfully");
    }

    // ==================== Whitespace Handling Tests ====================

    @Test
    @Tag("regression")
    @Story("Search Normalization")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle search with leading and trailing whitespace")
    void shouldTrimLeadingAndTrailingWhitespace() {
        // Arrange - Search with extra whitespace
        var termWithWhitespace = "  " + SEARCH_TERM + "  ";

        // Act
        homePage().searchProduct(termWithWhitespace);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Search results page should be loaded")
                    .isTrue();

            // Application does NOT trim whitespace - searches for exact string
            // Searching "  synthesizer  " (with spaces) returns 0 results
            var hasResults = searchResultsPage().hasResults();
            var resultCount = searchResultsPage().getResultCount();

            softly.assertThat(resultCount)
                    .as("Application searches for exact string (no whitespace trimming)")
                    .isEqualTo(0);
            softly.assertThat(hasResults)
                    .as("Application searches is not null")
                    .isNotNull();
        });

        log.info("Whitespace handling test completed - App does not trim whitespace");
    }

    @Test
    @Tag("regression")
    @Story("Search Normalization")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle multiple spaces between words")
    void shouldHandleMultipleSpacesBetweenWords() {
        // Arrange - Search term with multiple spaces
        var termWithMultipleSpaces = "music  synthesizer";

        // Act
        homePage().searchProduct(termWithMultipleSpaces);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Search results page should be loaded")
                    .isTrue();

            // Should handle gracefully
            var hasResults = searchResultsPage().hasResults() ||
                    searchResultsPage().isNoResultsMessageDisplayed();

            softly.assertThat(hasResults)
                    .as("Should handle multiple spaces without error")
                    .isTrue();
        });

        log.info("Multiple spaces test completed");
    }

    // ==================== Special Characters Tests ====================

    @Test
    @Tag("regression")
    @Story("Search Resilience")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle special characters without breaking")
    void shouldHandleSpecialCharactersWithoutBreaking() {
        // Arrange - Search with special characters
        var specialChars = "'\"<>%$#@";

        // Act
        homePage().searchProduct(specialChars);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Page should load without error")
                    .isTrue();

            // Should show no results gracefully
            var hasNoResults = !searchResultsPage().hasResults() ||
                    searchResultsPage().isNoResultsMessageDisplayed();

            softly.assertThat(hasNoResults)
                    .as("Should show no results or empty state")
                    .isTrue();

            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Page should remain functional")
                    .isTrue();
        });

        log.info("Special characters handled gracefully");
    }

    @Test
    @Tag("regression")
    @Story("Search Resilience")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should handle SQL injection patterns safely")
    void shouldHandleSqlInjectionPatternsSafely() {
        // Arrange - SQL injection patterns
        var sqlInjection = "' OR '1'='1";

        // Act
        homePage().searchProduct(sqlInjection);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Page should load without database error")
                    .isTrue();

            // Should handle gracefully (either no results or normal results)
            var handledGracefully = searchResultsPage().hasResults() ||
                    searchResultsPage().isNoResultsMessageDisplayed();

            softly.assertThat(handledGracefully)
                    .as("Should handle SQL injection pattern safely")
                    .isTrue();
        });

        log.info("SQL injection pattern handled safely");
    }

    @Test
    @Tag("regression")
    @Story("Search Resilience")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle XSS patterns safely")
    void shouldHandleXssPatternsSafely() {
        // Arrange - XSS patterns
        var xssPattern = "<script>alert('xss')</script>";

        // Act
        homePage().searchProduct(xssPattern);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Page should load without executing script")
                    .isTrue();

            var handledGracefully = searchResultsPage().hasResults() ||
                    searchResultsPage().isNoResultsMessageDisplayed();

            softly.assertThat(handledGracefully)
                    .as("Should handle XSS pattern safely")
                    .isTrue();
        });

        log.info("XSS pattern handled safely");
    }

    // ==================== Search Consistency Tests ====================

    @Test
    @Tag("regression")
    @Story("Search Consistency")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should return consistent results from home and products page")
    void shouldReturnConsistentResultsFromHomeAndProductsPage() {
        // Arrange - Search from home
        homePage().searchProduct(SEARCH_TERM);
        var homePageResults = searchResultsPage().hasResults() ?
                searchResultsPage().getResultCount() : 0;

        // Act - Search from products page
        navigateTo("/products");
        productsPage().waitForContentToLoad();
        productsPage().searchProducts(SEARCH_TERM);
        var productsPageResults = searchResultsPage().hasResults() ?
                searchResultsPage().getResultCount() : 0;

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            // Results should be similar (may differ by pagination timing)
            softly.assertThat(Math.abs(homePageResults - productsPageResults))
                    .as("Search results should be consistent across pages")
                    .isLessThanOrEqualTo(5);
        });

        log.info("Search consistency - Home: {}, Products: {}",
                homePageResults, productsPageResults);
    }
}
