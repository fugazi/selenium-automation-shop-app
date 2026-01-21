---
name: webapp-selenium-testing
description: Toolkit for interacting with and testing local web applications using Selenium WebDriver and Java. Supports verifying frontend functionality, managing synchronization with explicit waits, and viewing browser and page source for analyzing logs.
---

# Web Application Testing with Selenium WebDriver & Java

This skill enables comprehensive testing, debugging, and execution of browser-based automation for web applications
using Selenium WebDriver within a Java environment.

## When to use this skill

Use this skill when you need to:

- Create new Selenium WebDriver tests using Java (JUnit 5)
- Interact with web elements (clicks, inputs, selections) in a real browser
- Verify UI behavior and interactions
- Implement robust synchronization using Explicit Waits
- Debug UI behavior and DOM interactions
- Debug failing browser tests
- Debug web application issues
- Inspect browser console logs
- Set up test infrastructure for a new project using Maven
- Capture screenshots (`TakesScreenshot`) for reporting, documentation or debugging
- Validate complex user flows and form submissions
- Check responsive design across viewports
- Validate accessibility when necessary: integrate automated checks (e.g., `axe-core` with Selenium)
- Validate API calls with `Rest-Assured` to ensure backend responses meet expectations.

## Your Role

You coordinate the entire Selenium WebDriver test creation process:

- **Analyze**: Understand the user's test requirements and data needs.
- **Inspect**: Identify locators strategies prioritizing `id`, `data-testid`, or `cssSelector`.
- **Design**: Apply the Page Object Model (POM) strictly.
- **Implement**: Generate the test script with complete error handling and logging.
- **Verify**: Ensure assertions use AssertJ fluent style.

## Reference Documentation

Before starting any test creation, read these files to understand the standards:

- *Recommended*: Use the `.github/instructions/selenium-webdriver-java.instructions.md` for the standard test instructions

## Prerequisites

- Java JDK 21 or higher installed
- Maven for dependency management
- Standard dependencies: `selenium-java`, `rest-assured`, `lombok`, `assertj-core`, `slf4j-api`, `javafaker`, and a test
  runner (`junit-jupiter`)
- A locally running web application (or accessible URL) for testing
- Selenium WebDriver will be configured automatically via `Maven` dependencies
- *Note*: Selenium Manager (included in Selenium 4.6+) automatically handles browser driver binaries

## Core Capabilities

### 1. Browser Automation

- **Navigation**: Navigate to URLs (`driver.get()`, `driver.navigate().to()`), handling tabs/windows (
  `getWindowHandles`), managing browser history (`driver.navigate().back()`, `forward()`, `refresh()`).
- **Interaction**: Click buttons and links (`click()`), input text (`sendKeys()`), clear fields (`clear()`), submit
  forms (`submit()`), drag and drop (`Actions` class), hover over elements, right-click, double-click.
- **Selects**: Handling dropdowns using the `Select` class.
- **Frames/Alerts**: Switching context with `driver.switchTo()` (e.g., `frame()`, `alert()`).
- **Cookies**: Managing browser cookies (`manage().addCookie()`, `getCookieNamed()`, `deleteCookieNamed()`).
- **Forms**: Fill form fields, handle file uploads.

### 2. Verification (Assertions)

- **Library**: STRICTLY use `AssertJ` for all validations.
- **Soft Assertions**: Use `SoftAssertions` when validating multiple fields in a single form/page to detect all errors in one run.
- **Checks**: Visibility, text content, attributes, URL, and page titles.
- Assert element presence and state (`isEnabled`, `isSelected`, `isDisplayed`).
- Validate element attributes (e.g., `hasAttribute`, `attributeContains`).
- Verify text content (`getText`).
- Validate page titles and URLs.
- Check element visibility
- Test responsive behavior
- Assert element attributes and properties.

### 3. Debugging & Reporting

- Capture screenshots on failure or demand.
- Extract page source for DOM analysis.
- View browser console logs (where supported by the driver).
- Inspect network requests
- Debug failed tests

### 4. Code Style & Patterns

Use these patterns to ensure code quality:

- **Data Models**: Use Lombok `@Data` and `@Builder` for Test Data Objects (POJOs).
- **Waits**: NEVER use `Thread.sleep`. Use `WebDriverWait` with `ExpectedConditions`.
- **Variables**: Use Java 21 `var` inference for local variables to improve readability.
- **Locators**: Avoid XPATH unless absolutely necessary.
- **Assertions**: Use `AssertJ` for readable validations.
- **Dynamic Data**: Use `JavaFaker` for generating random test data (names, emails).

## Best Practices Checklist

✅ Always verify the app is running - Check that the URL is accessible before running tests
✅ Clean up resources - Always close the browser when done
✅ Implement Page Object Model - Separate location logic from test logic
✅ Handle timeouts gracefully - Set reasonable timeouts for slow operations
✅ Test incrementally - Start with simple interactions before complex flows
✅ Use selectors wisely - Prefer `data-testid` or prioritize stable selectors over CSS classes
✅ Avoid hard-coded waits - Don't use `Thread.sleep()`, use always explicit waits
✅ Keep tests independent - Each test should be able to run in isolation

## Running tests

To run tests using Maven:

### Run all tests

```bash
mvn test
```

### Run specific test class

```bash
mvn test -Dtest=LoginTest
```

### Run specific test method

```bash
mvn test -Dtest=LoginTest#testValidLogin
```

Now help the user create their Selenium test!