package org.fugazi.pages;

import java.time.Duration;

import io.qameta.allure.Step;

import org.fugazi.data.models.Credentials;
import org.openqa.selenium.By;
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
    private static final By ADMIN_ACCOUNT_BUTTON = By.cssSelector("[data-testid='admin-account-button']");
    private static final By CUSTOMER_ACCOUNT_BUTTON = By.cssSelector("[data-testid='customer-account-button']");
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
     * Login using admin quick login button (uses pre-configured admin credentials).
     * This clicks on "Use This Account" button next to the admin credentials display.
     * Note: In this demo app, this fills form and submits but may not actually authenticate.
     */
    @Step("Login using admin quick button")
    public void loginWithAdminAccount() {
        log.info("Logging in using admin quick button");
        try {
            if (isElementPresent(ADMIN_ACCOUNT_BUTTON)) {
                click(ADMIN_ACCOUNT_BUTTON);
                waitForPageLoad();
            } else {
                log.info("Admin quick button not found, using manual login with admin credentials");
                manualLogin(Credentials.ADMIN_CREDENTIALS.email(), Credentials.ADMIN_CREDENTIALS.password());
            }
        } catch (Exception e) {
            log.warn("Admin quick login failed, trying manual login: {}", e.getMessage());
            manualLogin(Credentials.ADMIN_CREDENTIALS.email(), Credentials.ADMIN_CREDENTIALS.password());
        }
    }

    /**
     * Login using customer quick login button (uses pre-configured customer credentials).
     * This clicks on "Use This Account" button next to the customer credentials display.
     */
    @Step("Login using customer quick button")
    public void loginWithCustomerAccount() {
        log.info("Logging in using customer quick button");
        try {
            if (isElementPresent(CUSTOMER_ACCOUNT_BUTTON)) {
                click(CUSTOMER_ACCOUNT_BUTTON);
                waitForPageLoad();
            } else {
                log.info("Customer quick button not found, using manual login with customer credentials");
                manualLogin(Credentials.CUSTOMER_CREDENTIALS.email(), Credentials.CUSTOMER_CREDENTIALS.password());
            }
        } catch (Exception e) {
            log.warn("Customer quick login failed, trying manual login: {}", e.getMessage());
            manualLogin(Credentials.CUSTOMER_CREDENTIALS.email(), Credentials.CUSTOMER_CREDENTIALS.password());
        }
    }

    /**
     * Manual login by filling email and password fields.
     * Used as fallback when quick login buttons are not available.
     * Enhanced with explicit waits to ensure authentication completes successfully.
     *
     * @param email    user email
     * @param password user password
     */
    @Step("Manual login with email: {email}")
    private void manualLogin(String email, String password) {
        log.info("Performing manual login with email: {}", email);

        var wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            log.debug("Waiting for login form to be visible");
            wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
            wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD_INPUT));

            log.debug("Clearing any existing values in email field");
            driver.findElement(EMAIL_INPUT).clear();

            log.debug("Clearing any existing values in password field");
            driver.findElement(PASSWORD_INPUT).clear();

            log.debug("Typing email: {}", email);
            driver.findElement(EMAIL_INPUT).sendKeys(email);

            log.debug("Typing password");
            driver.findElement(PASSWORD_INPUT).sendKeys(password);

            log.debug("Waiting for submit button to be clickable");
            wait.until(ExpectedConditions.elementToBeClickable(SUBMIT_BUTTON));

            log.debug("Clicking submit button");
            driver.findElement(SUBMIT_BUTTON).click();

            log.debug("Waiting for page load after login");
            waitForPageLoad();

            log.debug("Waiting for URL to change (no longer on login page)");
            wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("/login")));

            log.debug("Verifying login was successful - URL should not contain /login");
            var currentUrl = driver.getCurrentUrl();
            log.info("Current URL after login attempt: {}", currentUrl);

            assert currentUrl != null;
            if (currentUrl.contains("/login")) {
                log.warn("Login may have failed - still on login page");
            }

        } catch (Exception e) {
            log.error("Manual login failed: {}", e.getMessage());
            throw e;
        }
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
}
