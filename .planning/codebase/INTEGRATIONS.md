# External Integrations

**Analysis Date:** 2026-01-25

## APIs & External Services

**Test Target Application:**
- Music Tech Shop - https://music-tech-shop.vercel.app
  - Purpose: E-commerce application under test
  - Access: Via Selenium WebDriver
  - Auth: Custom authentication (test accounts in `src/test/resources/testdata/users.json`)
  - No API integration currently implemented

**CI/CD & Hosting:**
- GitHub Actions - Automated test execution
  - Triggers: Push to main/develop, PR to main, workflow_dispatch
  - Runner: ubuntu-latest
  - Browser: Edge installed dynamically in CI

**Report Hosting:**
- GitHub Pages - Allure report deployment
  - Trigger: On push to main branch
  - Location: https://[username].github.io/[repo]/allure-report

## Data Storage

**Databases:**
- None (stateless test framework)

**File Storage:**
- Local filesystem only
  - Test data: `src/test/resources/testdata/products.json`, `users.json`
  - Screenshots: `target/screenshots`
  - Allure results: `target/allure-results`
  - Allure report: `target/allure-report`

**Caching:**
- Maven dependency cache: `~/.m2/repository`
- GitHub Actions cache: Maven repository

## Authentication & Identity

**Auth Provider:**
- Custom authentication (test target application)
  - Implementation: Form-based login via UI
  - Test credentials: JSON-based test data in `src/test/resources/testdata/users.json`
  - No external OAuth/SSO providers

## Monitoring & Observability

**Error Tracking:**
- None (local test framework)

**Logs:**
- SLF4J with simple console implementation
- Level: Configurable via `log.level` in `config.properties` (default: INFO)
- No centralized logging service

## CI/CD & Deployment

**Hosting:**
- GitHub - Source code repository
- GitHub Actions - CI/CD pipeline

**CI Pipeline:**
- GitHub Actions workflow: `.github/workflows/selenium-tests.yml`
- Test execution: Maven Surefire with parallel execution
- Report generation: Allure Maven plugin
- Artifact upload: Test results, screenshots, Allure report

## Environment Configuration

**Required env vars:**
- None required (all configuration via properties file and Maven parameters)

**Optional system properties:**
- `base.url` - Override target application URL
- `browser` - Override browser type (chrome/firefox/edge)
- `headless` - Override headless mode (true/false)
- `implicit.wait.seconds` - Override implicit wait timeout
- `explicit.wait.seconds` - Override explicit wait timeout
- `groups` - Run specific test groups (smoke/regression)

**Secrets location:**
- GITHUB_TOKEN - Provided automatically by GitHub Actions
- No external secrets management system required

## Webhooks & Callbacks

**Incoming:**
- None

**Outgoing:**
- None (test framework only, no webhook integration)

## Browser Automation Services

**WebDriver Management:**
- Local browser drivers only
- Chrome, Firefox, Edge - Browsers installed locally or in CI
- No cloud services (Sauce Labs, BrowserStack, etc.)
- No grid services (Selenium Grid, etc.)

---

*Integration audit: 2026-01-25*
