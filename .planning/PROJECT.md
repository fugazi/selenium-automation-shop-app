# Selenium Automation - Music Tech Shop Tests

## What This Is

Framework de pruebas automatizadas E2E con Selenium WebDriver y Java para la aplicación Music Tech Shop. El proyecto actualmente tiene pruebas parciales de carrito de compras, home page y búsqueda, pero el flujo de checkout completo no tiene validación funcional.

## Core Value

Tests funcionales del flujo de checkout que validan las nuevas features y previenen regresiones en el camino crítico de compra.

## Requirements

### Validated

- ✓ Page Object Model implementado con BasePage y BaseTest — existing
- ✓ WebDriverFactory con soporte para Chrome, Firefox y Edge — existing
- ✓ Tests existentes de carrito (AddToCartTest) — existing
- ✓ Tests existentes de home page (HomePageTest) — existing
- ✓ Tests existentes de búsqueda (SearchProductTest) — existing
- ✓ JUnit 5 con ejecución paralela (4 threads, 2 forks) — existing
- ✓ Allure reporting con @Step, @Epic, @Feature, @Story — existing
- ✓ JavaFaker para datos dinámicos de prueba — existing
- ✓ Configuración vía config.properties y system properties — existing

### Active

- [ ] Tests funcionales del flujo de checkout completo
- [ ] Validación de pasos del checkout (navegación, formularios, confirmación)
- [ ] Page Objects para páginas del checkout (CheckoutPage, ShippingPage, PaymentPage, ConfirmationPage)
- [ ] Data models para datos de checkout (CheckoutData, ShippingAddress, PaymentMethod)
- [ ] Casos edge y errores en el checkout (campos inválidos, stock insuficiente, errores de pago)

### Out of Scope

- Métricas de cobertura de código (JaCoCo u otra herramienta) — no requerido actualmente
- Tests unitarios de componentes individuales — foco en tests E2E
- Mocking de servicios backend — tests usan aplicación real
- Pruebas de performance/load — fuera de alcance actual
- Pruebas de APIs backend — foco en tests UI con Selenium

## Context

Este es un proyecto brownfield de pruebas automatizadas E2E para una tienda online de productos de música (Music Tech Shop). El framework ya tiene estructura sólida con Page Object Model, WebDriver factory, ConfigurationManager, y utilities para capturar screenshots en fallos. Los tests existentes cubren navegación básica, búsqueda, y carrito de compras, pero el flujo de checkout está sin probar.

El proyecto usa JUnit 5.11.4, Selenium WebDriver 4.27.0, AssertJ 3.27.3, y Allure 2.29.0 para reporting. La ejecución es paralela con 4 threads y soporta headless mode para CI/CD. Actualmente no hay plugin de cobertura configurado (JaCoCo no detectado en pom.xml).

El codebase sigue buenas prácticas:
- Page Objects con métodos que retornan `this` para chaining o el siguiente Page para navegación
- Explicit waits (WebDriverWait) en lugar de Thread.sleep()
- Soft Assertions con mensajes descriptivos `.as()`
- @Step annotations en todos los métodos de Page Objects
- @Epic, @Feature, @Story, @Severity, @DisplayName en tests
- Java Records para DTOs inmutables
- JavaFaker para datos dinámicos que previenen colisiones

Nuevas features están siendo implementadas en el checkout, requiriendo validación automatizada para prevenir regresiones.

## Constraints

- **Tech Stack**: Java 21, Selenium WebDriver 4.27.0, JUnit 5.11.4 — framework ya establecido
- **Architecture**: Page Object Pattern obligatorio — mantener consistencia con tests existentes
- **Browsers**: Chrome, Firefox, Edge (default Edge) — WebDriverFactory ya configurado
- **Testing Standards**: AssertJ con SoftAssertions, @Step annotations, Allure reporting — obligatorio
- **Data Generation**: JavaFaker para campos dinámicos — evitar datos estáticos
- **CI/CD**: GitHub Actions en ubuntu-latest — tests deben ejecutarse en headless mode
- **Coverage Tools**: No JaCoCo o similar actualmente — prioridad en tests funcionales, no métricas de cobertura

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Mantener framework existente | El POM y estructura ya están funcionando bien, no hay razón para reescribir | — Pending |
| Foco exclusivo en checkout | El checkout es el camino crítico de compra, nuevas features requieren validación inmediata | — Pending |
| Tests funcionales de E2E | Validar flujo completo de usuario de punta a punta | — Pending |
| Sin mocking de backend | Tests usan aplicación real, más realistas y detectan bugs de integración | — Pending |
| No agregar JaCoCo todavía | Prioridad en crear tests funcionales, métricas de cobertura pueden agregarse después | — Pending |

---
*Last updated: 2026-01-25 after initialization*
