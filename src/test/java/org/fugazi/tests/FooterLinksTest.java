package org.fugazi.tests;

import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test class for footer links and sections functionality.
 * Covers footer structure, navigation links, social media links, and copyright.
 * <p>
 * REFACTORED: All locators moved to FooterComponent (POM pattern)
 * REFACTORED: BASE_URL replaced with config.getBaseUrl()
 */
@Slf4j
@Epic("Music Tech Shop")
@Feature("Footer")
@DisplayName("Footer Links Tests")
class FooterLinksTest extends BaseTest
{
    // ==================== FOOTER STRUCTURE TESTS ====================

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Footer Display")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display footer on homepage")
    void shouldDisplayFooterOnHomePage()
    {
        // Act & Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(getFooterComponent().isFooterDisplayed())
                .as("Footer should be present")
                .isTrue());

        log.info("Footer is displayed on homepage");
    }

    @Test
    @Tag("regression")
    @Story("Footer Display")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display all footer sections")
    void shouldDisplayAllFooterSections()
    {
        // Act & Assert
        var footer = getFooterComponent();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(footer.isAboutSectionDisplayed())
                    .as("Footer about section should be present")
                    .isTrue();

            softly.assertThat(footer.isProductsSectionDisplayed())
                    .as("Footer products section should be present")
                    .isTrue();

            softly.assertThat(footer.isInformationSectionDisplayed())
                    .as("Footer information section should be present")
                    .isTrue();

            softly.assertThat(footer.isSupportSectionDisplayed())
                    .as("Footer support/contact section should be present")
                    .isTrue();
        });

        log.info("All footer sections are displayed");
    }

    @Test
    @Tag("regression")
    @Story("Footer Display")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should display copyright information")
    void shouldDisplayCopyrightInformation()
    {
        // Act
        var footer = getFooterComponent();
        var copyrightText = footer.getCopyrightText();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(footer.isCopyrightSectionDisplayed())
                    .as("Footer copyright section should be present")
                    .isTrue();

            softly.assertThat(copyrightText)
                    .as("Copyright should contain year")
                    .containsIgnoringCase("2026");

            softly.assertThat(copyrightText)
                    .as("Copyright should contain company name")
                    .containsIgnoringCase("MUSIC-TECH");
        });

        log.info("Copyright information is displayed correctly");
    }

    // ==================== SOCIAL LINKS TESTS ====================

    @Test
    @Tag("regression")
    @Story("Social Links")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should display social media links")
    void shouldDisplaySocialMediaLinks()
    {
        // Act
        var footer = getFooterComponent();
        var socialLinksCount = footer.getSocialLinkIconsCount();

        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(socialLinksCount)
                .as("Should have at least one social link")
                .isGreaterThanOrEqualTo(1));

        log.info("Found {} social media links", socialLinksCount);
    }

    // ==================== CATEGORY LINKS TESTS ====================

    @Test
    @Tag("regression")
    @Story("Category Links")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display category links in footer")
    void shouldDisplayCategoryLinksInFooter()
    {
        // Act & Assert
        var footer = getFooterComponent();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(footer.isElectronicsLinkDisplayed())
                    .as("Electronics link should be present")
                    .isTrue();

            softly.assertThat(footer.isPhotographyLinkDisplayed())
                    .as("Photography link should be present")
                    .isTrue();

            softly.assertThat(footer.isAccessoriesLinkDisplayed())
                    .as("Accessories link should be present")
                    .isTrue();
        });

        log.info("All category links are displayed in footer");
    }

    @ParameterizedTest(name = "Navigate to {0} category from footer")
    @CsvSource({
            "Electronics, /products?category=Electronics",
            "Photography, /products?category=Photography",
            "Accessories, /products?category=Accessories"
    })
    @Tag("regression")
    @Story("Category Links")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to category page from footer link")
    void shouldNavigateToCategoryFromFooterLink(String category, String expectedUrlPart)
    {
        // Arrange
        var footer = getFooterComponent();

        // Act
        switch (category) {
            case "Electronics" -> footer.clickElectronicsLink();
            case "Photography" -> footer.clickPhotographyLink();
            case "Accessories" -> footer.clickAccessoriesLink();
            default -> log.warn("Unknown category: {}", category);
        }

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = driver.getCurrentUrl();
            softly.assertThat(currentUrl)
                    .as("URL should contain " + expectedUrlPart)
                    .contains(expectedUrlPart);
        });

        log.info("Navigated to {} category from footer", category);
    }

    // ==================== INFORMATION LINKS TESTS ====================

    @Test
    @Tag("regression")
    @Story("Information Links")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display company information links")
    void shouldDisplayCompanyInformationLinks()
    {
        // Act & Assert
        var footer = getFooterComponent();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(footer.isAboutLinkDisplayed())
                    .as("About Us link should be present")
                    .isTrue();

            softly.assertThat(footer.isShippingLinkDisplayed())
                    .as("Shipping Policy link should be present")
                    .isTrue();

            softly.assertThat(footer.isReturnsLinkDisplayed())
                    .as("Returns link should be present")
                    .isTrue();

            softly.assertThat(footer.isTermsLinkDisplayed())
                    .as("Terms link should be present")
                    .isTrue();
        });

        log.info("All company information links are displayed");
    }

    @ParameterizedTest(name = "Navigate to {0} page from footer")
    @CsvSource({
            "about, /about",
            "shipping, /shipping",
            "returns, /returns",
            "terms, /terms"
    })
    @Tag("regression")
    @Story("Information Links")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should navigate to information page from footer link")
    void shouldNavigateToInformationPageFromFooterLink(String linkName, String expectedUrlPart)
    {
        // Act
        var footer = getFooterComponent();
        footer.clickFooterLink(linkName); // Using generic method from FooterComponent

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = driver.getCurrentUrl();
            softly.assertThat(currentUrl)
                    .as("URL should contain " + expectedUrlPart)
                    .contains(expectedUrlPart);
        });

        log.info("Navigated to {} page from footer", linkName);
    }

    // ==================== FOOTER ON DIFFERENT PAGES TESTS ====================

    @Test
    @Tag("regression")
    @Story("Footer Consistency")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display footer on products page")
    void shouldDisplayFooterOnProductsPage()
    {
        // Arrange - using config instead of hardcoded URL
        driver.get(config.getBaseUrl() + "/products");

        // Act & Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(getFooterComponent().isFooterDisplayed())
                .as("Footer should be present on products page")
                .isTrue());

        log.info("Footer is displayed on products page");
    }
}
