# Coding Conventions

**Analysis Date:** 2026-01-25

## Naming Patterns

**Files:**
- Classes: PascalCase (e.g., `HomePage.java`, `CartPage.java`)
- Test classes: PascalCase ending with `Test` (e.g., `AddToCartTest.java`, `HomePageTest.java`)
- Page Objects: Singular (e.g., `CartPage`, `HomePage`, `LoginPage`)
- Data models: PascalCase (e.g., `User.java`, `Product.java`, `Credentials.java`)
- Components: PascalCase with `Component` suffix (e.g., `HeaderComponent.java`, `FooterComponent.java`)

**Methods:**
- Test methods: camelCase, pattern: `should[ExpectedResult]When[Action]` or `should[ExpectedResult]` (e.g., `shouldClickAddToCartButtonSuccessfully`, `shouldLoadHomePageSuccessfully`)
- Action methods: camelCase, descriptive verbs (e.g., `getFeaturedProducts`, `clickFirstProduct`, `searchProduct`)
- Verification methods: camelCase, starting with `is` (e.g., `isPageLoaded`, `isDisplayed`, `isEnabled`)

**Variables:**
- Local variables: camelCase, meaningful names (e.g., `searchTerm`, `productCount`, `cartItems`)
- WebElement locators: Include locator type in name (e.g., `By searchButton`, `By cartItems`)
- `var` keyword: Used for local variable type inference (Java 10+)

**Types:**
- Constants: UPPER_SNAKE_CASE (e.g., `HERO_SECTION`, `CART_ITEMS`, `CART_TOTAL`)
- Enums: PascalCase (e.g., `BrowserType`)

**Packages:**
- All lowercase (e.g., `org.fugazi.pages`, `org.fugazi.tests`, `org.fugazi.config`)

## Code Style

**Formatting:**
- No formal formatter plugin (spotless, checkstyle) detected in pom.xml
- Line length: 120 characters
- Indentation: 4 spaces (no tabs)
- Braces: Allman style (opening brace on new line)
- Single blank line between methods
- Double blank line between class sections

**Linting:**
- No explicit linting configuration detected
- Relies on compiler warnings and IDE linting

## Import Organization

**Order:**
1. `java.*`
2. `javax.*`
3. `org.*` (excluding Allure, JUnit, AssertJ, SLF4J)
4. `io.qameta.allure.*`
5. `org.junit.jupiter.*`
6. `org.assertj.*`
7. `org.slf4j.*`
8. Project imports (`org.fugazi.*`)

**Path Aliases:**
- None detected - all imports use fully qualified package names

**Allowed wildcards:**
```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import io.qameta.allure.*;
```

## Error Handling

**Patterns:**
- All Page Object methods use explicit waits with `WebDriverWait`
- Never use `Thread.sleep()` - framework compliance rule
- Graceful element checks with try-catch for `NoSuchElementException` and `StaleElementReferenceException`
- Example from `BasePage.isDisplayed()`:
  ```java
  protected boolean isDisplayed(By locator) {
      try {
          return driver.findElement(locator).isDisplayed();
      } catch (NoSuchElementException | StaleElementReferenceException e) {
          log.debug("Element not displayed: {}", locator);
          return false;
      }
  }
  ```

**WebDriver cleanup:**
- Graceful cleanup in `BaseTest.tearDown()`:
  ```java
  if (driver != null) {
      try {
          driver.quit();
      } catch (Exception e) {
          log.error("Error closing WebDriver: {}", e.getMessage());
      } finally {
          AllureTestListener.clearDriver();
      }
  }
  ```

**Retry logic:**
- Used for navigation and element interaction reliability
- See `BaseTest.navigateToBaseUrl()` for retry pattern

## Logging

**Framework:** SLF4J with `@Slf4j` annotation

**Patterns:**
- Never use `System.out.println()` - framework violation
- Use parameterized logging: `log.info("Action: {}", parameter)`
- Log levels:
  - `log.info()`: Major test actions and state changes
  - `log.debug()`: Detailed interaction steps
  - `log.warn()`: Non-critical issues, workarounds
  - `log.error()`: Failures and exceptions
- Example:
  ```java
  log.info("Clicked add to cart for product: '{}'", productTitle);
  log.debug("Waiting for element: {}", locator);
  ```

**Page Object logging:**
- All Page Object action methods log their actions via base class
- No console output in page objects

## Comments

**When to Comment:**
- Public methods have Javadoc
- Complex business logic explained inline
- Non-obvious behavior documented
- TODO comments for future improvements

**Javadoc/TSDoc:**
- Standard Javadoc format used for public methods
- Includes `@param`, `@return`, `@throws` tags
- Example from `BasePage`:
  ```java
  /**
   * Wait for an element to be visible on the page.
   *
   * @param locator the element locator
   * @return the visible WebElement
   */
  protected WebElement waitForVisibility(By locator) {
      log.debug("Waiting for visibility of element: {}", locator);
      return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
  }
  ```

## Function Design

**Size:**
- Methods typically under 20 lines
- Large methods (>50 lines) rare and indicate need for refactoring

**Parameters:**
- Descriptive parameter names
- `@Step` annotations include parameter names for Allure reports:
  ```java
  @Step("Search for: {searchTerm}")
  public void searchProduct(String searchTerm) { }
  ```

**Return Values:**
- Fluent interface: Methods return `this` for chaining
- Navigation: Methods return next `Page` object
- Query methods: Return appropriate types (boolean, int, String, List<T>)

## Module Design

**Exports:**
- All Page Objects extend `BasePage`
- All Tests extend `BaseTest`
- No explicit barrel files detected

**Component pattern:**
- Reusable UI components in `org.fugazi.pages.components`
- Components extend `BasePage`
- Used by pages: `HeaderComponent`, `FooterComponent`

**Factory pattern:**
- `WebDriverFactory` creates browser instances
- `TestDataFactory` generates test data using JavaFaker
- `ConfigurationManager` singleton pattern with double-checked locking

**Builder pattern:**
- Custom builders in data models (e.g., `User.UserBuilder`)
- Also uses Lombok's `@Builder` annotation

---

*Convention analysis: 2026-01-25*
