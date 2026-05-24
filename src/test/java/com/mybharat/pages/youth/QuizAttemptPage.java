package com.mybharat.pages.youth;

import java.time.Duration;
import java.util.List;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.utils.ConfigReader;import com.mybharat.pages.BasePage;

/**
 * PlayQuizPage - Handles quiz registration and answering all questions.
 */
public class QuizAttemptPage extends BasePage {

    private final String language;
    private final Random random = new Random();

    @FindBy(css = ".logo.mybharatlogo")
    private WebElement logoIcon;

    @FindBy(xpath = "//span[normalize-space()='Quiz & Essay']")
    private WebElement quizAndEssayTab;

    @FindBy(xpath = "//select[@id='quizLanguage']")
    private WebElement languageDropdown;

    public QuizAttemptPage(WebDriver driver, String language) {
        super(driver);
        this.language = (language != null) ? language : "English";
    }

    public QuizAttemptPage(WebDriver driver) {
        this(driver, "English");
    }

    /** Stores the quiz name extracted during the test */
    private String quizName = "Competitive Quiz";

    /**
     * Get the quiz name that was played.
     */
    public String getQuizName() {
        return quizName;
    }

    /**
     * Navigate to quiz section and start the quiz.
     */
    public void startQuiz() throws Exception {
        int timeout = Boolean.parseBoolean(System.getProperty("ciMode", "false")) ? 60 : 30;
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(timeout));

        // Navigate to home page using config URL
        ConfigReader config = new ConfigReader();
        driver.get(config.getUrl());
        waitForPageLoad();
        Thread.sleep(2000);

        // Close popup if present
        try {
            WebElement popup = driver.findElement(By.xpath("//i[@class='fa fa-times']"));
            if (popup.isDisplayed()) popup.click();
        } catch (Exception e) { /* no popup */ }

        // Click Quiz & Essay tab
        scrollPage(500);
        Thread.sleep(1000);
        try {
            waitForClickable(quizAndEssayTab);
            safeClick(quizAndEssayTab);
        } catch (Exception e) {
            // Fallback: try JS click or different locator
            try {
                WebElement quizTab = driver.findElement(By.xpath(
                        "//span[contains(text(),'Quiz')] | //a[contains(text(),'Quiz')] | //button[contains(text(),'Quiz')]"));
                scrollToElement(quizTab);
                Thread.sleep(500);
                jsClick(quizTab);
            } catch (Exception e2) {
                // Last resort: navigate directly to quiz URL
                driver.get(config.getUrl() + "/quiz");
                waitForPageLoad();
                Thread.sleep(2000);
            }
        }
        Thread.sleep(2000);
        scrollPage(1000);
        Thread.sleep(1000);

        // Extract quiz name from the card before clicking Start Quiz
        try {
            WebElement quizTitle = driver.findElement(By.xpath(
                    "(//h4[@class='event_name fontchange18'])[1]"));
            quizName = quizTitle.getText().trim();
            System.out.println("Quiz Name: " + quizName);
        } catch (Exception e) {
            System.out.println("Could not extract quiz name from card, using default");
        }

        // Click Start Quiz — try multiple locators
        WebElement startQuiz = null;
        try {
            startQuiz = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath("(//button[@type='button'][normalize-space()='Start Quiz'])[1]")));
        } catch (Exception e) {
            startQuiz = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='start_quiz']")));
        }
        scrollToElement(startQuiz);
        Thread.sleep(500);
        jsClick(startQuiz);

        // Save quiz name to file for workflow to read
        try {
            java.io.File quizFile = new java.io.File(System.getProperty("user.dir") + "/reports/quiz_name.txt");
            quizFile.getParentFile().mkdirs();
            java.nio.file.Files.writeString(quizFile.toPath(), quizName);
        } catch (Exception e) { /* ignore */ }

        // Click second "START QUIZ" button in the modal (quiz instructions modal)
        // From screenshot: this is an orange text link/button inside the modal
        try {
            WebElement startQuiz2 = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(
                            "//button[normalize-space()='START QUIZ'] | " +
                            "//a[normalize-space()='START QUIZ'] | " +
                            "//span[normalize-space()='START QUIZ'] | " +
                            "//*[normalize-space()='START QUIZ' and (self::button or self::a or self::span or self::div)]")));
            scrollToElement(startQuiz2);
            Thread.sleep(500);
            jsClick(startQuiz2);
            System.out.println("Clicked modal START QUIZ button");
        } catch (Exception e) {
            // Try case-insensitive and partial match
            try {
                WebElement startQuiz2 = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                        ExpectedConditions.elementToBeClickable(By.xpath(
                                "//*[contains(translate(normalize-space(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ'),'START QUIZ')]")));
                jsClick(startQuiz2);
                System.out.println("Clicked modal START QUIZ (fallback)");
            } catch (Exception e2) {
                System.out.println("Modal START QUIZ button not found — may not be required for this quiz");
            }
        }

        // Wait for page to stabilize after quiz start
        Thread.sleep(3000);
        waitForPageLoad();

        // Check if the details form is present (it may be skipped for some quizzes)
        boolean detailsFormPresent = false;
        try {
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.presenceOfElementLocated(By.xpath(
                            "//button[@id='checkDetailsFormButton'] | //input[contains(@id,'check_detail')] | //input[@name='whether_disability']")));
            detailsFormPresent = true;
        } catch (Exception e) {
            System.out.println("Details form not found — may have been skipped or page structure changed");
        }

        if (detailsFormPresent) {
            // Select "No" for disability — try multiple locators
            WebElement disability = null;
            String[] disabilityLocators = {
                "(//input[@id='check_detail_whether_disability'])[2]",
                "//input[@name='whether_disability' and @value='No']",
                "//input[@name='whether_disability'][2]",
                "//label[contains(text(),'No')]/preceding-sibling::input[@type='radio']",
                "(//input[@type='radio'][@name='whether_disability'])[2]",
                "//label[contains(text(),'No')]/input[@type='radio']",
                "//label[normalize-space()='No']/preceding-sibling::input"
            };
            for (String locator : disabilityLocators) {
                try {
                    disability = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                            ExpectedConditions.presenceOfElementLocated(By.xpath(locator)));
                    if (disability != null) break;
                } catch (Exception e) { /* try next */ }
            }
            if (disability == null) {
                // Last resort — try to find any "No" radio button on the page
                disability = longWait.until(
                        ExpectedConditions.presenceOfElementLocated(
                                By.xpath("(//input[@id='check_detail_whether_disability'])[2]")));
            }
            scrollToElement(disability);
            Thread.sleep(500);
            jsClick(disability);
            // Trigger change event to ensure form validation picks up the selection
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));", disability);
            Thread.sleep(500);

            // Click Proceed — try multiple locators
            WebElement proceed = null;
            String[] proceedLocators = {
                "//button[@id='checkDetailsFormButton']",
                "//button[contains(text(),'Proceed')]",
                "//button[contains(text(),'PROCEED')]",
                "//button[contains(text(),'Submit')]",
                "//button[@type='submit' and ancestor::form[contains(@id,'check')]]"
            };
            for (String locator : proceedLocators) {
                try {
                    proceed = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                            ExpectedConditions.elementToBeClickable(By.xpath(locator)));
                    if (proceed != null) break;
                } catch (Exception e) { /* try next */ }
            }
            if (proceed == null) {
                // Force-enable and click the button via JS
                proceed = longWait.until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@id='checkDetailsFormButton']")));
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].disabled = false; arguments[0].click();", proceed);
            } else {
                jsClick(proceed);
            }
            Thread.sleep(1000);
        }

        // Select language
        selectQuizLanguage();

        // Click Start Quiz button — try multiple locators
        try {
            WebElement startBtn = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath(
                            "//button[@id='startQuizButton'] | " +
                            "//button[contains(text(),'Start Quiz')] | " +
                            "//button[contains(text(),'START QUIZ')] | " +
                            "//a[contains(text(),'Start Quiz')]")));
            jsClick(startBtn);
        } catch (Exception e) {
            // Quiz may have auto-started after language selection or details form
            System.out.println("startQuizButton not found — quiz may have auto-started: " + e.getMessage());
            // Verify we're on the quiz questions page
            try {
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                        ExpectedConditions.presenceOfElementLocated(By.xpath(
                                "//div[contains(@class,'form-check')]//label | " +
                                "//div[contains(@class,'option')]//label | " +
                                "//button[@id='save_button']")));
                System.out.println("Quiz questions page detected — continuing");
            } catch (Exception e2) {
                throw new RuntimeException("Could not start quiz — neither startQuizButton nor questions found", e2);
            }
        }
    }

    /**
     * Attempt all 20 questions with random answers and submit.
     */
    public void attemptAllQuestionsAndSubmit() throws Exception {
        WebDriverWait qWait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        int totalQuestions = 20;

        Thread.sleep(3000);
        waitForPageLoad();

        for (int q = 1; q <= totalQuestions; q++) {
            System.out.println("Answering question " + q);
            Thread.sleep(1000); // Wait for question to fully load
            waitForPageLoad();

            // Find answer options (fresh lookup every time to avoid stale elements)
            List<WebElement> options = null;
            for (int attempt = 0; attempt < 3; attempt++) {
                try {
                    options = findAnswerOptions(qWait);
                    if (!options.isEmpty()) break;
                } catch (Exception e) {
                    Thread.sleep(1000);
                }
            }
            if (options == null || options.isEmpty()) {
                throw new Exception("No answer options found for question " + q);
            }

            // Select random answer — re-find to avoid stale reference
            try {
                WebElement selected = options.get(random.nextInt(options.size()));
                scrollToElement(selected);
                Thread.sleep(500);
                jsClick(selected);
            } catch (org.openqa.selenium.StaleElementReferenceException e) {
                // Element went stale — re-find and click
                Thread.sleep(1000);
                options = findAnswerOptions(qWait);
                WebElement selected = options.get(random.nextInt(options.size()));
                jsClick(selected);
            }

            // Click Next (except for last question)
            if (q < totalQuestions) {
                clickNextButton(qWait, js);
                Thread.sleep(1500); // Wait for next question to load
            }
        }

        // Submit quiz
        submitQuiz(qWait, js);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private void selectQuizLanguage() {
        try {
            // Try with fresh locator (more reliable after page transitions)
            WebElement langDrop = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//select[@id='quizLanguage']")));
            Select langSelect = new Select(langDrop);
            langSelect.selectByVisibleText(language);
            System.out.println("Selected language: " + language);
        } catch (Exception e) {
            // Language dropdown may not exist for single-language quizzes
            System.out.println("Language selection skipped (not available or single-language quiz): " + e.getMessage());
        }
    }

    private List<WebElement> findAnswerOptions(WebDriverWait qWait) {
        String[] selectors = {
            "//div[contains(@class,'form-check')]//label",
            "//div[contains(@class,'option')]//label",
            "//div[contains(@class,'radio')]//label"
        };
        for (String selector : selectors) {
            try {
                qWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(selector)));
                List<WebElement> options = driver.findElements(By.xpath(selector));
                if (!options.isEmpty()) return options;
            } catch (Exception e) { /* try next */ }
        }
        return List.of();
    }

    private void clickNextButton(WebDriverWait qWait, JavascriptExecutor js) {
        String[] selectors = {"//button[@id='save_button']", "//button[contains(text(),'Next')]"};
        for (String selector : selectors) {
            try {
                WebElement btn = qWait.until(ExpectedConditions.elementToBeClickable(By.xpath(selector)));
                js.executeScript("arguments[0].click();", btn);
                return;
            } catch (Exception e) { /* try next */ }
        }
    }

    private void submitQuiz(WebDriverWait qWait, JavascriptExecutor js) throws Exception {
        // First submit
        WebElement submit = qWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='submit_button']")));
        js.executeScript("arguments[0].click();", submit);
        Thread.sleep(1000);

        // Final confirm
        WebElement confirm = qWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='submit_quiz']")));
        js.executeScript("arguments[0].click();", confirm);
        System.out.println("Quiz submitted successfully");

        // Rate the quiz (click 4th star)
        Thread.sleep(2000);
        WebElement ratingStar = qWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//i[4]")));
        js.executeScript("arguments[0].click();", ratingStar);
        Thread.sleep(500);

        // Click feedback submit button
        WebElement feedbackSubmit = qWait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='submit_button']")));
        js.executeScript("arguments[0].click();", feedbackSubmit);
        System.out.println("Feedback submitted successfully");
    }

    /**
     * Download quiz certificate and close the modal.
     * Called after quiz submission and feedback.
     */
    public void downloadQuizCertificateAndClose() throws Exception {
        WebDriverWait qWait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Wait for certificate modal/download button to appear
        Thread.sleep(3000);

        // Click Download button
        try {
            WebElement downloadBtn = qWait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Download']")));
            scrollToElement(downloadBtn);
            Thread.sleep(500);
            js.executeScript("arguments[0].click();", downloadBtn);
            System.out.println("✅ Quiz certificate download clicked");
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("⚠ Download button not found: " + e.getMessage());
        }

        // Close the certificate modal
        try {
            WebElement closeModal = qWait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//span[@class='modal-close']")));
            js.executeScript("arguments[0].click();", closeModal);
            System.out.println("✅ Quiz certificate modal closed");
            Thread.sleep(1000);
        } catch (Exception e) {
            System.out.println("⚠ Modal close button not found: " + e.getMessage());
        }
    }
}
