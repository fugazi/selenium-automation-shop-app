# Test Execution Failure Summary
**Date**: 2026-01-26
**Command**: `mvn clean test -Dheadless=true -Dbrowser=chrome`

## Overall Results
- **Tests Run**: 185
- **Failures**: 10
- **Errors**: 1
- **Skipped**: 7
- **Execution Time**: 5 min 14 sec

## Failed Tests

### 1. UrlResilienceTest (4 failures)

#### 1.1 shouldHandleNonExistentRouteGracefully
- **Location**: `UrlResilienceTest.java:226`
- **Issue**: Expected `true` but was `false`
- **Description**: Application doesn't handle non-existent routes gracefully (404 handling may be broken)

#### 1.2 shouldHandleMalformedUrlGracefully
- **Location**: `UrlResilienceTest.java:252`
- **Issue**: Expected `true` but was `false`
- **Description**: Malformed URL handling is not working as expected

#### 1.3 shouldHandleInvalidProductIdGracefully
- **Location**: `UrlResilienceTest.java:44`
- **Issue**: Expected `true` but was `false`
- **Test Case**: Product ID 999999
- **Description**: Invalid product ID (999999) not handled correctly

#### 1.4 shouldHandleNegativeProductIdGracefully
- **Location**: `UrlResilienceTest.java:76`
- **Issue**: Expected `true` but was `false`
- **Description**: Negative product IDs not handled gracefully

### 2. ProductDetailExtendedTest (2 failures)

#### 2.1 shouldCalculateTotalPriceCorrectlyForMultipleQuantities
- **Location**: `ProductDetailExtendedTest.java:82, 90`
- **Issues**:
  - Expected quantity: `5` but was: `1`
  - Expected total: `4499.95` but was: `899.99` (difference: 3599.96)
- **Description**: Quantity selector not working - cannot set quantity to 5

#### 2.2 shouldUpdateTotalPriceWhenQuantityChanges
- **Location**: `ProductDetailExtendedTest.java:47`
- **Issue**: Expected quantity: `2` but was: `1`
- **Description**: Quantity increment button not functional

### 3. CartPersistenceTest (1 failure)

#### 3.1 shouldPreserveCartItemsAfterPageRefresh
- **Location**: `CartPersistenceTest.java:88`
- **Issue**: Cart empty after refresh, expected to contain "Casio CZ-101 Vintage Synthesizer"
- **Description**: Cart items not persisting after page refresh (localStorage/sessionStorage issue?)

### 4. CartWorkflowTest (1 error)

#### 4.1 setupWithLogin -> performLogin
- **Location**: `CartWorkflowTest.java:65`
- **Issue**: Login verification timeout - still on login page
- **Description**: Login functionality broken or test credentials not working

### 5. AuthenticationRedirectTest (1 failure, 1 error)

#### 5.1 shouldPreserveRedirectParameterWhenNavigatingToLoginFromCart
- **Location**: `AuthenticationRedirectTest.java:65`
- **Issue**: Expected URL to contain `redirect=/cart` but was `https://music-tech-shop.vercel.app/cart`
- **Description**: Redirect parameter not preserved when navigating to login from cart

#### 5.2 shouldNotRedirectToLoginWhenUserIsAlreadyAuthenticated
- **Location**: `AuthenticationRedirectTest.java:82`
- **Error**: Timeout - Expected condition failed: waiting for visibility of login email input
- **Description**: Authentication state not being maintained properly

### 6. SearchExtendedTest (1 failure)

#### 6.1 shouldTrimLeadingAndTrailingWhitespace
- **Location**: `SearchExtendedTest.java:109`
- **Issue**: Expected `true` but was `false`
- **Description**: Search not trimming whitespace properly

## Site Analysis Summary

### Pages Explored
1. **Home Page** (`/`)
   - Featured products with "Add to Cart" and "Details" buttons
   - Categories: Electronics, Photography, Accessories, Synthesizers, Studio Recording
   - Navigation to products and login pages

2. **Products Page** (`/products`)
   - Product grid with pagination (16 of 50 products, page 1 of 4)
   - Sorting options (All, Name A-Z)
   - Category filtering via query parameters
   - Badges: Best Seller, Limited Stock, New Arrival, Free Shipping

3. **Login Page** (`/login`)
   - Email and password form
   - Test credentials available:
     - Admin: `admin@test.com` / `admin123`
     - Customer: `user@test.com` / `user123`
   - "Continue as Guest" option
   - "Use This Account" buttons for quick testing

4. **Cart Page** (`/cart`)
   - Header with search, theme toggle, login
   - Footer with company info and links

## Recommendations

### High Priority Issues
1. **Login functionality**: Multiple tests failing due to login not working
2. **Quantity selector**: Critical for e-commerce, completely non-functional
3. **Cart persistence**: Items not persisting after refresh (data layer issue)
4. **URL resilience**: Edge cases not handled properly (404s, malformed URLs, invalid IDs)

### Medium Priority Issues
1. **Authentication redirect flow**: Redirect parameters not preserved
2. **Search whitespace handling**: Basic input sanitization missing

### Environment Issues
- **CDP Warning**: Chrome DevTools Protocol version mismatch (version 144 not supported by Selenium 4.27)
  - Recommendation: Add dependency `selenium-devtools-v144` or downgrade Chrome

## Next Steps
1. Review the complete test execution log: `test-execution-log.txt`
2. Investigate login functionality first (affects multiple tests)
3. Fix quantity selector in product detail page
4. Review cart persistence implementation
5. Handle edge cases for invalid URLs/product IDs
