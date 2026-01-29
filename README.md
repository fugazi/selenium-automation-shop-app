# 🎵 Selenium Automation Framework – Music Tech Shop

![Selenium](https://img.shields.io/badge/Selenium-4.40.0-43B02A?style=for-the-badge&logo=selenium&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit-5.14.2-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Allure](https://img.shields.io/badge/Allure-Report-FF7F00?style=for-the-badge&logo=qameta&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

<div align="center" role="region" aria-label="Project Banner">
  <img src="selenium-automation-music-tech-banner.jpg" alt="Banner for Selenium Automation Framework - Music Tech Shop" width="100%" />
  <p><strong>Comprehensive, maintainable, and scalable E2E automation framework for the <em>Music Tech Shop</em> e-commerce application, built with modern software engineering and accessibility best practices.</strong></p>
</div>

---

## 📖 Overview

The Selenium Automation Framework for Music Tech Shop is a robust, modular, and extensible solution for automated
end-to-end UI testing. It leverages the latest Java, Selenium WebDriver, JUnit 5, and Allure technologies to ensure
high-quality, reliable, and accessible test coverage for a modern e-commerce platform.

---

## ✨ Key Features

| Feature                     | Description                                                            |
|-----------------------------|------------------------------------------------------------------------|
| **Page Object Model (POM)** | Modular architecture separating test logic from UI interactions        |
| **Selenium WebDriver 4.40** | Native support for Chrome, Firefox, and Edge (headless & GUI)          |
| **JUnit 5**                 | Parallel execution, parameterized tests, and advanced extensions       |
| **Allure Reports**          | Interactive, accessible HTML reports with steps, screenshots, and logs |
| **Data-Driven Testing**     | Dynamic, realistic data generation with JavaFaker                      |
| **Robustness**              | Explicit waits, soft assertions (AssertJ), and retry for flaky tests   |
| **Cross-Browser**           | Simple configuration for multi-browser and headless execution          |
| **Accessibility**           | Automated accessibility checks (WCAG 2.2 AA) and inclusive test design |

---

## 🛠️ Prerequisites

- Java JDK 21 or higher
- Maven 3.8+
- Modern web browsers (Chrome, Firefox, Edge)
- (Optional) Allure CLI for local report viewing

---

## ⚙️ Installation & Configuration

1. **Clone the repository:**
   ```shell
   git clone <repository-url>
   cd selenium-automation-shop-app
   ```
2. **Install dependencies:**
   ```shell
   mvn clean install -DskipTests
   ```
3. **Configure settings:**
    - Edit `src/test/resources/config.properties` to set the base URL, default browser, timeouts, and other options.
    - Environment variables or Maven properties can override config (e.g., `-Dbrowser=chrome -Dheadless=true`).

---

## 🚦 Usage & Test Execution

### Common Commands

| Action                     | Command                                                                                    |
|----------------------------|--------------------------------------------------------------------------------------------|
| Run all tests              | `mvn clean test`                                                                           |
| Run in headless mode       | `mvn clean test -Dheadless=true -Dbrowser=chrome`                                          |
| Run with browser mode      | `mvn clean test -Dheadless=false -Dbrowser=chrome`                                         |
| Run a specific test class  | `mvn clean test -Dtest=LoginTest -Dheadless=true -Dbrowser=chrome`                         |
| Run a specific test method | `mvn clean test -Dtest=LoginTest#shouldLoginSuccessfully -Dheadless=true -Dbrowser=chrome` |
| Run by tag (e.g., smoke)   | `mvn clean test -Dgroups=smoke -Dheadless=true -Dbrowser=chrome` or `mvn test -Psmoke`     |
| Specify browser            | `mvn clean test -Dheadless=true -Dbrowser=firefox`                                         |

### Generate Allure Report

```shell
mvn allure:serve
```

---

## 🗂️ Project Structure

```text
src/main/java/org/fugazi/
├── config/          # Configuration classes (Singleton)
├── data/            # Test data models and factories (JavaFaker)
├── factory/         # WebDriverFactory (browser management)
├── listeners/       # Allure and test listeners (screenshots, logging)
├── pages/           # Page Objects & reusable UI components
└── utils/           # Utilities (waits, screenshots, helpers)

src/test/java/org/fugazi/
└── tests/           # Test classes (BaseTest, LoginTest, etc.)

src/test/resources/
├── config.properties
├── testdata/        # JSON test data (users, products)
└── allure.properties
```

---

## 🧪 Test Coverage

> Over 180 automated tests covering critical flows and edge cases.

| Module         | Description                                      | Status |
|----------------|--------------------------------------------------|--------|
| Authentication | Login, logout, negative cases, redirects         | ✅      |
| Catalog        | Listing, filters, sorting, pagination            | ✅      |
| Product Detail | Info, stock, reviews, recommendations            | ✅      |
| Search         | Simple, advanced, special characters             | ✅      |
| Cart           | Add/remove, calculations, persistence, workflows | ✅      |
| Resilience     | Invalid URLs, 404s, injections, broken routes    | ✅      |
| Information    | About, shipping, returns, terms, footer links    | ✅      |
| Accessibility  | ARIA, keyboard, skip links, semantic HTML        | ✅      |

---

## 🧱 Technologies & Libraries

- **Core:** Java 21, Maven
- **Web Automation:** Selenium WebDriver 4.40
- **Testing:** JUnit 5 (Jupiter), AssertJ
- **Reporting:** Allure Framework
- **Data Generation:** JavaFaker
- **Logging:** SLF4J + Logback
- **Utilities:** Lombok, Jackson Databind, Apache HttpClient, Rest-Assured

---

## ♿ Accessibility Commitment

- Automated accessibility tests (WCAG 2.2 AA) included (see `AccessibilityTest.java`)
- Semantic HTML, ARIA, keyboard navigation, and inclusive design patterns
- Documentation and reports are structured for screen reader and keyboard accessibility
- Please review and manually test with tools like [Accessibility Insights](https://accessibilityinsights.io/) to ensure
  full compliance

---

## 🤝 Contribution Guidelines

- Follow SOLID, DRY, and KISS principles
- Use Page Object Model and soft assertions (AssertJ)
- All test classes must extend `BaseTest`
- Use JavaFaker for dynamic test data
- Annotate Page Object actions with `@Step` for Allure
- Use explicit waits, never `Thread.sleep()`
- Ensure all code and documentation is accessible and bias-aware
- See [AGENTS.md](./AGENTS.md) for detailed code style and test standards

---

## 🏠 Contact & Support

For questions, suggestions, or support, please contact:

| Info         | Details                                               |
|--------------|-------------------------------------------------------|
| **Name**     | `Douglas Urrea Ocampo`                                |
| **Role**     | `SDET - Software Developer Engineer in Test`          |
| **Location** | `Medellin, Colombia`                                  |
| **Email**    | `douglas@douglasfugazi.co`                            |
| **LinkedIn** | [LinkedIn](https://www.linkedin.com/in/douglasfugazi) |
| **Website**  | [Website](https://douglasfugazi.co)                   |

---

* Clean Project: `mvn clean install -DskipTests`
* Display dependencies updates: `mvn versions:display-dependency-updates`
* Update properties: `mvn versions:update-properties`

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.

---

<p align="center">Made with 💚️ by Douglas Urrea Ocampo for the QA Community</p>
