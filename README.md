# Selenium Automation Framework - Music Tech Shop

![Selenium Automation](https://img.shields.io/badge/Selenium-4.27.0-43B02A?style=for-the-badge&logo=selenium&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit-5.11.4-25A162?style=for-the-badge&logo=junit5&logoColor=white)
![Java 21](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Allure](https://img.shields.io/badge/Allure-Report-FF7F00?style=for-the-badge&logo=qameta&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

<div align="center">
  <img src="selenium-automation-music-tech-banner.jpg" alt="Selenium Automation Banner" width="1200" />
<p>Framework de automatización de pruebas E2E robusto y escalable para la aplicación e-commerce **Music Tech Shop**, diseñado con las mejores prácticas de ingeniería de software en pruebas (SDET).</p>
</div>
---

## 🏠 Developer

| Información | Detalle |
|-------------|---------|
| **Name** | `Douglas Urrea Ocampo` |
| **Role** | `SDET - Software Developer Engineer in Test` |
| **Location** | `Medellin, Colombia` |
| **Email** | `douglas@douglasfugazi.co` |
| **LinkedIn** | [![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/douglasfugazi) |
| **Website** | [![Website](https://img.shields.io/badge/Portfolio-000000?style=flat&logo=vercel&logoColor=white)](https://douglasfugazi.co) |

---

## 🚀 Key Features

Este framework ha sido construido pensando en la mantenibilidad, estabilidad y escalabilidad:

*   **🏗️ Page Object Model (POM)**: Arquitectura modular que separa la lógica de los tests de la interacción con la UI.
*   **⚡ Selenium WebDriver 4.27**: Implementación moderna con soporte nativo para Chrome, Firefox y Edge.
*   **🧪 JUnit 5**: Aprovechando las últimas características como ejecución paralela, tests parametrizados y extensiones.
*   **📊 Allure Reports**: Reportes detallados con pasos, screenshots, logs y categorización de errores.
*   **🎲 Data Driven Testing**: Generación de datos dinámicos y realistas utilizando **JavaFaker**.
*   **🛡️ Robustez**:
    *   **Explicit Waits**: Cero uso de `Thread.sleep()`. Estrategia de espera inteligente.
    *   **Soft Assertions**: Validaciones múltiples por test usando **AssertJ**.
    *   **Retry Mechanism**: Manejo automático de tests flaky.
*   **🌐 Cross-Browser**: Configuración sencilla para ejecución en múltiples navegadores y modo headless.

---

## 📋 Prerrequisitos

Asegúrate de tener instalado lo siguiente antes de comenzar:

*   **Java JDK 21** o superior.
*   **Maven 3.8+**.
*   Navegadores web actualizados (Chrome, Firefox, Edge).
*   *(Opcional)* Allure CLI para visualizar reportes localmente.

---

## 🛠️ Instalación y Configuración

1.  **Clonar el repositorio:**
    ```bash
    git clone <repository-url>
    cd selenium-automation-shop-app
    ```

2.  **Instalar dependencias:**
    ```bash
    mvn clean install -DskipTests
    ```

3.  **Configuración (Opcional):**
    Edita `src/test/resources/config.properties` para ajustar la URL base, navegador por defecto, etc.

---

## 🧪 Ejecución de Tests

### Comandos Comunes

| Acción | Comando |
|--------|---------|
| **Ejecutar todo** | `mvn test` |
| **Modo Headless** | `mvn test -Dheadless=true` |
| **Test Específico** | `mvn test -Dtest=LoginTest` |
| **Método Específico** | `mvn test -Dtest=LoginTest#shouldLoginSuccessfully` |
| **Por Tag (Smoke)** | `mvn test -Dgroups=smoke` |
| **Navegador Específico** | `mvn test -Dbrowser=firefox` |

### Generación de Reportes

Para ver el reporte HTML interactivo de Allure:

```bash
mvn allure:serve
```

---

## 📁 Estructura del Proyecto

```
src/test/java/org/fugazi/
├── config/          # ⚙️ ConfigurationManager (Singleton)
├── data/            # 📦 Modelos y TestDataFactory (Faker)
├── factory/         # 🏭 WebDriverFactory (Browser management)
├── listeners/       # 👂 AllureTestListener (Screenshots on failure)
├── pages/           # 📄 Page Objects & Components (Header/Footer)
├── tests/           # 🧪 Test Classes (BaseTest, LoginTest, etc.)
└── utils/           # 🛠️ Utilities (Screenshots, Waits)
```

---

## 📊 Cobertura de Tests

El proyecto cuenta con más de **180 tests** cubriendo flujos críticos y casos borde:

| Módulo | Descripción | Estado |
|--------|-------------|--------|
| **Authentication** | Login, Logout, Casos negativos, Redirecciones | ✅ |
| **Catalog** | Listado, Filtros, Ordenamiento, Paginación | ✅ |
| **Product Detail** | Información, Stock, Reviews, Recomendaciones | ✅ |
| **Search** | Búsqueda simple, avanzada, caracteres especiales | ✅ |
| **Cart** | Agregar/Remover, Cálculos, Persistencia, Flujos completos | ✅ |
| **Resilience** | URLs inválidas, 404s, Inyecciones, Rutas rotas | ✅ |
| **Information** | About, Shipping, Returns, Terms, Footer Links | ✅ |

---

## ⚙️ Tecnologías y Librerías

*   **Core**: Java 21, Maven
*   **Web Automation**: Selenium WebDriver
*   **Testing Framework**: JUnit 5 (Jupiter)
*   **Assertions**: AssertJ (Fluent assertions)
*   **Logging**: SLF4J + Logback
*   **Data Generation**: JavaFaker
*   **Reporting**: Allure Framework
*   **Utilities**: Lombok, Jackson Databind, Apache HttpClient

---

## 📝 Licencia

Este proyecto está bajo la licencia **MIT**. Siéntete libre de usarlo y modificarlo.

---
*Created with ❤️ by Douglas Urrea Ocampo*