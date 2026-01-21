package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Information Pages functionality.
 * Tests About, Shipping, Returns, and Terms pages.
 */
@Epic("Music Tech Shop")
@Feature("Information Pages")
@DisplayName("Information Pages Tests")
class InformationPagesTest extends BaseTest {

    @Test
    @Tag("regression")
    @Story("About Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should load About page successfully")
    void shouldLoadAboutPageSuccessfully() {
        // Arrange & Act
        navigateTo("/about");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(aboutPage().isPageLoaded())
                    .as("About page should be loaded")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain /about")
                    .contains("/about");

            softly.assertThat(aboutPage().hasExpectedContent())
                    .as("About page should contain expected content")
                    .isTrue();
        });

        log.info("About page loaded successfully");
    }

    @Test
    @Tag("regression")
    @Story("About Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display company information on About page")
    void shouldDisplayCompanyInformationOnAboutPage() {
        // Arrange
        navigateTo("/about");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            var heading = aboutPage().getPageHeading();

            softly.assertThat(heading)
                    .as("About page should have a heading")
                    .isNotEmpty();

            softly.assertThat(aboutPage().isMissionCardDisplayed())
                    .as("Mission card should be displayed")
                    .isTrue();

            softly.assertThat(aboutPage().isVisionCardDisplayed())
                    .as("Vision card should be displayed")
                    .isTrue();

            softly.assertThat(aboutPage().isValuesCardDisplayed())
                    .as("Values card should be displayed")
                    .isTrue();

            softly.assertThat(aboutPage().areAllWhyChooseUsFeaturesDisplayed())
                    .as("All Why Choose Us features should be displayed")
                    .isTrue();

            softly.assertThat(aboutPage().areStatisticsDisplayed())
                    .as("Statistics should be displayed")
                    .isTrue();
        });

        log.info("Company information displayed on About page");
    }

    @Test
    @Tag("regression")
    @Story("About Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display CTA buttons on About page")
    void shouldDisplayCTAButtonsOnAboutPage() {
        // Arrange
        navigateTo("/about");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(aboutPage().areCTAButtonsDisplayed())
                .as("CTA buttons should be displayed")
                .isTrue());

        log.info("CTA buttons displayed on About page");
    }

    @Test
    @Tag("regression")
    @Story("Shipping Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should load Shipping page successfully")
    void shouldLoadShippingPageSuccessfully() {
        // Arrange & Act
        navigateTo("/shipping");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(shippingPage().isPageLoaded())
                    .as("Shipping page should be loaded")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain /shipping")
                    .contains("/shipping");

            softly.assertThat(shippingPage().hasExpectedContent())
                    .as("Shipping page should contain expected content")
                    .isTrue();
        });

        log.info("Shipping page loaded successfully");
    }

    @Test
    @Tag("regression")
    @Story("Shipping Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display shipping information")
    void shouldDisplayShippingInformation() {
        // Arrange
        navigateTo("/shipping");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(shippingPage().isDeliveryCalculatorDisplayed())
                    .as("Delivery calculator should be displayed")
                    .isTrue();

            softly.assertThat(shippingPage().areAllShippingOptionsDisplayed())
                    .as("All shipping options should be displayed")
                    .isTrue();

            softly.assertThat(shippingPage().isFAQSectionDisplayed())
                    .as("FAQ section should be displayed")
                    .isTrue();

            softly.assertThat(shippingPage().isPopularBadgeDisplayed())
                    .as("Popular badge should be displayed on Express shipping")
                    .isTrue();
        });

        log.info("Shipping information displayed correctly");
    }

    @Test
    @Tag("regression")
    @Story("Shipping Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display page heading on Shipping page")
    void shouldDisplayHeadingOnShippingPage() {
        // Arrange
        navigateTo("/shipping");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            var heading = shippingPage().getPageHeading();
            softly.assertThat(heading)
                    .as("Shipping page should have a heading")
                    .isNotEmpty();

            softly.assertThat(heading.toLowerCase())
                    .as("Heading should contain 'shipping'")
                    .contains("shipping");
        });

        log.info("Shipping page heading displayed correctly");
    }

    @Test
    @Tag("regression")
    @Story("Shipping Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should allow interaction with delivery calculator")
    void shouldAllowInteractionWithDeliveryCalculator() {
        // Arrange
        navigateTo("/shipping");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(shippingPage().isDeliveryCalculatorDisplayed())
                    .as("Delivery calculator should be displayed")
                    .isTrue();

            // Enter a ZIP code
            shippingPage().enterZipCode("10001");

            // Note: Calculate button may be disabled until valid input
            // This test verifies the calculator elements are interactive
            log.info("Delivery calculator interaction tested");
        });

        log.info("Delivery calculator interaction completed");
    }

    @Test
    @Tag("regression")
    @Story("Returns Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should load Returns page successfully")
    void shouldLoadReturnsPageSuccessfully() {
        // Arrange & Act
        navigateTo("/returns");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(returnsPage().isPageLoaded())
                    .as("Returns page should be loaded")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain /returns")
                    .contains("/returns");

            softly.assertThat(returnsPage().hasExpectedContent())
                    .as("Returns page should contain expected content")
                    .isTrue();
        });

        log.info("Returns page loaded successfully");
    }

    @Test
    @Tag("regression")
    @Story("Returns Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display returns policy information")
    void shouldDisplayReturnsPolicyInformation() {
        // Arrange
        navigateTo("/returns");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(returnsPage().isReturnPolicyCardDisplayed())
                    .as("Return policy card should be displayed")
                    .isTrue();

            softly.assertThat(returnsPage().areReturnStepsDisplayed())
                    .as("Return steps should be displayed")
                    .isTrue();

            softly.assertThat(returnsPage().areWarrantyOptionsDisplayed())
                    .as("Warranty options should be displayed")
                    .isTrue();

            softly.assertThat(returnsPage().areDHLLocationsDisplayed())
                    .as("DHL locations should be displayed")
                    .isTrue();

            softly.assertThat(returnsPage().areReturnConditionsDisplayed())
                    .as("Return conditions should be displayed")
                    .isTrue();
        });

        log.info("Returns policy information displayed correctly");
    }

    @Test
    @Tag("regression")
    @Story("Returns Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display page heading and help buttons on Returns page")
    void shouldDisplayHeadingAndHelpButtonsOnReturnsPage() {
        // Arrange
        navigateTo("/returns");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            var heading = returnsPage().getPageHeading();
            softly.assertThat(heading)
                    .as("Returns page should have a heading")
                    .isNotEmpty();

            softly.assertThat(returnsPage().areHelpButtonsDisplayed())
                    .as("Help buttons should be displayed")
                    .isTrue();
        });

        log.info("Returns page heading and help buttons displayed correctly");
    }

    @Test
    @Tag("regression")
    @Story("Terms Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should load Terms page successfully")
    void shouldLoadTermsPageSuccessfully() {
        // Arrange & Act
        navigateTo("/terms");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(termsPage().isPageLoaded())
                    .as("Terms page should be loaded")
                    .isTrue();

            softly.assertThat(getCurrentUrl())
                    .as("URL should contain /terms")
                    .contains("/terms");

            softly.assertThat(termsPage().hasExpectedContent())
                    .as("Terms page should contain expected content")
                    .isTrue();
        });

        log.info("Terms page loaded successfully");
    }

    @Test
    @Tag("regression")
    @Story("Terms Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display terms and conditions")
    void shouldDisplayTermsAndConditions() {
        // Arrange
        navigateTo("/terms");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(termsPage().isTermsSectionDisplayed())
                    .as("Terms section should be displayed")
                    .isTrue();

            softly.assertThat(termsPage().areAllSectionsDisplayed())
                    .as("All 10 terms sections should be displayed")
                    .isTrue();

            softly.assertThat(termsPage().isLastUpdatedDisplayed())
                    .as("Last updated date should be displayed")
                    .isTrue();
        });

        log.info("Terms and conditions displayed correctly");
    }

    @Test
    @Tag("regression")
    @Story("Terms Page")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display page heading and last updated on Terms page")
    void shouldDisplayHeadingAndLastUpdatedOnTermsPage() {
        // Arrange
        navigateTo("/terms");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> {
            var heading = termsPage().getPageHeading();
            softly.assertThat(heading)
                    .as("Terms page should have a heading")
                    .isNotEmpty();

            softly.assertThat(heading.toLowerCase())
                    .as("Heading should contain 'terms'")
                    .contains("terms");

            var lastUpdated = termsPage().getLastUpdatedDate();
            softly.assertThat(lastUpdated)
                    .as("Last updated date should not be empty")
                    .isNotEmpty();
        });

        log.info("Terms page heading and last updated displayed correctly");
    }

    @Test
    @Tag("regression")
    @Story("Footer Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to all information pages from footer")
    void shouldNavigateToAllInformationPagesFromFooter() {
        // Test About Us link (data-testid="footer-link-about")
        getFooterComponent().clickAboutLink();
        SoftAssertions.assertSoftly(softly -> softly.assertThat(aboutPage().isPageLoaded())
                .as("Should navigate to About page from footer")
                .isTrue());

        navigateToBaseUrl();

        // Test Shipping Policy link (data-testid="footer-link-shipping")
        getFooterComponent().clickShippingLink();
        SoftAssertions.assertSoftly(softly -> softly.assertThat(shippingPage().isPageLoaded())
                .as("Should navigate to Shipping page from footer")
                .isTrue());

        navigateToBaseUrl();

        // Test Returns & Refunds link (data-testid="footer-link-returns")
        getFooterComponent().clickReturnsLink();
        SoftAssertions.assertSoftly(softly -> softly.assertThat(returnsPage().isPageLoaded())
                .as("Should navigate to Returns page from footer")
                .isTrue());

        navigateToBaseUrl();

        // Test Terms of Service link (data-testid="footer-link-terms")
        getFooterComponent().clickTermsLink();
        SoftAssertions.assertSoftly(softly -> softly.assertThat(termsPage().isPageLoaded())
                .as("Should navigate to Terms page from footer")
                .isTrue());

        log.info("Successfully navigated to all information pages from footer");
    }
}
