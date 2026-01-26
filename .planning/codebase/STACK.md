# Technology Stack

**Analysis Date:** 2026-01-25

## Languages

**Primary:**
- Java 21 - Application source code (test framework, Page Objects, test classes)

**Secondary:**
- None detected

## Runtime

**Environment:**
- Java 21+ (Java 25 Amazon Corretto available on system)

**Package Manager:**
- Maven 3.9.11
- Lockfile: Not applicable (Maven POM-based)

## Frameworks

**Core:**
- Selenium WebDriver 4.27.0 - Browser automation for UI testing
- JUnit 5.11.4 - Test runner and framework
- Allure 2.29.0 - Test reporting and documentation

**Testing:**
- JUnit Jupiter 5.11.4 - Modern testing with parallel execution
- AssertJ 3.27.3 - Fluent assertions for test validation

**Build/Dev:**
- Maven Surefire 3.2.5 - Test execution with parallelism (4 threads, 2 forks)
- Maven Compiler 3.12.1 - Java compilation
- Lombok 1.18.36 - Code generation (getters, setters, builders, logging)

## Key Dependencies

**Critical:**
- selenium-java 4.27.0 - Core browser automation framework
- junit-jupiter 5.11.4 - Test execution engine
- assertj-core 3.27.3 - Assertion library for tests

**Infrastructure:**
- slf4j-api 2.0.16 - Logging abstraction
- slf4j-simple 2.0.16 - Simple logging implementation (console)
- javafaker 1.0.2 - Dynamic test data generation
- jackson-databind 2.18.2 - JSON parsing and serialization

**Available but not actively used:**
- httpclient5 5.4.1 - HTTP client for API interactions (available for backend test setup)
- rest-assured 5.4.0 - REST API testing library (available for API tests)

## Configuration

**Environment:**
- Properties file: `src/test/resources/config.properties`
- System property overrides: Support via Maven `-Dproperty=value`
- Key configs: base.url, browser, headless, timeouts

**Build:**
- Build config file: `pom.xml`
- Java version: 21 (source and target)
- Encoding: UTF-8

## Platform Requirements

**Development:**
- Java 21+ (Java 25 Amazon Corretto compatible)
- Maven 3.8+
- Chrome, Firefox, or Edge browser installed

**Production:**
- N/A (test-only project, no production deployment)
- CI/CD: GitHub Actions (ubuntu-latest runner)

---

*Stack analysis: 2026-01-25*
