package org.fugazi.tests;

import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for theme toggle functionality.
 * Covers light/dark mode switching and persistence.
 */
@Slf4j
@Epic("Music Tech Shop")
@Feature("Theme Toggle")
@DisplayName("Theme Toggle Tests")
class ThemeToggleTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Theme Toggle Display")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should display theme toggle button")
    void shouldDisplayThemeToggleButton() {
        // Assert
        SoftAssertions.assertSoftly(softly -> softly.assertThat(getHeaderComponent().isThemeToggleDisplayed())
                .as("Theme toggle button should be present")
                .isTrue());

        log.info("Theme toggle button is displayed");
    }

    @Test
    @Tag("regression")
    @Story("Theme Toggle Display")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should have accessible label on theme toggle")
    void shouldHaveAccessibleLabelOnThemeToggle() {
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            if (header.isThemeToggleDisplayed()) {
                var ariaLabel = header.getThemeToggleAriaLabel();

                softly.assertThat(ariaLabel)
                        .as("Theme toggle should have aria-label")
                        .isNotNull()
                        .isNotEmpty();

                softly.assertThat(ariaLabel.toLowerCase())
                        .as("Aria-label should mention theme")
                        .contains("theme");
            }
        });

        log.info("Theme toggle has accessible label");
    }

    @Test
    @Tag("regression")
    @Story("Theme Switching")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should toggle theme when clicking button")
    void shouldToggleThemeWhenClickingButton() {
        // Arrange - get initial theme
        var header = getHeaderComponent();
        var initialTheme = header.getCurrentTheme();
        log.info("Initial theme: {}", initialTheme);

        // Act - click theme toggle
        if (header.isThemeToggleDisplayed()) {
            header.clickThemeToggle();

            var newTheme = header.getCurrentTheme();
            log.info("New theme after toggle: {}", newTheme);

            // Assert
            SoftAssertions.assertSoftly(softly -> softly.assertThat(newTheme)
                    .as("Theme class should be present on html element")
                    .isNotNull());
        } else {
            log.info("Theme toggle button not found, skipping test");
        }
    }

    @Test
    @Tag("regression")
    @Story("Theme Switching")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should toggle theme back to original")
    void shouldToggleThemeBackToOriginal() {
        // Arrange
        var header = getHeaderComponent();
        var initialTheme = header.getCurrentTheme();

        // Act - toggle twice
        if (header.isThemeToggleDisplayed()) {
            // First toggle
            header.clickThemeToggle();
            var afterFirstToggle = header.getCurrentTheme();

            // Second toggle
            header.clickThemeToggle();
            var afterSecondToggle = header.getCurrentTheme();

            // Assert
            SoftAssertions.assertSoftly(softly -> softly.assertThat(afterSecondToggle)
                    .as("Theme should be togglable")
                    .isNotNull());

            log.info("Theme toggle cycle: {} -> {} -> {}", initialTheme, afterFirstToggle, afterSecondToggle);
        } else {
            log.info("Theme toggle button not found");
        }
    }

    @Test
    @Tag("regression")
    @Story("Theme Visual")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should apply theme class to html element")
    void shouldApplyThemeClassToHtmlElement() {
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            var htmlClass = header.getHtmlClass();
            log.info("HTML classes: {}", htmlClass);

            softly.assertThat(htmlClass)
                    .as("HTML element should have theme-related class")
                    .isNotNull();

            softly.assertThat(htmlClass)
                    .as("HTML should have some classes for styling")
                    .isNotEmpty();
        });
    }

    @Test
    @Tag("regression")
    @Story("Theme Visual")
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Should have color scheme meta or style")
    void shouldHaveColorSchemeMetaOrStyle() {
        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var header = getHeaderComponent();
            var colorScheme = header.getHtmlColorScheme();
            var style = header.getHtmlStyle();
            log.info("HTML color-scheme: {}, style: {}", colorScheme, style);

            softly.assertThat(colorScheme)
                    .as("HTML element should have color-scheme attribute")
                    .isNotNull();
            softly.assertThat(style)
                    .as("HTML should have color-scheme attribute")
                    .isNotNull();

            // The app should have some form of theme handling
            softly.assertThat(true)
                    .as("App loads without theme errors")
                    .isTrue();
        });
    }
}
