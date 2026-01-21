# Automation Scenario Expansion Plan (Selenium WebDriver + Java 21)

## Goal

Expand automated coverage for **Music-Tech Shop** (base URL: `https://music-tech-shop.vercel.app`) with **high-value, maintainable, and scalable** Selenium WebDriver tests, aligned with the current Page Object Model (POM) approach and JUnit 5 + Allure conventions used in this repo.

This plan focuses on:
- New **business-critical functional flows**
- **Negative** and **edge case** coverage that reduces production risk
- **Regression** scenarios that protect core shopping journeys
- Recommendations to keep the suite **stable** (avoid flaky tests) and **easy to extend**

> Scope note: This document intentionally contains **no Selenium code**. It is a strategic/technical plan only.

---

## Assumptions

1. The `base.url` configured in `src/test/resources/config.properties` is the target environment: `https://music-tech-shop.vercel.app`.
2. Product catalog is **data-driven** and may change over time (names, stock labels, pagination size).
3. `/cart` is protected and redirects to `/login?redirect=/cart` when the user is not authenticated (observed behavior).
4. Login page provides two test accounts:
   - Admin: `admin@test.com / admin123`
   - Customer: `user@test.com / user123`
5. Existing automated coverage currently focuses on:
   - Home page load + basic UI checks
   - Search flows (happy path + no-results)
   - Product Detail checks
   - “Add to cart” interactions (without validating cart state due to auth)
   - Cart operations tests exist but are **disabled** pending auth enablement

---

## Prioritization Criteria

Use the following criteria to decide what to automate first:

1. **Revenue / conversion impact**: anything affecting discoverability, product selection, and checkout readiness.
2. **Risk / defect likelihood**: areas with state, calculations, redirects, pagination, and filters.
3. **User frequency**: scenarios executed by most users most days.
4. **Stability potential**: scenarios that can be implemented with stable locators and deterministic assertions.
5. **Coverage gap** vs existing tests.
6. **Regression value**: scenarios that quickly detect breaking UI changes.

Recommended execution waves:
- **Wave 1 (Smoke + Core Regression)**: highest confidence suite for CI.
- **Wave 2 (Extended Regression)**: deeper catalog/cart/auth validation.
- **Wave 3 (Hardening / Edge & Resilience)**: resilience testing, invalid routes, boundary conditions.

---

## Application Exploration Summary (Functional)

High-level discovered features/pages:

- **Home (`/`)**
  - CTA: “Start Shopping” → `/products`
  - Category tiles → `/products?category=...`
  - Featured product cards (labels like *Best Seller*, *New Arrival*, *Limited Stock*, *Free Shipping*)
  - “Details” and “Add to Cart” actions on product cards

- **Products list (`/products`)**
  - Sort dropdown (e.g., “All”, “Name (A-Z)”) and category filtering via query params
  - Pagination controls (“Previous”, pages 1..4, “Next”)
  - Product cards with consistent actions

- **Product details (`/products/{id}`)**
  - Quantity selector
  - Total price calculation (unit price × quantity)
  - Add to Cart / Add to Favorites / Continue Shopping
  - Technical specifications section
  - Share actions (Facebook/Twitter/LinkedIn/WhatsApp/Copy Link)
  - Customer reviews section
  - “Discover more products” recommendations

- **Login (`/login`)**
  - Email/password fields with required validation messaging
  - CTA: “Continue as Guest”
  - Test credential quick-fill buttons (“Use This Account”)

- **Cart (`/cart`)**
  - When unauthenticated: redirects to login with redirect query param

---

## Proposed Automated Scenarios

Each scenario below is written as an automation target. The intent is to convert these into new JUnit 5 tests and/or parameterized tests later.

### A) Authentication & Access Control (High impact)

**A1. Unauthenticated user is redirected to login when accessing Cart** (Smoke)
- Start state: fresh session, no stored auth
- Steps:
  1. Navigate to `/cart`
  2. Validate redirected URL contains `/login?redirect=/cart`
  3. Validate login page is loaded
- Expected: user cannot view cart without auth

**A2. Login with valid Customer account navigates back to redirect target** (Regression)
- Start: open `/cart` to force redirect
- Steps:
  1. Land on login page with redirect param
  2. Log in using customer credentials
  3. Verify landing page is `/cart` (or cart UI is visible)
- Expected: redirect honored

**A3. Login with invalid credentials shows error feedback** (Negative)
- Steps:
  1. Open `/login`
  2. Enter invalid email/password
  3. Submit
- Expected: visible error message, no navigation to protected area

**A4. Login validation: empty submission shows field-level errors** (Negative)
- Steps:
  1. Open `/login`
  2. Submit without typing
- Expected: “Email is required and must be valid”, “Password is required” (or equivalent)

**A5. “Use This Account” quick-fill populates inputs correctly** (Regression)
- Steps:
  1. Click “Use This Account” for Customer
  2. Verify email/password fields contain expected values
  3. Repeat for Admin account
- Expected: fields auto-filled consistently

> Gap addressed: current suite has no login coverage, which blocks cart tests.

---

### B) Product Listing / Catalog (Discovery & conversion)

**B1. Category deep links open products page with category filter applied** (Smoke/Regression)
- Steps:
  1. From home, click each category tile (parameterized by category)
  2. Verify URL contains `?category=`
  3. Verify results are shown
  4. Verify each result card displays same category label as filter (if UI supports it)
- Expected: category navigation works

**B2. Sorting changes list order deterministically (e.g., Name A–Z)** (Regression)
- Steps:
  1. Open `/products`
  2. Record visible product names
  3. Select sort “Name (A-Z)” (or equivalent)
  4. Verify new order is alphabetically sorted (case-insensitive)
- Expected: sort obeyed

**B3. Pagination: Next/Previous and page number navigation** (Regression)
- Steps:
  1. Open `/products`
  2. Click page “2”
  3. Verify page indicator updates (e.g., “Page 2 of 4”)
  4. Click Next and validate page increments
  5. Click Previous and validate page decrements
- Expected: pagination stable

**B4. Pagination boundaries** (Edge)
- Steps:
  1. On page 1: verify Previous is disabled or does not navigate
  2. On last page: verify Next is disabled or does not navigate
- Expected: no broken navigation at edges

**B5. Products list resilient to empty/invalid category query param** (Negative/Resilience)
- Steps:
  1. Open `/products?category=NotARealCategory`
  2. Verify either “no products” state is shown or fallback to all products
- Expected: app behaves gracefully

> Gap addressed: existing coverage doesn’t validate sorting/pagination/filter semantics.

---

### C) Product Details (High business value)

**C1. Quantity selector changes total price correctly** (Smoke/Regression)
- Steps:
  1. Open a product detail `/products/{id}`
  2. Capture unit price
  3. Increase quantity to 2
  4. Validate total price == unit price × 2 (precision rules defined)
- Expected: calculation is correct

**C2. Quantity boundary: cannot set quantity to 0 or negative** (Negative)
- Steps:
  1. Attempt to decrease below 1 (via UI controls)
  2. Or type 0/negative if allowed
- Expected: quantity stays at 1 OR validation message appears

**C3. Stock label affects purchasability** (Regression)
- Steps:
  1. Find a product with “In Stock”
  2. Verify Add to Cart enabled
  3. If out-of-stock product exists: verify Add to Cart disabled and stock status correct
- Expected: stock rules respected

**C4. “Continue Shopping” returns to products list (or home) consistently** (Regression)
- Steps:
  1. From product detail, click Continue Shopping
  2. Verify navigation target + page loaded
- Expected: consistent navigation

**C5. “Discover more products” navigation** (Regression)
- Steps:
  1. Scroll to recommendations
  2. Click a recommended product
  3. Verify product id/title changes
- Expected: cross-navigation works

**C6. Reviews section rendering** (Regression)
- Steps:
  1. Verify review summary exists (rating + count)
  2. Validate each review contains reviewer name and date
- Expected: reviews are stable and readable

**C7. Share actions: Copy Link** (High signal, low flakiness)
- Steps:
  1. Click “Copy Link”
  2. Verify toast/snackbar appears OR clipboard value matches current URL (depending on UI)
- Expected: correct behavior

> Gap addressed: current PDP tests validate visibility, but not key business logic (quantity/total/share).

---

### D) Search (Improve beyond basics)

**D1. Search is case-insensitive** (Regression)
- Steps:
  1. Search “Synthesizer” and “synthesizer”
  2. Compare result count or verify both return results
- Expected: consistent UX

**D2. Search trimming and whitespace handling** (Edge)
- Steps:
  1. Search with leading/trailing spaces, e.g., `"  synth  "`
- Expected: behaves as if trimmed

**D3. Special characters in search input do not break page** (Negative/Resilience)
- Steps:
  1. Search for `"'<>%$#@"`
  2. Verify page does not error and shows empty/no-results state
- Expected: graceful handling

**D4. Search from `/products` list vs header search on home are consistent** (Regression)
- Steps:
  1. Search same term from home header
  2. Search same term from products/results header
  3. Compare result count or key product presence

> Gap addressed: current tests don’t cover normalization and injection-like inputs.

---

### E) Cart (Enable + expand once auth is implemented)

> Cart tests are currently disabled in the suite; once login is automated, cart becomes a prime regression target.

**E1. Add to cart updates cart state (badge/count) after login** (Smoke)
- Steps:
  1. Login as customer
  2. Add product to cart
  3. Verify cart icon badge increments OR cart page shows item

**E2. Cart totals: sum of line totals equals cart total** (Regression)
- Steps:
  1. Add 2 different products
  2. Change quantities (if cart supports quantity changes)
  3. Verify total calculation

**E3. Remove item updates totals and empty state** (Regression)
- Steps:
  1. Add item
  2. Remove item
  3. Verify empty cart message/state

**E4. Cart persistence rules** (Regression)
- Steps:
  1. Add items
  2. Refresh
  3. Re-open cart
- Expected: persists according to business rules (session/local storage)

**E5. Deep link behavior: `login?redirect=/cart` after add-to-cart** (Edge)
- If “Add to Cart” triggers auth redirect when unauthenticated:
  - verify after login user returns to same product/cart accordingly

---

### F) URL & Route Resilience (Low cost, high confidence)

**F1. Invalid product id shows graceful error / not-found state** (Negative)
- Steps:
  1. Open `/products/999999`
  2. Validate not-found UI or redirect
- Expected: no crash

**F2. Products page loads without JS errors (basic console sanity)** (Optional)
- Use a lightweight approach: watch for severe front-end errors if toolchain supports it

---

## Test Organization Recommendations (POM, layers, naming)

### 1) Keep tests business-focused
- Tests should assert **outcomes and user-visible state transitions**.
- Implementation details belong in pages/components.

### 2) Expand the Page Object layer intentionally
Based on app features, consider adding/expanding:
- `LoginPage` (already a concept in app, not currently in observed POM list)
- `ProductsPage` (covers listing, sorting, pagination, category filter)
- `ProductDetailPage` enhancements (quantity, total price, share, favorites)
- `CartPage` activation once auth is stable
- Reusable `Toast/NotificationComponent` (for “copy link” / add-to-cart feedback)

### 3) Maintain stable, intention-revealing methods
- Prefer methods like `selectSort("Name (A-Z)")`, `goToPage(2)`, `filterByCategory("Synthesizers")`.
- Avoid page object methods that expose raw WebElements or require tests to know locators.

### 4) Naming & tagging strategy
- Keep current convention: `FeatureNameTest` with method `should...When...` and Allure `@Epic/@Feature/@Story`.
- Define consistent tags:
  - `@Tag("smoke")`: minimal build gate
  - `@Tag("regression")`: full pipeline suite
  - Optional: `@Tag("auth")`, `@Tag("catalog")`, `@Tag("cart")`

### 5) Data strategy
- Use deterministic product selection where possible:
  - Prefer selecting by product id (e.g., `/products/16`) for stable PDP tests.
  - For list behaviors (pagination/sorting), avoid asserting exact product names; validate ordering rules and non-empty lists.

### 6) Flakiness prevention (test stability)
- Avoid assertions on dynamic labels like “Limited Stock” unless business requires it.
- Prefer verifying **presence of required UI** (title/price/actions), not the entire text content of large sections.
- Validate navigation via URL + unique page identifiers.

---

## Technical Risks & Considerations

1. **Auth mechanism unknown**
   - If auth is token-based in local storage, Selenium tests must handle it via UI login (preferred) or controlled storage injection (only if allowed by framework standards).

2. **Data volatility**
   - Catalog may change (product count, featured items). Tests must avoid brittle expectations like exact counts.

3. **Pagination and sort determinism**
   - Sorting correctness requires stable comparison logic (case-insensitive, locale). Decide expected behavior first.

4. **Clipboard limitations**
   - “Copy Link” can be flaky in CI due to clipboard restrictions. Prefer verifying a visible “copied” toast or fallback assertion.

5. **Redirect timing & synchronization**
   - Auth redirects and route changes are async; ensure future implementation uses explicit waits and page-ready indicators.

6. **Locator strategy**
   - If the app lacks `data-testid`, prioritize stable selectors (ids, names, stable CSS classes). If instability is observed, consider advocating adding `data-testid` attributes as a product quality improvement.

---

## Suggested Roadmap (Practical)

- **Sprint 1 / Wave 1**: A1, A2, B1, B3, C1, C4 (Smoke + high-impact regressions)
- **Sprint 2 / Wave 2**: A3–A5, B2, B4–B5, C2–C6, D1–D3
- **Sprint 3 / Wave 3**: Cart suite enablement (E1–E5) + route resilience (F1)

---

## Accessibility Note

This plan is written with accessibility-aware testing in mind (e.g., validating user-visible errors and predictable navigation), but it doesn’t guarantee full WCAG 2.2 AA compliance. Manual checks with tools like Accessibility Insights are still recommended.

