# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Selenium WebDriver E2E test automation framework for the Music Tech Shop e-commerce application (https://music-tech-shop.vercel.app). Built with Java 21, Selenium 4.27, JUnit 5, and follows Page Object Model architecture.

## Build and Test Commands

### Maven Test Execution

**Run all tests:** `mvn clean test`

**Run specific test class:** `mvn clean test -Dtest=HomePageTest -Dbrowser=chrome -Dheadless=false`

**Run specific test method:** `mvn clean test -Dtest=HomePageTest#cartShouldInitiallyBeEmpty -Dbrowser=chrome -Dheadless=false`

**Run by tag (requires Maven profile):** `mvn test -Psmoke` or `mvn test -Pregression`

**Run in headless mode:** `mvn test -Dheadless=true`

**Run with specific browser:** `mvn test -Dbrowser=chrome|firefox|edge`

**Generate Allure report:** `mvn allure:serve` (interactive) or `mvn allure:report` (static)

**Compile without tests:** `mvn compile`

**Install skipping tests:** `mvn clean install -DskipTests`

### Parallel Test Execution

The framework uses `maven-surefire-plugin` with method-level parallelism (4 threads, 2 forks) by default.

## Architecture and Code Structure

### Package Layout

```
src/test/java/org/fugazi/
├── config/
│   ├── ConfigurationManager.java    # Singleton for config management (loads config.properties)
│   └── BrowserType.java              # Enum of supported browsers
├── factory/
│   └── WebDriverFactory.java        # Creates WebDriver instances based on config
├── pages/
│   ├── BasePage.java                # Abstract base with waits/actions (all Page Objects extend this)
│   ├── HomePage.java
│   ├── ProductDetailPage.java
│   ├── SearchResultsPage.java
│   ├── CartPage.java
│   └── components/
│       ├── HeaderComponent.java     # Reusable header component
│       └── FooterComponent.java     # Reusable footer component
├── tests/
│   ├── BaseTest.java                # Base test class with setup/teardown and lazy Page Object getters
│   ├── HomePageTest.java
│   ├── ProductDetailTest.java
│   ├── SearchProductTest.java
│   ├── AddToCartTest.java
│   └── CartOperationsTest.java      # Disabled (requires authentication)
├── listeners/
│   └── AllureTestListener.java      # Takes screenshots on test failure
├── utils/
│   └── ScreenshotUtils.java         # Screenshot capture utilities
└── data/
    ├── models/
    │   ├── Product.java             # Product data model
    │   └── User.java                # User data model
    └── providers/
        └── TestDataFactory.java     # Test data generation using JavaFaker

src/test/resources/
├── config.properties                # Configuration properties (base URL, browser, timeouts)
├── allure.properties                # Allure reporting configuration
└── environment.properties           # Environment-specific values
```

### Key Design Patterns

**Page Object Model (POM):** All UI interaction goes through Page Object classes. Methods return `this` for chaining or the next Page object for navigation flows.

**Lazy Initialization:** Page Objects in `BaseTest` are instantiated on-demand via getter methods (e.g., `homePage()`, `cartPage()`).

**Configuration Hierarchy:** System properties (-D flags) override `config.properties` values. `ConfigurationManager` singleton provides thread-safe access.

**Explicit Waits:** All element interactions use `WebDriverWait` with `Duration.ofSeconds()` (Selenium 4 compliance). Never use `Thread.sleep()`.

**Component Pattern:** Shared UI elements (Header, Footer) are separate component classes to avoid duplication across Page Objects.

### Test Data Strategy

Use `TestDataFactory` with JavaFaker to generate dynamic data for non-deterministic fields (emails, names, addresses). This prevents test data collisions.

### Configuration Management

**ConfigurationManager singleton** provides access to:
- `getBaseUrl()` - Application URL
- `getBrowserType()` - Browser to use
- `isHeadless()` - Headless mode flag
- `getTimeout()` / `getImplicitWait()` / `getExplicitWait()` - Timeout values

**Override via Maven:** `-Dbrowser=chrome -Dheadless=true -Dbase.url=https://...`

### Allure Reporting Integration

**Test Annotations:** `@Epic`, `@Feature`, `@Story`, `@Description`, `@Severity`, `@Tag`

**Page Object Annotations:** `@Step("Description")` on all action methods

**Screenshots:** Automatically captured on test failure via `AllureTestListener`

## Code Style Requirements

**Mandatory:** See `.github/instructions/selenium-webdriver-java.instructions.md` and `AGENTS.md` for complete coding standards.

Key requirements:
- Soft Assertions with AssertJ: `SoftAssertions.assertSoftly(softly -> { ... })`
- Use `.as("description")` on all assertions for meaningful failure messages
- `@Slf4j` for logging (never `System.out.println`)
- `@Step` annotations on Page Object methods
- Modern Java: Records, Streams API, `Optional`, `var`, `Duration` for timeouts
- Test classes extend `BaseTest`, use lazy Page Object getters

## Browser Support

Chrome (default), Firefox, Edge. Configurable via `-Dbrowser=chrome|firefox|edge`.

## Important Notes

- Target application: https://music-tech-shop.vercel.app
- CartOperationsTest is disabled (requires authentication)
- Retry mechanism available via `@Retry` annotation for flaky tests
- Framework uses Selenium 4.27 and requires Java 21+
