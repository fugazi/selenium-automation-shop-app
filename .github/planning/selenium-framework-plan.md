# Plan: Framework Selenium WebDriver para Music Tech Shop E-commerce

> **Fecha de creación:** 2026-01-09  
> **Estado:** ✅ Completado  
> **URL objetivo:** https://music-tech-shop.vercel.app

## TL;DR

Crear un framework de automatización robusto y escalable usando Selenium WebDriver con Java 21+, siguiendo el patrón Page Object Model (POM), principios SOLID y Clean Code. El framework utilizará **EdgeDriver por defecto** (con soporte extensible para Chrome/Firefox), **JavaFaker** para datos dinámicos, **Jackson** para datos externos JSON, ejecución paralela con `maven-surefire-plugin`, y reporting con **Allure**. Se incluirán componentes reutilizables (Header/Footer) y categorización de tests con JUnit 5 `@Tag`.

---

## Estructura del Proyecto

```
src/
├── test/
│   ├── java/org/fugazi/
│   │   ├── config/
│   │   │   ├── ConfigurationManager.java
│   │   │   └── BrowserType.java
│   │   ├── factory/
│   │   │   └── WebDriverFactory.java
│   │   ├── pages/
│   │   │   ├── BasePage.java
│   │   │   ├── HomePage.java
│   │   │   ├── ProductListPage.java
│   │   │   ├── ProductDetailPage.java
│   │   │   ├── CartPage.java
│   │   │   ├── SearchResultsPage.java
│   │   │   └── components/
│   │   │       ├── HeaderComponent.java
│   │   │       └── FooterComponent.java
│   │   ├── tests/
│   │   │   ├── BaseTest.java
│   │   │   ├── HomePageTest.java
│   │   │   ├── SearchProductTest.java
│   │   │   ├── ProductDetailTest.java
│   │   │   ├── AddToCartTest.java
│   │   │   └── CartOperationsTest.java
│   │   ├── listeners/
│   │   │   └── AllureTestListener.java
│   │   ├── utils/
│   │   │   ├── WaitUtils.java
│   │   │   └── ScreenshotUtils.java
│   │   └── data/
│   │       ├── models/
│   │       │   ├── Product.java
│   │       │   └── User.java
│   │       └── providers/
│   │           └── TestDataFactory.java
│   └── resources/
│       ├── config.properties
│       ├── allure.properties
│       └── testdata/
│           ├── products.json
│           └── users.json
.github/
└── workflows/
    └── selenium-tests.yml
```

---

## Steps de Implementación

### ✅ Paso 1: Actualizar `pom.xml` con Plugins y Configuración

**Archivo:** [pom.xml](../../pom.xml)

**Cambios requeridos:**
- Agregar `maven-surefire-plugin` con configuración de ejecución paralela
- Agregar `allure-maven-plugin` para generación de reportes
- Crear perfil `headless` para ejecución en CI/CD

---

### ✅ Paso 2: Crear Estructura de Paquetes Base

**Ubicación:** `src/test/java/org/fugazi/`

**Paquetes a crear:**
- `config/` - Gestión de configuración
- `factory/` - Factory para WebDriver
- `pages/` - Page Objects
- `pages/components/` - Componentes reutilizables
- `tests/` - Clases de test
- `listeners/` - Listeners para Allure
- `utils/` - Utilidades comunes
- `data/models/` - POJOs para datos
- `data/providers/` - Factories de datos

---

### ✅ Paso 3: Implementar Configuración (`config/`)

**Archivos a crear:**

1. **`BrowserType.java`** - Enum con tipos de browser soportados
2. **`ConfigurationManager.java`** - Singleton para gestión de configuración

---

### ✅ Paso 4: Crear WebDriverFactory (`factory/`)

**Archivo:** `WebDriverFactory.java`

---

### ✅ Paso 5: Implementar BasePage (`pages/`)

**Archivo:** `BasePage.java`

---

### ✅ Paso 6: Crear Componentes Reutilizables (`pages/components/`)

**Archivos:** `HeaderComponent.java`, `FooterComponent.java`

---

### ✅ Paso 7: Crear Page Objects (`pages/`)

**Páginas:** `HomePage`, `ProductListPage`, `ProductDetailPage`, `CartPage`, `SearchResultsPage`

---

### ✅ Paso 8: Implementar TestDataFactory (`data/`)

**Archivos:** `Product.java`, `User.java`, `TestDataFactory.java`

---

### ✅ Paso 9: Crear BaseTest (`tests/`)

**Archivo:** `BaseTest.java`

---

### ✅ Paso 10: Implementar AllureTestListener (`listeners/`)

**Archivo:** `AllureTestListener.java`

---

### ✅ Paso 11: Crear Tests E2E (`tests/`)

**Tests:** `HomePageTest`, `SearchProductTest`, `ProductDetailTest`, `AddToCartTest`, `CartOperationsTest`

---

### ✅ Paso 12: Configurar Archivos de Recursos (`resources/`)

**Archivos:** `config.properties`, `allure.properties`, `testdata/*.json`

---

### ✅ Paso 13: Crear CI/CD Workflow (`.github/workflows/`)

**Archivo:** `selenium-tests.yml`

---

## Ejecución de Tests

### Comandos Maven

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar solo tests de smoke
mvn test -Dgroups=smoke

# Ejecutar solo tests de regression
mvn test -Dgroups=regression

# Ejecutar en modo headless
mvn test -Dheadless=true

# Ejecutar con browser específico
mvn test -Dbrowser=edge

# Generar reporte Allure
mvn allure:serve
```

---

## Checklist de Seguimiento

| # | Paso | Estado | Notas |
|---|------|--------|-------|
| 1 | Actualizar pom.xml | ✅ Completado | Plugins surefire, allure, perfiles |
| 2 | Crear estructura de paquetes | ✅ Completado | config, factory, pages, tests, etc. |
| 3 | Implementar config/ | ✅ Completado | BrowserType, ConfigurationManager |
| 4 | Crear WebDriverFactory | ✅ Completado | Edge, Chrome, Firefox support |
| 5 | Implementar BasePage | ✅ Completado | Métodos fluent, waits |
| 6 | Crear componentes reutilizables | ✅ Completado | HeaderComponent, FooterComponent |
| 7 | Crear Page Objects | ✅ Completado | Home, ProductDetail, Cart, Search |
| 8 | Implementar TestDataFactory | ✅ Completado | JavaFaker + Jackson |
| 9 | Crear BaseTest | ✅ Completado | Setup/Teardown, Allure integration |
| 10 | Implementar AllureTestListener | ✅ Completado | Screenshots on failure |
| 11 | Crear Tests E2E | ✅ Completado | 5 test classes con @Tag |
| 12 | Configurar resources | ✅ Completado | config.properties, allure.properties, testdata |
| 13 | Crear CI/CD workflow | ✅ Completado | GitHub Actions con Edge |

---

## Decisiones Técnicas

| Decisión | Selección | Justificación |
|----------|-----------|---------------|
| Browser default | EdgeDriver | Requerimiento del usuario, extensible a Chrome/Firefox |
| Datos de prueba | JavaFaker + Jackson | Datos dinámicos sin hardcoding, JSON para datos estáticos |
| Ejecución paralela | maven-surefire-plugin | Paralelismo a nivel de métodos |
| Categorización tests | JUnit 5 `@Tag` | Flexibilidad para ejecutar smoke/regression |
| Componentes UI | Separados (Header/Footer) | Evitar duplicación en Page Objects |
| Reporting | Allure | Integración con CI/CD, screenshots automáticos |

---

## Próximas Fases (Futuro)

- [ ] **Fase 2:** Pruebas de Accesibilidad con Axe-core
- [ ] **Fase 3:** Pruebas de API con Rest-Assured
- [ ] **Fase 4:** Pruebas de Performance

---

> **Nota:** Este plan está diseñado para ser ejecutado paso a paso. Cada paso debe completarse y validarse antes de continuar con el siguiente.

