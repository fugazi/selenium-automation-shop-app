# Architecture Research

**Domain:** E2E Checkout Test Architecture for E-commerce
**Researched:** January 25, 2026
**Confidence:** HIGH

## Standard Architecture

### System Overview

```
┌─────────────────────────────────────────────────────────────┐
│                   Selenium POM Framework                    │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐       │
│  │ BasePage │  │ HomePage │  │ CartPage │  │CheckoutPage│       │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘       │
│       │            │            │            │              │
│       └────────────┴────────────┴────────────┴──────────────┤
│                        Page Objects                         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐    │
│  │                    Components                       │    │
│  │  ┌──────────┐  ┌──────────┐               │    │
│  │  │ Address   │  │Payment    │  ┌─────────┐  │    │
│  │  │ Component │  │Component   │  │Order    │  │    │
│  │  └──────────┘  └──────────┘  │Summary   │  │    │
│  │                            └─────────┘  │    │
│  └─────────────────────────────────────────────────────┘    │
├─────────────────────────────────────────────────────────────┤
│                        Data Models                         │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐            │
│  │ Checkout  │  │ Address   │  │ Payment   │            │
│  │   Data   │  │   Data   │  │   Data   │            │
│  └──────────┘  └──────────┘  └──────────┘            │
└─────────────────────────────────────────────────────────────┘
```

### Component Responsibilities

| Component | Responsibility | Typical Implementation |
|-----------|----------------|------------------------|
| **BasePage** | Provides common wait helpers, click/type utilities, page load detection | Abstract class with WebDriverWait, @Step annotations for Allure |
| **BaseTest** | WebDriver lifecycle, lazy Page Object initialization | Abstract class with @BeforeEach/@AfterEach, page() getters |
| **CheckoutPage** | Encapsulates checkout flow pages (address → payment → review) | Page Object with sections for each checkout step |
| **AddressFormComponent** | Reusable address input form | Component with fields for street, city, state, zip |
| **PaymentMethodComponent** | Payment selection and card entry | Component with radio buttons, card inputs, validation |
| **OrderSummaryComponent** | Order review section before confirmation | Component with item list, totals, confirm button |
| **CheckoutData** | Immutable data container for checkout test data | Java Record with address and payment details |
| **TestDataFactory** | Generates valid/invalid test data | Static factory using JavaFaker for dynamic data |

## Recommended Project Structure

```
src/test/java/org/fugazi/
├── pages/                       # Page Objects
│   ├── BasePage.java            # Already exists (base class)
│   ├── HomePage.java             # Already exists
│   ├── CartPage.java             # Already exists
│   ├── ProductDetailPage.java     # Already exists
│   ├── CheckoutPage.java          # NEW: Main checkout page object
│   └── components/              # Reusable UI components
│       ├── AddressFormComponent.java       # NEW: Address input form
│       ├── PaymentMethodComponent.java    # NEW: Payment selection
│       ├── OrderSummaryComponent.java     # NEW: Order review section
│       ├── HeaderComponent.java          # Already exists
│       └── FooterComponent.java          # Already exists
├── data/
│   ├── models/                    # Data models
│   │   ├── User.java              # Already exists
│   │   ├── Product.java          # Already exists
│   │   ├── Credentials.java      # Already exists
│   │   ├── CheckoutData.java    # NEW: Checkout data record
│   │   └── Address.java         # NEW: Address data record
│   └── providers/
│       └── TestDataFactory.java   # Already exists (extend with checkout data)
└── tests/
    ├── BaseTest.java             # Already exists
    ├── AddToCartTest.java        # Already exists
    ├── CartWorkflowTest.java      # Already exists
    └── CheckoutFlowTest.java     # NEW: E2E checkout tests
```

### Structure Rationale

- **pages/**: Organizes all Page Objects by page type. Checkout-related pages grouped together.
- **pages/components/**: Separates reusable UI components that span multiple pages (address forms, payment selectors).
- **data/models/**: Contains immutable Java Records for test data. Consistent with Lombok usage.
- **data/providers/**: Centralizes test data generation using JavaFaker. Ensures test independence.

## Architectural Patterns

### Pattern 1: Page Object Model with Section-Based Organization

**What:** Organize checkout page as a single Page Object with internal sections for each checkout step.

**When to use:** When checkout is a multi-step wizard on a single page (common in SPAs).

**Trade-offs:**
- **Pros:** Single navigation point, easier to maintain checkout flow state, follows existing framework pattern
- **Cons:** Large page object can become unwieldy if steps are independent pages

**Example:**
```java
public class CheckoutPage extends BasePage {
    // Section 1: Shipping Address
    private final AddressFormComponent shippingAddress;

    // Section 2: Payment Method
    private final PaymentMethodComponent paymentMethod;

    // Section 3: Order Review
    private final OrderSummaryComponent orderSummary;

    public CheckoutPage(WebDriver driver) {
        super(driver);
        this.shippingAddress = new AddressFormComponent(driver);
        this.paymentMethod = new PaymentMethodComponent(driver);
        this.orderSummary = new OrderSummaryComponent(driver);
    }

    // Fluent interface for checkout flow
    @Step("Enter shipping address")
    public CheckoutPage enterShippingAddress(Address address) {
        shippingAddress.fillForm(address);
        return this;
    }

    @Step("Select payment method and enter card details")
    public CheckoutPage enterPaymentDetails(PaymentMethod payment) {
        paymentMethod.selectPaymentMethod(payment);
        paymentMethod.enterCardDetails(payment);
        return this;
    }

    @Step("Review order and confirm")
    public OrderConfirmationPage confirmOrder() {
        orderSummary.reviewItems();
        orderSummary.clickConfirmButton();
        return new OrderConfirmationPage(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return isDisplayed(SHIPPING_SECTION);
    }
}
```

### Pattern 2: Component-Based Page Objects

**What:** Extract reusable form components (address, payment, summary) into separate Component classes.

**When to use:** When forms appear across multiple pages or when sections are complex enough to warrant separation.

**Trade-offs:**
- **Pros:** Highly reusable, easier to test components in isolation, cleaner code organization
- **Cons:** More files to maintain, can add indirection

**Example:**
```java
public class AddressFormComponent extends BasePage {
    private final By FIRST_NAME = By.name("firstName");
    private final By LAST_NAME = By.name("lastName");
    private final By ADDRESS_LINE = By.name("address");
    private final By CITY = By.name("city");
    private final By STATE = By.name("state");
    private final By ZIP_CODE = By.name("zipCode");

    public AddressFormComponent(WebDriver driver) {
        super(driver);
    }

    @Step("Fill address form with: {address}")
    public void fillForm(Address address) {
        type(FIRST_NAME, address.firstName());
        type(LAST_NAME, address.lastName());
        type(ADDRESS_LINE, address.addressLine());
        type(CITY, address.city());
        selectOption(STATE, address.state());
        type(ZIP_CODE, address.zipCode());
    }

    @Step("Verify address form errors are displayed")
    public List<String> getValidationErrors() {
        return getElements(By.cssSelector(".error-message"))
            .stream()
            .map(WebElement::getText)
            .toList();
    }

    @Step("Submit address form")
    public void submit() {
        click(By.cssSelector("button[type='submit']"));
    }
}
```

### Pattern 3: Data-Driven Testing with Java Records

**What:** Use immutable Java Records for test data, generated via TestDataFactory.

**When to use:** For all test data. Ensures immutability, test independence, and clean API.

**Trade-offs:**
- **Pros:** Type-safe, immutable by default, builder pattern optional, works with AssertJ
- **Cons:** Requires Java 14+ (already met by Java 21 project)

**Example:**
```java
public record Address(
    String firstName,
    String lastName,
    String addressLine,
    String city,
    String state,
    String zipCode,
    String country
) {
}

public record PaymentMethod(
    PaymentType type,  // CREDIT_CARD, PAYPAL, APPLE_PAY
    String cardNumber,
    String cardholderName,
    String expiryMonth,
    String expiryYear,
    String cvv
) {
}

public record CheckoutData(
    Address shippingAddress,
    Address billingAddress,
    PaymentMethod payment
) {
}

// Factory for generating test data
public class TestDataFactory {
    @Getter private static final Faker faker = new Faker(Locale.US);

    public static CheckoutData validCheckoutData() {
        return new CheckoutData(
            validAddress(),
            validAddress(),
            validCreditCardPayment()
        );
    }

    public static Address validAddress() {
        return new Address(
            faker.name().firstName(),
            faker.name().lastName(),
            faker.address().streetAddress(),
            faker.address().city(),
            faker.address().stateAbbr(),
            faker.address().zipCode(),
            "United States"
        );
    }

    public static PaymentMethod validCreditCardPayment() {
        return new PaymentMethod(
            PaymentType.CREDIT_CARD,
            "4111111111111111",  // Test card number
            faker.name().fullName(),
            "12",
            "2026",
            "123"
        );
    }
}
```

### Pattern 4: Facade for E2E Checkout Workflow

**What:** Create a Facade class that encapsulates the entire checkout flow from cart to order confirmation.

**When to use:** When tests need to execute complex multi-page workflows repeatedly. Reduces test code complexity.

**Trade-offs:**
- **Pros:** Test code reads like user story, centralized business logic, easier to maintain
- **Cons:** Adds abstraction layer, can hide useful page details

**Example:**
```java
public class CheckoutFlowFacade {
    private final WebDriver driver;
    private final CartPage cartPage;
    private final CheckoutPage checkoutPage;

    public CheckoutFlowFacade(WebDriver driver) {
        this.driver = driver;
        this.cartPage = new CartPage(driver);
        this.checkoutPage = new CheckoutPage(driver);
    }

    @Step("Complete checkout: Add to cart → Fill address → Enter payment → Confirm")
    public OrderConfirmationPage completeCheckout(
            CheckoutData checkoutData
    ) {
        // Step 1: Navigate to checkout
        cartPage.clickCheckoutButton();

        // Step 2: Fill shipping address
        checkoutPage
                .enterShippingAddress(checkoutData.shippingAddress())
                .continueToPayment();

        // Step 3: Enter payment details
        checkoutPage
                .enterPaymentDetails(checkoutData.payment())
                .continueToReview();

        // Step 4: Confirm order
        return checkoutPage.confirmOrder();
    }

    @Step("Complete checkout with guest user")
    public OrderConfirmationPage completeGuestCheckout(CheckoutData checkoutData) {
        cartPage.clickCheckoutButton();
        checkoutPage
                .selectGuestCheckout()
                .enterShippingAddress(checkoutData.shippingAddress())
                .enterPaymentDetails(checkoutData.payment())
                .confirmOrder();
        return new OrderConfirmationPage(driver);
    }
}
```

### Pattern 5: Fluent Interface for Checkout Steps

**What:** Each action method returns `this` (for chaining) or the next Page object (for navigation).

**When to use:** Recommended for all Page Objects in this project. Matches existing framework style.

**Trade-offs:**
- **Pros:** Readable test code, clear navigation flow, method chaining
- **Cons:** Can create long method chains if not careful

**Example (Test using fluent interface):**
```java
@Test
@DisplayName("Should complete checkout with valid address and payment")
void shouldCompleteCheckoutWithValidData() {
    var checkoutData = TestDataFactory.validCheckoutData();

    cartPage().clickCheckoutButton()
             .enterShippingAddress(checkoutData.shippingAddress())
             .continueToPayment()
             .enterPaymentDetails(checkoutData.payment())
             .continueToReview()
             .reviewOrderDetails()
             .confirmOrder();

    softly().assertThat(driver.getCurrentUrl())
                .as("Should navigate to order confirmation page")
                .contains("/order-confirmation");
}
```

## Data Flow

### Checkout Flow State

```
[User Action: Add to Cart]
    ↓
CartPage → checkoutPage() → CheckoutPage Initialized
    ↓
[User Action: Fill Address Form]
    ↓
AddressFormComponent.fillForm(address) → Validates fields → Enables Next Step
    ↓
[User Action: Continue to Payment]
    ↓
CheckoutPage.continueToPayment() → PaymentMethodComponent Visible → Payment Section Enabled
    ↓
[User Action: Enter Payment Details]
    ↓
PaymentMethodComponent.enterCardDetails(payment) → Validates format → Enables Confirm Button
    ↓
[User Action: Review and Confirm]
    ↓
OrderSummaryComponent.confirmOrder() → Creates Order → Navigates to Confirmation
    ↓
[Response] ← OrderConfirmationPage ← URL Change ← Order ID Displayed
```

### Test Data Flow

```
TestDataFactory.validCheckoutData()
    ↓ (generates immutable CheckoutData record)
CheckoutData (contains Address, PaymentMethod)
    ↓ (passed to test methods)
Page Object methods (enterShippingAddress, enterPaymentDetails)
    ↓ (fills UI elements)
Application State (stores in session/database)
    ↓ (on confirm)
Order Created → Confirmation Page Displayed
```

### Key Data Flows

1. **Address Validation Flow:** Input address → Client-side validation → Server-side validation → Error display or next step enabled
2. **Payment Processing Flow:** Select payment method → Enter card details → Validate format → Submit → Payment gateway response
3. **Order Creation Flow:** Review items + totals → Confirm → Backend order creation → Order ID generation → Redirect to confirmation

## Scaling Considerations

| Scale | Architecture Adjustments |
|-------|--------------------------|
| 0-1k concurrent tests | Current single-threaded WebDriver is sufficient. No changes needed. |
| 1k-10k concurrent tests | Use Selenium Grid or cloud platform. Existing architecture supports parallel execution via JUnit 5. |
| 10k+ concurrent tests | Consider sharding tests by feature group. Add parallel WebDriver management. |

### Scaling Priorities

1. **First bottleneck:** Test execution time for E2E checkout flows. Fix by: Parallel execution, efficient wait strategies.
2. **Second bottleneck:** Flaky tests due to dynamic page loads. Fix by: Using explicit waits, handling stale elements with retry logic.

## Anti-Patterns

### Anti-Pattern 1: Putting Assertions in Page Objects

**What people do:** Adding `assertThat()` or `assertTrue()` inside Page Object methods.

**Why it's wrong:** Violates Single Responsibility Principle. Page Objects should handle UI interaction, tests should handle assertions.

**Do this instead:** Return state from Page Objects, let tests perform assertions.

**Wrong:**
```java
@Step("Enter shipping address")
public void enterShippingAddress(Address address) {
    fillForm(address);
    // DON'T DO THIS:
    assertThat(getErrorMessage()).isEmpty();  // Assertion in Page Object
}
```

**Correct:**
```java
@Step("Enter shipping address and return error messages")
public List<String> enterShippingAddress(Address address) {
    fillForm(address);
    // DO THIS:
    return getValidationErrors();  // Return state for test to assert
}

// In test:
var errors = checkoutPage.enterShippingAddress(address);
softly().assertThat(errors).as("Should have no validation errors").isEmpty();
```

### Anti-Pattern 2: Using Thread.sleep() for Page Navigation

**What people do:** Adding `Thread.sleep(2000)` after clicking "Continue to Payment" to wait for next section.

**Why it's wrong:** Unreliable, adds unnecessary test execution time, framework already provides explicit waits.

**Do this instead:** Use BasePage wait helpers (`waitForVisibility`, `waitForUrlChange`) or custom wait conditions.

**Wrong:**
```java
@Step("Continue to payment")
public CheckoutPage continueToPayment() {
    click(CONTINUE_BUTTON);
    Thread.sleep(2000);  // DON'T DO THIS
    return this;
}
```

**Correct:**
```java
@Step("Continue to payment")
public CheckoutPage continueToPayment() {
    var currentUrl = getCurrentUrl();
    click(CONTINUE_BUTTON);

    // DO THIS: Wait for URL change or next section to be visible
    waitForUrlChange(currentUrl);
    waitForVisibility(PAYMENT_SECTION);
    return this;
}
```

### Anti-Pattern 3: Hardcoding Test Data in Tests

**What people do:** Creating checkout data directly in test methods with string literals like `"123 Main Street"`.

**Why it's wrong:** Tests aren't data-driven, can't easily test variations, duplicates data setup code.

**Do this instead:** Use TestDataFactory with JavaFaker to generate dynamic, realistic data.

**Wrong:**
```java
@Test
void shouldCheckoutWithValidAddress() {
    checkoutPage.enterShippingAddress(
        new Address("John", "Doe", "123 Main St", "City", "CA", "12345", "US")
    );
    checkoutPage.enterPaymentDetails(
        new PaymentMethod(CREDIT_CARD, "4111111111111111", "John Doe", "12", "2026", "123")
    );
}
```

**Correct:**
```java
@Test
void shouldCheckoutWithValidAddress() {
    var checkoutData = TestDataFactory.validCheckoutData();
    checkoutPage.enterShippingAddress(checkoutData.shippingAddress());
    checkoutPage.enterPaymentDetails(checkoutData.payment());
}
```

### Anti-Pattern 4: Over-Coupling Page Objects

**What people do:** Making CheckoutPage depend directly on CartPage or HomePage objects.

**Why it's wrong:** Creates circular dependencies, makes Page Objects harder to test in isolation, violates SRP.

**Do this instead:** Use Facade pattern for complex workflows, keep Page Objects independent.

**Wrong:**
```java
public class CheckoutPage extends BasePage {
    private CartPage cartPage;  // DON'T DO THIS: Coupling

    public CheckoutPage(WebDriver driver, CartPage cartPage) {
        super(driver);
        this.cartPage = cartPage;
    }
}
```

**Correct:**
```java
public class CheckoutPage extends BasePage {
    // DO THIS: Independent, receives WebDriver only
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }
}

// Use Facade to orchestrate:
public class CheckoutFlowFacade {
    private final CartPage cartPage;
    private final CheckoutPage checkoutPage;

    // Coordinates between pages but pages remain independent
}
```

## Integration Points

### External Services

| Service | Integration Pattern | Notes |
|---------|---------------------|-------|
| **Payment Gateway** (Stripe/PayPal) | Stub/iframe handling | Checkout tests should use test card numbers, handle iframe if payment is embedded |
| **Address Validation API** | Mock via network interception | Use DevTools to intercept API calls for faster validation testing |
| **Backend Order Service** | API + UI hybrid validation | Create order via API, verify in UI confirmation page (recommended for reliability) |

### Internal Boundaries

| Boundary | Communication | Notes |
|----------|---------------|-------|
| **CartPage ↔ CheckoutPage** | Navigation via "Checkout" button | CartPage.clickCheckoutButton() returns CheckoutPage |
| **CheckoutPage ↔ AddressFormComponent** | Composition (has-a relationship) | CheckoutPage owns AddressFormComponent instance |
| **CheckoutPage ↔ PaymentMethodComponent** | Composition (has-a relationship) | CheckoutPage delegates to PaymentMethodComponent for card entry |
| **CheckoutPage ↔ OrderSummaryComponent** | Composition (has-a relationship) | CheckoutPage delegates to OrderSummaryComponent for final review |
| **Tests ↔ Page Objects** | Test-to-Page communication | Tests call Page methods, verify state via getters or assertions |

## Sources

- **Official Documentation:**
  - Page Object Model - Selenium.dev: https://www.selenium.dev/documentation/test_practices/encouraged/page_object_models/
  - BrowserStack Design Patterns Guide (2025): https://www.browserstack.com/guide/design-patterns-in-automation-framework

- **Community Articles (HIGH confidence):**
  - "The Ultimate Guide to Page Object Model (POM) in Test Automation" - Medium, Sep 2025: https://medium.com/@sajith-dilshan/the-ultimate-guide-to-page-object-model-pom-in-test-automation-2663d8788cc6
  - "16 Selenium Best Practices For Test Automation 2025" - TestMu.ai, 2025: https://www.testmu.ai/blog/selenium-best-practices-for-web-testing/
  - "Best Practices for Writing Stable Selenium Tests in 2025" - Medium, 2025: https://medium.com/@abhishek.builds/best-practices-for-writing-stable-selenium-tests-in-2025-c240961f697b

- **Existing Framework Analysis (HIGH confidence):**
  - BasePage.java, BaseTest.java, CartPage.java, HomePage.java
  - HeaderComponent.java, FooterComponent.java
  - TestDataFactory.java, User.java, Product.java records

---
*Architecture research for: E2E Checkout Test Architecture*
*Researched: January 25, 2026*
