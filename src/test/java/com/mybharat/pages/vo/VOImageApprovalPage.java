package com.mybharat.pages.vo;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;
import com.mybharat.utils.ConfigReader;

/**
 * VOImageApprovalPage - Org side: navigate to event, open Images by Youth tab,
 * Reject the first image card and Approve the second image card.
 *
 * Flow:
 *   1. Scroll down on youth profile page → Click "View More"
 *   2. Click org name in the table
 *   3. Click "Events" in left sidebar
 *   4. Search event by name (second-last from Event_Name.xlsx)
 *   5. Click on the matching event card
 *   6. Scroll down → Click "Images by Youth" tab
 *   7. Reject first image card
 *   8. Approve second image card
 */
public class VOImageApprovalPage extends BasePage {

    private static final Logger log = LogManager.getLogger(VOImageApprovalPage.class);
    private final ConfigReader config = new ConfigReader();

    public VOImageApprovalPage(WebDriver driver) {
        super(driver);
    }

    // -------------------------------------------------------------------------
    // Step 1 + 2: Scroll → View More → Click Org Name
    // -------------------------------------------------------------------------

    public void navigateToOrgDashboard() throws InterruptedException {
        log.info("Navigating to Org Dashboard...");
        dismissOverlay();

        // Scroll to bottom of current page
        log.info("Scrolling down...");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
        Thread.sleep(2000);

        // Click "View More"
        try {
            WebElement viewMore = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(text(),'View More')] | //button[contains(text(),'View More')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", viewMore);
            Thread.sleep(3000);
            waitForPageLoad();
            dismissOverlay();
            log.info("✅ Clicked View More. URL: {}", driver.getCurrentUrl());
        } catch (Exception e) {
            log.warn("View More not found, navigating directly to org list...");
            driver.get(config.getUrl() + "/mybharat_organizations");
            Thread.sleep(3000);
            dismissOverlay();
        }

        // Click org name (first row, second column link in table)
        try {
            WebElement orgLink = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//table//tbody//tr[1]//td[2]//a | //table//tbody//a[1]")));
            String orgName = orgLink.getText();
            scrollToElement(orgLink);
            Thread.sleep(300);
            orgLink.click();
            Thread.sleep(3000);
            waitForPageLoad();
            dismissOverlay();
            log.info("✅ Clicked org name: {}", orgName);
        } catch (Exception e) {
            log.warn("Org name link not found in table: {}", e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Step 3: Click Events in left sidebar
    // -------------------------------------------------------------------------

    public void clickEventsInSidebar() throws InterruptedException {
        log.info("Clicking Events in left sidebar...");
        try {
            WebElement eventsLink = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[normalize-space()='Events'] | //span[normalize-space()='Events']/ancestor::a")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", eventsLink);
            Thread.sleep(3000);
            waitForPageLoad();
            dismissOverlay();
            log.info("✅ Clicked Events. URL: {}", driver.getCurrentUrl());
        } catch (Exception e) {
            log.warn("Events sidebar link not found: {}", e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Step 4: Search event by name (second-last from Event_Name.xlsx)
    // -------------------------------------------------------------------------

    public String getSecondLastEventName() {
        String path = System.getProperty("user.dir") + java.io.File.separator
                + "resources" + java.io.File.separator + "Event_Name.xlsx";
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Event_Data");
            if (sheet == null) sheet = workbook.getSheetAt(0);

            int lastRow = sheet.getLastRowNum();
            int targetRow = (lastRow >= 1) ? lastRow - 1 : lastRow;

            org.apache.poi.ss.usermodel.Row row = sheet.getRow(targetRow);
            if (row != null && row.getCell(0) != null) {
                String name = row.getCell(0).getStringCellValue().trim();
                log.info("Second-last event from Excel (row {}): {}", targetRow, name);
                return name;
            }
        } catch (Exception e) {
            log.error("Failed to read Event_Name.xlsx: {}", e.getMessage());
        }
        return "";
    }

    public void searchEventByName(String eventName) throws InterruptedException {
        log.info("Searching for event: {}", eventName);
        dismissOverlay();

        // Extract numeric suffix for search (e.g. "4239" from "VO Automation Event 4239")
        String searchKeyword = eventName;
        java.util.regex.Matcher numMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*$").matcher(eventName);
        if (numMatcher.find()) {
            searchKeyword = numMatcher.group(1);
            log.info("Using numeric suffix as search keyword: {}", searchKeyword);
        }

        // Try to find a search input on the events list page
        try {
            WebElement searchInput = new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath("//input[@type='search' or @type='text' or contains(@placeholder,'Search') or contains(@placeholder,'search') or contains(@name,'search')]")));
            searchInput.clear();
            searchInput.sendKeys(searchKeyword);
            Thread.sleep(500);
            searchInput.sendKeys(Keys.ENTER);
            Thread.sleep(3000);
            dismissOverlay();
            log.info("✅ Searched for: {}", searchKeyword);
        } catch (Exception e) {
            log.warn("Search input not found, will try to find card directly: {}", e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Step 4: Try a card for image approval — returns true if images exist
    // -------------------------------------------------------------------------

    /**
     * Click the LAST event card, scroll down, click "Images by Youth" tab,
     * check if images exist. Returns true if images are available.
     */
    public boolean tryLastCardForImageApproval() throws InterruptedException {
        log.info("Trying LAST event card for image approval...");
        List<WebElement> cards = getAllEventCards();
        if (cards.isEmpty()) {
            log.warn("No event cards found");
            return false;
        }
        int lastIndex = cards.size() - 1;
        log.info("Total cards: {}, clicking last card (index {})", cards.size(), lastIndex + 1);
        return clickCardAndCheckImages(cards, lastIndex);
    }

    /**
     * Click the 2nd LAST event card, scroll down, click "Images by Youth" tab,
     * check if images exist. Returns true if images are available.
     */
    public boolean trySecondLastCardForImageApproval() throws InterruptedException {
        log.info("Trying 2nd LAST event card for image approval...");
        List<WebElement> cards = getAllEventCards();
        if (cards.size() < 2) {
            log.warn("Less than 2 cards found ({}), cannot try 2nd last", cards.size());
            return false;
        }
        int secondLastIndex = cards.size() - 2;
        log.info("Total cards: {}, clicking 2nd last card (index {})", cards.size(), secondLastIndex + 1);
        return clickCardAndCheckImages(cards, secondLastIndex);
    }

    /**
     * Click event card at given index, scroll down, click "Images by Youth" tab,
     * check if images exist. Returns true if images are available, false if "There is no Images."
     */
    public boolean tryCardForImageApproval(int cardIndex) throws InterruptedException {
        log.info("Trying event card at index {} for image approval...", cardIndex + 1);
        List<WebElement> cards = getAllEventCards();
        if (cards.size() <= cardIndex) {
            log.warn("Card index {} not available (only {} cards found)", cardIndex + 1, cards.size());
            return false;
        }
        return clickCardAndCheckImages(cards, cardIndex);
    }

    private boolean clickCardAndCheckImages(List<WebElement> cards, int cardIndex) throws InterruptedException {
        dismissOverlay();

        WebElement targetCard = cards.get(cardIndex);
        String href = targetCard.getAttribute("href");
        log.info("Clicking card {} — href: {}", cardIndex + 1, href);

        if (href != null && !href.isEmpty()) {
            driver.get(href);
        } else {
            scrollToElement(targetCard);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetCard);
        }

        Thread.sleep(4000);
        forceHideLoader();
        waitForPageLoad();
        dismissOverlay();
        log.info("✅ On event detail page. URL: {}", driver.getCurrentUrl());

        // Scroll down and click "Images by Youth" tab
        clickImagesByYouthTab();

        // Check if images exist or "There is no Images."
        Thread.sleep(2000);
        boolean hasImages = checkIfImagesExist();

        if (!hasImages) {
            log.info("No images found on card {}", cardIndex + 1);
            return false;
        }

        log.info("✅ Images found on card {}", cardIndex + 1);
        return true;
    }

    private List<WebElement> getAllEventCards() {
        List<WebElement> cards = driver.findElements(By.xpath(
                "//a[contains(@href,'event_detail_nyf') or contains(@href,'orgeventmanagement/event_detail')]"));

        if (cards.isEmpty()) {
            cards = driver.findElements(By.xpath(
                    "//div[contains(@class,'card')]//a[contains(@href,'event')] | " +
                    "//div[contains(@class,'event')]//a"));
        }

        if (cards.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<WebElement> jsCards = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(
                    "var results=[]; var all=document.querySelectorAll('a');" +
                    "for(var i=0;i<all.length;i++){" +
                    "  var h=all[i].href||'';" +
                    "  if(h.indexOf('event_detail')!==-1 || h.indexOf('orgeventmanagement')!==-1) results.push(all[i]);" +
                    "}" +
                    "return results;");
            if (jsCards != null && !jsCards.isEmpty()) cards = jsCards;
        }

        log.info("Total event cards found: {}", cards.size());
        return cards;
    }

    /**
     * Check if images exist under "Images by Youth" tab.
     * Returns false if "There is no Images." text is present or no Reject/Approve buttons found.
     */
    private boolean checkIfImagesExist() {
        List<WebElement> noImagesMsg = driver.findElements(
                By.xpath("//*[contains(text(),'There is no Images') or contains(text(),'No Images')]"));
        if (!noImagesMsg.isEmpty()) {
            log.info("Found 'There is no Images.' message");
            return false;
        }

        List<WebElement> actionBtns = driver.findElements(
                By.xpath("//button[normalize-space()='Reject' or normalize-space()='Approve']"
                        + " | //a[normalize-space()='Reject' or normalize-space()='Approve']"));
        if (actionBtns.isEmpty()) {
            log.info("No Reject/Approve buttons found — no images to review");
            return false;
        }

        return true;
    }

    /**
     * Go back to events list page — scroll up, click "Go Back" button or browser back.
     */
    public void goBackToEventsList() throws InterruptedException {
        log.info("Going back to events list...");

        // Scroll to top first
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        Thread.sleep(1000);

        // Try clicking "Go Back" link
        try {
            WebElement goBackBtn = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(text(),'Go Back')] | //button[contains(text(),'Go Back')]"
                                    + " | //a[contains(@class,'back')]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", goBackBtn);
            Thread.sleep(3000);
            waitForPageLoad();
            dismissOverlay();
            log.info("✅ Clicked 'Go Back'");
        } catch (Exception e) {
            // Fallback: browser back
            log.info("Go Back button not found, using browser back...");
            driver.navigate().back();
            Thread.sleep(3000);
            dismissOverlay();
            log.info("✅ Navigated back");
        }
    }

    // -------------------------------------------------------------------------
    // Step 5a: Click the 1st event card on the org events list page
    // -------------------------------------------------------------------------

    public void clickFirstEventCard() throws InterruptedException {
        log.info("Clicking the 1st event card on org events list...");
        dismissOverlay();
        Thread.sleep(2000);

        // Collect all event card links
        List<WebElement> cards = driver.findElements(By.xpath(
                "//a[contains(@href,'event_detail_nyf') or contains(@href,'orgeventmanagement/event_detail')]"));

        if (cards.isEmpty()) {
            cards = driver.findElements(By.xpath(
                    "//div[contains(@class,'card')]//a[contains(@href,'event')] | " +
                    "//div[contains(@class,'event')]//a"));
            log.info("Fallback card search: found {} cards", cards.size());
        }

        if (cards.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<WebElement> jsCards = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(
                    "var results=[]; var all=document.querySelectorAll('a');" +
                    "for(var i=0;i<all.length;i++){" +
                    "  var h=all[i].href||'';" +
                    "  if(h.indexOf('event_detail')!==-1 || h.indexOf('orgeventmanagement')!==-1) results.push(all[i]);" +
                    "}" +
                    "return results;");
            if (jsCards != null && !jsCards.isEmpty()) cards = jsCards;
        }

        log.info("Total event cards found: {}", cards.size());

        if (cards.isEmpty()) {
            log.error("No event cards found on the page");
            throw new RuntimeException("No event cards found");
        }

        // Click 1st card
        WebElement targetCard = cards.get(0);
        String href = targetCard.getAttribute("href");
        log.info("Clicking 1st event card href: {}", href);

        if (href != null && !href.isEmpty()) {
            driver.get(href);
        } else {
            scrollToElement(targetCard);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetCard);
        }

        Thread.sleep(4000);
        forceHideLoader();
        waitForPageLoad();
        dismissOverlay();
        log.info("✅ On event detail page. URL: {}", driver.getCurrentUrl());
    }

    // -------------------------------------------------------------------------
    // Step 5b: Click on the matching event card by name
    // -------------------------------------------------------------------------

    public void clickEventCard(String eventName) throws InterruptedException {
        log.info("Clicking event card for: {}", eventName);
        dismissOverlay();
        Thread.sleep(1000);

        String lowerName = eventName.toLowerCase().trim();
        String numericSuffix = "";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)\\s*$").matcher(lowerName);
        if (m.find()) numericSuffix = m.group(1);

        // Try full name match first
        List<WebElement> cards = driver.findElements(By.xpath(
                "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + lowerName + "')]"));

        // Fallback: numeric suffix
        if (cards.isEmpty() && !numericSuffix.isEmpty()) {
            log.info("Full name not found, trying numeric suffix: {}", numericSuffix);
            cards = driver.findElements(By.xpath(
                    "//a[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" + numericSuffix + "')]"));
        }

        // Fallback: any event detail link
        if (cards.isEmpty()) {
            cards = driver.findElements(By.xpath("//a[contains(@href,'event_detail') or contains(@href,'orgeventmanagement')]"));
            log.info("Href fallback: found {} cards", cards.size());
        }

        if (cards.isEmpty()) {
            @SuppressWarnings("unchecked")
            List<WebElement> jsCards = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(
                    "var results=[]; var all=document.querySelectorAll('a');" +
                    "for(var i=0;i<all.length;i++){" +
                    "  var t=all[i].textContent.toLowerCase();" +
                    "  if(t.indexOf('" + lowerName + "')!==-1 || ('" + numericSuffix + "'!=='' && t.indexOf('" + numericSuffix + "')!==-1)) results.push(all[i]);" +
                    "}" +
                    "return results;");
            if (jsCards != null && !jsCards.isEmpty()) cards = jsCards;
        }

        if (cards.isEmpty()) {
            log.error("Event card not found for: {}", eventName);
            throw new RuntimeException("Event card not found for: " + eventName);
        }

        WebElement card = cards.get(0);
        String href = card.getAttribute("href");
        log.info("Clicking card href: {}", href);

        if (href != null && !href.isEmpty()) {
            driver.get(href);
        } else {
            scrollToElement(card);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
        }

        Thread.sleep(4000);
        forceHideLoader();
        waitForPageLoad();
        dismissOverlay();
        log.info("✅ On event detail page. URL: {}", driver.getCurrentUrl());
    }

    // -------------------------------------------------------------------------
    // Step 5b: Click the 2nd event card on the org events list page (by position)
    // -------------------------------------------------------------------------

    public void clickSecondEventCard() throws InterruptedException {
        log.info("Clicking the 2nd event card on org events list...");
        dismissOverlay();
        Thread.sleep(2000);

        // Collect all event card links — org event management links
        List<WebElement> cards = driver.findElements(By.xpath(
                "//a[contains(@href,'event_detail_nyf') or contains(@href,'orgeventmanagement/event_detail')]"));

        if (cards.isEmpty()) {
            // Fallback: any card-level clickable link inside event card divs
            cards = driver.findElements(By.xpath(
                    "//div[contains(@class,'card')]//a[contains(@href,'event')] | " +
                    "//div[contains(@class,'event')]//a"));
            log.info("Fallback card search: found {} cards", cards.size());
        }

        if (cards.isEmpty()) {
            // JS fallback: collect all event detail hrefs
            @SuppressWarnings("unchecked")
            List<WebElement> jsCards = (List<WebElement>) ((JavascriptExecutor) driver).executeScript(
                    "var results=[]; var all=document.querySelectorAll('a');" +
                    "for(var i=0;i<all.length;i++){" +
                    "  var h=all[i].href||'';" +
                    "  if(h.indexOf('event_detail')!==-1 || h.indexOf('orgeventmanagement')!==-1) results.push(all[i]);" +
                    "}" +
                    "return results;");
            if (jsCards != null && !jsCards.isEmpty()) cards = jsCards;
        }

        log.info("Total event cards found: {}", cards.size());

        if (cards.size() < 2) {
            log.warn("Less than 2 event cards found ({}), clicking first available", cards.size());
        }

        // Pick 2nd card (index 1), fallback to 1st
        WebElement targetCard = cards.size() >= 2 ? cards.get(1) : cards.get(0);
        String href = targetCard.getAttribute("href");
        log.info("Clicking 2nd event card href: {}", href);

        if (href != null && !href.isEmpty()) {
            driver.get(href);
        } else {
            scrollToElement(targetCard);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", targetCard);
        }

        Thread.sleep(4000);
        forceHideLoader();
        waitForPageLoad();
        dismissOverlay();
        log.info("✅ On event detail page. URL: {}", driver.getCurrentUrl());
    }

    // -------------------------------------------------------------------------
    // Step 6: Scroll down → Click "Images by Youth" tab
    // -------------------------------------------------------------------------

    public void clickImagesByYouthTab() throws InterruptedException {
        log.info("Scrolling down and clicking 'Images by Youth' tab...");
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight * 0.7);");
        Thread.sleep(2000);
        dismissOverlay();

        try {
            WebElement tab = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(text(),'Images by Youth') or contains(text(),'Images By Youth')]"
                                    + " | //button[contains(text(),'Images by Youth')]"
                                    + " | //li//a[contains(text(),'Youth')]")));
            scrollToElement(tab);
            Thread.sleep(300);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
            Thread.sleep(2000);
            log.info("✅ Clicked 'Images by Youth' tab");
        } catch (Exception e) {
            log.warn("Images by Youth tab not found via XPath, trying JS...");
            ((JavascriptExecutor) driver).executeScript(
                    "var tabs=document.querySelectorAll('a,button,li');" +
                    "for(var i=0;i<tabs.length;i++){" +
                    "  if(tabs[i].textContent.toLowerCase().indexOf('images by youth')!==-1){tabs[i].click();break;}" +
                    "}");
            Thread.sleep(2000);
            log.info("✅ Clicked 'Images by Youth' tab (JS fallback)");
        }
    }

    // -------------------------------------------------------------------------
    // Step 7 + 8: Reject first card, Approve second card
    // -------------------------------------------------------------------------

    public void rejectFirstAndApproveSecond() throws InterruptedException {
        log.info("Finding image cards under 'Images by Youth'...");
        Thread.sleep(2000);
        dismissOverlay();

        // Scroll down to image cards
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight * 0.6);");
        Thread.sleep(1500);

        // Find all Reject buttons
        List<WebElement> rejectBtns = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//button[normalize-space()='Reject'] | //a[normalize-space()='Reject']")));

        // Find all Approve buttons
        List<WebElement> approveBtns = new WebDriverWait(driver, Duration.ofSeconds(15)).until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(
                        By.xpath("//button[normalize-space()='Approve'] | //a[normalize-space()='Approve']")));

        log.info("Found {} Reject buttons and {} Approve buttons", rejectBtns.size(), approveBtns.size());

        // Reject first card
        if (!rejectBtns.isEmpty()) {
            WebElement firstReject = rejectBtns.get(0);
            scrollToElement(firstReject);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstReject);
            Thread.sleep(2000);
            handleConfirmationPopup();
            log.info("✅ Rejected first image card");
        } else {
            log.warn("No Reject buttons found");
        }

        Thread.sleep(2000);
        dismissOverlay();

        // Re-fetch Approve buttons after reject (DOM may have refreshed)
        approveBtns = driver.findElements(
                By.xpath("//button[normalize-space()='Approve'] | //a[normalize-space()='Approve']"));
        log.info("After reject: found {} Approve buttons", approveBtns.size());

        // Approve second card (index 1)
        if (approveBtns.size() >= 2) {
            WebElement secondApprove = approveBtns.get(1);
            scrollToElement(secondApprove);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", secondApprove);
            Thread.sleep(2000);
            handleConfirmationPopup();
            log.info("✅ Approved second image card");
        } else if (approveBtns.size() == 1) {
            // Only one approve left — click it
            WebElement approveBtn = approveBtns.get(0);
            scrollToElement(approveBtn);
            Thread.sleep(500);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", approveBtn);
            Thread.sleep(2000);
            handleConfirmationPopup();
            log.info("✅ Approved image card (only one available)");
        } else {
            log.warn("No Approve buttons found after reject");
        }

        log.info("✅ Image approval/rejection complete");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void handleConfirmationPopup() throws InterruptedException {
        // Handle any confirmation modal (OK / Yes / Confirm)
        try {
            WebElement confirmBtn = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[normalize-space()='OK' or normalize-space()='Yes' or normalize-space()='Confirm']"
                                    + " | //a[normalize-space()='OK' or normalize-space()='Yes']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", confirmBtn);
            Thread.sleep(1500);
            log.info("✅ Dismissed confirmation popup");
        } catch (Exception e) {
            // No popup — that's fine
        }
        dismissOverlay();
    }

    private void forceHideLoader() {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "var o=document.getElementById('overlay');if(o)o.style.display='none';" +
                    "var l=document.getElementById('loader2');if(l)l.style.display='none';" +
                    "var all=document.querySelectorAll('[class*=loader],[class*=overlay],[class*=spinner]');" +
                    "for(var i=0;i<all.length;i++){all[i].style.display='none';}" +
                    "try{$('#overlay').hide();$('#loader2').hide();}catch(e){}");
        } catch (Exception ignored) {}
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
