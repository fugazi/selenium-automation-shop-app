# Technology Stack

**Project:** Music Tech Shop - Checkout Flow Testing
**Researched:** January 25, 2026
**Confidence:** HIGH (verified with Selenium official docs and multiple industry sources)

## Recommended Stack

### Core Framework (Already In Place)
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| Selenium WebDriver | 4.27.0 | Browser automation | Already configured in project. Industry standard for E2E UI testing. W3C WebDriver compliant, cross-browser support. |
| JUnit 5 | 5.11.4 | Test framework | Already configured. Supports parameterized tests, parallel execution, and modern Jupiter API. |
| AssertJ | 3.27.3 | Assertions | Already configured. Soft assertions ideal for multiple checkout validations. Superior error messages over JUnit assertions. |
| Allure | 2.29.0 | Reporting | Already configured. Excellent for complex checkout flows with step-by-step visualization. |
| Java | 21 | Language | Modern features (records, sealed classes, pattern matching) improve test readability. |

### Test Data Generation (Enhance Existing)
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| JavaFaker | 1.0.2 | Dynamic test data | Already configured. **For checkout flows, expand usage** to generate realistic addresses, phone numbers, and credit card data. Prevents duplicate email conflicts and ensures tests are deterministic. |
| Builder Pattern | Custom | Data object construction | Already used in User.java. **Recommended for all checkout data models** (ShippingInfo, PaymentDetails, OrderDetails). |

### Checkout-Specific Additions

#### Page Object Extensions
| Technology | Purpose | When to Use |
|------------|---------|-------------|
| **Multi-Step Checkout Page Objects** | Model sequential checkout stages (Shipping → Payment → Review → Confirmation) | Always. Each step is a separate Page Object returning the next step's page. |
| **Page Component Objects** | Reusable UI components within checkout pages | When checkout has repeated elements (e.g., address form, payment method selection card) that appear in multiple contexts. |

#### Test Data Strategy
| Technology | Purpose | When to Use |
|------------|---------|-------------|
| **JavaFaker for Address/Contact Data** | Generate unique, realistic shipping addresses | All checkout tests. Use `faker.address().streetAddress()`, `faker.address().city()`, `faker.address().zipCode()`, `faker.phoneNumber().cellPhone()` |
| **Mock Payment Data** | Generate test-only payment card numbers | Only for testing environments. Never use real card numbers. Use Faker's `business().creditCardNumber()` for valid Luhn-checked numbers. |
| **Data Factory Pattern** | Centralize test data generation | Always. Extend existing `TestDataFactory` with checkout-specific methods (e.g., `generateShippingInfo()`, `generatePaymentDetails()`). |

#### API Integration (Recommended Pattern)
| Technology | Version | Purpose | Why |
|------------|---------|---------|-----|
| **Apache HttpClient 5** | 5.4.1 | Backend test data setup | Already configured. **Use to create test users, products, and cart items via API before UI tests**. This reduces test execution time and makes tests deterministic. |
| **Jackson** | 2.18.2 | JSON serialization/deserialization | Already configured. Map API responses to test data models (User, Product, Order). Use `@JsonProperty` for mapping API fields. |
| **Rest-Assured** | 5.4.0 | API testing | Already configured. Optional: For testing checkout API endpoints directly in integration tests. |

### Test Design Patterns

| Pattern | Implementation | Why for Checkout |
|---------|---------------|------------------|
| **Fluent Page Object Chaining** | Methods return next page: `public PaymentPage goToPayment()` | Models user journey through checkout. If step order changes, only method signatures break, not test logic. |
| **Form Validation Testing** | Separate tests for each validation rule | Checkout forms have multiple validations (required fields, format checks). Isolate each validation to ensure failures are specific. |
| **Data-Driven Testing** | `@ParameterizedTest` with checkout scenarios | Test multiple edge cases: new vs. returning user, different shipping methods, payment type variations. |
| **State Verification** | Assert intermediate states, not just final outcome | Verify cart contents persist through checkout, shipping costs are calculated correctly, order summary matches items. |

### Waits and Synchronization (Already In Place - Use Consistently)
| Technology | Purpose | Why |
|------------|---------|-----|
| **Explicit Waits (WebDriverWait)** | Handle dynamic checkout elements | Already in BasePage. **Critical for checkout**: payment loading, shipping address validation, order confirmation. Never use `Thread.sleep()`. |
| **ExpectedConditions** | Wait conditions | `visibilityOf()`, `elementToBeClickable()`, `urlToBe()`. Use for payment buttons, shipping method selection, confirmation page load. |

## Alternatives Considered

| Category | Recommended | Alternative | Why Not |
|----------|-------------|-------------|---------|
| Test Data | **JavaFaker** | Hardcoded values in tests | Hardcoding creates brittle tests, duplicates, and false positives. Faker generates realistic data every run. |
| Page Object Model | **Multi-step POM** | Monolithic single page object | Single checkout page object doesn't model user journey. Changes to step order require rewriting tests. Separate pages make intent explicit. |
| Test Setup | **API-first test data** | Browser-only setup | Setting up users, products, and cart items via UI is slow and flaky. API is fast, deterministic, and reliable. |
| Payment Testing | **Mock/test card numbers** | Real payment gateways | Testing with real payment cards is slow, expensive, and risky in CI/CD. Mock cards are instant and safe. |
| Assertions | **Soft Assertions (AssertJ)** | Hard assertions (JUnit) | Checkout validates multiple fields (address, payment, order summary). Hard assertions stop at first failure, hiding other bugs. Soft assertions report all failures. |

## Installation

**No new dependencies required** for checkout testing. Extend existing stack:

```xml
<!-- Already in pom.xml - no changes needed -->
<dependency>
    <groupId>com.github.javafaker</groupId>
    <artifactId>javafaker</artifactId>
    <version>1.0.2</version>
</dependency>

<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.4.1</version>
</dependency>
```

## Checkout-Specific Implementation Patterns

### 1. Multi-Step Checkout Page Objects

**Structure:**
```java
// Each checkout step is a separate page object
public class ShippingPage extends BasePage {
    public PaymentPage enterShippingInfo(ShippingInfo info) {
        // Fill shipping form
        // Click "Continue to Payment"
        return new PaymentPage(driver);
    }
}

public class PaymentPage extends BasePage {
    public ReviewPage enterPaymentDetails(PaymentDetails payment) {
        // Select payment method
        // Enter card details
        // Click "Review Order"
        return new ReviewPage(driver);
    }
}

public class ReviewPage extends BasePage {
    public ConfirmationPage placeOrder() {
        // Verify order summary
        // Click "Place Order"
        return new ConfirmationPage(driver);
    }
}
```

**Why:** Models user journey, makes navigation explicit, isolates changes.

### 2. Form Validation Testing

**Pattern:**
```java
@ParameterizedTest
@CsvSource({
    ",,Email is required",  // Empty fields
    "invalid-email,,Invalid email format",  // Bad format
    "test@example.com,,Password is required",  // Missing password
})
void shouldShowValidationErrors(String email, String password, String expectedError) {
    // Fill form with invalid data
    // Click submit
    // Assert error message matches expectedError
}
```

**Why:** Each validation rule has dedicated test. Failures are specific and actionable.

### 3. Test Data with JavaFaker

**Extend TestDataFactory:**
```java
public class TestDataFactory {
    private static final Faker faker = new Faker(Locale.US);

    // Existing methods...

    // New for checkout
    public static ShippingInfo generateShippingInfo() {
        return ShippingInfo.builder()
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .address(faker.address().streetAddress())
            .city(faker.address().city())
            .state(faker.address().state())
            .zipCode(faker.address().zipCode())
            .phone(faker.phoneNumber().cellPhone())
            .build();
    }

    public static PaymentDetails generatePaymentDetails() {
        return PaymentDetails.builder()
            .cardNumber(faker.business().creditCardNumber())
            .cardHolder(faker.name().fullName())
            .expiryDate("12/28")  // Fixed future date for tests
            .cvv("123")
            .build();
    }
}
```

**Why:** Unique, realistic data every run. No duplicates, no hardcoded values.

### 4. API-First Test Setup

**Pattern:**
```java
@BeforeEach
void setUpTestData() throws Exception {
    // Create test user via API (fast, no UI)
    var userResponse = httpClient.execute(
        createPost("/api/users", generateUserJson())
    );
    testUser = objectMapper.readValue(userResponse, User.class);

    // Add products to cart via API
    httpClient.execute(
        createPost("/api/cart/items", generateCartItemJson(testProduct.getSku()))
    );
}

@Test
void shouldCompleteCheckoutSuccessfully() {
    // Navigate to checkout (cart already populated)
    cartPage().goToCheckout();

    // Fill shipping and payment
    shippingPage().enterShippingInfo(TestDataFactory.generateShippingInfo());
    paymentPage().enterPaymentDetails(TestDataFactory.generatePaymentDetails());

    // Verify confirmation
    confirmationPage().verifyOrderDetails(testUser, testProduct);
}
```

**Why:** Setup is fast (seconds vs. minutes), deterministic, and doesn't rely on UI stability for test data.

## What NOT to Use and Why

### Anti-Pattern 1: Monolithic Checkout Test Scripts
**Problem:** Testing entire checkout (cart → shipping → payment → confirmation) in one test method.

**Why avoid:**
- Difficult to debug: Failure could be anywhere in the flow
- False positives: One bug breaks validation of unrelated steps
- Long execution time: Re-running entire flow for every change is inefficient

**Instead:** Break into focused tests:
- `shouldNavigateFromCartToShipping()`
- `shouldValidateShippingForm()`
- `shouldSelectPaymentMethod()`
- `shouldDisplayOrderConfirmation()`

### Anti-Pattern 2: Thread.sleep() for Waits
**Problem:** Using `Thread.sleep()` to wait for checkout elements (e.g., payment loading, address validation).

**Why avoid:**
- Slows execution: Fixed waits are longer than necessary
- Flaky: Network/server response times vary; sleep may be too short or too long
- Unreliable: Cannot detect when element actually appears

**Instead:** Use explicit waits (already in BasePage):
```java
waitForClickable(By.id("place-order-button")).click();
waitForVisibility(By.className("order-confirmation"));
```

### Anti-Pattern 3: Hardcoded Test Data
**Problem:** Hardcoding email `"test@example.com"`, phone `"555-123-4567"`, address `"123 Main St"`.

**Why avoid:**
- Duplicate failures: Second test run fails if data already exists
- Unrealistic: Doesn't test real-world data variations
- Brittle: Changes to required fields break multiple tests

**Instead:** Use JavaFaker:
```java
String email = faker.internet().emailAddress();  // Unique every run
String phone = faker.phoneNumber().cellPhone();
String address = faker.address().streetAddress();
```

### Anti-Pattern 4: Skipping Page Object Model for Checkout
**Problem:** Writing checkout interactions directly in test methods.

**Why avoid:**
- UI changes break multiple tests
- Locators scattered throughout test suite
- No code reuse for common checkout actions

**Instead:** Create separate Page Objects for each checkout step (ShippingPage, PaymentPage, ReviewPage, ConfirmationPage).

### Anti-Pattern 5: Testing Payment Gateways in UI
**Problem:** Using real payment cards or gateways in UI tests.

**Why avoid:**
- Slow: Each payment takes seconds, tests become hours
- Expensive: Real transactions cost money or hit test limits
- Risky: Real payment data in tests, accidental production charges

**Instead:**
- Use mock/test card numbers (Stripe test cards, PayPal sandbox)
- Test payment gateway logic via API tests
- Only validate UI integration, not actual payment processing

### Anti-Pattern 6: Ignoring State Verification
**Problem:** Only asserting final order confirmation, not intermediate states.

**Why avoid:**
- Missing bugs: Cart contents changed, shipping cost miscalculated, wrong products
- Weak diagnostics: Don't know where checkout failed

**Instead:** Assert at each step:
```java
// After entering shipping
assertThat(shippingPage.getShippingCost()).as("Shipping cost").isEqualTo("9.99");

// After payment
assertThat(reviewPage.getTotal()).as("Order total").isEqualTo(itemPrice + shipping + tax);

// After confirmation
assertThat(confirmationPage.getOrderNumber()).as("Order number").isNotEmpty();
```

## Best Practices Summary

### For Page Objects
1. **One method per action**: `enterShippingInfo()`, `selectPaymentMethod()`, `placeOrder()`
2. **Return next page**: Enables fluent chaining and models user journey
3. **No assertions in pages**: Only tests assert, pages provide state accessors
4. **Component objects for reusable parts**: Address form, payment method card

### For Test Data
1. **Never hardcode**: Use JavaFaker for all dynamic fields
2. **Builder pattern**: Fluent data construction (`ShippingInfo.builder().firstName(...).build()`)
3. **API setup**: Create users/products/carts via API before UI tests
4. **Separate environments**: Test data for dev/staging, never production data

### For Test Design
1. **Parameterized tests**: Test multiple validation scenarios with data providers
2. **Soft assertions**: Validate multiple fields without stopping at first failure
3. **Single responsibility**: Each test validates one thing
4. **Independent**: Tests run in any order, don't share state

### For Wait Strategy
1. **Explicit waits only**: Never use `Thread.sleep()`
2. **Wait for right condition**: `visibilityOf`, `clickable`, `urlToBe`
3. **Reuse wait helpers**: BasePage already has `waitForVisibility()`, `waitForClickable()`
4. **Handle stale elements**: Retry on `StaleElementReferenceException` (already in BasePage)

## Sources

- **Official Selenium Documentation**: Page Object Models (HIGH confidence) - https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/
- **TestRail**: Selenium Test Automation Guide (MEDIUM confidence) - https://www.testrail.com/blog/selenium-test-automation/
- **Testleaf**: Selenium Anti-Patterns (MEDIUM confidence) - https://www.testleaf.com/blog/selenium-anti-pattern-what-not-to-do-in-automation/
- **Medium (Sajith Dilshan)**: Java Faker for Test Automation (MEDIUM confidence) - https://medium.com/@sajith-dilshan/supercharging-test-automation-with-java-faker-generating-realistic-test-data-0891f1312067
- **Athen**: Form Validation with Selenium (MEDIUM confidence) - https://athen.tech/automating-web-form-validation-with-selenium/
- **Testlio**: E-commerce Testing Guide (MEDIUM confidence) - https://testlio.com/blog/ecommerce-testing-guide/
- **Existing Project Code**: BaseTest.java, BasePage.java, TestDataFactory.java, User.java, pom.xml (HIGH confidence)
