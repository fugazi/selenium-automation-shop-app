# Codebase Structure

**Analysis Date:** 2026-01-25

## Directory Layout

```
selenium-automation-shop-app/
├── .github/                      # GitHub-specific configuration
│   ├── agents/                   # Custom AI agent configurations
│   ├── instructions/             # Agent instruction files
│   ├── planning/                 # GitHub planning documents
│   ├── skills/                   # Agent skill definitions
│   └── workflows/                # GitHub Actions workflows
├── .planning/                    # Planning documentation (not committed)
│   └── codebase/                 # Codebase analysis documents
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/fugazi/
│   │           └── Main.java     # Placeholder main class (not used in tests)
│   └── test/
│       ├── java/
│       │   └── org/fugazi/
│       │       ├── config/          # Configuration management
│       │       ├── data/            # Test data models and factories
│       │       │   ├── models/      # Data model classes
│       │       │   └── providers/   # Test data generation
│       │       ├── factory/         # WebDriver factory
│       │       ├── listeners/       # Test lifecycle listeners
│       │       ├── pages/           # Page Object Model
│       │       │   └── components/  # Reusable UI components
│       │       ├── tests/           # JUnit 5 test classes
│       │       └── utils/           # Utility classes
│       └── resources/
│           ├── allure.properties      # Allure report configuration
│           ├── config.properties     # Test configuration
│           └── testdata/            # JSON test data files
├── target/                      # Build output (not committed)
│   ├── allure-results/           # Allure test results
│   ├── allure-report/            # Generated Allure report
│   └── screenshots/              # Failure screenshots
├── pom.xml                      # Maven project configuration
├── AGENTS.md                    # Agent guidelines and instructions
├── CLAUDE.md                    # Project-specific Claude instructions
└── README.md                    # Project documentation
```

## Directory Purposes

**src/test/java/org/fugazi/config:**
- Purpose: Manages test configuration and browser settings
- Contains: Configuration classes (ConfigurationManager, BrowserType)
- Key files: `ConfigurationManager.java`, `BrowserType.java`

**src/test/java/org/fugazi/data:**
- Purpose: Defines test data models and provides test data generation
- Contains: Data models (Product, User, Credentials) and data factories
- Key files: `Product.java`, `User.java`, `TestDataFactory.java`

**src/test/java/org/fugazi/data/models:**
- Purpose: Data transfer objects for test data
- Contains: Java models with Lombok annotations
- Key files: `Product.java`, `User.java`, `Credentials.java`

**src/test/java/org/fugazi/data/providers:**
- Purpose: Dynamic test data generation using JavaFaker
- Contains: Factory classes for generating test data
- Key files: `TestDataFactory.java`

**src/test/java/org/fugazi/factory:**
- Purpose: Creates and configures WebDriver instances
- Contains: Factory classes for browser instantiation
- Key files: `WebDriverFactory.java`

**src/test/java/org/fugazi/listeners:**
- Purpose: Hooks into JUnit 5 test lifecycle for reporting
- Contains: Test listeners and lifecycle handlers
- Key files: `AllureTestListener.java`

**src/test/java/org/fugazi/pages:**
- Purpose: Page Object Model implementation for UI pages
- Contains: Page objects representing application pages
- Key files: `BasePage.java`, `HomePage.java`, `CartPage.java`, `ProductDetailPage.java`, `LoginPage.java`, `ProductsPage.java`, `SearchResultsPage.java`, `AboutPage.java`, `ShippingPage.java`, `ReturnsPage.java`, `TermsPage.java`

**src/test/java/org/fugazi/pages/components:**
- Purpose: Reusable UI components shared across pages
- Contains: Component objects for common UI elements
- Key files: `HeaderComponent.java`, `FooterComponent.java`

**src/test/java/org/fugazi/tests:**
- Purpose: JUnit 5 test classes
- Contains: Test classes extending BaseTest
- Key files: `BaseTest.java`, `AddToCartTest.java`, `CartOperationsTest.java`, `CartWorkflowTest.java`, `HomePageTest.java`, `FooterLinksTest.java`, `InformationPagesTest.java`, `AccessibilityTest.java`

**src/test/java/org/fugazi/utils:**
- Purpose: Helper utilities and common functionality
- Contains: Utility classes for cross-cutting concerns
- Key files: `ScreenshotUtils.java`

**src/test/resources:**
- Purpose: Test configuration and static resources
- Contains: Properties files, JSON test data, Allure configuration
- Key files: `config.properties`, `allure.properties`, `testdata/products.json`, `testdata/users.json`

## Key File Locations

**Entry Points:**
- `pom.xml`: Maven build configuration, dependencies, profiles
- `src/test/java/org/fugazi/tests/BaseTest.java`: Base test class with setup/teardown

**Configuration:**
- `src/test/resources/config.properties`: Test configuration (URL, browser, timeouts)
- `src/test/resources/allure.properties`: Allure report configuration
- `src/test/java/org/fugazi/config/ConfigurationManager.java`: Configuration manager singleton

**Core Logic:**
- `src/test/java/org/fugazi/pages/BasePage.java`: Abstract base page with wait strategies
- `src/test/java/org/fugazi/pages/`: All page object implementations
- `src/test/java/org/fugazi/pages/components/`: Reusable page components

**Testing:**
- `src/test/java/org/fugazi/tests/`: All test classes
- `src/test/resources/testdata/`: JSON test data fixtures

**Utilities:**
- `src/test/java/org/fugazi/utils/ScreenshotUtils.java`: Screenshot capture utilities
- `src/test/java/org/fugazi/listeners/AllureTestListener.java`: Test lifecycle hooks

## Naming Conventions

**Files:**
- Test classes: `[Feature]Test.java` (e.g., `AddToCartTest.java`)
- Page classes: `[Page]Page.java` (e.g., `HomePage.java`, singular form)
- Component classes: `[Component]Component.java` (e.g., `HeaderComponent.java`)
- Base classes: `Base[Type].java` (e.g., `BasePage.java`, `BaseTest.java`)
- Utility classes: `[Utility]Utils.java` (e.g., `ScreenshotUtils.java`)
- Factory classes: `[Resource]Factory.java` (e.g., `WebDriverFactory.java`)
- Manager classes: `[Resource]Manager.java` (e.g., `ConfigurationManager.java`)

**Directories:**
- All lowercase (e.g., `config/`, `pages/`, `tests/`)
- Subdirectories named by category (e.g., `pages/components/`, `data/models/`, `data/providers/`)

## Where to Add New Code

**New Feature Tests:**
- Primary code: `src/test/java/org/fugazi/tests/[Feature]Test.java`
- Test method pattern: `should[ExpectedResult]When[Action]`
- Required annotations: @Test, @DisplayName, @Tag("smoke"|"regression"), @Story, @Severity

**New Page Object:**
- Implementation: `src/test/java/org/fugazi/pages/[Page]Page.java`
- Extend: `BasePage`
- Implement: `isPageLoaded()` method
- Annotate: Action methods with @Step
- Return: `this` for chaining or next Page object

**New Page Component:**
- Implementation: `src/test/java/org/fugazi/pages/components/[Component]Component.java`
- Extend: `BasePage`
- Use: When component is shared across multiple pages

**New Test Data Model:**
- Implementation: `src/test/java/org/fugazi/data/models/[Model].java`
- Use: Lombok annotations (@Getter, @Setter, @Data)
- Add builder: If complex construction needed

**New Test Data Provider:**
- Implementation: `src/test/java/org/fugazi/data/providers/[Provider]Factory.java`
- Use: JavaFaker for dynamic data generation
- Pattern: Static factory methods

**New Utility:**
- Implementation: `src/test/java/org/fugazi/utils/[Utility]Utils.java`
- Use: Static methods with private constructor
- Add: @Slf4j if logging needed

**New Configuration:**
- Update: `src/test/resources/config.properties` for new config values
- Access: Add getter method in `ConfigurationManager.java`
- Override: Support system property -Dconfig.key=value

## Special Directories

**.github/:**
- Purpose: GitHub-specific configuration (workflows, agents, skills)
- Generated: Partially (some content from AI agents)
- Committed: Yes

**target/:**
- Purpose: Maven build output directory
- Generated: Yes (by Maven build process)
- Committed: No (in .gitignore)

**.planning/:
- Purpose: Planning documents and analysis (not committed to git)
- Generated: Yes (by GSD commands)
- Committed: No (local only)

**.allure/:
- Purpose: Allure report binary files
- Generated: Yes
- Committed: Yes (Allure runtime)

**.idea/:
- Purpose: IntelliJ IDEA project configuration
- Generated: Yes
- Committed: No (in .gitignore)

**.claude/:
- Purpose: Claude AI agent configuration
- Generated: Yes
- Committed: Yes

---

*Structure analysis: 2026-01-25*
