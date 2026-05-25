package com.mybharat.pages.youth;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;

/**
 * LogoutPage - Handles user logout across all portal contexts.
 * 
 * Supports:
 *   1. Youth React profile — rounded-full button → menuitem "Logout"
 *   2. ELP/Org admin portal — avatar/name click → dropdown with "Logout" text
 *   3. Homepage — "Welcome [name]" circle → dropdown with "Log Out"
 * 
 * Can be reused after any flow that requires logging out.
 */
public class LogoutPage extends BasePage {

    private static final Logger log = LogManager.getLogger(LogoutPage.class);
    private WebDriverWait longWait;

    public LogoutPage(WebDriver driver) {
        super(driver);
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Perform logout: open user menu → click logout → wait for page load.
     */
    public void logout() throws InterruptedException {
        log.info("Performing logout...");
        Thread.sleep(2000);

        openUserMenu();
        Thread.sleep(1000);
        clickLogout();

        waitForPageLoad();
        Thread.sleep(3000);
        log.info("✅ User logged out successfully");
    }

    /**
     * Open the user menu/avatar dropdown — tries multiple strategies.
     */
    private void openUserMenu() throws InterruptedException {
        // Strategy 1: ELP/Org admin portal — avatar with "Welcome" or user name
        try {
            WebElement avatar = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(
                            "//img[contains(@class,'avatar') or contains(@class,'profile')] | " +
                            "//*[contains(@class,'user-avatar')] | " +
                            "//span[contains(@class,'welcome')] | " +
                            "//*[contains(text(),'Welcome')]/ancestor::*[self::a or self::button or self::div][contains(@class,'cursor') or contains(@class,'click') or @role] | " +
                            "//nav//*[contains(@class,'rounded-circle') or contains(@class,'avatar')]")));
            scrollToElement(avatar);
            Thread.sleep(300);
            try { avatar.click(); } catch (Exception e) { jsClick(avatar); }
            log.info("Opened user menu (avatar/welcome)");
            return;
        } catch (Exception e) {
            log.info("Avatar/welcome not found, trying other strategies...");
        }

        // Strategy 2: Youth React profile — rounded-full button
        try {
            WebElement menu = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(
                            "//button[contains(@class,'rounded-full')] | " +
                            "//button[contains(@class,'flex') and contains(@class,'items-center') and contains(@class,'rounded')]")));
            scrollToElement(menu);
            Thread.sleep(300);
            try { menu.click(); } catch (Exception e) { jsClick(menu); }
            log.info("Opened user menu (rounded-full button)");
            return;
        } catch (Exception e) {
            log.info("Rounded-full button not found, trying fallback...");
        }

        // Strategy 3: Any clickable element in top-right nav that looks like a user menu
        try {
            WebElement menu = longWait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//nav//button[last()] | " +
                    "//header//*[contains(@class,'user') or contains(@class,'profile') or contains(@class,'avatar')]")));
            try { menu.click(); } catch (Exception e) { jsClick(menu); }
            log.info("Opened user menu (fallback)");
        } catch (Exception e) {
            log.error("Could not find user menu to open");
            throw new RuntimeException("User menu not found for logout", e);
        }
    }

    /**
     * Click the logout option from the dropdown — tries multiple locators.
     */
    private void clickLogout() throws InterruptedException {
        Thread.sleep(500);

        String[] logoutXpaths = {
            "//*[normalize-space()='Logout']",
            "//*[normalize-space()='Log Out']",
            "//*[normalize-space()='Log out']",
            "//a[contains(text(),'Logout')]",
            "//button[contains(text(),'Logout')]",
            "//button[@role='menuitem']",
            "//*[contains(@class,'logout')]"
        };

        for (String xpath : logoutXpaths) {
            try {
                WebElement logoutBtn = new WebDriverWait(driver, Duration.ofSeconds(3)).until(
                        ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                try { logoutBtn.click(); } catch (Exception e) { jsClick(logoutBtn); }
                log.info("Clicked logout: {}", xpath);
                return;
            } catch (Exception e) {
                // Try next locator
            }
        }

        log.error("Could not find logout button in dropdown");
        throw new RuntimeException("Logout button not found");
    }
}
