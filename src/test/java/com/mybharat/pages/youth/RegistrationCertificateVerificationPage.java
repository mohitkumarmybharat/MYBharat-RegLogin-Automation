package com.mybharat.pages.youth;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;
import com.mybharat.utils.ConfigReader;

/**
 * RegistrationCertificateVerificationPage - Handles certificate download on the
 * NEW React-based Youth profile page.
 * 
 * The certificates are in the "My Certifications" section on the About tab.
 * 
 * Flow:
 *   1. Navigate to profile page (if not already there)
 *   2. Scroll down to "My Certifications" section
 *   3. Click the certificate card to open modal
 *   4. Click "Download PNG" button in the modal
 *   5. Wait for download to complete
 *   6. Close the modal
 * 
 * React DOM structure:
 *   - Certificate card: <div class="border border-gray-300 rounded-xl shadow ... cursor-pointer">
 *   - Modal: <div class="fixed inset-0 z-999 bg-black/50 ...">
 *   - Close button: <button><IoIosCloseCircle size={36}/></button>
 *   - Download PNG: <button class="... bg-[#bc4717] ...">Download PNG</button>
 *   - Download PDF: <button class="... bg-[#bc4717] ...">Download PDF</button>
 */
public class RegistrationCertificateVerificationPage extends BasePage {

    private static final Logger log = LogManager.getLogger(RegistrationCertificateVerificationPage.class);
    private final ConfigReader config = new ConfigReader();

    // Locators for the new React UI
    private static final By CERTIFICATIONS_SECTION = By.xpath(
            "//*[contains(text(),'My Certifications') or contains(text(),'Certifications')]");
    private static final By CERTIFICATE_CARD = By.xpath(
            "//div[contains(@class,'cursor-pointer') and contains(@class,'rounded-xl') and contains(@class,'shadow')]");
    private static final By DOWNLOAD_PNG_BTN = By.xpath(
            "//button[contains(text(),'Download PNG')]");
    private static final By DOWNLOAD_PDF_BTN = By.xpath(
            "//button[contains(text(),'Download PDF')]");
    private static final By MODAL_CLOSE_BTN = By.xpath(
            "//div[contains(@class,'fixed')]//button[contains(@class,'cursor-pointer')]");
    private static final By MODAL_OVERLAY = By.xpath(
            "//div[contains(@class,'fixed') and contains(@class,'inset-0')]");

    public RegistrationCertificateVerificationPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Download the registration certificate as PNG from the React profile page.
     * 
     * @return true if download was triggered successfully
     */
    public boolean downloadCertificate() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Ensure we're on the profile page
        String profileUrl = config.getProperty("profileUrl");
        if (profileUrl == null || profileUrl.isEmpty()) {
            profileUrl = config.getUrl() + "/youth-profile";
        }

        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.contains("youth-profile")) {
            log.info("Navigating to profile page for certificate download...");
            driver.get(profileUrl);
            waitForPageLoad();
            Thread.sleep(3000);
        }

        // Scroll down to My Certifications section
        log.info("Scrolling to My Certifications section...");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(1000);

        // Find and scroll to the certifications section
        try {
            WebElement certSection = wait.until(ExpectedConditions.presenceOfElementLocated(CERTIFICATIONS_SECTION));
            scrollToElement(certSection);
            Thread.sleep(500);
        } catch (Exception e) {
            log.warn("Certifications section header not found, scrolling more...");
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 500);");
            Thread.sleep(500);
        }

        // Click the certificate card to open the modal
        try {
            WebElement certCard = wait.until(ExpectedConditions.elementToBeClickable(CERTIFICATE_CARD));
            scrollToElement(certCard);
            Thread.sleep(300);
            certCard.click();
            log.info("Clicked certificate card");
        } catch (Exception e) {
            log.error("Certificate card not found: {}", e.getMessage());
            return false;
        }

        // Wait for modal to appear
        Thread.sleep(1000);

        // Click "Download PNG" button
        try {
            WebElement downloadBtn = wait.until(ExpectedConditions.elementToBeClickable(DOWNLOAD_PNG_BTN));
            scrollToElement(downloadBtn);
            Thread.sleep(300);
            downloadBtn.click();
            log.info("Clicked 'Download PNG' button");
        } catch (Exception e) {
            log.error("Download PNG button not found: {}", e.getMessage());
            // Try closing modal before returning
            closeModal();
            return false;
        }

        // Wait for download to process (html-to-image conversion + file save)
        Thread.sleep(3000);

        // Close the modal
        closeModal();

        log.info("✅ Certificate download triggered successfully");
        return true;
    }

    /**
     * Close the certificate modal.
     */
    private void closeModal() {
        try {
            // Try clicking the X close button
            WebElement closeBtn = driver.findElement(MODAL_CLOSE_BTN);
            closeBtn.click();
            Thread.sleep(500);
            log.info("Modal closed");
        } catch (Exception e) {
            // Fallback: click the overlay backdrop to close
            try {
                WebElement overlay = driver.findElement(MODAL_OVERLAY);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].click();", overlay);
                Thread.sleep(500);
            } catch (Exception e2) {
                // Press Escape as last resort
                try {
                    driver.findElement(By.tagName("body")).sendKeys(org.openqa.selenium.Keys.ESCAPE);
                } catch (Exception e3) {
                    log.warn("Could not close modal");
                }
            }
        }
    }
}
