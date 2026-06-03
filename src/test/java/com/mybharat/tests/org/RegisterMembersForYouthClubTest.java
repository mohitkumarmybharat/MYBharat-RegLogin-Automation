package com.mybharat.tests.org;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;
import com.mybharat.listeners.TestListeners;
import com.mybharat.utils.ConfigReader;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * RegisterMembersForYouthClubTest — Registers 6 fresh youth users for Youth Club membership.
 *
 * Strategy: 3 parallel browsers first, then 3 more parallel browsers.
 * Email: nnnyouth{N}@yopmail.com (N auto-increments from last used number in Excel)
 * OTP Fetch: Yopmail browser tab with synchronized lock (one at a time to avoid CAPTCHA)
 *
 * Saves registered emails to:
 *   - Youth_<env>.xlsx → "YouthClubMembers" sheet
 *
 * Run:
 *   mvn test "-Denv=prod" "-Dbrowser=chrome" "-Dsurefire.suiteXmlFiles=testSuites/testng-youthclub.xml"
 */
@Listeners(TestListeners.class)
public class RegisterMembersForYouthClubTest {

    private static final Logger log = LogManager.getLogger(RegisterMembersForYouthClubTest.class);
    private static final int MEMBER_COUNT = 6;
    private static final String EMAIL_DOMAIN = "@yopmail.com";

    /** Thread-safe list to collect registered emails from all parallel threads */
    private static final CopyOnWriteArrayList<String> registeredEmails = new CopyOnWriteArrayList<>();

    /** Starting number for nnnyouth emails (read from Excel in @BeforeClass) */
    private static int startNumber = 1;

    private final ConfigReader config = new ConfigReader();

    @BeforeClass(alwaysRun = true)
    public void setup() {
        WebDriverManager.chromedriver().setup();
        registeredEmails.clear();

        // Read last nnnyouth number from Youth_<env>.xlsx "YouthClubMembers" sheet
        startNumber = getLastNnnyouthNumber() + 1;
        log.info("═══ Registering {} fresh members for Youth Club ═══", MEMBER_COUNT);
        log.info("═══ Email: nnnyouth{}..nnnyouth{}{} ═══", startNumber, startNumber + MEMBER_COUNT - 1, EMAIL_DOMAIN);
    }

    /** Lock to serialize Yopmail access (avoids CAPTCHA from parallel hits) */
    private static final Object YOPMAIL_LOCK = new Object();

    // =========================================================================
    // BATCH 1: Register first 3 members in parallel (Yopmail serialized)
    // =========================================================================

    @Test(priority = 1, description = "Register member batch 1 (3 parallel, Yopmail serialized)")
    public void registerBatch1() throws Exception {
        log.info("═══ BATCH 1: Registering members {} to {} (3 parallel) ═══", startNumber, startNumber + 2);
        List<Thread> threads = new ArrayList<>();
        CopyOnWriteArrayList<String> batchErrors = new CopyOnWriteArrayList<>();

        for (int i = 0; i < 3; i++) {
            int memberNum = startNumber + i;
            String email = "nnnyouth" + memberNum + EMAIL_DOMAIN;
            Thread t = new Thread(() -> {
                try {
                    registerSingleMember(email, memberNum);
                    registeredEmails.add(email);
                    log.info("[Batch1] ✅ Registered: {}", email);
                } catch (Exception e) {
                    log.error("[Batch1] ❌ Failed: {} — {}", email, e.getMessage());
                    batchErrors.add(email + ": " + e.getMessage());
                }
            }, "Member-" + memberNum);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        log.info("═══ BATCH 1 COMPLETE: {}/3 registered ═══", 3 - batchErrors.size());
    }

    // =========================================================================
    // BATCH 2: Register next 3 members in parallel (Yopmail serialized)
    // =========================================================================

    @Test(priority = 2, dependsOnMethods = "registerBatch1",
          description = "Register member batch 2 (3 parallel, Yopmail serialized)")
    public void registerBatch2() throws Exception {
        log.info("═══ BATCH 2: Registering members {} to {} (3 parallel) ═══", startNumber + 3, startNumber + 5);
        List<Thread> threads = new ArrayList<>();
        CopyOnWriteArrayList<String> batchErrors = new CopyOnWriteArrayList<>();

        for (int i = 3; i < 6; i++) {
            int memberNum = startNumber + i;
            String email = "nnnyouth" + memberNum + EMAIL_DOMAIN;
            Thread t = new Thread(() -> {
                try {
                    registerSingleMember(email, memberNum);
                    registeredEmails.add(email);
                    log.info("[Batch2] ✅ Registered: {}", email);
                } catch (Exception e) {
                    log.error("[Batch2] ❌ Failed: {} — {}", email, e.getMessage());
                    batchErrors.add(email + ": " + e.getMessage());
                }
            }, "Member-" + memberNum);
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        log.info("═══ BATCH 2 COMPLETE: {}/3 registered ═══", 3 - batchErrors.size());
    }

    // =========================================================================
    // SAVE TO EXCEL
    // =========================================================================

    @AfterClass(alwaysRun = true)
    public void saveEmailsToExcel() {
        if (registeredEmails.isEmpty()) {
            log.warn("No members registered — skipping Excel save");
            return;
        }

        String env = config.getEnv();
        String filePath = System.getProperty("user.dir") + File.separator
                + "resources" + File.separator + "Youth_" + env + ".xlsx";

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        try {
            Workbook workbook;
            if (file.exists() && file.length() > 0) {
                FileInputStream fis = new FileInputStream(file);
                workbook = new XSSFWorkbook(fis);
                fis.close();
            } else {
                workbook = new XSSFWorkbook();
            }

            Sheet sheet = workbook.getSheet("YouthClubMembers");
            if (sheet == null) {
                sheet = workbook.createSheet("YouthClubMembers");
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Email");
            }

            int nextRow = sheet.getLastRowNum() + 1;
            for (String email : registeredEmails) {
                Row row = sheet.createRow(nextRow++);
                row.createCell(0).setCellValue(email);
            }

            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();
            workbook.close();

            log.info("═══ ✅ {} member emails saved to Youth_{}.xlsx (YouthClubMembers sheet) ═══",
                    registeredEmails.size(), env);
            registeredEmails.forEach(e -> log.info("   → {}", e));

        } catch (Exception e) {
            log.error("Failed to save member emails to Excel: {}", e.getMessage());
            throw new RuntimeException("Failed to save member emails", e);
        }
    }

    // =========================================================================
    // CORE REGISTRATION LOGIC
    // =========================================================================

    private void registerSingleMember(String email, int memberNum) throws Exception {
        Faker faker = new Faker();
        WebDriver driver = null;

        try {
            driver = createDriver();
            log.info("[Member {}] Starting registration: {}", memberNum, email);

            // Step 1: Navigate to app
            driver.get(config.getUrl());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(45));
            safeSleep(3000);

            // Close popup if present
            closePopup(driver);

            // Click Register Now → Register (Indian)
            WebElement registerNow = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[@class='fontchange']")));
            registerNow.click();
            safeSleep(500);

            WebElement registerBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@class='btn btn_login lang_yuva_register_as_youth_btn fontchange']")));
            registerBtn.click();
            safeSleep(1000);

            // Step 2: Enter email and request OTP
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//input[@id='user_mobile'])[1]")));
            emailInput.clear();
            emailInput.sendKeys(email);

            WebElement getOtpBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.generate_otp")));
            getOtpBtn.click();
            safeSleep(2000);
            log.info("[Member {}] OTP requested for: {}", memberNum, email);

            // Step 3: Fetch OTP from Yopmail (synchronized — one at a time to avoid CAPTCHA)
            String otp = fetchOTPFromYopmail(driver, email, memberNum);
            log.info("[Member {}] OTP fetched: {}", memberNum, otp);

            // Step 4: Enter OTP and verify
            WebElement otpField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("(//input[@id='otp-field-1'])[1]")));
            otpField.sendKeys(otp);

            WebElement verifyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@id='btn-verify-otp']")));
            verifyBtn.click();
            safeSleep(1000);

            // Step 5: Fill registration form
            fillRegistrationForm(driver, wait, faker);
            log.info("[Member {}] Form filled", memberNum);

            // Step 6: Submit
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[@id='registrationButton']")));
            scrollToElement(driver, submitBtn);
            submitBtn.click();
            safeSleep(5000);

            // Step 7: Handle submit popup
            handleSubmitPopup(driver);
            safeSleep(2000);

            log.info("[Member {}] ✅ Registration PASSED: {}", memberNum, email);

        } finally {
            if (driver != null) {
                try { driver.quit(); } catch (Exception e) { /* ignore */ }
            }
        }
    }

    // =========================================================================
    // YOPMAIL OTP FETCH — Synchronized (one at a time to avoid CAPTCHA)
    // =========================================================================

    /**
     * Fetch OTP from Yopmail using browser tab.
     * Synchronized so only one thread accesses Yopmail at a time (prevents CAPTCHA).
     * Page load + form fill happens in parallel, only Yopmail access is serialized.
     */
    private String fetchOTPFromYopmail(WebDriver driver, String email, int memberNum) throws InterruptedException {
        // Wait for OTP email to arrive before acquiring lock
        safeSleep(5000);

        synchronized (YOPMAIL_LOCK) {
            log.info("[Member {}] Yopmail lock acquired — fetching OTP for: {}", memberNum, email);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            String emailPrefix = email.split("@")[0];

            // Open Yopmail in new tab
            driver.switchTo().newWindow(WindowType.TAB);
            driver.get(config.getDummyEmailUrl());
            safeSleep(2000);

            // Dismiss cookie/ad popup if present
            try {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "var p=document.getElementById('r_parent');if(p)p.style.display='none';" +
                        "var c=document.querySelector('.r_popup');if(c)c.style.display='none';");
            } catch (Exception e) { /* no popup */ }
            safeSleep(500);

            // Enter email and go
            WebElement inbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login")));
            inbox.clear();
            inbox.sendKeys(emailPrefix);
            driver.findElement(By.cssSelector(".material-icons-outlined.f36")).click();
            safeSleep(3000);

            // Dismiss popup again after navigation
            try {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "var p=document.getElementById('r_parent');if(p)p.style.display='none';");
            } catch (Exception e) { /* ignore */ }
            safeSleep(500);

            // Click refresh with JS (avoids element intercept)
            try {
                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "document.getElementById('refresh').click();");
            } catch (Exception e) { /* ignore */ }
            safeSleep(2000);

            // Extract OTP — with retry
            String otp = "";
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    driver.switchTo().defaultContent();
                    java.util.List<WebElement> frames = driver.findElements(By.id("ifmail"));
                    if (frames.isEmpty()) {
                        log.warn("[Member {}] ifmail frame not found (attempt {}/3) — refreshing", memberNum, attempt);
                        try {
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                    "document.getElementById('refresh').click();");
                        } catch (Exception e) { driver.navigate().refresh(); }
                        safeSleep(3000);
                        continue;
                    }

                    driver.switchTo().frame("ifmail");
                    WebElement otpElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//p[contains(text(),'Your one-time password (OTP) for registering on My')]")));
                    String otpText = otpElement.getText();
                    otp = otpText.split("\\. This")[0].trim().split(" is ")[1].trim();
                    break;
                } catch (Exception e) {
                    try {
                        WebElement body = driver.findElement(By.tagName("body"));
                        Matcher matcher = Pattern.compile("\\b(\\d{4,6})\\b").matcher(body.getText());
                        if (matcher.find()) { otp = matcher.group(1); break; }
                    } catch (Exception e2) { /* ignore */ }

                    if (attempt < 3) {
                        driver.switchTo().defaultContent();
                        safeSleep(3000);
                        try {
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                                    "document.getElementById('refresh').click();");
                        } catch (Exception ex) { /* ignore */ }
                        safeSleep(2000);
                    }
                }
            }

            // Close Yopmail tab and switch back
            driver.switchTo().defaultContent();
            ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
            if (tabs.size() > 1) {
                driver.switchTo().window(tabs.get(1)).close();
                driver.switchTo().window(tabs.get(0));
            }
            safeSleep(1000);

            if (otp.isEmpty()) throw new RuntimeException("Could not extract OTP for member " + memberNum);
            log.info("[Member {}] OTP: {} — releasing lock", memberNum, otp);
            return otp;
        }
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private int getLastNnnyouthNumber() {
        String env = config.getEnv();
        String filePath = System.getProperty("user.dir") + File.separator
                + "resources" + File.separator + "Youth_" + env + ".xlsx";

        File file = new File(filePath);
        if (!file.exists()) return 0;

        try (FileInputStream fis = new FileInputStream(file);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheet("YouthClubMembers");
            if (sheet == null) return 0;

            int maxNum = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || row.getCell(0) == null) continue;
                String email = row.getCell(0).getCellType() == CellType.STRING
                        ? row.getCell(0).getStringCellValue().trim()
                        : row.getCell(0).toString().trim();
                // Extract number from nnnyouth{N}@yopmail.com
                if (email.startsWith("nnnyouth") && email.contains("@")) {
                    try {
                        String numStr = email.replace("nnnyouth", "").split("@")[0];
                        int num = Integer.parseInt(numStr);
                        if (num > maxNum) maxNum = num;
                    } catch (NumberFormatException e) { /* skip */ }
                }
            }
            log.info("Last nnnyouth number in Excel: {}", maxNum);
            return maxNum;
        } catch (Exception e) {
            log.warn("Could not read YouthClubMembers sheet: {}", e.getMessage());
            return 0;
        }
    }

    private WebDriver createDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--remote-allow-origins=*",
                "--disable-blink-features=AutomationControlled",
                "--no-sandbox",
                "--disable-dev-shm-usage"
        );
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        return driver;
    }

    private void fillRegistrationForm(WebDriver driver, WebDriverWait wait, Faker faker) throws InterruptedException {
        WebElement firstName = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstname")));
        scrollToElement(driver, firstName);
        firstName.clear();
        firstName.sendKeys(faker.name().firstName());

        WebElement lastName = driver.findElement(By.id("lastname"));
        lastName.clear();
        lastName.sendKeys(faker.name().lastName());

        driver.findElement(By.id("dobDD")).sendKeys(String.valueOf(faker.number().numberBetween(1, 28)));
        driver.findElement(By.id("dobMM")).sendKeys(String.valueOf(faker.number().numberBetween(1, 12)));
        driver.findElement(By.id("dobYYYY")).sendKeys(String.valueOf(faker.number().numberBetween(1970, 2003)));

        new Select(driver.findElement(By.id("gender"))).selectByVisibleText(faker.options().option("Male", "Female"));
        new Select(driver.findElement(By.xpath("//select[contains(.,'Select Category')]"))).selectByIndex(faker.number().numberBetween(2, 5));

        // Always select Uttar Pradesh for member registration
        Select stateSelect = new Select(driver.findElement(By.id("state")));
        try {
            stateSelect.selectByVisibleText("UTTAR PRADESH");
        } catch (Exception e) {
            // Fallback: try different casing
            try {
                stateSelect.selectByVisibleText("Uttar Pradesh");
            } catch (Exception e2) {
                stateSelect.selectByIndex(32); // Uttar Pradesh index fallback
            }
        }
        safeSleep(1000);
        new Select(wait.until(ExpectedConditions.elementToBeClickable(By.id("district")))).selectByIndex(1);
        safeSleep(500);

        clickJS(driver, driver.findElement(By.xpath("(//input[@id='flexRadioDefault1'])[1]")));
        safeSleep(500);

        Select ulb = new Select(driver.findElement(By.id("ulb")));
        if (ulb.getOptions().size() > 1) ulb.selectByIndex(1);

        driver.findElement(By.xpath("(//input[@id='pincode_urban'])[1]"))
                .sendKeys(String.valueOf(faker.number().numberBetween(100000, 999999)));

        clickJS(driver, driver.findElement(By.xpath("(//input[@id='NSS'])[1]")));

        new Select(driver.findElement(By.id("qualification"))).selectByIndex(4);
        new Select(driver.findElement(By.id("institution_type"))).selectByIndex(1);
        new Select(driver.findElement(By.id("institution_state"))).selectByIndex(6);
        safeSleep(500);
        new Select(driver.findElement(By.id("institution_district"))).selectByIndex(1);
        safeSleep(500);

        driver.findElement(By.xpath("//div[contains(text(),'Search and select an institution')]")).click();
        WebElement instInput = driver.findElement(By.xpath("(//div[contains(@class,'choices')]/input)[4]"));
        instInput.sendKeys("s");
        instInput.sendKeys(Keys.ENTER);

        driver.findElement(By.xpath("//div[contains(text(),'Search and select a sport')]")).click();
        WebElement sportInput = driver.findElement(By.xpath("(//div[contains(@class,'choices')]/input)[5]"));
        sportInput.sendKeys("B");
        sportInput.sendKeys(Keys.ENTER);

        try {
            WebElement participate = driver.findElement(By.id("khel_participate"));
            if (participate.isDisplayed()) clickJS(driver, participate);
        } catch (Exception e) { /* skip */ }

        scrollToElement(driver, driver.findElement(By.cssSelector("#defaultCheck1")));
        clickJS(driver, driver.findElement(By.cssSelector("#defaultCheck1")));
        clickJS(driver, driver.findElement(By.id("ncs_consent")));
    }

    private void handleSubmitPopup(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        try {
            WebElement popup = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("body > div:nth-child(1) > div:nth-child(1) > main:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(2) > main:nth-child(2) > div:nth-child(2) > div:nth-child(1) > div:nth-child(1) > div:nth-child(3) > div:nth-child(3) > button:nth-child(1)")));
            clickJS(driver, popup);
        } catch (Exception e1) {
            try {
                WebElement popupAlt = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("(//button[contains(@class,'bg-[#bc4717]')])[1]")));
                clickJS(driver, popupAlt);
            } catch (Exception e2) {
                try {
                    clickJS(driver, wait.until(ExpectedConditions.elementToBeClickable(By.id("btnAdditionalDetails"))));
                } catch (Exception e3) {
                    log.info("No submit popup — continuing");
                }
            }
        }
        safeSleep(2000);
        try { driver.switchTo().alert().accept(); } catch (Exception e) { /* no alert */ }
    }

    private void closePopup(WebDriver driver) {
        try {
            WebElement popup = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.elementToBeClickable(By.xpath("//i[@class='fa fa-times']")));
            popup.click();
            safeSleep(500);
        } catch (Exception e) { /* no popup */ }
    }

    private void scrollToElement(WebDriver driver, WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center',behavior:'smooth'});", element);
    }

    private void clickJS(WebDriver driver, WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void safeSleep(long millis) {
        try { Thread.sleep(millis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    /** Public accessor for other tests to get the registered emails */
    public static List<String> getRegisteredEmails() {
        return new ArrayList<>(registeredEmails);
    }
}
