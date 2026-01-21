package org.fugazi.pages;

import java.time.Duration;

import io.qameta.allure.Step;

import org.fugazi.data.models.Credentials;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object representing the Login Page.
 * Provides functionality for user authentication including login with credentials,
 * quick login buttons for test accounts, and guest checkout.
 */
public class LoginPage extends BasePage {

    // Locators based on actual page structure
    private static final By PAGE_TITLE = By.cssSelector("h1.text-4xl");
    private static final By LOGIN_FORM = By.cssSelector("[data-testid='login-form']");
    private static final By EMAIL_INPUT = By.cssSelector("[data-testid='login-email-input']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-testid='login-password-input']");
    private static final By SUBMIT_BUTTON = By.cssSelector("[data-testid='login-submit-button']");
    private static final By CONTINUE_AS_GUEST_LINK = By.cssSelector("[data-testid='continue-as-guest-link']");
    private static final By EMAIL_ERROR_MESSAGE = By.cssSelector("[data-testid='email-error-message']");
    private static final By PASSWORD_ERROR_MESSAGE = By.cssSelector("[data-testid='password-error-message']");

    // Test credentials display elements
    private static final By ADMIN_CREDENTIALS_DISPLAY = By.xpath("//p[contains(text(),'admin@test.com')]");
    private static final By CUSTOMER_CREDENTIALS_DISPLAY = By.xpath("//p[contains(text(),'user@test.com')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    @Step("Verify login page is loaded")
    public boolean isPageLoaded() {
        log.debug("Checking if login page is loaded");
        waitForPageLoad();

        // Check if URL contains /login
        var currentUrl = getCurrentUrl();
        if (currentUrl != null && currentUrl.contains("/login")) {
            // Also verify the form is visible
            return isDisplayed(LOGIN_FORM);
        }

        return false;
    }

    /**
     * Get the login page title.
     *
     * @return page title text
     */
    @Step("Get login page title")
    public String getPageTitleText() {
        return getText(PAGE_TITLE);
    }

    /**
     * Check if the login form is displayed.
     *
     * @return true if login form is visible
     */
    @Step("Check if login form is displayed")
    public boolean isLoginFormDisplayed() {
        return isDisplayed(LOGIN_FORM);
    }

    /**
     * Check if the email input field is displayed.
     *
     * @return true if email input is visible
     */
    @Step("Check if email input is displayed")
    public boolean isEmailInputDisplayed() {
        return isDisplayed(EMAIL_INPUT);
    }

    /**
     * Check if the password input field is displayed.
     *
     * @return true if password input is visible
     */
    @Step("Check if password input is displayed")
    public boolean isPasswordInputDisplayed() {
        return isDisplayed(PASSWORD_INPUT);
    }

    /**
     * Check if the sign in (submit) button is displayed.
     *
     * @return true if sign in button is visible
     */
    @Step("Check if sign in button is displayed")
    public boolean isSignInButtonDisplayed() {
        return isDisplayed(SUBMIT_BUTTON);
    }

    /**
     * Login with email and password credentials.
     * Waits for authentication to complete successfully by verifying URL change.
     *
     * @param email    user email
     * @param password user password
     */
    @Step("Login with email: {email}")
    public void login(String email, String password) {
        log.info("Logging in with email: {}", email);

        // Enter email
        type(EMAIL_INPUT, email);

        // Enter password
        type(PASSWORD_INPUT, password);

        // Click sign in button
        click(SUBMIT_BUTTON);

        waitForPageLoad();
        waitForSuccessfulLogin();
    }

    /**
     * Login using Credentials object.
     *
     * @param credentials Credentials object containing email and password
     */
    @Step("Login with credentials")
    public void login(Credentials credentials) {
        login(credentials.email(), credentials.password());
    }

    /**
     * Login using admin credentials from Credentials class.
     * Uses form-based login for maximum reliability.
     * Waits for authentication to complete successfully by verifying URL change.
     */
    @Step("Login with admin credentials")
    public void loginWithAdminAccount() {
        log.info("Logging in with admin credentials");
        login(Credentials.ADMIN_CREDENTIALS.email(), Credentials.ADMIN_CREDENTIALS.password());
    }

    /**
     * Login using customer credentials from Credentials class.
     * Uses form-based login for maximum reliability.
     * Waits for authentication to complete successfully by verifying URL change.
     */
    @Step("Login with customer credentials")
    public void loginWithCustomerAccount() {
        log.info("Logging in with customer credentials");
        login(Credentials.CUSTOMER_CREDENTIALS.email(), Credentials.CUSTOMER_CREDENTIALS.password());
    }


    /**
     * Continue as a guest without logging in.
     */
    @Step("Continue as guest")
    public void continueAsGuest() {
        log.info("Continuing as guest");
        click(CONTINUE_AS_GUEST_LINK);
        waitForPageLoad();
    }

    /**
     * Get the error message for email field (if any).
     *
     * @return email error message text
     */
    @Step("Get email error message")
    public String getEmailErrorMessage() {
        if (isDisplayed(EMAIL_ERROR_MESSAGE)) {
            return getText(EMAIL_ERROR_MESSAGE);
        }
        return "";
    }

    /**
     * Get the error message for password field (if any).
     *
     * @return password error message text
     */
    @Step("Get password error message")
    public String getPasswordErrorMessage() {
        if (isDisplayed(PASSWORD_ERROR_MESSAGE)) {
            return getText(PASSWORD_ERROR_MESSAGE);
        }
        return "";
    }

    /**
     * Get all error messages from the login form.
     *
     * @return combined error messages or empty string if no errors
     */
    @Step("Get all error messages")
    public String getErrorMessages() {
        var emailError = getEmailErrorMessage();
        var passwordError = getPasswordErrorMessage();

        if (emailError.isEmpty() && passwordError.isEmpty()) {
            return "";
        }

        return String.format("Email: %s | Password: %s", emailError, passwordError);
    }

    /**
     * Check if test credentials are displayed on the page.
     *
     * @return true if admin and customer credentials are visible
     */
    @Step("Check if test credentials are displayed")
    public boolean areTestCredentialsDisplayed() {
        return isDisplayed(ADMIN_CREDENTIALS_DISPLAY) && isDisplayed(CUSTOMER_CREDENTIALS_DISPLAY);
    }

    /**
     * Check if currently on login page.
     *
     * @return true if URL contains /login
     */
    @Step("Check if currently on login page")
    public boolean isOnLoginPage() {
        var currentUrl = getCurrentUrl();
        return currentUrl != null && currentUrl.contains("/login");
    }

    /**
     * Wait for successful login by verifying URL has changed from login page.
     * This ensures the authentication completed before the test continues.
     * Uses explicit wait to handle potential delays in authentication.
     */
    @Step("Wait for successful login")
    private void waitForSuccessfulLogin() {
        log.debug("Waiting for successful login - verifying URL change");
        var wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait for URL to no longer contain /login
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

            var currentUrl = driver.getCurrentUrl();
            log.info("Login successful - redirected to: {}", currentUrl);

            // Verify we're not still on login page
            if (currentUrl != null && currentUrl.contains("/login")) {
                log.error("Login verification failed - still on login page");
                throw new AssertionError("Login failed - URL still contains /login: " + currentUrl);
            }

        } catch (TimeoutException e) {
            var currentUrl = driver.getCurrentUrl();
            log.error("Login verification timeout - current URL: {}", currentUrl);
            throw new AssertionError("Login verification timeout - still on login page: " + currentUrl, e);
        }
    }
}
