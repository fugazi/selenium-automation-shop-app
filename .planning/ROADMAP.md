# Roadmap: Selenium Automation - Checkout Flow Testing

## Overview

Validación completa del flujo de checkout de Music Tech Shop mediante pruebas E2E automatizadas, desde el carrito hasta la confirmación de pedido. El proyecto ya tiene una base sólida con Page Object Model, WebDriver factory y pruebas existentes (carrito, home page, búsqueda). Este roadmap agrega las pruebas del checkout, el camino crítico de compra que previene regresiones en nuevas features.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [ ] **Phase 1: Foundation - Page Objects y Datos de Checkout**
- [ ] **Phase 2: Checkout Flow - Navegación, Validación y Pago**
- [ ] **Phase 3: Order Confirmation - Detalles de Pedido y Email**

## Phase Details

### Phase 1: Foundation - Page Objects y Datos de Checkout
**Goal**: Estructura Page Object del checkout completa y data models para datos de prueba
**Depends on**: Nada (primera fase)
**Requirements**: Ninguna (fase habilitadora para fases siguientes)
**Success Criteria** (what must be TRUE):
  1. Page Objects del checkout existen y encapsulan la navegación multi-paso (ShippingPage, PaymentPage, ReviewPage)
  2. Component Objects reutilizables existen para formularios de dirección y método de pago
  3. Data models (CheckoutData, ShippingAddress, PaymentMethod) existen y se generan dinámicamente con JavaFaker
  4. TestDataFactory tiene métodos para generar datos válidos e inválidos de checkout
**Plans**: TBD

Plans:
- [ ] 01-01: Crear CheckoutPage con estructura multi-paso (Shipping → Payment → Review → Confirmation)
- [ ] 01-02: Crear Component Objects para AddressFormComponent y PaymentMethodComponent
- [ ] 01-03: Crear Data models CheckoutData, ShippingAddress, PaymentMethod como Java Records
- [ ] 01-04: Extender TestDataFactory con métodos de generación de datos de checkout

### Phase 2: Checkout Flow - Navegación, Validación y Pago
**Goal**: Usuario puede navegar y completar el flujo de checkout exitosamente con validación
**Depends on**: Phase 1
**Requirements**: CHK-01, CHK-02, CHK-03, CHK-04, VAL-01, VAL-02, PAY-01
**Success Criteria** (what must be TRUE):
  1. Usuario invitado puede navegar desde el carrito hasta completar el checkout exitosamente
  2. Usuario registrado puede navegar desde el carrito hasta completar el checkout exitosamente
  3. Items, cantidades y precios del carrito persisten y se muestran correctamente durante todo el checkout
  4. Formulario de checkout rechaza campos requeridos vacíos (nombre, dirección, ciudad, código postal)
  5. Formulario de checkout rechaza emails con formato inválido y acepta emails válidos
  6. Pago con tarjeta de crédito funciona y completa el checkout exitosamente
**Plans**: TBD

Plans:
- [ ] 02-01: Tests de navegación del carrito a checkout y persistencia del estado del carrito
- [ ] 02-02: Tests de happy path checkout para usuario invitado
- [ ] 02-03: Tests de happy path checkout para usuario registrado
- [ ] 02-04: Tests de validación de campos requeridos del formulario de checkout
- [ ] 02-05: Tests de validación de formato de email
- [ ] 02-06: Tests de pago exitoso con tarjeta de crédito

### Phase 3: Order Confirmation - Detalles de Pedido y Email
**Goal**: Usuario recibe confirmación visual y por email de su pedido completado
**Depends on**: Phase 2
**Requirements**: CNF-01, CNF-02
**Success Criteria** (what must be TRUE):
  1. Página de confirmación muestra el ID de pedido único y detalles completos del pedido después del checkout
  2. Email de confirmación se envía al usuario del pedido y contiene los detalles del pedido
**Plans**: TBD

Plans:
- [ ] 03-01: Crear ConfirmationPage para validar la página de confirmación de pedido
- [ ] 03-02: Test de validación de página de confirmación muestra ID de pedido y detalles
- [ ] 03-03: Test de verificación de envío de email de confirmación

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation - Page Objects y Datos de Checkout | 0/4 | Not started | - |
| 2. Checkout Flow - Navegación, Validación y Pago | 0/6 | Not started | - |
| 3. Order Confirmation - Detalles de Pedido y Email | 0/3 | Not started | - |

---

**Last updated:** 2026-01-25
