package com.mybharat.tests.youth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.Retry;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.LandingPage;
import com.mybharat.pages.youth.LoginPage;
import com.mybharat.pages.youth.LogoutPage;
import com.mybharat.pages.youth.RegistrationPage;

/**
 * RegistrationLogoutLoginTest - End-to-end test for the complete youth flow:
 *   Register → Logout → Login (with OTP verification via Yopmail)
 *
 * Purpose: Validates the full user lifecycle in a single continuous browser session.
 *   1. Registers a new Indian youth user with a randomly generated @yopmail.com email
 *   2. Logs out the newly registered user (clears session)
 *   3. Logs back in using the same email (OTP fetched from Yopmail browser tab)
 *
 * Prerequisites:
 *   - Yopmail.com must be accessible (for OTP retrieval via browser)
 *   - resources/ folder must be writable (Excel persistence)
 *
 * Run:
 *   mvn test -Denv=prod -Dbrowser=chrome -Dsurefire.suiteXmlFiles=testSuites/testng-reg-logout-login.xml
 *
 * Dependencies: BaseTest, LandingPage, RegistrationPage, LogoutPage, LoginPage, TestListeners
 * Developer: QA Team
 *
 * @see RegistrationPage
 * @see LogoutPage
 * @see LoginPage
 */
@Listeners(TestListeners.class)
public class RegistrationLogoutLoginTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(RegistrationLogoutLoginTest.class);

    private LandingPage landingPage;
    private RegistrationPage registrationPage;
    private LogoutPage logoutPage;
    private LoginPage loginPage;

    /** Email used during registration — shared across test methods */
    private String registeredEmail;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        landingPage = new LandingPage(driver);
        registrationPage = new RegistrationPage(driver);
        logoutPage = new LogoutPage(driver);
        loginPage = new LoginPage(driver);
    }

    // =========================================================================
    // Step 1: Registration
    // =========================================================================

    @Test(priority = 1, groups = {"smoke", "registration"},
          retryAnalyzer = Retry.class,
          description = "Register a new Indian youth: Open app → Enter email → Verify OTP → Fill form → Submit → Save email")
    public void registerNewYouth() throws Exception {
        log.info("=== Step 1: Starting Youth Registration ===");

        // Open application
        openApp();
        log.info("Opened application URL");

        // Close popup if present
        landingPage.closePopupIfPresent();
        log.info("Popup handled");

        // Click Register for Indian
        landingPage.clickRegisterForIndian();
        log.info("Clicked Register for Indian");

        // Enter email and request OTP
        registrationPage.enterEmailAndRequestOTP();
        log.info("Email entered and OTP requested");

        // Fetch OTP from Yopmail and verify
        registrationPage.fetchAndVerifyOTP();
        log.info("OTP fetched from Yopmail and verified");

        // Fill the registration form
        registrationPage.fillRegistrationForm();
        log.info("Registration form filled");

        // Submit the form
        registrationPage.submitForm();
        log.info("Form submitted");

        // Handle post-submission popup
        registrationPage.clickSubmitPopup();
        log.info("Submit popup handled");

        // Save email to Excel for subsequent login
        registeredEmail = registrationPage.getEmail();
        registrationPage.saveEmailToExcel();

        log.info("=== ✅ Registration PASSED | Email: {} ===", registeredEmail);
    }

    // =========================================================================
    // Step 2: Logout
    // =========================================================================

    @Test(priority = 2, groups = {"smoke", "logout"},
          dependsOnMethods = "registerNewYouth",
          retryAnalyzer = Retry.class,
          description = "Logout the newly registered user: Clear session → Navigate to home page")
    public void logoutAfterRegistration() throws Exception {
        log.info("=== Step 2: Starting Logout ===");

        // Perform logout (clears cookies/storage, navigates to home)
        logoutPage.logout();

        log.info("=== ✅ Logout PASSED — session cleared, redirected to home ===");
    }

    // =========================================================================
    // Step 3: Login
    // =========================================================================

    @Test(priority = 3, groups = {"smoke", "login"},
          dependsOnMethods = "logoutAfterRegistration",
          retryAnalyzer = Retry.class,
          description = "Login with OTP: Sign In → Enter email from Excel → Send OTP → Verify OTP → Assert login success")
    public void loginAfterLogout() throws Exception {
        log.info("=== Step 3: Starting Login ===");

        // Navigate to home page
        loginPage.navigateToHomePage();
        log.info("Navigated to home page");

        // Close popup if present
        loginPage.closePopupIfPresent();
        log.info("Popup handled");

        // Click Sign In
        loginPage.clickSignIn();
        log.info("Clicked Sign In");

        // Enter email (reads last email from Excel — the one we just registered)
        loginPage.enterEmailForOTPLogin();
        log.info("Email entered from Excel: {}", loginPage.getLastRegisteredEmail());

        // Check consent checkbox
        loginPage.clickConsentCheckbox();
        log.info("Consent checkbox checked");

        // Click Login to send OTP
        loginPage.clickLoginToSendOTP();
        log.info("Login button clicked — OTP sent");

        // Fetch OTP from Yopmail and enter it
        loginPage.fetchOTPFromYopmail();
        log.info("OTP fetched from Yopmail and entered");

        // Verify OTP
        loginPage.clickVerifyOTP();
        log.info("OTP verified");

        // Assert login was successful
        boolean isLoggedIn = loginPage.isLoginSuccessful();
        Assert.assertTrue(isLoggedIn, "Login should be successful after OTP verification");

        log.info("=== ✅ Login PASSED — user is now logged in ===");
    }
}
