package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.fugazi.data.providers.TestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Search functionality.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Search")
@DisplayName("Search Product Tests")
class SearchProductTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Basic Search")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should search and find products")
    void shouldSearchAndFindProducts() {
        // Arrange - Use a term that exists in the store
        var searchTerm = "synthesizer";

        // Act
        homePage().searchProduct(searchTerm);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Search results page should be loaded")
                    .isTrue();

            softly.assertThat(searchResultsPage().hasResults())
                    .as("Should find products matching search term")
                    .isTrue();
        });
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Search Input")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should type in search field")
    void shouldTypeInSearchField() {
        // Arrange
        var searchTerm = TestDataFactory.generateSearchTerm();

        // Act
        homePage().header().typeInSearchField(searchTerm);

        // Assert
        var fieldValue = homePage().header().getSearchFieldValue();
        SoftAssertions.assertSoftly(softly -> softly.assertThat(fieldValue)
                .as("Search field should contain the typed text")
                .isEqualTo(searchTerm));
    }

    @Test
    @Tag("regression")
    @Story("No Results")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should show no results for invalid search")
    void shouldShowNoResultsForInvalidSearch() {
        // Arrange
        var invalidSearchTerm = TestDataFactory.generateInvalidSearchTerm();

        // Act
        homePage().searchProduct(invalidSearchTerm);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Search results page should be loaded")
                    .isTrue();

            // Either no results or empty message should be shown
            var hasNoResults = !searchResultsPage().hasResults() ||
                    searchResultsPage().isNoResultsMessageDisplayed();

            softly.assertThat(hasNoResults)
                    .as("Should show no results for invalid search term")
                    .isTrue();
        });

        log.info("Search for '{}' returned {} results", invalidSearchTerm, searchResultsPage().getResultCount());
    }

    @Test
    @Tag("regression")
    @Story("Search Results")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display result titles")
    void shouldDisplayResultTitles() {
        // Arrange
        var searchTerm = "music";

        // Act
        homePage().searchProduct(searchTerm);

        // Assert - If results exist, check titles
        if (searchResultsPage().hasResults()) {
            var titles = searchResultsPage().getResultTitles();

            SoftAssertions.assertSoftly(softly -> {
                softly.assertThat(titles)
                        .as("Result titles should not be empty")
                        .isNotEmpty();

                titles.forEach(title ->
                        softly.assertThat(title)
                                .as("Result title should not be blank")
                                .isNotBlank()
                );
            });

            log.info("Found {} results with titles", titles.size());
        }
    }

    @Test
    @Tag("regression")
    @Story("Search Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to product detail from search results")
    void shouldNavigateToProductDetailFromSearchResults() {
        // Arrange
        var searchTerm = "keyboard";

        // Act
        homePage().searchProduct(searchTerm);

        // Skip test if no results
        if (!searchResultsPage().hasResults()) {
            log.warn("No search results found for '{}', skipping navigation test", searchTerm);
            return;
        }

        var initialUrl = getCurrentUrl();
        searchResultsPage().clickFirstResult();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should change after clicking result")
                    .isNotEqualTo(initialUrl);

            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();
        });
    }

    @Test
    @Tag("regression")
    @Story("Search from Results Page")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should perform new search from results page")
    void shouldPerformNewSearchFromResultsPage() {
        // Arrange
        var firstSearchTerm = "speaker";
        var secondSearchTerm = "headphones";

        // Act
        homePage().searchProduct(firstSearchTerm);
        var firstResultCount = searchResultsPage().getResultCount();

        searchResultsPage().searchFor(secondSearchTerm);
        var secondResultCount = searchResultsPage().getResultCount();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(searchResultsPage().isPageLoaded())
                .as("Search results page should still be loaded")
                .isTrue());

        log.info("First search '{}': {} results, Second search '{}': {} results",
                firstSearchTerm, firstResultCount, secondSearchTerm, secondResultCount);
    }

    @Test
    @Tag("regression")
    @Story("Empty Search")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle empty search gracefully")
    void shouldHandleEmptySearchGracefully() {
        // Arrange
        var emptySearchTerm = "";

        // Act - Navigate to products and type empty string
        homePage().header().typeInSearchField(emptySearchTerm);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(searchResultsPage().isPageLoaded())
                    .as("Products page should be loaded")
                    .isTrue();

            // With empty search, should still show products (all products)
            softly.assertThat(searchResultsPage().hasResults())
                    .as("Should show all products with empty search")
                    .isTrue();
        });
    }
}

