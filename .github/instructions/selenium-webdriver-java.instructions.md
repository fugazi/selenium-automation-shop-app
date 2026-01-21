
description: 'Selenium WebDriver with Java test generation instructions'
applyTo: 'src/test/java/**/*.java', 'src/main/java/**/*.java'
---

## Test Writing Guidelines

### Code Quality Standards

- **Locators**: Prioritize stable selectors. Use `By.id()`, `By.name()`, or `By.cssSelector("[data-testid='...']")`.
  Avoid fragile XPaths based on absolute indexes. Use meaningful variable names for `WebElement` instances.
- **Page Object Model (POM)**: Every test must interact with the UI through Page Object classes. Encapsulate element
  locators and interaction logic within these classes.
- **Explicit Waits**: **Never** use `Thread.sleep()`. Rely exclusively on `WebDriverWait` and `ExpectedConditions` to
  handle asynchronous elements (visibility, clickability, presence).
- **Fluent Interface**: Design Page Object methods to return `this` or the next `Page` object to allow method chaining,
  improving readability.
- **Clean Code**: Follow SOLID principles. Keep tests focused on business logic and Page Objects focused on
  implementation details.

### Modern Java Standards

- **Records**: Use Java Records for immutable data carriers (e.g., test data, configuration).
- **Streams API**: Leverage Streams API for collection processing with lambda expressions.
- **Optional**: Use `Optional` for values that may or may not be present, avoiding `NullPointerExceptions`.
- - **Regular Expressions (Regex)**: Use `Pattern` and `Matcher` for text validation and extraction.
- **Lombok Integration**: Use `@Slf4j` for logging instead of `System.out.println`. Use `@Getter` and `@Builder` for
  POJOs or Data Models to keep the code clean.
- **Reporting (Allure)**: Annotate Page Object methods with `@Step("Description of action")` to ensure detailed
  execution steps in reports. Use `@Epic`, `@Feature`, `@Story`, `@Description`, and `@Severity` to categorize and
  enrich test documentation.
- **Data Generation (JavaFaker)**: Use `Faker` to generate dynamic test data (emails, names, addresses) to ensure test
  independence and prevent data collisions.

### Test Structure (JUnit 5)

- **Annotations**: Use `@Test` for test methods, `@BeforeEach` (JUnit 5) for setup, and `@AfterEach` (JUnit 5) for
  teardown (closing the driver).
- **Naming Convention**: Use descriptive names for test classes and methods. Class: `FeatureNameTest.java`. Method:
  `should[ExpectedResult]When[Action]`.
- **Grouping**: Every test class should have `@Tag` or `@Test(groups = {...})` to categorize tests (e.g.,
  `@Tag("smoke")` or `@Tag("regression")`).
- **Display Names**: Every test method should have `@DisplayName("Human readable title")` (JUnit 5) to improve
  reporting.
- **Parameterization**: Use `@ParameterizedTest` and `@MethodSource` or `@CsvSource` for data-driven testing.

### Assertion Best Practices (Soft Assertions First)

- **Soft Assertions (AssertJ)**: **Mandatory use of Soft Assertions** for multiple validations in a single test. This
  allows the test to continue executing and report all failures at the end.
- **Implementation**: Prefer `SoftAssertions.assertSoftly(softly -> { ... })` to ensure `assertAll()` is triggered
  automatically.
- **Descriptive Assertions**: Always use the `.as("Error message detail")` method before any assertion to provide
  context in Allure reports. This is crucial for debugging and understanding test failures.
- **State Verification**: Assert not just the final outcome, but also key state transitions if they are critical to the
  flow.
- **Collection Assertions**: Use AssertJ's collection-specific assertions for better readability.

### File Organization (Maven Standard)

- **Pages**: `src/main/java/com/project/pages/` (Page Objects + Allure Steps).
- **Tests**: `src/test/java/com/project/tests/` (Test logic + Soft Assertions).
- **Models**: `src/main/java/com/project/models/` (Lombok-annotated DTOs for JSON/API).
- **Utilities**: `src/main/java/com/project/utils/` (Wait wrappers, Faker providers, HttpClient clients).
- **Configuration**: `src/main/resources/` (Test data, environment properties).
- **Factories**: `src/main/java/com/project/factories/` (Driver factory, Page Object factories).
- **Components**: `src/main/java/com/project/components/` (Reusable UI components like headers, footers, modals).
-
    - **Enums**: `src/main/java/com/project/enums/` (Application-specific enumerations).
- **Base Classes**: `src/main/java/com/project/base/` (BaseTest for driver initialization/cleanup and BasePage for
  common element interactions and wait wrappers).

---

## Example Test Structure (Java + Selenium + JUnit 5)

### WebDriver Initialization

```java
 /**
 * Initialize the WebDriverManager with EdgeDriver.
 * Go to the website under Test and maximize the browser window.
 */
@BeforeEach
public void setupUrl() {
    driver = new EdgeDriver();
    driver.manage().window().maximize();
    getBaseUrl(driver);
}

/**
 * Close the browser window.
 */
@AfterEach
public void tearDown() {
    driver.quit();
}
```

### Page Object Example

```java
package com.project.pages;

import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

@Slf4j
public class InventoryPage extends BasePage {
    private final By headerTitle = By.className("title");
    private final By firstItemName = By.className("inventory_item_name");
    private final By cartBadge = By.className("shopping_cart_badge");

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    @Step("Get inventory header text")
    public String getHeaderText() {
        return getText(headerTitle);
    }

    @Step("Get first item name")
    public String getFirstItemName() {
        return getText(firstItemName);
    }

    @Step("Check if cart badge is displayed")
    public boolean isCartBadgeVisible() {
        return isDisplayed(cartBadge);
    }
}
```

### Test Class with Soft Assertions (AssertJ)

```java
package com.project.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;

@Epic("E-commerce Flow")
@Feature("Inventory Display")
class InventoryTest extends BaseTest {

    @Test
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should verify inventory page elements correctly")
    void verifyInventoryPageElements() {
        // Act
        loginPage.login("standard_user", "secret_sauce");

        // Soft Assertions Block
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(inventoryPage.getHeaderText())
                    .as("Inventory header title")
                    .isEqualToIgnoringCase("Products");

            softly.assertThat(inventoryPage.getFirstItemName())
                    .as("First item name should not be empty")
                    .isNotEmpty();

            softly.assertThat(inventoryPage.isCartBadgeVisible())
                    .as("Cart badge should be hidden by default")
                    .isFalse();
        });
    }
}
```

---

## Technical Integration Specifics

- **Wait Strategy (Selenium 4)**: Standardize waits using new `WebDriverWait(driver, Duration.ofSeconds(15));`.
- **API Interaction (HttpClient 5 + Jackson)**: Use `HttpClient` for backend setups (e.g., creating a user via API) and
  Jackson for mapping API responses. Use `@JsonProperty` in DTOs for mapping API responses.
- **Logging**: Use `log.info("Action: {}", parameter)` to keep the console and logs informative. Avoid `System.out.println()`.
- **Configuration Management**: Utilize a dedicated configuration class (e.g., `ConfigReader.java`) to load properties
  from `src/main/resources/config.properties` for environment-specific settings (URLs, API keys).
- **Driver Management**: Implement a `WebDriverFactory` to provide different browser drivers (Chrome, Firefox, Edge)
  based on configuration, ensuring proper setup and teardown.

## Quality Checklist for LLM

Before finalizing tests, ensure:

* [ ] No `Thread.sleep()` is present in the code.
* [ ] All UI interactions go through a `Page Object`.
* [ ] Explicit waits are used for every dynamic element interaction.
* [ ] The driver is properly instantiated and closed in the `Base` class.
* [ ] Softly Assertions are clear and provide descriptive failure messages.
* [ ] Code follows Java CamelCase naming conventions.
* [ ] Uses `@Slf4j` for all logging.
* [ ] Implements `@Step` in all Page Object action methods.
* [ ] Uses `Duration` instead of int for timeouts (Selenium 4 compliance).
* [ ] Generates dynamic data with `Faker` for non-deterministic fields.
* [ ] Properly handles JSON/API DTOs using `Lombok` and `Jackson`.
* [ ] Includes AssertJ `assertThat` with descriptive `.as()` messages.
* [ ] Avoid `System.out.println()` in favor of SLF4J.
* [ ] All test classes extend `BaseTest`.
* [ ] All Page Object methods return `this` or the next `Page` object.
* [ ] All test methods have `@DisplayName` and `@Tag` annotations.
* [ ] All test methods use `@ParameterizedTest` and `@MethodSource` or `@CsvSource` for data-driven testing.
* [ ] Uses Java new features: Records, Pattern Matching, Sequenced Collections (`.getFirst()`, `.getLast()`).
* [ ] Uses Streams API and Lambda expressions where appropriate.
* [ ] Uses `Optional` for nullable values.
* [ ] Uses Regular Expressions (Pattern/Matcher) for validation and extraction.
* [ ] All collections use `.toList()` instead of `.collect(Collectors.toList())`.
