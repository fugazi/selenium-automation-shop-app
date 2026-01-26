package org.fugazi.tests;

import io.qameta.allure.*;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Test class for Authentication Redirect functionality.
 * Tests access control and redirect behavior for protected routes.
 */
@Epic("Music Tech Shop E2E Tests")
@Feature("Authentication")
@DisplayName("Authentication Redirect Tests")
class AuthenticationRedirectTest extends BaseTest {

    @Test
    @Tag("smoke")
    @Tag("regression")
    @Story("Access Control")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should redirect to login when unauthenticated user accesses cart")
    void shouldRedirectToLoginWhenUnauthenticatedUserAccessesCart() {
        // Act - Navigate directly to cart page without authentication
        navigateTo("/cart");

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();
            
            softly.assertThat(currentUrl)
                    .as("URL should contain /login")
                    .contains("/login");

            softly.assertThat(currentUrl)
                    .as("URL should contain redirect=/cart parameter")
                    .contains("redirect=/cart");

            softly.assertThat(loginPage().isPageLoaded())
                    .as("Should be redirected to login page")
                    .isTrue();
        });

        log.info("Unauthenticated cart access redirected to login: {}", getCurrentUrl());
    }

    @Test
    @Tag("regression")
    @Story("Access Control")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Should preserve redirect parameter when navigating to login from cart")
    void shouldPreserveRedirectParameterWhenNavigatingToLoginFromCart() {
        // Arrange - Navigate to cart (triggers redirect)
        navigateTo("/cart");

        // Act - Get URL and verify redirect parameter
        var currentUrl = getCurrentUrl();

        // Assert
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(currentUrl)
                    .as("URL should contain redirect parameter")
                    .contains("redirect=/cart");

            softly.assertThat(loginPage().isPageLoaded())
                    .as("Login page should be loaded with redirect")
                    .isTrue();
        });

        log.info("Redirect parameter preserved: {}", currentUrl);
    }

    @Test
    @Tag("regression")
    @Story("Access Control")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Should not redirect to login when user is already authenticated")
    void shouldNotRedirectToLoginWhenUserIsAlreadyAuthenticated() {
        // Arrange - Login first
        loginPage().loginWithCustomerAccount();
        
        // Act - Navigate to cart
        navigateTo("/cart");

        // Assert - Should stay on cart page, not redirect to log in
        SoftAssertions.assertSoftly(softly -> {
            var currentUrl = getCurrentUrl();
            
            softly.assertThat(currentUrl)
                    .as("URL should contain /cart")
                    .contains("/cart");

            softly.assertThat(currentUrl)
                    .as("URL should NOT contain /login")
                    .doesNotContain("/login");

            softly.assertThat(cartPage().isPageLoaded())
                    .as("Cart page should be accessible")
                    .isTrue();
        });

        log.info("Authenticated user accessed cart directly: {}", getCurrentUrl());
    }
}
