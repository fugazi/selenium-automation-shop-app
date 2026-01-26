# Architecture

**Analysis Date:** 2026-01-25

## Pattern Overview

**Overall:** Page Object Model (POM) with Factory and Singleton patterns

**Key Characteristics:**
- Page Object Model for UI abstraction
- Factory pattern for WebDriver instantiation
- Singleton pattern for configuration management
- Component-based architecture (HeaderComponent, FooterComponent)
- Lazy initialization pattern for Page Objects
- Test-driven architecture with JUnit 5
- Data generation using JavaFaker

## Layers

**Test Layer:**
- Purpose: Orchestrates test scenarios and verifies expected outcomes
- Location: `src/test/java/org/fugazi/tests/`
- Contains: Test classes extending BaseTest (e.g., `AddToCartTest.java`, `CartWorkflowTest.java`)
- Depends on: Page Objects, BaseTest, AssertJ
- Used by: Maven Surefire plugin, JUnit 5

**Page Object Layer:**
- Purpose: Encapsulates UI interactions for each application page
- Location: `src/test/java/org/fugazi/pages/`
- Contains: Page classes (e.g., `HomePage.java`, `CartPage.java`, `ProductDetailPage.java`) and reusable components (e.g., `HeaderComponent.java`, `FooterComponent.java`)
- Depends on: BasePage, WebDriver, Selenium locators
- Used by: Test classes, BaseTest lazy getters

**Factory Layer:**
- Purpose: Creates and configures WebDriver instances
- Location: `src/test/java/org/fugazi/factory/`
- Contains: `WebDriverFactory.java`
- Depends on: ConfigurationManager, Selenium WebDriver
- Used by: BaseTest.setUp()

**Configuration Layer:**
- Purpose: Manages application configuration and environment settings
- Location: `src/test/java/org/fugazi/config/`
- Contains: `ConfigurationManager.java`, `BrowserType.java`
- Depends on: Java Properties, System properties
- Used by: WebDriverFactory, BaseTest, all Page Objects

**Data Layer:**
- Purpose: Provides test data models and generation factories
- Location: `src/test/java/org/fugazi/data/`
- Contains: Data models (e.g., `Product.java`, `User.java`), `TestDataFactory.java`
- Depends on: JavaFaker, Lombok
- Used by: Test classes for test data generation

**Utility Layer:**
- Purpose: Provides common helper functionality
- Location: `src/test/java/org/fugazi/utils/`
- Contains: `ScreenshotUtils.java`
- Depends on: Selenium WebDriver, SLF4J
- Used by: Listeners, Page Objects

**Listener Layer:**
- Purpose: Hooks into test lifecycle for reporting and debugging
- Location: `src/test/java/org/fugazi/listeners/`
- Contains: `AllureTestListener.java`
- Depends on: JUnit 5 Extension API, Allure
- Used by: BaseTest via @ExtendWith annotation

## Data Flow

**Test Execution Flow:**

1. Maven Surefire plugin invokes JUnit 5 test runner
2. BaseTest.@BeforeEach creates WebDriver via WebDriverFactory
3. ConfigurationManager loads configuration (properties file → system properties → defaults)
4. WebDriver navigates to base URL (config.getBaseUrl())
5. Test method calls lazy page object getter (e.g., `homePage()`)
6. Page Object method executes UI action with explicit waits
7. AssertJ SoftAssertions verify expected state
8. BaseTest.@AfterEach quits WebDriver and clears ThreadLocal storage
9. AllureTestListener captures screenshots/page source on failure

**Page Navigation Flow:**

1. Test calls action method on Page Object (e.g., `homePage().clickFirstProduct()`)
2. Page method performs click, waits for URL change
3. Method returns next Page Object (e.g., `productDetailPage()`)
4. Test continues with new page object
5. Page.isPageLoaded() verifies correct page state

**Configuration Flow:**

1. ConfigurationManager.getInstance() called (singleton pattern)
2. Load properties from `src/test/resources/config.properties`
3. Check for system property overrides (-Dbrowser=chrome)
4. Fall back to defaults if no config found
5. Return configuration values to callers

**State Management:**
- WebDriver stored in ThreadLocal in AllureTestListener (for parallel execution)
- Page Objects initialized lazily in BaseTest (cached per test)
- No shared state between tests (fresh WebDriver per test)
- Configuration is singleton (read-only after initialization)

## Key Abstractions

**BasePage:**
- Purpose: Abstract base class providing common Page Object functionality
- Examples: `src/test/java/org/fugazi/pages/BasePage.java`
- Pattern: Template Method (isPageLoaded() abstract, other methods concrete)
- Provides: Wait strategies (waitForVisibility, waitForClickable, waitForPageLoad), common actions (click, type, getText, isDisplayed), JavaScript execution, scroll utilities

**BaseTest:**
- Purpose: Abstract base test class with common setup/teardown
- Examples: `src/test/java/org/fugazi/tests/BaseTest.java`
- Pattern: Lazy initialization for Page Objects
- Provides: WebDriver lifecycle, lazy Page Object getters (homePage(), cartPage(), etc.), SoftAssertions helper, navigation utilities

**WebDriverFactory:**
- Purpose: Creates configured WebDriver instances
- Examples: `src/test/java/org/fugazi/factory/WebDriverFactory.java`
- Pattern: Factory Method with switch expression
- Supports: Chrome, Firefox, Edge with headless option

**ConfigurationManager:**
- Purpose: Centralized configuration management
- Examples: `src/test/java/org/fugazi/config/ConfigurationManager.java`
- Pattern: Singleton with double-checked locking
- Priority: System property > Config file > Default value

**Page Components:**
- Purpose: Reusable UI elements shared across pages
- Examples: `src/test/java/org/fugazi/pages/components/HeaderComponent.java`, `FooterComponent.java`
- Pattern: Composition (Pages use components)
- Extends: BasePage

## Entry Points

**Maven Test Execution:**
- Location: Command line (`mvn test`)
- Triggers: Maven Surefire plugin
- Responsibilities: Compiles tests, runs JUnit 5, generates Allure results

**JUnit 5 Test Runner:**
- Location: JUnit 5 platform (via Maven)
- Triggers: Test class execution
- Responsibilities: Discover tests, run with parallel execution, manage lifecycle hooks

**BaseTest.setUp():**
- Location: `src/test/java/org/fugazi/tests/BaseTest.java` (line 58-73)
- Triggers: @BeforeEach annotation
- Responsibilities: Create WebDriver, set Allure driver reference, navigate to base URL

**Main Application Entry Point:**
- Location: `src/main/java/org/fugazi/Main.java`
- Triggers: Not used in test context (placeholder main method)
- Responsibilities: None for testing (test framework handles execution)

## Error Handling

**Strategy:** Defensive programming with explicit waits and graceful degradation

**Patterns:**
- **WebDriver Wait:** All element interactions wrapped in explicit waits (ExpectedConditions)
- **Retry Logic:** Navigation failures retried with exponential backoff
- **Stale Element Handling:** Catch StaleElementReferenceException and retry click
- **Soft Assertions:** Collect all assertion failures before failing test
- **Graceful Element Checks:** isDisplayed() returns false rather than throwing exception
- **Screenshot Capture:** AllureTestListener captures screenshots on test failure
- **WebDriver Cleanup:** try-catch-finally in @AfterEach to ensure driver.quit() always called

## Cross-Cutting Concerns

**Logging:**
- Framework: SLF4J with slf4j-simple implementation
- Levels: DEBUG (detailed operation flow), INFO (key events), WARN (non-critical issues), ERROR (failures)
- Usage: All classes annotated with @Slf4j (Lombok), never use System.out.println()

**Validation:**
- Page Load: isPageLoaded() abstract method in BasePage, implemented by each page
- State Verification: SoftAssertions with descriptive .as() messages
- Element Presence: isElementPresent() checks without throwing

**Authentication:**
- Approach: LoginPage object with customer account login methods
- Location: `src/test/java/org/fugazi/pages/LoginPage.java`
- Flow: Navigate to /login → enter credentials → submit → verify redirect

**Reporting:**
- Framework: Allure Test Report
- Annotations: @Epic, @Feature, @Story, @Severity, @DisplayName on test classes/methods
- Steps: @Step annotations on Page Object action methods
- Attachments: Screenshots and page source on failure
- Configuration: `src/test/resources/allure.properties`

**Parallel Execution:**
- Method: JUnit 5 parallel execution with threadCount=4, forkCount=2
- Thread Safety: ThreadLocal in AllureTestListener for WebDriver storage
- Isolation: Each test gets fresh WebDriver instance

**Data Generation:**
- Framework: JavaFaker for dynamic test data
- Location: `src/test/java/org/fugazi/data/providers/TestDataFactory.java`
- Purpose: Prevents data collisions between test runs

---

*Architecture analysis: 2026-01-25*
