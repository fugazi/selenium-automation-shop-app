---
name: accessibility-selenium-testing
description: Toolkit for validating Web Content Accessibility Guidelines (WCAG) compliance using Selenium WebDriver (Java) and the Axe Core engine. Supports full-page analysis, component-specific scanning, and rule filtering.
---

# Accessibility Testing with Selenium WebDriver & Axe Core

This skill enables automated accessibility analysis within the existing Selenium WebDriver framework. It utilizes the **Axe-core** engine to detect WCAG violations (A, AA, AAA) and Best Practice issues directly in the browser.

## When to use this skill

Use this skill when you need to:

- **Validate WCAG Compliance**: Ensure pages meet WCAG 2.1 or 2.2 Level A and AA standards.
- **Audit UI Components**: Scan specific modals, forms, keyboard navigation, color contrast ratios, or widgets in isolation.
- **Gatekeeping**: Prevent critical accessibility violations (like missing generic labels or low contrast) from reaching production.
- **Verification**: Ensure accessibility (ARIA attributes) is maintained across releases, preventing regressions.
- **Regression Testing**: Verify that new UI changes haven't introduced accessibility barriers.
- **Reporting**: Generate readable reports of accessibility violations for developers.
- **Filter Rules**: Exclude specific known issues or false positives from test failures.

## WCAG Levels

- **Level A**: Basic accessibility (must have)
- **Level AA**: Intermediate accessibility (should have, legal requirement in many jurisdictions)
- **Level AAA**: Advanced accessibility (nice to have)

## Your Role

As an Accessibility Automation Specialist, your role involves:

- **Integration**: Injecting the Axe script into the WebDriver instance.
- **Configuration**: Setting up the `AxeBuilder` with specific tags (e.g., "wcag2aa", "best-practice").
- **Analysis**: Parsing the JSON result from Axe to identify *violations*.
- **Assertion**: Failing the test only when legitimate violations exceed the threshold.
- **Reporting**: Logging specific details (Help URL, HTML target, Impact) to the console or report files.

## Reference Documentation

Before starting any test creation, read these files to understand the standards:

- **Axe Selenium Java Library**: [Deque Axe Selenium](https://github.com/dequelabs/axe-core-maven)
- **Rule Descriptions**: [Deque Axe Rules](https://dequeuniversity.com/rules/axe/4.8)
- **WCAG Standards**: [W3C WCAG 2.1](https://www.w3.org/TR/WCAG21/)
- **Documentation*: Use `Context7` MCP Server to navigate and explore more documentation.

## Prerequisites

- **Java JDK 21**
- **Maven Dependency**: `com.deque.html.axe-core/selenium` (Latest version)
- **Selenium WebDriver** instance (ChromeDriver/EdgeDriver)
- **AssertJ** (Recommended for readable failure messages)
- **Gson/Jackson** (Usually included with Axe) for parsing reports.

## Core Capabilities

### 1. Axe Builder Implementation
Utilize the Fluent API `AxeBuilder` to configure the scan:

- **Full Page Scan**: `new AxeBuilder().analyze(driver)`
- **Component Scan**: `new AxeBuilder().include(By.id("my-component")).analyze(driver)`
- **Rule Configuration**: `.withTags(Arrays.asList("wcag2a", "wcag2aa"))`
- **Exclusions**: `.exclude(By.cssSelector(".legacy-footer"))` (Use carefully)

### 2. Validation & Assertion
- **Violation Checking**: Analyze the `Results` object. `results.getViolations()` should be empty.
- **Impact Filtering**: Filter violations by impact level (Critical, Serious, Moderate, Minor).
- **Soft Assertions**: Use AssertJ or SoftAssertions to report *all* accessibility errors found on a page before failing the test.

### 3. Reporting
- **Console Logging**: Print readable violation summaries (Rule ID + Help URL + Selector).
- **Report Generation**: Serialize the `Results` object to JSON for external tools/dashboards.

## Code Style & Patterns

Use the following patterns for robust accessibility testing implementation:

### Basic Full-Page Analysis

```java
/**
 * Basic accessibility scan using axe-core with WCAG 2.1 AA compliance.
 * Recommended for most accessibility testing scenarios.
 */
public void verifyPageAccessibility(WebDriver driver) {
    Results results = new AxeBuilder()
        .withTags(List.of("wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "best-practice"))
        .analyze(driver);

    // Log violations for debugging
    logViolations(results.getViolations());

    // Assert no violations found
    assertThat(results.violationFree())
        .as("Accessibility violations found on page: %s", driver.getCurrentUrl())
        .isTrue();
}
```

### Component-Specific Scanning

```java
/**
 * Scan specific UI components for accessibility issues.
 * Useful for testing modals, forms, or isolated widgets.
 */
public void verifyComponentAccessibility(WebDriver driver, String... cssSelectors) {
    AxeBuilder builder = new AxeBuilder()
        .withTags(List.of("wcag2a", "wcag2aa"));
    
    // Include specific components
    for (String selector : cssSelectors) {
        builder.include(selector);
    }
    
    Results results = builder.analyze(driver);
    
    assertThat(results.violationFree())
        .as("Component accessibility check failed")
        .isTrue();
}
```

### Excluding Known Issues (Use Sparingly)

```java
/**
 * Analysis with exclusions for third-party components or known false positives.
 * Use exclusions carefully and document the reason.
 */
public void verifyAccessibilityWithExclusions(WebDriver driver) {
    Results results = new AxeBuilder()
        .withTags(List.of("wcag2a", "wcag2aa"))
        .exclude(".third-party-widget")           // Third-party component
        .exclude(".legacy-component")             // Legacy code (tracked in backlog)
        .disableRules(List.of("color-contrast"))  // Disable specific rules if needed
        .analyze(driver);

    assertThat(results.violationFree())
        .as("Accessibility violations found on page: %s", driver.getCurrentUrl())
        .isTrue();
}
```

### JUnit 5 Integration Example

```java
class AccessibilityTest {

    @Test
    void homePage_shouldBeAccessible() {
        driver.get("https://example.com");

        Results results = new AxeBuilder()
                .withTags(List.of("wcag2a", "wcag2aa", "wcag21aa"))
                .analyze(driver);

        assertThat(results.violationFree())
                .as("Homepage should meet WCAG 2.1 AA standards")
                .isTrue();

        // Assert only critical/serious violations fail the test
        List<Rule> criticalViolations = results.getViolations().stream()
                .filter(v -> List.of("critical", "serious").contains(v.getImpact()))
                .toList();
    }
}
```

## Manual Testing Checklist

### Keyboard Navigation
- [ ] All interactive elements accessible via Tab
- [ ] Shift+Tab navigates backwards
- [ ] Enter/Space activates buttons/links
- [ ] Arrow keys work in custom widgets
- [ ] Escape closes modals/dropdowns
- [ ] Skip links present and functional
- [ ] Focus visible on all elements
- [ ] No keyboard traps

### Screen Readers
- [ ] Page has descriptive title
- [ ] Headings form logical hierarchy
- [ ] Images have alt text
- [ ] Form inputs have labels
- [ ] Error messages announced
- [ ] Dynamic content changes announced
- [ ] Links have descriptive text
- [ ] Custom widgets have ARIA roles

### Visual
- [ ] Color contrast meets AA (4.5:1 normal, 3:1 large text)
- [ ] Information not conveyed by color alone
- [ ] Text can be resized 200% without breaking
- [ ] Focus indicators visible
- [ ] Content readable in dark/light modes

## Best Practices Checklist
✅ Test "Ready" States: Ensure the DOM is fully loaded and stable (no spinners) before triggering Axe analysis.
✅ Scan Unique States: Don't scan the header/footer in every test. Focus scans on unique page states (e.g., "Modal Open", "Form Error State").
✅ Zero Tolerance for Critical: Always fail the build on "Critical" and "Serious" impacts.
✅ Use Specific Tags: Define if you are testing for wcag2a, wcag2aa, or best-practice to avoid noise.
✅ Avoid "Sleeps": Axe injects JavaScript; ensure standard explicit waits are used before the injection.
✅ Readable Failures: Do not just print "Test Failed". Print the Help URL provided by Axe so developers can fix it.
✅ Component Isolation: If testing a large legacy app, include() specific new components to test them in isolation.