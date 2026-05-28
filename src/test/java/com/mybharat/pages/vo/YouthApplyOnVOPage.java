package com.mybharat.pages.vo;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;
import com.mybharat.utils.ConfigReader;

public class YouthApplyOnVOPage extends BasePage {

    private static final Logger log = LogManager.getLogger(YouthApplyOnVOPage.class);
    private final ConfigReader config = new ConfigReader();

    public YouthApplyOnVOPage(WebDriver driver) {
        super(driver);
    }

    public void openVolunteerForBharat() throws InterruptedException {
        log.info("Opening Volunteer for Bharat page...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        String baseUrl = config.getUrl();
        driver.get(baseUrl + "/youth-profile");
        waitForPageLoad();
        Thread.sleep(3000);
        dismissOverlay();

        WebElement voLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[normalize-space()='Volunteer for Bharat'] | //a[contains(@href,'events_yuva')]")));
        scrollToElement(voLink);
        Thread.sleep(300);
        voLink.click();
        Thread.sleep(3000);
        waitForPageLoad();
        dismissOverlay();
        log.info("✅ On Volunteer for Bharat page. URL: {}", driver.getCurrentUrl());
    }

    public void searchEvent(String eventName) throws InterruptedException {
        log.info("Searching for event: {}", eventName);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        dismissOverlay();

        // Search by "Swachhta Hi Seva" — realistic event name
        String searchKeyword = "Swachhta Hi Seva";

        // Select "All" in Country dropdown
        try {
            WebElement countryDropdown = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.name("filter-country")));
            Select select = new Select(countryDropdown);
            try { select.selectByVisibleText("All"); } catch (Exception e) { select.selectByIndex(0); }
            Thread.sleep(500);
            log.info("✅ Country: All");
        } catch (Exception e) { log.warn("Country dropdown not found"); }

        // Type search keyword (name="filter-vo-name")
        try {
            WebElement eventNameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.name("filter-vo-name")));
            eventNameInput.clear();
            eventNameInput.sendKeys(searchKeyword);
            Thread.sleep(500);
            log.info("✅ Event name typed: {}", searchKeyword);
        } catch (Exception e) { log.warn("Event name input not found"); }

        // Click search
        try {
            WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[@class='filter-content-search']"
                            + " | //button[contains(@class,'search') or contains(text(),'Search')]"
                            + " | //input[@type='submit' and contains(@value,'Search')]")));
            searchBtn.click();
            Thread.sleep(5000); // Wait longer for search results to load
            dismissOverlay();
            log.info("✅ Search clicked");
        } catch (Exception e) {
            log.warn("Search button not found, trying Enter key...");
            try {
                WebElement eventNameInput = driver.findElement(By.name("filter-vo-name"));
                eventNameInput.sendKeys(Keys.ENTER);
                Thread.sleep(5000);
                dismissOverlay();
            } catch (Exception e2) {
                log.warn("Enter key fallback also failed");
            }
        }
    }

    public void clickEventByName(String eventName) throws InterruptedException {
        log.info("Clicking event card: {}", eventName);
        dismissOverlay();
        Thread.sleep(2000);

        // Match "swachhta hi seva" in card text
        String shortName = "swachhta hi seva";
        log.info("Searching for card text containing: '{}'", shortName);

        // Find all matching event cards
        String combinedXpath = "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + shortName + "')]";
        List<WebElement> eventCards = driver.findElements(By.xpath(combinedXpath));

        if (eventCards.isEmpty()) {
            // Try JS approach
            @SuppressWarnings("unchecked")
            List<WebElement> jsCards = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(
                    "var results = []; var all = document.querySelectorAll('a');" +
                    "for(var i=0; i<all.length; i++) {" +
                    "  if(all[i].textContent.toLowerCase().indexOf('" + shortName + "') !== -1) results.push(all[i]);" +
                    "}" +
                    "return results;");
            if (jsCards != null && !jsCards.isEmpty()) {
                eventCards = jsCards;
            }
        }

        if (eventCards.isEmpty()) {
            log.error("No event cards found for: {}", eventName);
            throw new RuntimeException("Event card not found for: " + eventName);
        }

        log.info("Found {} event card(s)", eventCards.size());

        // Click first card; if it doesn't open, try the next one
        for (int i = 0; i < eventCards.size() && i < 3; i++) {
            WebElement card = eventCards.get(i);
            String href = card.getAttribute("href");
            log.info("Trying card {} — href: {}", i + 1, href);

            if (href != null && !href.isEmpty()) {
                driver.get(href);
            } else {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
            }

            Thread.sleep(3000);

            // Check if loader stopped within 10 seconds
            boolean loaderStopped = waitForLoaderToStop(10);
            if (loaderStopped) {
                log.info("✅ Loader stopped. On event detail page. URL: {}", driver.getCurrentUrl());
                return;
            }

            // Loader didn't stop — force dismiss and check if page loaded
            log.warn("Loader stuck on card {}. Force dismissing...", i + 1);
            forceHideLoader();
            Thread.sleep(2000);

            // Check if the page actually loaded (has event content)
            boolean pageLoaded = isEventDetailPageLoaded();
            if (pageLoaded) {
                log.info("✅ Page loaded after force dismiss. URL: {}", driver.getCurrentUrl());
                return;
            }

            // Page didn't load — go back and try next card
            log.warn("Page not loaded for card {}. Going back to try next card...", i + 1);
            driver.navigate().back();
            Thread.sleep(3000);
            dismissOverlay();

            // Re-find cards after going back
            eventCards = driver.findElements(By.xpath(combinedXpath));
            if (eventCards.isEmpty()) break;
        }

        // Final fallback: force dismiss loader and continue
        forceHideLoader();
        Thread.sleep(2000);
        log.info("Proceeding with current page. URL: {}", driver.getCurrentUrl());
    }

    private boolean waitForLoaderToStop(int timeoutSeconds) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds)).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.id("overlay")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void forceHideLoader() {
        ((JavascriptExecutor) driver).executeScript(
                "var o=document.getElementById('overlay');if(o)o.style.display='none';" +
                "var l=document.getElementById('loader2');if(l)l.style.display='none';" +
                "var all=document.querySelectorAll('[class*=loader],[class*=overlay],[class*=spinner]');" +
                "for(var i=0;i<all.length;i++){all[i].style.display='none';}" +
                "try{$('#overlay').hide();$('#loader2').hide();}catch(e){}");
    }

    private boolean isEventDetailPageLoaded() {
        try {
            // Check if the page has event detail content (Images by Youth tab, Apply button, etc.)
            String url = driver.getCurrentUrl();
            if (url.contains("events_detail")) {
                return driver.findElements(By.xpath(
                        "//a[contains(text(),'Images by Youth') or contains(text(),'Images By Youth')]"
                        + " | //button[contains(text(),'Apply')]"
                        + " | //div[contains(@class,'event-detail')]"
                        + " | //h1 | //h2 | //h3")).size() > 0;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void clickApplyButton() throws InterruptedException {
        log.info("Clicking Apply button...");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        dismissOverlay();

        WebElement applyBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(@id,'elp_edit') or contains(text(),'Apply')]")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", applyBtn);
        Thread.sleep(300);
        applyBtn.click();
        Thread.sleep(3000);
        dismissOverlay();
        log.info("✅ Clicked Apply button");
    }

    private void dismissOverlay() {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.invisibilityOfElementLocated(By.id("overlay")));
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                    "var o=document.getElementById('overlay');if(o)o.style.display='none';" +
                    "var l=document.getElementById('loader2');if(l)l.style.display='none';" +
                    "try{$('#overlay').hide();$('#loader2').hide();}catch(e){}");
        }
        try { Thread.sleep(500); } catch (InterruptedException ignored) {}
    }
}
