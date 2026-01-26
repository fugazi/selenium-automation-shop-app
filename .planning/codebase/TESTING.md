# Testing Patterns

**Analysis Date:** 2026-01-25

## Test Framework

**Runner:**
- JUnit 5 (Jupiter) [5.11.4]
- Config: Maven Surefire Plugin [3.2.5] in `pom.xml`

**Assertion Library:**
- AssertJ [3.27.3] - Preferred over JUnit assertions
- Soft Assertions: `org.assertj.core.api.SoftAssertions`

**Run Commands:**
```bash
# Run all tests
mvn clean test

# Run single test class
mvn test -Dtest=AddToCartTest

# Run single test method
mvn test -Dtest=AddToCartTest#shouldClickAddToCartButtonSuccessfully

# Run by tag
mvn test -Psmoke
mvn test -Pregression

# Run with specific browser
mvn test -Dbrowser=chrome
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge

# Run headless
mvn test -Dheadless=true

# Generate Allure report
mvn allure:serve
```

**Parallel Execution:**
- Configured in `pom.xml`: `<parallel>methods</parallel>`
- Thread count: 4
- Fork count: 2
- JUnit Jupiter parallel mode enabled

## Test File Organization

**Location:**
- Pattern: Co-located with main code (separate source root)
- Path: `src/test/java/org/fugazi/tests/`

**Naming:**
- Pattern: `[FeatureName]Test.java`
- Examples: `AddToCartTest.java`, `HomePageTest.java`, `SearchProductTest.java`
- All test classes must extend `BaseTest`

**Structure:**
```
src/
├── test/java/org/fugazi/
│   ├── config/          # Configuration classes
│   ├── data/           # Test data models and factories
│   │   ├── models/     # DTOs (User, Product, Credentials)
│   │   └── providers/  # TestDataFactory
│   ├── factory/         # WebDriverFactory
│   ├── listeners/       # AllureTestListener
│   ├── pages/          # Page Objects
│   │   └── components/ # Reusable UI components
│   ├── tests/          # Test classes
│   └── utils/          # Utility classes
└── test/resources/
    ├── config.properties
    ├── allure.properties
    └── testdata/        # JSON test data files
        ├── users.json
        └── products.json
```

## Test Structure

**Suite Organization:**
```java
@Epic("Music Tech Shop E2E Tests")
@Feature("Add to Cart")
@DisplayName("Add to Cart Tests")
class AddToCartTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Add Product")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Should click add to cart button successfully")
    void shouldClickAddToCartButtonSuccessfully() {
        // Arrange
        homePage().clickFirstProduct();
        var productTitle = productDetailPage().getProductTitle();

        // Assert preconditions
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(productDetailPage().isPageLoaded())
                    .as("Product detail page should be loaded")
                    .isTrue();
        });

        // Act
        productDetailPage().clickAddToCartAndWait();

        // Assert
        log.info("Successfully clicked add to cart for product: '{}'", productTitle);
    }
}
```

**Annotations Required:**
- `@Test` - Marks test method
- `@DisplayName("...")` - Human-readable test name
- `@Tag("smoke"|"regression")` - Test categorization
- `@Epic("...")` - Epic-level grouping (class level)
- `@Feature("...")` - Feature-level grouping (class level)
- `@Story("...")` - User story context
- `@Severity(SeverityLevel.xxx)` - BLOCKER, CRITICAL, NORMAL, MINOR, TRIVIAL

**Patterns:**
- Setup: `@BeforeEach` in `BaseTest` initializes WebDriver
- Teardown: `@AfterEach` in `BaseTest` quits WebDriver
- Page Objects: Lazy initialization via getter methods
- All test methods use `@Step` annotation via Page Objects

**Soft Assertions Pattern:**
```java
SoftAssertions.assertSoftly(softly -> {
    softly.assertThat(actual).as("Description").isEqualTo(expected);
    softly.assertThat(another).as("Another description").isNotNull();
});
```

**Mandatory `.as()` Descriptors:**
- Every assertion must have a descriptive `.as()` message
- Explains what is being tested and expected outcome
- Critical for test failure diagnosis

## Mocking

**Framework:** Not applicable for Selenium UI tests

**Patterns:**
- No mocking framework used (Mockito not in dependencies)
- Tests interact with real application via WebDriver
- API interactions for setup (e.g., creating test users) use RestAssured
- Example: `loginPage().loginWithCustomerAccount()` - performs real login

**What to Mock:**
- Not applicable - UI tests use real browser and application

**What NOT to Mock:**
- UI elements and interactions
- WebDriver and Page Objects
- Application state

## Fixtures and Factories

**Test Data:**
```java
// Using JavaFaker for dynamic test data
public class TestDataFactory {
    @Getter
    private static final Faker faker = new Faker(Locale.US);

    public static String generateSearchTerm() {
        return faker.commerce().material();
    }

    public static String generateInvalidSearchTerm() {
        return faker.regexify("[A-Z]{3}[0-9]{5}[a-z]{3}");
    }
}
```

**Location:**
- `src/test/java/org/fugazi/data/providers/TestDataFactory.java`
- Static JSON files in `src/test/resources/testdata/`
  - `users.json` - Test user credentials
  - `products.json` - Expected product data

**Usage Pattern:**
```java
// Use Faker for non-deterministic fields
var searchTerm = TestDataFactory.generateSearchTerm();

// Use JSON files for reference data
var user = User.builder()
    .email("test@example.com")
    .password("Test@123")
    .build();
```

## Coverage

**Requirements:** None enforced in pom.xml

**View Coverage:**
- No JaCoCo or similar coverage plugin detected
- Coverage metrics not generated automatically

**Note:** While Allure is used for reporting, it doesn't provide code coverage metrics. Consider adding JaCoCo plugin if coverage requirements arise.

## Test Types

**Unit Tests:**
- Not present in this codebase
- Focus is on E2E UI testing

**Integration Tests:**
- Selenium WebDriver tests classify as integration/E2E
- Test full user workflows through application
- Tests validate UI interactions, navigation, and state changes

**E2E Tests:**
- Framework: Selenium WebDriver [4.27.0]
- Browsers: Chrome, Firefox, Edge (configurable)
- Default: Edge
- Headless mode supported

**Test Categories:**
- `smoke` - Critical path tests, run on every build
- `regression` - Comprehensive test suite
- Profiles in `pom.xml` for running by category

## Common Patterns

**Async Testing:**
- Uses explicit waits (WebDriverWait) instead of `Thread.sleep()`
- Framework rule: Never use `Thread.sleep()`
- Wait helpers in `BasePage`:
  ```java
  waitForVisibility(By locator)
  waitForClickable(By locator)
  waitForPresence(By locator)
  waitForPageLoad()
  waitForUrlChange(String currentUrl)
  waitForAnimationsToComplete()
  ```

**Error Testing:**
```java
@Test
void shouldHandleInvalidSearch() {
    var invalidTerm = TestDataFactory.generateInvalidSearchTerm();
    homePage().searchProduct(invalidTerm);

    SoftAssertions.assertSoftly(softly -> {
        var hasNoResults = !searchResultsPage().hasResults() ||
                searchResultsPage().isNoResultsMessageDisplayed();

        softly.assertThat(hasNoResults)
                .as("Should show no results for invalid search")
                .isTrue();
    });
}
```

**Parameterized Testing:**
```java
@ParameterizedTest(name = "Navigate to {0} page from footer")
@CsvSource({
    "about, /about",
    "shipping, /shipping",
    "returns, /returns",
    "terms, /terms"
})
@Tag("regression")
@Story("Information Links")
@DisplayName("Should navigate to information page from footer link")
void shouldNavigateToInformationPageFromFooterLink(String linkName, String expectedUrlPart) {
    var footer = getFooterComponent();
    switch (linkName.toLowerCase()) {
        case "about" -> footer.clickAboutLink();
        case "shipping" -> footer.clickShippingLink();
        case "returns" -> footer.clickReturnsLink();
        case "terms" -> footer.clickTermsLink();
    }

    SoftAssertions.assertSoftly(softly -> {
        softly.assertThat(driver.getCurrentUrl())
                .as("URL should contain " + expectedUrlPart)
                .contains(expectedUrlPart);
    });
}
```

**Page Object Initialization:**
```java
// Lazy initialization in BaseTest
protected HomePage homePage() {
    if (homePage == null) {
        homePage = new HomePage(driver);
    }
    return homePage;
}

// Usage in tests
homePage().clickFirstProduct();
```

**Allure Reporting Integration:**
```java
// Page Object methods
@Step("Add product to cart with quantity: {quantity}")
public CartPage addToCart(int quantity) {
    // Implementation
}

// Test class annotations
@Epic("Music Tech Shop E2E Tests")
@Feature("Add to Cart")
@Story("Add Product")
@Severity(SeverityLevel.BLOCKER)
```

**Screenshot on Failure:**
- Automatic via `AllureTestListener`
- Captures screenshot and page source on test failure
- Saves to `target/screenshots/` for local debugging
- Attaches to Allure report

---

*Testing analysis: 2026-01-25*
