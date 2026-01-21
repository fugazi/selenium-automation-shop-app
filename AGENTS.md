# AGENTS.md

This file provides guidelines and instructions for AI agents operating in this Selenium automation repository.

## Project Overview

Java 21 Maven project for automated UI testing using Selenium WebDriver. Tests validate the Music Tech Shop e-commerce application with JUnit 5, AssertJ, and Allure reporting.

## Build and Test Commands

### Running Tests

**Run all tests:** `mvn clean test`

**Run a single test class:** `mvn test -Dtest=AddToCartTest`

**Run a single test method:** `mvn test -Dtest=AddToCartTest#shouldClickAddToCartButtonSuccessfully`

**Run tests by tag:** `mvn test -Psmoke` or `mvn test -Pregression`

**Run with specific browser:** `mvn test -Dbrowser=chrome|firefox|edge`

**Run in headless mode:** `mvn test -Dheadless=true`

**Generate Allure report:** `mvn allure:serve`

### Build Commands

**Compile (no tests):** `mvn compile`

**Skip tests during build:** `mvn clean install -DskipTests`

## Code Style Guidelines

### General Principles

- Follow SOLID principles
- Use Java Records for immutable data carriers (test data, DTOs, configuration)
- Leverage Streams API and lambdas
- Use `Optional` for nullable values
- Use `var` for local variable type inference
- Use `Duration.ofSeconds()` for timeouts (Selenium 4 compliance)
- Use Sequenced Collections methods (`.getFirst()`, `.getLast()`) where applicable
- Use `Pattern` and `Matcher` for text validation and extraction
- Avoid XPath with absolute indexes - use relative XPaths only when necessary

### Data Generation

**Use JavaFaker for dynamic test data:**
```java
Faker faker = new Faker();
String email = faker.internet().emailAddress();
String name = faker.name().fullName();
String address = faker.address().fullAddress();
```
This ensures test independence and prevents data collisions.

### Naming Conventions

**Classes:** PascalCase, test classes end with `Test` (e.g., `AddToCartTest`), Page Objects are singular (e.g., `CartPage`)

**Methods:** camelCase, pattern: `should[ExpectedResult]When[Action]`

**Variables:** camelCase, meaningful names; `WebElement` variables include locator type: `By searchButton = By.id("search")`

**Constants:** UPPER_SNAKE_CASE

**Packages:** lowercase (e.g., `org.fugazi.pages`)

### Import Organization

Order: java.* → javax.* → org.* → io.qameta.allure.* → org.junit.jupiter.* → org.assertj.* → org.slf4j.* → project imports

**Allowed wildcards:**
```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import io.qameta.allure.*;
```

### Code Formatting

- Line length: 120 characters
- Indentation: 4 spaces (no tabs)
- Braces: Allman style (opening brace on new line)
- Single blank line between methods, double between class sections

### Page Object Model

**Locator priority:** `By.id()` → `By.name()` → `By.cssSelector("[data-testid='...']`) → `By.xpath()` (last resort)

**Structure:**
- Methods return `this` for chaining
- Methods return next `Page` object for navigation
- Locators are `private final` fields
- All action methods have `@Step` annotation

### Wait Strategy

**Never use `Thread.sleep()`** - always use explicit waits with `WebDriverWait`:

```text
wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
```

**Wait helper methods in BasePage:**
- `waitForVisibility(By)` | `waitForClickable(By)` | `waitForPresence(By)` | `waitForPageLoad()` | `waitForUrlChange(String)`

### Assertion Standards

**Mandatory Soft Assertions:**
```text
SoftAssertions.assertSoftly(softly -> {
    softly.assertThat(actual).as("Description").isEqualTo(expected);
});
```

**Always use `.as()`** for descriptive failure messages

**AssertJ preferred over JUnit assertions**

**Collection Assertions:** Use AssertJ's collection-specific assertions for better readability.

### Test Structure

**Required annotations:**
```java
@Epic("...")
@Feature("...")
@DisplayName("...")
@Test
@Tag("smoke"|"regression")
@Story("...")
@Severity(SeverityLevel.xxx)
@DisplayName("...")
void shouldDoSomething() { }
```

**All test classes extend `BaseTest`**, use lazy initialization for Page Objects: `homePage()`, `cartPage()`, etc.

**Parameterized Tests:** Use `@ParameterizedTest` with `@MethodSource` or `@CsvSource` for data-driven testing.

**State Verification:** Assert not just the final outcome, but also key state transitions if they are critical to the flow.

### Logging

**Use `@Slf4j`** - never `System.out.println()`:
```text
log.info("Action: {}", parameter);
log.debug("Debug info");
log.error("Error: {}", e.getMessage());
```

### Lombok Usage

- `@Slf4j` - logger
- `@Getter`/`@Setter` - accessors
- `@Builder` - builder pattern
- `@Data` - combines getters/setters/toString/equals/hashCode

### Allure Reporting

**Annotate Page Object methods:**
```java
@Step("Add product to cart with quantity: {quantity}")
public CartPage addToCart(int quantity) { }
```

**Test class annotations:** `@Epic`, `@Feature`, `@Story`, `@Description`, `@Severity`

### Error Handling

**WebDriver cleanup:**
```java
@AfterEach
void tearDown() {
    if (driver != null) {
        try { driver.quit(); }
        catch (Exception e) { log.error("Error: {}", e.getMessage()); }
        finally { AllureTestListener.clearDriver(); }
    }
}
```

**Graceful element checks:**
```java
protected boolean isDisplayed(By locator) {
    try { return driver.findElement(locator).isDisplayed(); }
    catch (NoSuchElementException | StaleElementReferenceException e) {
        log.debug("Element not displayed: {}", locator);
        return false;
    }
}
```

### Configuration

**Use ConfigurationManager singleton:**
```java
ConfigurationManager config = ConfigurationManager.getInstance();
String url = config.getBaseUrl();
boolean headless = config.isHeadless();
```

**Override via:** `mvn test -Dbrowser=chrome -Dheadless=true -Dbase.url=...`

## File Organization

```
src/main/java/org/fugazi/
├── config/          # Configuration classes
├── data/            # Test data models and factories
├── factory/         # WebDriver factory
├── listeners/       # Allure and test listeners
├── pages/           # Page Objects
│   └── components/  # Reusable UI components
└── utils/           # Utility classes

src/test/java/org/fugazi/
└── tests/           # Test classes

src/test/resources/
├── config.properties
├── testdata/        # JSON test data
└── categories.json  # Allure categories
```

## Existing Agent Instructions

Reference `.github/instructions/` for additional guidance:
- `selenium-webdriver-java.instructions.md` - Selenium guidelines
- `agents.instructions.md` - Custom agent file creation
- `agent-skills.instructions.md` - Agent skills creation

## Browser Support

Chrome, Firefox, Edge. Default: Edge.

## Environment Variables

- `browser` - Override browser type
- `headless` - Run headless (true/false)
- `base.url` - Target application URL
- `explicit.wait.seconds` - Custom wait timeout

## Technical Integration

**API Interaction (HttpClient 5 + Jackson):** Use `HttpClient` for backend setups (e.g., creating test users via API) and Jackson for mapping API responses. Use `@JsonProperty` in DTOs for mapping API responses.

**Driver Management:** `WebDriverFactory` provides different browser drivers (Chrome, Firefox, Edge) based on configuration, ensuring proper setup and teardown.

## Quality Checklist

Before finalizing tests, ensure:

- [ ] No `Thread.sleep()` is present in the code
- [ ] All UI interactions go through a `Page Object`
- [ ] Explicit waits are used for every dynamic element interaction
- [ ] The driver is properly instantiated and closed in `BaseTest`
- [ ] Soft Assertions are clear and provide descriptive `.as()` messages
- [ ] Uses `@Slf4j` for all logging (never `System.out.println()`)
- [ ] Implements `@Step` in all Page Object action methods
- [ ] Uses `Duration` instead of int for timeouts (Selenium 4 compliance)
- [ ] Generates dynamic data with `Faker` for non-deterministic fields
- [ ] Properly handles JSON/API DTOs using `Lombok` and `Jackson`
- [ ] All test classes extend `BaseTest`
- [ ] All test methods have `@DisplayName` and `@Tag` annotations
