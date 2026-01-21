# Plan de Refactorización - Framework Selenium WebDriver

> **Fecha de creación:** 2026-01-13  
> **Estado:** 🔄 En Progreso  
> **Objetivo:** Limpiar, refactorizar y alinear el framework con las instrucciones y estándares de la industria

---

## TL;DR

Refactorizar el framework existente para:
1. Eliminar código muerto (clases no utilizadas)
2. Eliminar todos los `Thread.sleep()` (PROHIBIDO según instrucciones)
3. Implementar Soft Assertions en todos los tests (OBLIGATORIO)
4. Validar cumplimiento de Clean Code y SOLID

---

## Análisis del Estado Actual

### Resumen de Problemas Detectados

| Categoría | Cantidad | Prioridad |
|-----------|----------|-----------|
| `Thread.sleep()` en código | 15 usos | 🔴 Alta |
| Hard Assertions en tests | Todos los tests | 🔴 Alta |
| Clases Utils sin uso | 4 archivos | 🟡 Media |
| Extensions sin uso | 2 archivos | 🟡 Media |

### Detalle de Código No Utilizado

| Archivo | Ubicación | Estado | Acción |
|---------|-----------|--------|--------|
| `WaitUtils.java` | `utils/` | ❌ No se usa | **ELIMINAR** |
| `DateUtils.java` | `utils/` | ❌ No se usa | **ELIMINAR** |
| `StringUtils.java` | `utils/` | ❌ No se usa | **ELIMINAR** |
| `TestDataGenerator.java` | `utils/` | ❌ No se usa | **ELIMINAR** |
| `Retry.java` | `extensions/` | ❌ No se usa | **ELIMINAR** |
| `RetryExtension.java` | `extensions/` | ❌ No se usa | **ELIMINAR** |
| `ScreenshotUtils.java` | `utils/` | ✅ Se usa en `AllureTestListener` | **MANTENER** |

### Detalle de Thread.sleep() por Archivo

| Archivo | Líneas | Cantidad |
|---------|--------|----------|
| `HomePage.java` | 90, 103, 124, 207, 248 | 5 usos |
| `SearchResultsPage.java` | 87, 94 | 2 usos |
| `AddToCartTest.java` | 50, 112, 158, 225, 267 | 5 usos |
| `CartOperationsTest.java` | 106 | 1 uso |
| `WaitUtils.java` | 275 | 1 uso (se eliminará) |
| `RetryExtension.java` | 44 | 1 uso (se eliminará) |

---

## Plan de Fases

### FASE 1: Eliminar Código Muerto
**Estado:** ✅ Completado  
**Prioridad:** Alta  
**Complejidad:** Baja  
**Fecha completado:** 2026-01-13

**Objetivo:** Eliminar archivos y directorios que no se utilizan.

**Archivos eliminados:**
- [x] `src/test/java/org/fugazi/utils/WaitUtils.java` ✅
- [x] `src/test/java/org/fugazi/utils/DateUtils.java` ✅
- [x] `src/test/java/org/fugazi/utils/StringUtils.java` ✅
- [x] `src/test/java/org/fugazi/utils/TestDataGenerator.java` ✅
- [x] `src/test/java/org/fugazi/extensions/Retry.java` ✅
- [x] `src/test/java/org/fugazi/extensions/RetryExtension.java` ✅
- [x] Directorio `extensions/` eliminado ✅

**Criterio de éxito:**
- ✅ Proyecto compila sin errores (`mvn compile test-compile` exitoso)
- ✅ No hay referencias rotas

---

### FASE 2: Eliminar Thread.sleep() de Pages
**Estado:** ✅ Completado  
**Prioridad:** Alta  
**Complejidad:** Media  
**Fecha completado:** 2026-01-13

**Objetivo:** Reemplazar todos los `Thread.sleep()` en Page Objects con esperas explícitas.

**Archivos refactorizados:**
- [x] `BasePage.java` - Agregados métodos de espera avanzados:
  - `waitForInvisibility(By, int)` - Con timeout personalizado
  - `waitForUrlChange(String)` - Espera cambio de URL
  - `waitForUrlContains(String)` - Espera URL contenga texto
  - `waitForAnimationsToComplete()` - Espera fin de animaciones CSS
  - `waitForMinimumElements(By, int)` - Espera mínimo de elementos
- [x] `HomePage.java` - Eliminados 5 usos de Thread.sleep():
  - `getFeaturedProducts()` → usa `waitForAnimationsToComplete()` y `waitForMinimumElements()`
  - `getFeaturedProductsCount()` → usa `waitForAnimationsToComplete()`
  - `clickFirstProduct()` → usa `waitForUrlChange()`
  - `clickProductByIndex()` → usa `waitForUrlChange()`
- [x] `SearchResultsPage.java` - Eliminados 2 usos de Thread.sleep():
  - `waitForSkeletonsToDisappear()` → usa `wait.until()` con condición personalizada

**Criterio de éxito:**
- ✅ Cero `Thread.sleep()` en Pages
- ✅ Proyecto compila sin errores

---

### FASE 3: Eliminar Thread.sleep() de Tests
**Estado:** ✅ Completado  
**Prioridad:** Alta  
**Complejidad:** Media  
**Fecha completado:** 2026-01-13

**Objetivo:** Reemplazar todos los `Thread.sleep()` en clases de Test.

**Archivos refactorizados:**
- [x] `ProductDetailPage.java` - Agregado método `clickAddToCartAndWait()`:
  - Espera cambio de URL, mensaje de éxito, o estado del botón
- [x] `CartPage.java` - Agregado método `removeItemAndWait()`:
  - Espera actualización del carrito (conteo de items)
- [x] `AddToCartTest.java` - Eliminados 5 usos de Thread.sleep():
  - `shouldClickAddToCartButtonSuccessfully()` → usa `clickAddToCartAndWait()`
  - `shouldBeAbleToAddMultipleDifferentProducts()` → usa `clickAddToCartAndWait()`
  - `shouldBeAbleToClickAddToCartMultipleTimes()` → usa `clickAddToCartAndWait()`
  - `shouldBeAbleToContinueShoppingAfterAddingToCart()` → usa `clickAddToCartAndWait()`
  - `shouldAddProductsFromDifferentIndices()` → usa `clickAddToCartAndWait()`
- [x] `CartOperationsTest.java` - Eliminado 1 uso de Thread.sleep():
  - `shouldRemoveItemFromCart()` → usa `removeItemAndWait()`

**Criterio de éxito:**
- ✅ Cero `Thread.sleep()` en Tests
- ✅ Proyecto compila sin errores

---

### FASE 4: Implementar Soft Assertions
**Estado:** ✅ Completado  
**Prioridad:** Alta  
**Complejidad:** Media  
**Fecha completado:** 2026-01-13

**Objetivo:** Migrar todos los tests a usar `SoftAssertions.assertSoftly()` de AssertJ.

**Archivos refactorizados:**
- [x] `HomePageTest.java` - 10 tests migrados a SoftAssertions
- [x] `SearchProductTest.java` - 7 tests migrados a SoftAssertions
- [x] `ProductDetailTest.java` - 8 tests migrados a SoftAssertions
- [x] `AddToCartTest.java` - 7 tests migrados a SoftAssertions
- [x] `CartOperationsTest.java` - 11 tests migrados a SoftAssertions

**Patrón implementado (ejemplo):**

```text
SoftAssertions.assertSoftly(softly -> {
    softly.assertThat(condition)
          .as("Descripción clara del assertion")
          .isTrue();
});
```

**Criterio de éxito:**
- ✅ Todos los tests usan `SoftAssertions.assertSoftly()`
- ✅ Cada assertion tiene `.as("descripción")`
- ✅ Proyecto compila sin errores

---

### FASE 4.1: Limpieza de Métodos y Locators No Utilizados
**Estado:** ✅ Completado  
**Prioridad:** Alta  
**Complejidad:** Media  
**Fecha completado:** 2026-01-13

**Objetivo:** Eliminar métodos y locators que no se utilizan en ningún test, siguiendo principios de Clean Code.

**Archivos refactorizados:**

#### SearchResultsPage.java - 18 métodos y 7 locators eliminados
- Eliminados: `header()`, `footer()`, `isLoading()`, `getNoResultsMessage()`, `getResultsCountText()`, `hasProduct()`, `clickProduct()`, `getResultPrice()`, `clearSearch()`, `getCurrentSearchTerm()`, `isSortAvailable()`, `areFiltersAvailable()`, `isPaginationAvailable()`, `clickNextPage()`, `clickPrevPage()`, `goToPage()`, `goToCart()`, `getCartItemCount()`
- Locators eliminados: `CATEGORY_FILTER`, `SORT_FILTER`, `PRODUCTS_COUNT_TEXT`, `PAGINATION_PREV`, `PAGINATION_NEXT`, `PAGINATION_BUTTONS`, `RESULT_PRICE`
- FooterComponent eliminado

#### HomePage.java - 6 métodos y 3 locators eliminados
- Eliminados: `isHeroSectionDisplayed()`, `clickProduct(String)`, `addProductToCart()`, `isCategoriesSectionDisplayed()`, `getPageHeading()`, `goToCart()`
- Locators eliminados: `CATEGORIES_SECTION`, `ADD_TO_CART_BUTTON`, `PAGE_TITLE`

#### ProductDetailPage.java - 9 métodos y 5 locators eliminados
- Eliminados: `footer()`, `getQuantity()`, `setQuantity()`, `increaseQuantity()`, `decreaseQuantity()`, `addToCartWithQuantity()`, `isSuccessMessageDisplayed()`, `getSuccessMessage()`, `getProductCategory()`, `getStockStatus()`, `hasRelatedProducts()`, `getCartItemCount()`
- Locators eliminados: `QUANTITY_DISPLAY`, `QUANTITY_INCREASE`, `QUANTITY_DECREASE`, `PRODUCT_CATEGORY`, `RELATED_PRODUCTS`
- FooterComponent eliminado

#### CartPage.java - 12 métodos y 7 locators eliminados
- Eliminados: `header()`, `footer()`, `getEmptyCartMessage()`, `hasItem()`, `getItemPrice()`, `updateItemQuantity()`, `increaseItemQuantity()`, `decreaseItemQuantity()`, `removeItem(String)`, `getSubtotal()`, `clickCheckout()`
- Locators eliminados: `CART_ITEM_PRICE`, `CART_ITEM_TOTAL`, `QUANTITY_INCREASE`, `QUANTITY_DECREASE`, `CART_SUBTOTAL`, `GUEST_LINK`
- HeaderComponent y FooterComponent eliminados

#### BasePage.java - 10 métodos eliminados
- Eliminados: `waitForAllVisible()`, `waitForInvisibility()` (ambos overloads), `waitForUrlContains()`, `waitForTextPresent()`, `clickWithJs()`, `typeAndSubmit()`, `scrollToTop()`, `hoverOver()`, `navigateTo()`, `refreshPage()`, `takeScreenshot()`
- Actions field eliminado

**Resumen de limpieza:**
- **55+ métodos eliminados** en total
- **22+ locators eliminados** en total
- Código reducido significativamente sin pérdida de funcionalidad

**Criterio de éxito:**
- ✅ Todos los métodos públicos tienen uso en tests
- ✅ Todos los locators tienen uso en métodos
- ✅ Proyecto compila sin errores

**Corrección adicional - Locators Inline:**
Se detectaron y corrigieron locators definidos inline dentro de métodos en `HomePage.java`:
- Línea 127: `By.cssSelector("[data-testid^='product-title-link-'] h3, h3")` → `PRODUCT_TITLE_TEXT`
- Línea 156: `By.cssSelector("[data-testid^='product-title-link-']")` → `PRODUCT_TITLE_LINK`
- Línea 194: `By.cssSelector("[data-testid^='product-title-link-']")` → `PRODUCT_TITLE_LINK`

Locators agregados como constantes static:
```java
private static final By PRODUCT_TITLE_LINK = By.cssSelector("[data-testid^='product-title-link-']");
private static final By PRODUCT_TITLE_TEXT = By.cssSelector("[data-testid^='product-title-link-'] h3, h3");
```

---

### FASE 5: Validación Final y Documentación
**Estado:** ✅ Completado  
**Prioridad:** Alta  
**Complejidad:** Baja  
**Fecha completado:** 2026-01-13

**Objetivo:** Validar que el framework cumple con todas las instrucciones.

**Tareas:**
- [x] Ejecutar `mvn clean test` - `Tests run: 42, Failures: 0, Errors: 0, Skipped: 11`
- [x] Ejecutar `mvn allure:report` / `mvn allure:serve` - ✅ Reporte generado correctamente
- [x] Verificar Quality Checklist del archivo de instrucciones
- [x] Actualizar `selenium-framework-plan.md` con estado final (en este documento)
- [x] Documentar cambios realizados

**Corrección aplicada (Allure):**
- Se separó `allure.version` (BOM) de `allure.commandline.version` (CLI usada por el plugin).
- Se actualizó `allure-maven` a **2.13.0** para compatibilidad con Java 21.

**Quality Checklist (de instrucciones):**
- [x] No `Thread.sleep()` en el código
- [x] Todas las interacciones UI van a través de Page Objects
- [x] Waits explícitos para cada interacción con elementos dinámicos
- [x] Driver correctamente inicializado y cerrado en Base class
- [x] Soft Assertions con mensajes descriptivos
- [x] Código sigue convenciones CamelCase de Java
- [x] Logging consistente con SLF4J (LoggerFactory)
- [x] Implementa `@Step` en métodos de Page Objects
- [x] Usa `Duration` en lugar de int para timeouts (Selenium 4)
- [x] Genera datos dinámicos con `Faker`
- [x] Incluye AssertJ `assertThat` con `.as()` descriptivos

---

## Historial de Cambios

| Fecha | Fase | Cambios Realizados |
|-------|------|-------------------|
| 2026-01-13 | - | Plan creado, análisis inicial completado |
| 2026-01-13 | FASE 1 | ✅ Eliminados 6 archivos de código muerto: `WaitUtils.java`, `DateUtils.java`, `StringUtils.java`, `TestDataGenerator.java`, `Retry.java`, `RetryExtension.java`. Directorio `extensions/` eliminado. |
| 2026-01-13 | FASE 2 | ✅ Eliminados 7 Thread.sleep() de Pages. Agregados métodos de espera avanzados en `BasePage`. Refactorizados `HomePage.java` y `SearchResultsPage.java`. |
| 2026-01-13 | FASE 3 | ✅ Eliminados 6 Thread.sleep() de Tests. Agregados métodos `clickAddToCartAndWait()` en `ProductDetailPage` y `removeItemAndWait()` en `CartPage`. Refactorizados `AddToCartTest.java` y `CartOperationsTest.java`. |
| 2026-01-13 | FASE 4 | ✅ Migrados 43 tests a SoftAssertions. Refactorizados 5 archivos de test: `HomePageTest`, `SearchProductTest`, `ProductDetailTest`, `AddToCartTest`, `CartOperationsTest`. |
| 2026-01-13 | FASE 4.1 | ✅ Limpieza profunda: ~55 métodos y ~22 locators eliminados. Refactorizados `BasePage`, `HomePage`, `SearchResultsPage`, `ProductDetailPage`, `CartPage`. |
| 2026-01-13 | FASE 5 | ✅ Validación final: tests OK, Allure OK (plugin actualizado a 2.13.0, commandline 2.29.0). |

---

## Notas Importantes

### Reglas NO Negociables (del contexto del usuario)

1. **Thread.sleep()**: PROHIBIDO en cualquier parte del código
2. **Soft Assertions**: OBLIGATORIO en todos los tests
3. **Código muerto**: ELIMINAR sin excepción
4. **Page Objects**: Locators como atributos, NO dentro de métodos
5. **Clean Code**: Nombres claros, sin duplicación, responsabilidad única

### Referencias

- Instrucciones: `.github/instructions/selenium-webdriver-java.instructions.md`
- Skills: `.github/skills/webapp-selenium-testing/SKILL.md`
- Plan original: `.github/planning/selenium-framework-plan.md`

---

> **Nota:** Cada fase debe completarse y validarse antes de continuar con la siguiente. El usuario debe confirmar la finalización de cada fase.
