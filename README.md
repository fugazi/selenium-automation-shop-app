# Selenium Automation Framework - Music Tech Shop

Framework de automatización de pruebas E2E para la aplicación Music Tech Shop, construido con Selenium WebDriver, Java 21 y JUnit 5.

## 🚀 Características

- **Page Object Model (POM)**: Estructura limpia y mantenible
- **Selenium WebDriver 4.27**: Última versión con soporte para Chrome/Firefox/Edge
- **JUnit 5**: Framework de testing moderno con ejecución paralela
- **Allure Reports**: Reportes visuales detallados
- **Data Driven Testing**: Utilidades para generación de datos de prueba
- **Configuración Flexible**: Soporte para múltiples navegadores y entornos
- **Retry Mechanism**: Reintentos automáticos para tests flaky

## 📋 Prerequisitos

- Java 21 o superior
- Maven 3.8+
- Chrome/Firefox/Edge (última versión)
- Allure CLI (opcional, para visualizar reportes)

## 🛠️ Instalación

```bash
# Clonar el repositorio
git clone <repository-url>
cd selenium-automation-shop-app

# Instalar dependencias
mvn clean install -DskipTests
```

## 🧪 Ejecución de Tests

### Ejecutar todos los tests
```bash
mvn test
```
```bash
mvn clean test -Dheadless=true -Dbrowser=chrome
```

### Ejecutar tests en modo headless
```bash
mvn test -Dheadless=true
```

### Ejecutar tests con navegador
```bash
mvn clean test -Dtest=HomePageTest#cartShouldInitiallyBeEmpty -Dbrowser=chrome -Dheadless=false
```

### Ejecutar tests específicos
```bash
# Por clase
mvn clean test -Dtest=HomePageTest -Dbrowser=chrome -Dheadless=false

# Por tag
mvn test -Dgroups=smoke
```

### Configurar navegador
```bash
# Chrome (por defecto)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge
```

## 📊 Reportes

### Generar reporte Allure
```bash
mvn allure:serve
```

### Generar reporte sin servidor
```bash
mvn allure:report
```

## 📁 Estructura del Proyecto

```
src/test/java/org/fugazi/
├── config/
│   └── ConfigurationManager.java    # Gestión de configuración
├── extensions/
│   ├── Retry.java                   # Anotación de reintentos
│   └── RetryExtension.java          # Extensión JUnit para reintentos
├── factory/
│   └── WebDriverFactory.java        # Factory de WebDriver
├── listeners/
│   └── AllureTestListener.java      # Listener para Allure
├── pages/
│   ├── BasePage.java                # Página base
│   ├── HomePage.java                # Página principal
│   ├── ProductDetailPage.java       # Detalle de producto
│   ├── SearchResultsPage.java       # Resultados de búsqueda
│   ├── CartPage.java                # Carrito de compras
│   └── components/
│       ├── HeaderComponent.java     # Componente header
│       └── FooterComponent.java     # Componente footer
├── tests/
│   ├── BaseTest.java                # Test base
│   ├── HomePageTest.java            # Tests de home
│   ├── ProductDetailTest.java       # Tests de producto
│   ├── SearchProductTest.java       # Tests de búsqueda
│   ├── AddToCartTest.java           # Tests de agregar al carrito
│   └── CartOperationsTest.java      # Tests de operaciones de carrito (disabled)
└── utils/
    ├── ScreenshotUtils.java         # Utilidades para screenshots
```

## ⚙️ Configuración

El archivo `src/test/resources/config.properties` contiene las configuraciones:

```properties
# Base URL
base.url=https://music-tech-shop.vercel.app

# Browser settings
browser.type=chrome
browser.headless=false
browser.maximize=true

# Timeouts
timeout.implicit=10
timeout.explicit=10
timeout.page.load=30
```

## 📝 Test Suites

| Suite | Tests | Estado |
|-------|-------|--------|
| HomePageTest | 9 | ✅ Active |
| ProductDetailTest | 8 | ✅ Active |
| SearchProductTest | 7 | ✅ Active |
| AddToCartTest | 7 | ✅ Active |
| CartOperationsTest | 11 | ⏸️ Disabled (requires auth) |

## 🏷️ Tags

- `@smoke`: Tests críticos de sanidad
- `@regression`: Suite completa de regresión
- `@wip`: Tests en desarrollo

## 🔄 Retry Mechanism

Para tests flaky, usa la anotación `@Retry`:

```java
@Test
@Retry(3) // Reintentar hasta 3 veces
void flakyTest() {
    // ...
}
```

## 📈 Best Practices Implementadas

1. **Page Object Model**: Separación de la lógica de UI
2. **Fluent Waits**: Esperas explícitas para elementos (sin `Thread.sleep()`)
3. **Soft Assertions**: Uso de AssertJ con mensajes descriptivos `.as()`
4. **Data Generation**: Datos aleatorios con JavaFaker para evitar colisiones
5. **Component Pattern**: Componentes reutilizables (Header, Footer)
6. **Allure Annotations**: Steps y attachments para debugging con `@Step`
7. **Configuration Management**: Propiedades externalizadas
8. **Modern Java 21**: Records, Streams, Optional, var, Duration para timeouts

## 📋 Plan de Evaluación de Tests

**Documento detallado:** [`TEST_EVALUATION_PLAN.md`](./TEST_EVALUATION_PLAN.md)

Este plan proporciona una estrategia completa para:
- ✅ Evaluar todos los tests existentes (135+ tests en 14 clases)
- 🔍 Identificar tests funcionando vs. tests fallando
- 🔧 Corregir violaciones de mejores prácticas
- 📊 Verificar compliance del framework
- 📈 Establecer métricas de salud del test suite

### Estado Actual del Framework

| Métrica | Valor |
|---------|-------|
| Total Tests | 135 |
| Test Classes | 14 |
| Tests Activos | 100% |
| Compliance | **~100%** ✅ |
| Framework Violations | **0** (todos corregidos) |
| SoftAssertions Compliance | **100%** ✅ (128/128) |
| Test Annotations Compliance | **100%** ✅ (126/126) |
| Authentication Stability | **100%** ✅ (25/25) |
| Hardcoded Credentials | **0** ✅ (usando constantes) |
| Test Timeout Rate | **6.7%** (9/135) - aceptable ⚠️ |

**Última Actualización:** 2026-01-21

### Análisis Completados

1. **Phase 1-3: Evaluación y Remediación** ✅
   - `REMEDIATION_LOG.md` - Corrección de Thread.sleep() violations
   - `TEST_EXECUTION_RESULTS.md` - Resultados de 135 tests ejecutados

2. **Prioridad 2 & 4: Análisis de Código y Autenticación** ✅
   - `PRIORITY_2_4_ANALYSIS.md` - Análisis detallado de:
     - @Step annotations coverage (69.5%)
     - Test annotations compliance (100%)
     - SoftAssertions pattern (100%)
     - Authentication stability (100%)

3. **Opción B: Investigación de Timeouts** ✅
   - `TIMEOUT_AND_CODE_QUALITY_SUMMARY.md` - Análisis completo de:
     - 9 tests con timeout diagnosticados
     - Root cause: parallel execution resource contention
     - Code quality improvements (Priority 1.3 completado)
     - Recomendaciones y próximos pasos

### Ejecutar Evaluación Rápida

```bash
# 1. Ejecutar smoke tests (críticos)
mvn clean test -Psmoke -Dbrowser=edge -Dheadless=false

# 2. Buscar violaciones de Thread.sleep
grep -rn "Thread.sleep" src/test/java/org/fugazi/tests/

# 3. Generar reporte Allure
mvn allure:serve
```

Ver [`TEST_EVALUATION_PLAN.md`](./TEST_EVALUATION_PLAN.md) para el plan completo de 8 fases.

## 🐛 Troubleshooting

### Error: ChromeDriver version mismatch
```bash
# Actualizar WebDriverManager
mvn dependency:resolve
```

### Error: net::ERR_CONNECTION_RESET
Este es un error transitorio de red. Los tests tienen retry automático.

### Tests muy lentos
```bash
# Ejecutar en paralelo
mvn test -Djunit.jupiter.execution.parallel.enabled=true
```

## 📄 Licencia

MIT License

