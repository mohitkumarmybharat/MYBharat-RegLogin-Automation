package com.mybharat.pages.org;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;
import com.mybharat.utils.ConfigReader;

/**
 * JoinYouthClubPage — Ionic Angular Join Organization automation for Youth Club.
 *
 * Flow: Navigate to profile → Click "Join Organization" → Select "Youth Club" type
 *       → Select Sub Category → Select Org Name → Select Designation
 *       → Select Hierarchy levels → Fill Address → Preview → Checkbox → Download → Submit
 *
 * Reuses the proven selectIonicDropdown pattern from the working JoinOrganizationPage.
 */
public class JoinYouthClubPage extends BasePage {

    private static final Logger log = LogManager.getLogger(JoinYouthClubPage.class);
    private final ConfigReader config = new ConfigReader();
    private final Random random = new Random();

    private String selectedDesignation;
    private String selectedLevel2;

    private static final int OVERLAY_WAIT = 10;

    // =========================================================================
    // STABLE LOCATORS
    // =========================================================================

    private static final By JOIN_ORG_BTN   = By.id("joinOrg");
    private static final By ION_LOADING    = By.cssSelector("ion-loading");

    // ion-select label-relative XPath
    private static final String ORG_TYPE_LABEL    = "Organization Type";
    private static final String SUBCATEGORY_LABEL = "Sub Category";
    private static final String DESIGNATION_LABEL = "Designation";
    private static final String LEVEL1_LABEL      = "LEVEL 1";
    private static final String LEVEL2_LABEL      = "LEVEL 2";
    private static final String LEVEL3_LABEL      = "LEVEL 3";

    private static final String ION_SELECT_BY_LABEL =
            "//ion-label[contains(.,'%s')]/following::ion-select[1]";

    // Organization Name — app-search-select (searchable dropdown)
    private static final By ORG_NAME_INPUT = By.cssSelector("app-search-select ion-input");

    // Address fields
    private static final String ADDRESS1_XPATH = "//ion-input[@formcontrolname='address1']//input";
    private static final String ADDRESS2_XPATH = "//ion-input[@formcontrolname='address2']//input";

    // Action buttons
    private static final String PREVIEW_BTN  = "//ion-button[contains(.,'Preview')]";
    private static final String CHECKBOX_XPATH = "//ion-checkbox";
    private static final String DOWNLOAD_BTN = "//ion-button[contains(.,'Download')]";

    public JoinYouthClubPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    public void clickJoinOrganization() {
        log.info("[NAV] Clicking Join Organization...");
        waitForPageLoad();
        dismissOverlay();

        // Navigate to the Ionic Angular public profile page
        String baseUrl = config.getUrl();
        String profileUrl = baseUrl.replace("mybharat.gov.in", "web.mybharat.gov.in") + "/reports/public_profile";
        log.info("[NAV] Navigating to: {}", profileUrl);
        driver.get(profileUrl);
        waitForPageLoad();
        waitForAngularStable();
        dismissOverlay();

        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(JOIN_ORG_BTN));
        scrollToElement(btn);
        waitForClickable(btn);
        safeClick(btn);
        waitForPageLoad();
        waitForAngularStable();

        wait.until(d -> d.findElements(By.xpath(
                String.format(ION_SELECT_BY_LABEL, ORG_TYPE_LABEL))).size() > 0);
        log.info("[NAV] ✅ Join Organization page loaded");
    }

    public boolean isPageLoaded() {
        try {
            return driver.findElements(By.xpath(
                    String.format(ION_SELECT_BY_LABEL, ORG_TYPE_LABEL))).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // STEP 1: Organization Type
    // =========================================================================

    public void selectOrganizationType(String value) {
        log.info("[STEP] Selecting Organization Type: '{}'", value);
        safeSleep(1000);
        selectIonicDropdown(ORG_TYPE_LABEL, value);
        safeSleep(2000);
        log.info("[STEP] ✅ Organization Type = {}", value);
    }

    // =========================================================================
    // STEP 2: Sub Category
    // =========================================================================

    public void selectSubcategory(String value) {
        log.info("[STEP] Selecting Sub Category: '{}'", value);
        waitForIonSelectReady(SUBCATEGORY_LABEL);
        selectIonicDropdown(SUBCATEGORY_LABEL, value);
        safeSleep(2000);
        log.info("[STEP] ✅ Sub Category = {}", value);
    }

    // =========================================================================
    // STEP 3: Organization Name (app-search-select — click to open, select from list)
    // =========================================================================

    public void selectOrganizationName(String value) {
        log.info("[STEP] Selecting Organization Name: '{}'", value);

        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                WebElement ionInput = wait.until(ExpectedConditions.elementToBeClickable(ORG_NAME_INPUT));
                scrollToElement(ionInput);
                ionInput.click();
                safeSleep(3000);

                WebElement option = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//ion-item[contains(.,'" + value + "')]")));
                log.info("[STEP]   Found: '{}'", option.getText().trim());
                option.click();
                safeSleep(1000);
                waitForAngularStable();
                log.info("[STEP] ✅ Organization Name = {}", value);
                return;

            } catch (Exception e) {
                log.warn("[STEP]   Attempt {} failed: {}", attempt, e.getMessage());
                dismissOverlay();
                if (attempt == 3) throw new RuntimeException("Failed to select Org Name: " + value, e);
                safeSleep(2000);
            }
        }
    }

    // =========================================================================
    // STEP 4: Designation (random from provided values)
    // =========================================================================

    public String selectDesignation(String[] validNames) {
        log.info("[STEP] Selecting Designation from: {}", java.util.Arrays.toString(validNames));
        waitForIonSelectReady(DESIGNATION_LABEL);

        selectedDesignation = validNames[random.nextInt(validNames.length)];
        log.info("[STEP]   Picking: {}", selectedDesignation);

        String xpath = String.format(ION_SELECT_BY_LABEL, DESIGNATION_LABEL);
        WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        scrollToElement(selectEl);
        safeClick(selectEl);
        safeSleep(1500);

        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(OVERLAY_WAIT)).until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//ion-item[contains(.,'" + selectedDesignation + "')]")));
        option.click();
        safeSleep(500);
        clickOverlayOkIfPresent();
        safeSleep(500);
        waitForAngularStable();

        log.info("[STEP] ✅ Designation = {}", selectedDesignation);
        return selectedDesignation;
    }

    // =========================================================================
    // STEP 5: Hierarchy — Youth Club has flat 4-level structure per document
    // President → General Secretary / Treasurer / Member (all report to President)
    // =========================================================================

    public void selectHierarchy() {
        log.info("[HIER] Handling hierarchy for designation: {}", selectedDesignation);

        // LEVEL 1 — always present
        waitForIonSelectReady(LEVEL1_LABEL);
        selectIonicDropdown(LEVEL1_LABEL, "ALL");
        log.info("[HIER] ✅ LEVEL 1 = ALL");

        // Check if LEVEL 2 exists (depends on designation)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                    d.findElements(By.xpath(String.format(ION_SELECT_BY_LABEL, LEVEL2_LABEL))).size() > 0);
            waitForIonSelectReady(LEVEL2_LABEL);
            selectedLevel2 = selectRandomIonicDropdown(LEVEL2_LABEL,
                    new String[]{"BACKEND DEVELOPER", "FRONTEND DEVELOPER", "ALL"});
            log.info("[HIER] ✅ LEVEL 2 = {}", selectedLevel2);

            // Check if LEVEL 3 exists
            try {
                new WebDriverWait(driver, Duration.ofSeconds(5)).until(d ->
                        d.findElements(By.xpath(String.format(ION_SELECT_BY_LABEL, LEVEL3_LABEL))).size() > 0);
                waitForIonSelectReady(LEVEL3_LABEL);
                String[] level3Options = "BACKEND DEVELOPER".equalsIgnoreCase(selectedLevel2)
                        ? new String[]{"JAVA", "PYTHON"}
                        : new String[]{"REACT", "REACT NATIVE", "ALL"};
                String selectedLevel3 = selectRandomIonicDropdown(LEVEL3_LABEL, level3Options);
                log.info("[HIER] ✅ LEVEL 3 = {}", selectedLevel3);
            } catch (Exception e) {
                log.info("[HIER] No LEVEL 3 — hierarchy complete");
            }
        } catch (Exception e) {
            log.info("[HIER] No LEVEL 2 — hierarchy complete at LEVEL 1");
        }
    }

    // =========================================================================
    // STEP 6: Address
    // =========================================================================

    public void enterAddress(String line1, String line2) {
        log.info("[STEP] Entering address...");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        safeSleep(2000);

        WebElement addr1 = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath(ADDRESS1_XPATH)));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center',behavior:'instant'});", addr1);
        safeSleep(500);
        ((JavascriptExecutor) driver).executeScript("arguments[0].focus(); arguments[0].click();", addr1);
        safeSleep(300);
        addr1.sendKeys(line1);
        log.info("[STEP]   Address Line 1 = {}", line1);
        safeSleep(500);

        // Address Line 2 — find next input after addr1
        WebElement addr2 = (WebElement) ((JavascriptExecutor) driver).executeScript(
                "var inputs = document.querySelectorAll('ion-input input[type=\"text\"], ion-input input:not([type])');" +
                "var addr1 = arguments[0]; var found = false;" +
                "for(var i=0; i<inputs.length; i++) {" +
                "  if(inputs[i] === addr1) { found = true; continue; }" +
                "  if(found && inputs[i].offsetParent !== null) return inputs[i];" +
                "}" +
                "return null;", addr1);

        if (addr2 != null) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center',behavior:'instant'});", addr2);
            safeSleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].focus(); arguments[0].click();", addr2);
            safeSleep(300);
            addr2.sendKeys(line2);
            log.info("[STEP]   Address Line 2 = {}", line2);
        }
        safeSleep(500);
        log.info("[STEP] ✅ Address entered");
    }

    // =========================================================================
    // STEP 7: Preview
    // =========================================================================

    public void clickPreview() {
        log.info("[STEP] Clicking Preview...");
        safeSleep(1000);
        scrollPage(300);
        safeSleep(1000);
        WebElement btn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(PREVIEW_BTN)));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center',behavior:'instant'});", btn);
        safeSleep(500);
        wait.until(ExpectedConditions.elementToBeClickable(btn));
        btn.click();
        safeSleep(2000);
        waitForPageLoad();
        waitForAngularStable();
        log.info("[STEP] ✅ Preview clicked");
    }

    public boolean isPreviewLoaded() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d ->
                    d.findElements(By.xpath(CHECKBOX_XPATH)).size() > 0
                    || d.findElements(By.xpath(DOWNLOAD_BTN)).size() > 0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // =========================================================================
    // STEP 8: Checkbox + Download + Submit
    // =========================================================================

    public void clickCheckbox() {
        log.info("[STEP] Clicking declaration checkbox...");
        WebElement cb = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(CHECKBOX_XPATH)));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center',behavior:'instant'});", cb);
        safeSleep(500);
        safeClick(cb);
        safeSleep(1000);
        log.info("[STEP] ✅ Checkbox clicked");
    }

    public boolean clickDownload() {
        log.info("[STEP] Clicking Download...");
        try {
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(DOWNLOAD_BTN)));
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block:'center',behavior:'instant'});", btn);
            safeSleep(500);
            safeClick(btn);
            safeSleep(3000);
            log.info("[STEP] ✅ Download clicked");
            return true;
        } catch (Exception e) {
            log.warn("[STEP]   Download button not found — skipping");
            return false;
        }
    }

    public void clickSubmit() {
        log.info("[STEP] Clicking SUBMIT...");
        safeSleep(1000);
        Boolean clicked = (Boolean) ((JavascriptExecutor) driver).executeScript(
                "var btns = document.querySelectorAll('ion-button, button');" +
                "for(var i=0; i<btns.length; i++) {" +
                "  var t = (btns[i].textContent || btns[i].innerText || '').trim().toLowerCase();" +
                "  if(t.indexOf('submit') >= 0) {" +
                "    btns[i].scrollIntoView({block:'center'});" +
                "    btns[i].click();" +
                "    return true;" +
                "  }" +
                "}" +
                "return false;");
        if (Boolean.TRUE.equals(clicked)) {
            safeSleep(3000);
            waitForPageLoad();
            waitForAngularStable();
            log.info("[STEP] ✅ SUBMIT clicked");
        } else {
            throw new RuntimeException("SUBMIT button not found");
        }
    }

    public boolean isSubmissionSuccessful() {
        try {
            return new WebDriverWait(driver, Duration.ofSeconds(15)).until(d -> {
                String src = d.getPageSource().toLowerCase();
                return src.contains("success") || src.contains("submitted")
                        || src.contains("congratulation") || src.contains("approval");
            });
        } catch (Exception e) {
            return false;
        }
    }

    public void clickGoToProfile() {
        log.info("[STEP] Clicking 'GO TO MY BHARAT PROFILE'...");
        safeSleep(5000);
        WebElement ionButton = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//ion-button[contains(., 'GO TO MY BHARAT PROFILE')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ionButton);
        safeSleep(3000);
        waitForPageLoad();
        waitForAngularStable();
        log.info("[STEP] ✅ Navigated to My Bharat Profile");
    }

    public String getSelectedDesignation() { return selectedDesignation; }

    // =========================================================================
    // CORE HELPERS — proven Ionic overlay pattern
    // =========================================================================

    private void selectIonicDropdown(String labelName, String optionText) {
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("[DROP] selectIonicDropdown('{}', '{}') attempt {}", labelName, optionText, attempt);
                String xpath = String.format(ION_SELECT_BY_LABEL, labelName);
                WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
                scrollToElement(selectEl);
                safeClick(selectEl);
                safeSleep(1000);

                WebElement option = new WebDriverWait(driver, Duration.ofSeconds(OVERLAY_WAIT)).until(
                        ExpectedConditions.elementToBeClickable(
                                By.xpath("//ion-item[contains(.,'" + optionText + "')]")));
                log.info("[DROP]   Found option: '{}'", option.getText().trim());
                option.click();
                safeSleep(500);
                clickOverlayOkIfPresent();
                safeSleep(500);
                waitForAngularStable();
                return;

            } catch (StaleElementReferenceException e) {
                log.warn("[DROP]   StaleElement attempt {}/{}", attempt, maxRetries);
                if (attempt == maxRetries) throw e;
                safeSleep(1000);
            } catch (TimeoutException e) {
                log.warn("[DROP]   Timeout attempt {}/{}", attempt, maxRetries);
                pressEscape();
                if (attempt == maxRetries) throw e;
                safeSleep(1000);
            }
        }
    }

    private String selectRandomIonicDropdown(String labelName, String[] allowedValues) {
        String chosen = allowedValues[random.nextInt(allowedValues.length)];
        log.info("[DROP] selectRandomIonicDropdown('{}', picking '{}')", labelName, chosen);

        String xpath = String.format(ION_SELECT_BY_LABEL, labelName);
        WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        scrollToElement(selectEl);
        safeClick(selectEl);
        safeSleep(1500);

        WebElement option = new WebDriverWait(driver, Duration.ofSeconds(OVERLAY_WAIT)).until(
                ExpectedConditions.elementToBeClickable(
                        By.xpath("//ion-item[contains(.,'" + chosen + "')]")));
        option.click();
        safeSleep(500);
        clickOverlayOkIfPresent();
        safeSleep(500);
        waitForAngularStable();
        log.info("[DROP]   ✅ {} = {}", labelName, chosen);
        return chosen;
    }

    private void clickOverlayOkIfPresent() {
        safeSleep(300);
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "var btns = document.querySelectorAll('.alert-button-group button, ion-alert .alert-button-group button');" +
                    "for(var i=0; i<btns.length; i++) {" +
                    "  var t = (btns[i].textContent || btns[i].innerText || '').trim();" +
                    "  if(t === 'OK' || t === 'Ok' || t === 'Done') {" +
                    "    btns[i].click(); return true;" +
                    "  }" +
                    "}" +
                    "return false;");
        } catch (Exception e) { /* no OK button */ }
    }

    private void waitForIonSelectReady(String labelName) {
        String xpath = String.format(ION_SELECT_BY_LABEL, labelName);
        log.info("[SYNC] Waiting for ion-select '{}' to be ready...", labelName);
        new WebDriverWait(driver, Duration.ofSeconds(25)).until(d -> {
            try {
                List<WebElement> elements = d.findElements(By.xpath(xpath));
                if (elements.isEmpty()) return false;
                WebElement el = elements.get(0);
                if (!el.isDisplayed()) return false;
                String disabled = el.getAttribute("disabled");
                if ("true".equals(disabled) || "".equals(disabled)) return false;
                String ariaDisabled = el.getAttribute("aria-disabled");
                if ("true".equals(ariaDisabled)) return false;
                String cssClass = el.getAttribute("class");
                if (cssClass != null && cssClass.contains("select-disabled")) return false;
                return true;
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
        safeSleep(500);
        log.info("[SYNC] ✅ ion-select '{}' is ready", labelName);
    }

    private void waitForAngularStable() {
        waitForPageLoad();
        try {
            new WebDriverWait(driver, Duration.ofSeconds(OVERLAY_WAIT)).until(
                    ExpectedConditions.invisibilityOfElementLocated(ION_LOADING));
        } catch (Exception e) { /* no loader */ }
        safeSleep(500);
    }

    private void dismissOverlay() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.id("overlay")));
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "var o=document.getElementById('overlay');if(o)o.style.display='none';" +
                    "var l=document.getElementById('loader2');if(l)l.style.display='none';");
        }
    }

    private void pressEscape() {
        try {
            driver.findElement(By.tagName("body")).sendKeys(Keys.ESCAPE);
        } catch (Exception e) { /* ignore */ }
    }

    private void safeSleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
