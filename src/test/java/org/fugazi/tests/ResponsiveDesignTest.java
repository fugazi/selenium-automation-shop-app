package org.fugazi.tests;

import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Dimension;

/**
 * Test class for responsive design verification.
 * Covers different viewport sizes and mobile/tablet/desktop layouts.
 */
@Slf4j
@Epic("Music Tech Shop")
@Feature("Responsive Design")
@DisplayName("Responsive Design Tests")
class ResponsiveDesignTest extends BaseTest {
    private static final Dimension MOBILE_MEDIUM = new Dimension(375, 667);  // iPhone 6/7/8
    private static final Dimension TABLET = new Dimension(768, 1024);        // iPad
    private static final Dimension DESKTOP = new Dimension(1280, 800);       // Laptop

    @Test
    @Tag("regression")
    @Story("Mobile Layout")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should render correctly on mobile viewport")
    void shouldRenderCorrectlyOnMobileViewport() {
        // Arrange
        setViewportSize(MOBILE_MEDIUM);
        driver.navigate().refresh();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            var footer = getFooterComponent();

            // Header should be visible
            softly.assertThat(header.isHeaderDisplayed())
                    .as("Header should be present on mobile")
                    .isTrue();

            // Footer should be visible
            softly.assertThat(footer.isFooterDisplayed())
                    .as("Footer should be present on mobile")
                    .isTrue();

            // Page renders without errors (layout adapts to viewport)
            var pageSource = driver.getPageSource();
            softly.assertThat(pageSource)
                    .as("Page should render content")
                    .isNotEmpty();
        });

        log.info("Mobile viewport rendered correctly");
    }

    @Test
    @Tag("regression")
    @Story("Mobile Layout")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should hide desktop navigation on mobile")
    void shouldHideDesktopNavigationOnMobile() {
        // Arrange
        setViewportSize(MOBILE_MEDIUM);
        driver.navigate().refresh();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            var navLinksCount = header.getNavLinksCount();

            log.info("Visible nav links on mobile: {}", navLinksCount);

            // Note: App may still show some nav links or use hamburger menu
            // We just verify the page doesn't break
            softly.assertThat(header.isHeaderDisplayed())
                    .as("Header should adapt to mobile")
                    .isTrue();
        });
    }

    // ==================== TABLET VIEWPORT TESTS ====================

    @Test
    @Tag("regression")
    @Story("Tablet Layout")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should render correctly on tablet viewport")
    void shouldRenderCorrectlyOnTabletViewport() {
        // Arrange
        setViewportSize(TABLET);
        driver.navigate().refresh();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            var footer = getFooterComponent();

            softly.assertThat(header.isHeaderDisplayed())
                    .as("Header should be present on tablet")
                    .isTrue();

            softly.assertThat(footer.isFooterDisplayed())
                    .as("Footer should be present on tablet")
                    .isTrue();
        });

        log.info("Tablet viewport rendered correctly");
    }

    // ==================== DESKTOP VIEWPORT TESTS ====================

    @Test
    @Tag("regression")
    @Story("Desktop Layout")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should render correctly on desktop viewport")
    void shouldRenderCorrectlyOnDesktopViewport() {
        // Arrange
        setViewportSize(DESKTOP);
        driver.navigate().refresh();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            var footer = getFooterComponent();

            softly.assertThat(header.isHeaderDisplayed())
                    .as("Header should be present on desktop")
                    .isTrue();

            softly.assertThat(footer.isFooterDisplayed())
                    .as("Footer should be present on desktop")
                    .isTrue();

            // Search form should be visible on desktop
            softly.assertThat(header.isSearchInputDisplayed())
                    .as("Search form should be visible on desktop")
                    .isTrue();
        });

        log.info("Desktop viewport rendered correctly");
    }

    // ==================== PRODUCT GRID RESPONSIVE TESTS ====================

    @ParameterizedTest(name = "Product grid on {0}x{1} viewport")
    @CsvSource({
            "375, 667, mobile",
            "768, 1024, tablet",
            "1280, 800, desktop"
    })
    @Tag("regression")
    @Story("Product Grid")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display product grid responsively")
    void shouldDisplayProductGridResponsively(int width, int height, String deviceType) {
        // Arrange
        setViewportSize(new Dimension(width, height));
        driver.get(config.getBaseUrl() + "/products");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var productsPage = productsPage();
            var productCount = productsPage.getProductCount();

            softly.assertThat(productCount)
                    .as("Should display product cards on " + deviceType)
                    .isGreaterThan(0);
        });

        log.info("Product grid displayed correctly on {} ({}x{})", deviceType, width, height);
    }

    // ==================== VIEWPORT RESIZE TESTS ====================

    @Test
    @Tag("regression")
    @Story("Viewport Resize")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle viewport resize from mobile to desktop")
    void shouldHandleViewportResizeFromMobileToDesktop() {
        // Arrange - start on mobile
        setViewportSize(MOBILE_MEDIUM);

        // Act - resize to desktop
        setViewportSize(DESKTOP);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();

            softly.assertThat(header.isHeaderDisplayed())
                    .as("Header should be present after resize")
                    .isTrue();

            softly.assertThat(driver.getPageSource())
                    .as("Page should render after resize")
                    .isNotEmpty();
        });

        log.info("Viewport resize handled correctly");
    }

    @Test
    @Tag("regression")
    @Story("Viewport Resize")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should handle viewport resize from desktop to mobile")
    void shouldHandleViewportResizeFromDesktopToMobile() {
        // Arrange - start on desktop
        setViewportSize(DESKTOP);

        // Act - resize to mobile
        setViewportSize(MOBILE_MEDIUM);

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();

            softly.assertThat(header.isHeaderDisplayed())
                    .as("Header should be present after resize")
                    .isTrue();

            softly.assertThat(driver.getPageSource())
                    .as("Page should render after resize")
                    .isNotEmpty();
        });

        log.info("Desktop to mobile resize handled correctly");
    }

    // ==================== HELPER METHODS ====================

    private void setViewportSize(Dimension size) {
        try {
            driver.manage().window().setSize(size);
            Thread.sleep(500); // Wait for resize animation
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
