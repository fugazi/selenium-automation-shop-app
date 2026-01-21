package org.fugazi.tests;

import io.qameta.allure.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Home Page functionality.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Home Page")
@DisplayName("Home Page Tests")
class HomePageTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Page Load")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should load home page successfully")
    void shouldLoadHomePageSuccessfully() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(homePage().isPageLoaded())
                    .as("Home page should be loaded")
                    .isTrue();

            softly.assertThat(homePage().header().isHeaderDisplayed())
                    .as("Header should be displayed")
                    .isTrue();
        });
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Page Title")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should have correct page title")
    void shouldHaveCorrectPageTitle() {
        // Act
        var title = getPageTitle();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(title)
                .as("Page title should not be empty")
                .isNotBlank());

        log.info("Page title: {}", title);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Featured Products")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should display featured products")
    void shouldDisplayFeaturedProducts() {
        // Act
        var productCount = homePage().getFeaturedProductsCount();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(productCount)
                .as("Should have at least one featured product")
                .isGreaterThan(0));

        log.info("Found {} featured products", productCount);
    }

    @Test
    @Tag("regression")
    @Story("Featured Products")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should get product names from featured products")
    void shouldGetProductNames() {
        // Act
        var productNames = homePage().getProductNames();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productNames)
                    .as("Product names should not be empty")
                    .isNotEmpty();

            productNames.forEach(name ->
                softly.assertThat(name)
                        .as("Product name should not be blank")
                        .isNotBlank()
            );
        });

        log.info("Product names: {}", productNames);
    }

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Header Component")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should display header with logo and search")
    void shouldDisplayHeaderWithLogoAndSearch() {
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(homePage().header().isLogoDisplayed())
                    .as("Logo should be displayed")
                    .isTrue();

            softly.assertThat(homePage().header().isSearchInputDisplayed())
                    .as("Search input should be displayed")
                    .isTrue();
        });
    }

    @Test
    @Tag("regression")
    @Story("Header Component")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display cart icon in header")
    void shouldDisplayCartIcon() {
        SoftAssertions.assertSoftly(softly -> softly.assertThat(homePage().header().isCartIconDisplayed())
                .as("Cart icon should be displayed")
                .isTrue());
    }

    @Test
    @Tag("regression")
    @Story("Footer Component")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should display footer")
    void shouldDisplayFooter() {
        // Act
        homePage().footer().scrollToFooter();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(homePage().footer().isFooterDisplayed())
                .as("Footer should be displayed")
                .isTrue());
    }

    @Test
    @Tag("regression")
    @Story("Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to product when clicking on it")
    void shouldNavigateToProductWhenClicking() {
        // Arrange
        var initialUrl = getCurrentUrl();

        // Act
        homePage().clickFirstProduct();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(getCurrentUrl())
                    .as("URL should change after clicking product")
                    .isNotEqualTo(initialUrl);

            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();
        });
    }

    @Test
    @Tag("regression")
    @Story("Cart")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Cart should initially be empty")
    void cartShouldInitiallyBeEmpty() {
        // Act
        var cartCount = homePage().getCartItemCount();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(cartCount)
                .as("Cart should be empty initially")
                .isEqualTo(0));
    }
}

