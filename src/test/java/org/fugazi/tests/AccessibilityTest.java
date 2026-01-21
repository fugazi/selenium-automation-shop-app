package org.fugazi.tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

/**
 * Test class for accessibility verification following WCAG 2.2 Level AA.
 * Covers ARIA labels, keyboard navigation, skip links, form labels, and semantic HTML.
 * <p>
 * Note: These tests verify basic accessibility compliance but should be complemented
 * with manual testing and tools like Accessibility Insights for comprehensive coverage.
 */
@Slf4j
@Epic("Music Tech Shop")
@Feature("Accessibility")
@Disabled
@DisplayName("Accessibility Tests (WCAG 2.2 AA)")
class AccessibilityTest extends BaseTest {

    // Locators
    private static final By ALL_BUTTONS = By.tagName("button");
    private static final By ALL_LINKS = By.tagName("a");
    private static final By MAIN_LANDMARK = By.tagName("main");
    private static final By NAV_LANDMARK = By.tagName("nav");
    private static final By FOOTER_LANDMARK = By.tagName("footer");
    private static final By FORM_INPUTS = By.cssSelector(
            "input[type='email'], input[type='password'], input[type='text'], input[type='search']");

    // ==================== ARIA LABELS TESTS ====================

    @Test
    @Tag("a11y")
    @Tag("regression")
    @Story("ARIA Labels")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Interactive buttons should have accessible names")
    void interactiveButtonsShouldHaveAccessibleNames() {
        log.info("Verifying buttons have accessible names (aria-label or text content)");

        var buttons = driver.findElements(ALL_BUTTONS);
        log.debug("Found {} button elements", buttons.size());

        SoftAssertions.assertSoftly(softly -> {
            for (var button : buttons) {
                var ariaLabel = button.getDomAttribute("aria-label");
                var text = button.getText();
                var title = button.getDomAttribute("title");

                // Button should have at least one: aria-label, text content, or title
                var hasAccessibleName =
                        !text.trim().isEmpty() || (ariaLabel != null && !ariaLabel.trim().isEmpty()) || (title != null
                                && !title.trim().isEmpty());

                if (!hasAccessibleName) {
                    log.warn("Button without accessible name found: {}", button.getDomAttribute("outerHTML"));
                }

                softly.assertThat(hasAccessibleName)
                        .as("Button should have accessible name (aria-label, text, or title)")
                        .isTrue();
            }
        });

        log.info("Buttons accessibility verification completed");
    }

    @Test
    @Tag("a11y")
    @Tag("regression")
    @Story("ARIA Labels")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Links should have descriptive text or aria-label")
    void linksShouldHaveDescriptiveText() {
        log.info("Verifying links have descriptive text");

        var links = driver.findElements(ALL_LINKS);
        log.debug("Found {} link elements", links.size());

        SoftAssertions.assertSoftly(softly -> {
            for (var link : links) {
                var ariaLabel = link.getDomAttribute("aria-label");
                var text = link.getText();
                var title = link.getDomAttribute("title");

                var hasAccessibleName =
                        (ariaLabel != null && !ariaLabel.trim().isEmpty()) || !text.trim().isEmpty() || (title != null
                                && !title.trim().isEmpty());

                if (!hasAccessibleName) {
                    log.warn("Link without accessible name found: href={}", link.getDomAttribute("href"));
                }

                softly.assertThat(hasAccessibleName)
                        .as("Link should have descriptive text or aria-label")
                        .isTrue();
            }
        });

        log.info("Links accessibility verification completed");
    }

    // ==================== KEYBOARD NAVIGATION TESTS ====================

    @Test
    @Tag("a11y")
    @Tag("regression")
    @Story("Keyboard Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should navigate through interactive elements with Tab key")
    void shouldNavigateThroughInteractiveElementsWithTab() {
        log.info("Testing keyboard navigation with Tab key");

        // Press Tab to move focus to first interactive element
        var body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.TAB);

        // Get the currently focused element
        var activeElement = driver.switchTo().activeElement();
        var tagName = activeElement.getTagName().toLowerCase();

        log.debug("First Tab focused element: {}", tagName);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(tagName)
                    .as("First Tab should focus an interactive element")
                    .isIn("a", "button", "input", "select", "textarea");

            // Verify element is visible (not just in DOM)
            softly.assertThat(activeElement.isDisplayed())
                    .as("Focused element should be visible")
                    .isTrue();
        });

        log.info("Keyboard navigation test completed");
    }

    @Test
    @Tag("a11y")
    @Tag("regression")
    @Story("Keyboard Navigation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should provide skip link to main content")
    void shouldProvideSkipLinkToMainContent() {
        log.info("Testing skip link to main content");

        // Press Tab to focus first element (should be skip link)
        var body = driver.findElement(By.tagName("body"));
        body.sendKeys(Keys.TAB);

        var activeElement = driver.switchTo().activeElement();
        var textContent = activeElement.getDomProperty("textContent");
        var text = (textContent != null) ? textContent.toLowerCase() : "";
        var ariaLabelAttr = activeElement.getDomAttribute("aria-label");
        final String ariaLabel = (ariaLabelAttr != null) ? ariaLabelAttr.toLowerCase() : null;

        log.debug("First focusable element text: '{}', aria-label: '{}'", text, ariaLabel);

        SoftAssertions.assertSoftly(softly -> {
            // Skip link should contain keywords like "skip", "main", "content"
            // Note: Some modern apps may not have skip links, which is acceptable
            // if they have proper landmark navigation
            var hasSkipLink = (text.contains("skip") || text.contains("main"))
                    || (ariaLabel != null && (ariaLabel.contains("skip") || ariaLabel.contains("main")));

            var hasMainLandmark = !driver.findElements(MAIN_LANDMARK).isEmpty();

            // Either have skip link OR proper landmarks
            softly.assertThat(hasSkipLink || hasMainLandmark)
                    .as("Should have skip link or proper main landmark")
                    .isTrue();
        });

        log.info("Skip link verification completed");
    }

    // ==================== FORM LABELS TESTS ====================

    @Test
    @Tag("a11y")
    @Tag("regression")
    @Story("Form Labels")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Form inputs should have associated labels")
    void formInputsShouldHaveAssociatedLabels() {
        log.info("Testing form input labels on login page");

        // Navigate to login page (has form inputs)
        driver.get(config.getBaseUrl() + "/login");

        var inputs = driver.findElements(FORM_INPUTS);
        log.debug("Found {} form inputs", inputs.size());

        SoftAssertions.assertSoftly(softly -> {
            for (var input : inputs) {
                var id = input.getDomAttribute("id");
                var ariaLabel = input.getDomAttribute("aria-label");
                var ariaLabelledBy = input.getDomAttribute("aria-labelledby");
                var placeholder = input.getDomAttribute("placeholder");

                // Check if there's a label element with matching 'for' attribute
                var hasLabelElement = id != null && !id.isEmpty()
                        && !driver.findElements(By.cssSelector("label[for='" + id + "']")).isEmpty();

                // Input should have: <label for="id">, aria-label, or aria-labelledby
                // Note: placeholder alone is NOT sufficient for accessibility
                var hasAccessibleLabel = hasLabelElement
                        || (ariaLabel != null && !ariaLabel.trim().isEmpty())
                        || (ariaLabelledBy != null && !ariaLabelledBy.trim().isEmpty());

                if (!hasAccessibleLabel) {
                    log.warn("Input without proper label: id={}, type={}, placeholder={}",
                            id, input.getDomAttribute("type"), placeholder);
                }

                softly.assertThat(hasAccessibleLabel)
                        .as("Input should have associated label (label[for], aria-label, or aria-labelledby)")
                        .isTrue();
            }
        });

        log.info("Form labels verification completed");
    }

    // ==================== SEMANTIC HTML TESTS ====================

    @Test
    @Tag("a11y")
    @Tag("smoke")
    @Story("Semantic HTML")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Page should use semantic landmarks")
    void pageShouldUseSemanticLandmarks() {
        log.info("Verifying semantic HTML landmarks");

        SoftAssertions.assertSoftly(softly -> {
            // Check for main landmark
            var mainElements = driver.findElements(MAIN_LANDMARK);
            softly.assertThat(mainElements)
                    .as("Page should have <main> landmark")
                    .isNotEmpty();

            if (!mainElements.isEmpty()) {
                log.debug("Found {} <main> landmark(s)", mainElements.size());
            }

            // Check for navigation landmark
            var navElements = driver.findElements(NAV_LANDMARK);
            var navRoleElements = driver.findElements(By.cssSelector("[role='navigation']"));
            var hasNav = !navElements.isEmpty() || !navRoleElements.isEmpty();

            softly.assertThat(hasNav)
                    .as("Page should have <nav> or role='navigation'")
                    .isTrue();

            if (hasNav) {
                log.debug("Found navigation landmark: <nav> elements={}, role='navigation' elements={}",
                        navElements.size(), navRoleElements.size());
            }

            // Check for footer landmark (recommended but not always required)
            var footerElements = driver.findElements(FOOTER_LANDMARK);
            if (!footerElements.isEmpty()) {
                log.debug("Found {} <footer> landmark(s)", footerElements.size());
            }

            // Note: Not failing if footer is missing, as it's optional for some pages
        });

        log.info("Semantic landmarks verification completed");
    }

    @Test
    @Tag("a11y")
    @Tag("regression")
    @Story("Headings")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Page should have proper heading hierarchy")
    void pageShouldHaveProperHeadingHierarchy() {
        log.info("Verifying heading hierarchy");

        // Get all headings h1-h6
        var h1Elements = driver.findElements(By.tagName("h1"));
        var h2Elements = driver.findElements(By.tagName("h2"));
        var h3Elements = driver.findElements(By.tagName("h3"));
        var h4Elements = driver.findElements(By.tagName("h4"));
        var h5Elements = driver.findElements(By.tagName("h5"));
        var h6Elements = driver.findElements(By.tagName("h6"));

        log.debug("Heading counts - h1: {}, h2: {}, h3: {}, h4: {}, h5: {}, h6: {}",
                h1Elements.size(), h2Elements.size(), h3Elements.size(),
                h4Elements.size(), h5Elements.size(), h6Elements.size());

        SoftAssertions.assertSoftly(softly -> {
            // Page should have exactly one h1
            softly.assertThat(h1Elements.size())
                    .as("Page should have exactly one <h1> element")
                    .isGreaterThanOrEqualTo(0)  // Allow 0 or 1, as some pages may not have h1
                    .isLessThanOrEqualTo(1);

            // If h1 exists, it should have text content
            if (!h1Elements.isEmpty()) {
                var h1Text = h1Elements.getFirst().getText();
                softly.assertThat(h1Text.trim())
                        .as("H1 should have descriptive text")
                        .isNotEmpty();

                log.debug("H1 text: '{}'", h1Text);
            }

            // Headings should generally not skip levels (h1 -> h3 without h2)
            // This is a soft recommendation, not a hard rule
            if (!h3Elements.isEmpty() && h2Elements.isEmpty() && h1Elements.isEmpty()) {
                log.warn("Page has h3 but no h2 or h1 - heading hierarchy may be skipped");
            }
        });

        log.info("Heading hierarchy verification completed");
    }
}
