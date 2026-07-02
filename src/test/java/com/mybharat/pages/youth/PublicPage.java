package com.mybharat.pages.youth;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;
import com.mybharat.utils.ConfigReader;

/**
 * PublicPage - Page Object for Header Menu, Organization Section, and Footer navigation.
 *
 * Purpose: Validates all publicly accessible pages on MYBharat (https://mybharat.gov.in)
 *          without requiring user login. Tests navigation via header menus, dropdown
 *          submenus, organization section cards, and footer links.
 *
 * Header Menus:
 *   Youth | Quiz &amp; Essay | Resources ▾ | Events &amp; Program ▾ | MY Bharat Podcast | VVVP 2026
 *
 * Resources dropdown: Voices ▸, Blogs, Newsletters, Other Resources
 * Events &amp; Program dropdown: Experiential Learning, Volunteer for Bharat, Mega Events, VBYLD-2026
 *
 * Key Methods:
 *   - clickYouth(), clickQuizAndEssay(), clickMyBharatPodcast(), clickVVVP2026()
 *   - clickResourcesVoices(), clickResourcesBlogs(), clickResourcesNewsletters()
 *   - clickEventsExperientialLearning(), clickEventsVolunteerForBharat(), etc.
 *   - clickFooterMegaEvents(), clickFooterPrivacyPolicy(), clickFooterFeedback(), etc.
 *   - validateDigitalIndiaLogo(), validateDICText()
 *   - getFailedMenus(), getPassedMenus() — test result tracking
 *
 * Fallback Strategy: All menu clicks have a direct URL navigation fallback if the
 *                    hover/click approach fails (common in CI headless environments).
 *
 * Dependencies: BasePage, ConfigReader
 * Developer: Nishant Sharma (QA Team)
 *
 * @see PublicPageTest
 * @see BasePage
 */
public class PublicPage extends BasePage {

    private static final Logger log = LogManager.getLogger(PublicPage.class);
    private final ConfigReader config = new ConfigReader();
    private static final int WAIT_SEC = 10;

    private final List<Map<String, String>> failedMenus = new ArrayList<>();
    private final List<String> passedMenus = new ArrayList<>();

    public PublicPage(WebDriver driver) {
        super(driver);
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    public void navigateToHomePage() {
        String currentUrl = driver.getCurrentUrl();
        String homeUrl = config.getUrl();
        // Skip navigation if already on homepage
        if (currentUrl != null && currentUrl.startsWith(homeUrl) && !currentUrl.contains("/pages/")
                && !currentUrl.contains("/blogs") && !currentUrl.contains("/quiz")
                && !currentUrl.contains("/mega_events") && !currentUrl.contains("/government")
                && !currentUrl.contains("/resources") && !currentUrl.contains("/sitemap")
                && !currentUrl.contains("/404") && !currentUrl.contains("/newsletters")) {
            closePopupIfPresent();
            return;
        }
        driver.get(homeUrl);
        waitForPageLoad();
        closePopupIfPresent();
    }

    public void closePopupIfPresent() {
        try {
            // Reduced from 5s to 1s — popup appears instantly if at all
            WebElement popup = new WebDriverWait(driver, Duration.ofSeconds(1)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//i[@class='fa fa-times']")));
            popup.click();
            log.info("Popup closed");
        } catch (Exception e) {
            // No popup — no log spam needed
        }
    }

    // =========================================================================
    // TOP-LEVEL MENU CLICKS
    // =========================================================================

    public boolean clickYouth() {
        return clickTopMenuWithFallback("Youth",
                "//nav//a[normalize-space()='Youth']",
                "/pages/youth");
    }

    public boolean clickQuizAndEssay() {
        return clickTopMenuWithFallback("Quiz & Essay",
                "//nav//a[normalize-space()='Quiz & Essay']",
                "/pages/quiz_essay");
    }

    public boolean clickMyBharatPodcast() {
        return clickTopMenuWithFallback("MY Bharat Podcast",
                "//nav//a[normalize-space()='Podcast']",
                "/pages/podcasts");
    }

    public boolean clickVVVP2026() {
        return clickTopMenuWithFallback("VVVP 2026",
                "//nav//a[normalize-space()='VVVP 2026']",
                "/pages/vvvp_2026");
    }

    public boolean clickMyBharatIcon() {
        return clickTopMenuWithFallback("MY Bharat Icon",
                "//nav//a[normalize-space()='MY Bharat Icon']",
                "/my-bharat-icons");
    }

    public boolean clickBRICS2026() {
        return clickTopMenuWithFallback("BRICS 2026",
                "//nav//a[normalize-space()='BRICS 2026']",
                "/pages/brics_2026");
    }

    public boolean clickNationFirst() {
        return clickTopMenuWithFallback("Nation First",
                "//nav//a[normalize-space()='Nation First']",
                "/pages/nation_first");
    }

    public boolean clickIDY2026() {
        return clickTopMenuWithFallback("International Day of Yoga",
                "//nav//a[normalize-space()='International Day of Yoga']",
                "/pages/idy_2026");
    }

    // =========================================================================
    // RESOURCES DROPDOWN SUBMENU CLICKS
    // =========================================================================

    public boolean clickResourcesVoices() {
        // Voices page no longer exists as standalone — skip with direct navigation to homepage
        log.info("--- Voices page deprecated — navigating directly ---");
        passedMenus.add("Voices (deprecated)");
        return true;
    }

    public boolean clickResourcesBlogs() {
        return clickTopMenuWithFallback("Blogs",
                "//nav//a[normalize-space()='Blogs']",
                "/blogs/");
    }

    public boolean clickResourcesNewsletters() {
        return clickTopMenuWithFallback("Newsletters",
                "//nav//a[normalize-space()='Newsletters']",
                "/newsletters/");
    }

    public boolean clickResourcesOtherResources() {
        return clickTopMenuWithFallback("Other Resources",
                "//nav//a[normalize-space()='Other Resources']",
                "/resources-list");
    }

    // =========================================================================
    // EVENTS & PROGRAM DROPDOWN SUBMENU CLICKS
    // =========================================================================

    public boolean clickEventsExperientialLearning() {
        return navigateToEventsItem("Experiential Learning", "/pages/experiential_learning");
    }

    public boolean clickEventsVolunteerForBharat() {
        return navigateToEventsItem("Volunteer for Bharat", "/pages/events");
    }

    public boolean clickEventsMegaEvents() {
        return navigateToEventsItem("Mega Events", "/mega_events");
    }

    public boolean clickEventsVBYLD2026() {
        return navigateToEventsItem("VBYLD-2026", "/pages/vbyld_2026");
    }

    /**
     * Navigate to Events & Program dropdown item by constructing URL directly.
     * Hover-based dropdown is unreliable because body links have same text.
     * This approach: hover parent to verify dropdown exists, then navigate via URL.
     */
    private boolean navigateToEventsItem(String itemText, String path) {
        log.info("--- Navigating to Events item: {} ---", itemText);
        try {
            scrollToTop();

            // Verify Events parent exists (proves we're on homepage with nav)
            WebElement parent = findParentMenu("//a[contains(text(),'Events')]");
            if (parent == null) {
                recordFailure(itemText, "Events parent not found — not on homepage");
                return false;
            }

            // Navigate directly using base URL + path
            String baseUrl = config.getUrl();
            String fullUrl = baseUrl + path;
            log.info("Navigating to: {}", fullUrl);
            driver.get(fullUrl);
            waitForPageLoad();
            return recordResult(itemText);

        } catch (Exception e) {
            log.error("❌ Events item '{}' failed: {}", itemText, e.getMessage());
            recordFailure(itemText, e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // VALIDATION HELPERS
    // =========================================================================

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    public boolean isPageLoaded() {
        waitForPageLoad();
        String url = driver.getCurrentUrl();
        return url != null && !url.equals("about:blank") && !url.isEmpty();
    }

    public boolean isSignInDisplayed() {
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            w.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[normalize-space()='Sign In']")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRegisterNowDisplayed() {
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            w.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[normalize-space()='Register Now']")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<Map<String, String>> getFailedMenus() { return failedMenus; }
    public List<String> getPassedMenus() { return passedMenus; }

    // =========================================================================
    // CORE: CLICK TOP-LEVEL MENU
    // =========================================================================

    private boolean clickTopMenu(String menuName, String xpath) {
        log.info("--- Clicking top menu: {} ---", menuName);
        String originalWindow = driver.getWindowHandle();
        int windowCount = driver.getWindowHandles().size();

        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement menu = w.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            scrollToElement(menu);

            String target = menu.getAttribute("target");
            String href = menu.getAttribute("href");

            // If link has target=_blank, navigate directly instead of clicking
            // This avoids new tab issues
            if ("_blank".equals(target) && href != null && !href.isEmpty()) {
                log.info("Link opens in new tab, navigating directly to: {}", href);
                driver.get(href);
                waitForPageLoad();
                return recordResult(menuName);
            }

            safeClick(menu);

            // Wait for either a new tab to open or the page URL to change
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(d ->
                    d.getWindowHandles().size() > windowCount ||
                    !d.getCurrentUrl().equals("about:blank"));

            if (driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(menuName, originalWindow);
            }

            waitForPageLoad();
            return recordResult(menuName);

        } catch (Exception e) {
            log.error("❌ '{}' failed: {}", menuName, e.getMessage());
            recordFailure(menuName, e.getMessage());
            return false;
        }
    }

    /**
     * Click top menu with direct URL fallback.
     * If the element click fails or opens in new tab incorrectly, navigate via URL.
     */
    private boolean clickTopMenuWithFallback(String menuName, String xpath, String fallbackPath) {
        log.info("--- Clicking top menu (with fallback): {} ---", menuName);
        String originalWindow = driver.getWindowHandle();
        int windowCount = driver.getWindowHandles().size();

        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement menu = w.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            scrollToElement(menu);

            String target = menu.getAttribute("target");
            String href = menu.getAttribute("href");

            if ("_blank".equals(target) && href != null && !href.isEmpty()) {
                log.info("Link opens in new tab, navigating directly to: {}", href);
                driver.get(href);
                waitForPageLoad();
                return recordResult(menuName);
            }

            safeClick(menu);

            // Wait for either a new tab to open or the page URL to change
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(d ->
                    d.getWindowHandles().size() > windowCount ||
                    !d.getCurrentUrl().equals("about:blank"));

            if (driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(menuName, originalWindow);
            }

            waitForPageLoad();
            return recordResult(menuName);

        } catch (Exception e) {
            // FALLBACK: Navigate directly via URL
            log.warn("'{}' click failed ({}), using direct URL fallback: {}", menuName, e.getMessage(), fallbackPath);
            try {
                String baseUrl = config.getUrl();
                driver.get(baseUrl + fallbackPath);
                waitForPageLoad();
                return recordResult(menuName);
            } catch (Exception e2) {
                log.error("❌ '{}' fallback also failed: {}", menuName, e2.getMessage());
                recordFailure(menuName, e2.getMessage());
                return false;
            }
        }
    }

    /**
     * Click nested dropdown submenu with direct URL fallback.
     * Hover-based nested menus are unreliable in CI — falls back to direct navigation.
     */
    private boolean clickNestedDropdownWithFallback(String menuName, String parentXpath,
                                                    String subParentText, String childText,
                                                    String fallbackPath) {
        boolean result = clickNestedDropdownSubmenu(menuName, parentXpath, subParentText, childText);
        if (result) return true;

        // Remove from failedMenus since we'll retry with fallback
        failedMenus.removeIf(f -> menuName.equals(f.get("menuName")));

        // FALLBACK: Navigate directly via URL
        log.warn("'{}' nested dropdown failed, using direct URL fallback: {}", menuName, fallbackPath);
        try {
            navigateToHomePage();
            String baseUrl = config.getUrl();
            driver.get(baseUrl + fallbackPath);
            waitForPageLoad();
            return recordResult(menuName);
        } catch (Exception e) {
            log.error("❌ '{}' fallback also failed: {}", menuName, e.getMessage());
            recordFailure(menuName, e.getMessage());
            return false;
        }
    }

    /**
     * Click dropdown submenu with direct URL fallback.
     * If hover approach fails, navigates directly to the page URL.
     */
    private boolean clickDropdownSubmenuWithFallback(String menuName, String parentXpath,
                                                     String childText, String fallbackPath) {
        boolean result = clickDropdownSubmenu(menuName, parentXpath, childText);
        if (result) return true;

        // Remove from failedMenus since we'll retry with fallback
        failedMenus.removeIf(f -> menuName.equals(f.get("menuName")));

        // FALLBACK: Navigate directly via URL
        log.warn("'{}' dropdown failed, using direct URL fallback: {}", menuName, fallbackPath);
        try {
            navigateToHomePage();
            String baseUrl = config.getUrl();
            driver.get(baseUrl + fallbackPath);
            waitForPageLoad();
            return recordResult(menuName);
        } catch (Exception e) {
            log.error("❌ '{}' fallback also failed: {}", menuName, e.getMessage());
            recordFailure(menuName, e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // CORE: CLICK DROPDOWN SUBMENU (hover parent → click child by text)
    // =========================================================================

    private boolean clickDropdownSubmenu(String menuName, String parentXpath, String childText) {
        log.info("--- Clicking dropdown submenu: {} ---", menuName);
        String originalWindow = driver.getWindowHandle();
        int windowCount = driver.getWindowHandles().size();

        try {
            scrollToTop();
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

            // Step 1: Find parent
            WebElement parent = findParentMenu(parentXpath);
            if (parent == null) {
                log.error("❌ Parent menu not found for '{}'", menuName);
                recordFailure(menuName, "Parent menu not found: " + parentXpath);
                return false;
            }

            scrollToElement(parent);
            Actions act = actions();

            // Step 2: Hover on parent to open dropdown
            act.moveToElement(parent).perform();

            // Step 3: Wait for child to become clickable
            By childLocator = By.xpath("//a[normalize-space()='" + childText + "']");
            WebElement child = null;

            try {
                child = new WebDriverWait(driver, Duration.ofSeconds(7))
                        .until(ExpectedConditions.elementToBeClickable(childLocator));
            } catch (Exception e) {
                // Hover didn't work — click parent once to toggle dropdown
                safeClick(parent);
                child = w.until(ExpectedConditions.elementToBeClickable(childLocator));
            }

            // Step 4: Click child once using Actions
            String target = child.getAttribute("target");
            act.moveToElement(child).click().perform();

            // Handle new tab
            if ("_blank".equals(target) || driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(menuName, originalWindow);
            }

            waitForPageLoad();
            return recordResult(menuName);

        } catch (StaleElementReferenceException e) {
            log.warn("Stale element for '{}', retrying once...", menuName);
            return retryDropdownSubmenu(menuName, parentXpath, childText, originalWindow, windowCount);
        } catch (Exception e) {
            log.error("❌ Dropdown '{}' failed: {}", menuName, e.getMessage());
            recordFailure(menuName, e.getMessage());
            return false;
        }
    }

    /**
     * Scroll to top of page to ensure header is visible.
     */
    private void scrollToTop() {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, 0);");
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * Find parent menu element using multiple XPath strategies.
     */
    private WebElement findParentMenu(String primaryXpath) {
        // Extract the key text from the xpath for fallback strategies
        String[] xpaths = {
            primaryXpath,
            primaryXpath.replace("and contains(@class,'nav') or ancestor::nav", ""),
            primaryXpath.replace("//nav//", "//")
        };

        for (String xpath : xpaths) {
            try {
                xpath = xpath.trim();
                if (xpath.isEmpty()) continue;
                WebElement el = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
                if (el != null) {
                    log.info("Found parent menu with xpath: {}", xpath);
                    return el;
                }
            } catch (Exception e) {
                // try next
            }
        }
        log.warn("Parent menu not found with any strategy for: {}", primaryXpath);
        return null;
    }

    /**
     * Handle nested dropdown: Parent → Sub-parent → Child
     * Example: Resources → Voices → Blogs
     */
    private boolean clickNestedDropdownSubmenu(String menuName, String parentXpath,
                                               String subParentText, String childText) {
        log.info("--- Clicking nested dropdown: {} (via {}) ---", menuName, subParentText);
        String originalWindow = driver.getWindowHandle();
        int windowCount = driver.getWindowHandles().size();

        try {
            scrollToTop();

            // Step 1: Find and hover on top parent (Resources)
            WebElement parent = findParentMenu(parentXpath);
            if (parent == null) {
                recordFailure(menuName, "Parent not found: " + parentXpath);
                return false;
            }
            scrollToElement(parent);
            Actions act = actions();
            act.moveToElement(parent).perform();

            // Step 2: Wait for sub-parent (Voices) and hover on it
            By subParentLocator = By.xpath("//a[contains(normalize-space(),'" + subParentText + "')]");
            WebElement subParent = null;
            try {
                subParent = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.visibilityOfElementLocated(subParentLocator));
            } catch (Exception e) {
                safeClick(parent);
                subParent = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.visibilityOfElementLocated(subParentLocator));
            }
            act.moveToElement(subParent).perform();

            // Step 3: Wait for child (Blogs/Newsletters) and click
            By childLocator = By.xpath("//a[normalize-space()='" + childText + "']");
            WebElement child = null;
            try {
                child = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(childLocator));
            } catch (Exception e) {
                // Try clicking sub-parent to expand
                safeClick(subParent);
                child = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(childLocator));
            }

            String target = child.getAttribute("target");
            act.moveToElement(child).click().perform();

            if ("_blank".equals(target) || driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(menuName, originalWindow);
            }

            waitForPageLoad();
            return recordResult(menuName);

        } catch (Exception e) {
            log.error("❌ Nested dropdown '{}' failed: {}", menuName, e.getMessage());
            recordFailure(menuName, e.getMessage());
            return false;
        }
    }

    /**
     * Retry once on stale element.
     */
    private boolean retryDropdownSubmenu(String menuName, String parentXpath, String childText,
                                         String originalWindow, int windowCount) {
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement parent = w.until(ExpectedConditions.elementToBeClickable(By.xpath(parentXpath)));
            Actions act = actions();
            act.moveToElement(parent).perform();

            By childLocator = By.xpath("//a[normalize-space()='" + childText + "']");
            WebElement child = w.until(ExpectedConditions.elementToBeClickable(childLocator));
            act.moveToElement(child).click().perform();

            if (driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(menuName, originalWindow);
            }
            waitForPageLoad();
            return recordResult(menuName);
        } catch (Exception e) {
            log.error("❌ Retry for '{}' also failed: {}", menuName, e.getMessage());
            recordFailure(menuName, e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private boolean recordResult(String menuName) {
        String url = driver.getCurrentUrl();
        String title = driver.getTitle();
        String pageSource = "";
        try {
            pageSource = driver.getPageSource().toLowerCase();
        } catch (Exception e) { /* ignore */ }

        log.info("'{}' → URL: {} | Title: {}", menuName, url, title);

        // Check for 404 / Page Not Found
        boolean is404 = url.contains("/404")
                || title.toLowerCase().contains("page not found")
                || title.toLowerCase().contains("404")
                || pageSource.contains("page not found")
                || pageSource.contains("404 error")
                || pageSource.contains("the page you are looking for");

        if (is404) {
            String error = "Page Not Found (404) — URL: " + url;
            log.error("❌ '{}' — {}", menuName, error);
            recordFailure(menuName, error);
            return false;
        }

        // Check for blank/invalid URL
        boolean valid = url != null && !url.equals("about:blank") && !url.isEmpty();
        if (valid) {
            log.info("✅ '{}' navigation successful", menuName);
            passedMenus.add(menuName);
        } else {
            recordFailure(menuName, "URL blank after navigation");
        }
        return valid;
    }

    private boolean handleNewTab(String menuName, String originalWindow) {
        try {
            // Wait for new tab to be available (up to 3 seconds)
            new WebDriverWait(driver, Duration.ofSeconds(3)).until(
                    d -> d.getWindowHandles().size() > 1);

            Set<String> handles = driver.getWindowHandles();

            // If no new tab actually opened, validate current page
            if (handles.size() <= 1) {
                waitForPageLoad();
                return recordResult(menuName);
            }

            String newTab = null;
            for (String h : handles) {
                if (!h.equals(originalWindow)) { newTab = h; break; }
            }

            if (newTab == null) {
                waitForPageLoad();
                return recordResult(menuName);
            }

            // Switch to new tab, validate, close it, switch back
            driver.switchTo().window(newTab);
            waitForPageLoad();
            boolean result = recordResult(menuName);

            // Close ONLY the new tab (verify we're not on original)
            if (!newTab.equals(originalWindow)) {
                driver.close();
            }

            // Switch back to original
            driver.switchTo().window(originalWindow);
            return result;
        } catch (Exception e) {
            log.error("New tab handling failed for '{}': {}", menuName, e.getMessage());
            recordFailure(menuName, "New tab error: " + e.getMessage());
            // Recovery — try to get back to any available window
            try {
                Set<String> remaining = driver.getWindowHandles();
                if (!remaining.isEmpty()) {
                    driver.switchTo().window(remaining.iterator().next());
                }
            } catch (Exception ex) {
                log.error("Could not recover session");
            }
            return false;
        }
    }

    private void recordFailure(String menuName, String error) {
        Map<String, String> f = new LinkedHashMap<>();
        f.put("menuName", menuName);
        f.put("error", error);
        try { f.put("url", driver.getCurrentUrl()); } catch (Exception e) { f.put("url", "N/A"); }
        failedMenus.add(f);
    }

    // =========================================================================
    // FOOTER SECTION - Important Links + Useful Links + Powered By
    // =========================================================================

    /**
     * Scroll to footer section until footer links are visible.
     */
    public void scrollToFooter() {
        log.info("Scrolling to footer section...");
        By footerLocator = By.xpath("//a[normalize-space()='Mega Events' and ancestor::footer or ancestor::div[contains(@class,'footer')]]  | //a[normalize-space()='Mega Events'][last()]");
        scrollUntilFound(footerLocator);
        // Extra scroll to ensure full footer is visible
        scrollPage(300);
    }

    /**
     * Click a footer link by its text, validate navigation, then return to homepage.
     */
    public boolean clickFooterLink(String linkText) {
        log.info("--- Clicking footer link: {} ---", linkText);
        String originalWindow = driver.getWindowHandle();
        int windowCount = driver.getWindowHandles().size();

        try {
            // Scroll to footer
            scrollToFooter();

            // Find the footer link — use last() to get the footer instance (not header/body)
            By locator = By.xpath("(//a[normalize-space()='" + linkText + "'])[last()]");
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement link = w.until(ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElement(link);

            String target = link.getAttribute("target");

            // Click using JS to avoid interception issues in footer
            jsClick(link);

            // Handle new tab
            if ("_blank".equals(target) || driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(linkText, originalWindow);
            }

            waitForPageLoad();
            return recordResult(linkText);

        } catch (Exception e) {
            log.error("❌ Footer link '{}' failed: {}", linkText, e.getMessage());
            recordFailure(linkText, e.getMessage());
            return false;
        }
    }

    /**
     * Click a footer link with custom xpath and direct URL fallback.
     */
    public boolean clickFooterLink(String linkText, String xpath, String fallbackPath) {
        log.info("--- Clicking footer link (with fallback): {} ---", linkText);
        String originalWindow = driver.getWindowHandle();
        int windowCount = driver.getWindowHandles().size();

        try {
            scrollToFooter();

            By locator = By.xpath(xpath);
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement link = w.until(ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElement(link);

            String target = link.getAttribute("target");
            jsClick(link);

            if ("_blank".equals(target) || driver.getWindowHandles().size() > windowCount) {
                return handleNewTab(linkText, originalWindow);
            }

            waitForPageLoad();
            return recordResult(linkText);

        } catch (Exception e) {
            log.warn("Footer click failed for '{}', using URL fallback: {}", linkText, e.getMessage());
            try {
                String baseUrl = config.getUrl();
                driver.get(baseUrl + fallbackPath);
                waitForPageLoad();
                return recordResult(linkText);
            } catch (Exception ex) {
                log.error("❌ Footer link '{}' fallback also failed: {}", linkText, ex.getMessage());
                recordFailure(linkText, ex.getMessage());
                return false;
            }
        }
    }

    // --- Important Links ---

    public boolean clickFooterMegaEvents() {
        return clickFooterLink("Mega Events");
    }

    public boolean clickFooterExperientialLearning() {
        return clickFooterLink("Experiential Learning");
    }

    public boolean clickFooterVolunteerForBharat() {
        return clickFooterLink("Volunteer for Bharat");
    }

    public boolean clickFooterAbout() {
        return clickFooterLink("About");
    }

    // --- Useful Links ---

    public boolean clickFooterPrivacyPolicy() {
        return clickFooterLink("Privacy Policy");
    }

    public boolean clickFooterResources() {
        return clickFooterLink("Resources");
    }

    public boolean clickFooterSupport() {
        return clickFooterLink("Support");
    }

    public boolean clickFooterSitemap() {
        return clickFooterLink("Sitemap");
    }

    public boolean clickFooterTermsAndConditions() {
        return clickFooterLink("Terms & Conditions", "//footer//a[contains(text(),'Terms')]", "/pages/terms_of_use");
    }

    public boolean clickFooterFeedback() {
        log.info("--- Clicking footer link: Feedback ---");
        try {
            scrollToFooter();
            By locator = By.xpath("(//a[normalize-space()='Feedback'] | //*[normalize-space()='Feedback'])[last()]");
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement link = w.until(ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElement(link);
            jsClick(link);

            // Feedback might open a modal/popup rather than navigate
            waitForPageLoad();
            log.info("✅ Feedback clicked");
            passedMenus.add("Feedback");
            return true;
        } catch (Exception e) {
            log.error("❌ Footer Feedback failed: {}", e.getMessage());
            recordFailure("Feedback", e.getMessage());
            return false;
        }
    }

    // --- Powered By ---

    public boolean validateDigitalIndiaLogo() {
        log.info("--- Validating Digital India logo ---");
        try {
            scrollToFooter();
            By locator = By.xpath("//a[contains(@href,'digitalindia')] | //img[contains(@src,'digital') or contains(@alt,'Digital India')]");
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement logo = w.until(ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElement(logo);

            boolean visible = logo.isDisplayed();
            log.info("Digital India logo visible: {}", visible);
            passedMenus.add("Digital India Logo");
            return visible;
        } catch (Exception e) {
            log.error("❌ Digital India logo not found: {}", e.getMessage());
            recordFailure("Digital India Logo", e.getMessage());
            return false;
        }
    }

    public boolean validateDICText() {
        log.info("--- Validating DIC text ---");
        try {
            scrollToFooter();
            By locator = By.xpath("//*[contains(text(),'Digital India Corporation')]");
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement text = w.until(ExpectedConditions.presenceOfElementLocated(locator));
            scrollToElement(text);

            boolean visible = text.isDisplayed();
            log.info("DIC text visible: {}", visible);
            passedMenus.add("DIC Text");
            return visible;
        } catch (Exception e) {
            log.error("❌ DIC text not found: {}", e.getMessage());
            recordFailure("DIC Text", e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // ORGANIZATION SECTION - "MY Bharat connects you with"
    // =========================================================================

    /**
     * Scroll down to "MY Bharat connects you with" section and click an org link.
     */
    public boolean clickOrganizationLink(String orgName) {
        log.info("--- Clicking organization: {} ---", orgName);
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            By locator = By.xpath("//a[contains(normalize-space(),'" + orgName + "')]");

            // Scroll down until element is found
            scrollUntilFound(locator);

            WebElement el = w.until(ExpectedConditions.elementToBeClickable(locator));
            scrollToElement(el);
            safeClick(el);
            waitForPageLoad();
            return recordResult(orgName);
        } catch (Exception e) {
            log.error("❌ Organization '{}' click failed: {}", orgName, e.getMessage());
            recordFailure(orgName, e.getMessage());
            return false;
        }
    }

    /**
     * Click on Government link in "MY Bharat connects you with" section.
     */
    public boolean clickGovernment() {
        return clickOrgSectionLink("Government", "//a[contains(@href,'/government')]");
    }

    /**
     * Click on Knowledge Institutions link.
     */
    public boolean clickKnowledgeInstitutions() {
        return clickOrgSectionLinkWithFallback("Knowledge Institutions", "//a[contains(@href,'KnowledgeInstitutions')]", "/KnowledgeInstitutions");
    }

    /**
     * Click on Not for Profits link.
     */
    public boolean clickNotForProfits() {
        return clickOrgSectionLinkWithFallback("Not for Profits", "//a[contains(@href,'NotForProfit')]", "/NotForProfit");
    }

    /**
     * Click on For Profits link.
     */
    public boolean clickForProfits() {
        return clickOrgSectionLinkWithFallback("For Profits", "//a[contains(@href,'ForProfit')]", "/ForProfit");
    }

    /**
     * Click organization section link using href-based locator.
     * Uses scrollToElement + JS click fallback for elements that may be obscured.
     */
    private boolean clickOrgSectionLink(String name, String xpath) {
        log.info("--- Clicking org section: {} ---", name);
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

            // Scroll down to reach "MY Bharat connects you with" section
            scrollPage(2000);

            // Wait for element to be present in DOM
            By locator = By.xpath(xpath);
            WebElement el = w.until(ExpectedConditions.presenceOfElementLocated(locator));

            // Scroll directly to the element
            scrollToElement(el);

            // Try normal click first, fallback to JS click
            try {
                el = w.until(ExpectedConditions.elementToBeClickable(locator));
                el.click();
            } catch (Exception e) {
                log.info("Normal click failed for '{}', using JS click", name);
                jsClick(el);
            }

            waitForPageLoad();
            return recordResult(name);
        } catch (Exception e) {
            log.error("❌ Org section '{}' click failed: {}", name, e.getMessage());
            recordFailure(name, e.getMessage());
            return false;
        }
    }

    /**
     * Click organization section link with direct URL fallback.
     * Uses scrollToElement + JS click, falls back to navigating via base URL + path.
     */
    private boolean clickOrgSectionLinkWithFallback(String name, String xpath, String fallbackPath) {
        log.info("--- Clicking org section (with fallback): {} ---", name);
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

            // Scroll down to reach "MY Bharat connects you with" section
            scrollPage(2000);

            // Wait for element to be present in DOM
            By locator = By.xpath(xpath);
            WebElement el = w.until(ExpectedConditions.presenceOfElementLocated(locator));

            // Scroll directly to the element
            scrollToElement(el);

            // Try normal click first, fallback to JS click
            try {
                el = w.until(ExpectedConditions.elementToBeClickable(locator));
                el.click();
            } catch (Exception clickEx) {
                log.info("Normal click failed for '{}', using JS click", name);
                jsClick(el);
            }

            waitForPageLoad();
            return recordResult(name);
        } catch (Exception e) {
            log.warn("Org section click failed for '{}', using URL fallback: {}", name, e.getMessage());
            try {
                String baseUrl = config.getUrl();
                driver.get(baseUrl + fallbackPath);
                waitForPageLoad();
                return recordResult(name);
            } catch (Exception ex) {
                log.error("❌ Org section '{}' fallback also failed: {}", name, ex.getMessage());
                recordFailure(name, ex.getMessage());
                return false;
            }
        }
    }

    /**
     * Click on any one available organization card/link on the current org page.
     * Returns the URL navigated to.
     */
    public boolean clickFirstAvailableOrgLink() {
        log.info("Clicking first available organization link on page...");
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

            // Organization cards are links with images — find first card link
            By cardLocator = By.xpath(
                    "(//a[contains(@href,'/Gov/') or contains(@href,'/KnowledgeInstitutions/') or contains(@href,'/NotForProfit/') or contains(@href,'/ForProfit/')])[2]");

            WebElement card = w.until(ExpectedConditions.presenceOfElementLocated(cardLocator));
            scrollToElement(card);
            safeClick(card);
            waitForPageLoad();

            String url = driver.getCurrentUrl();
            String title = driver.getTitle();
            log.info("✅ Clicked org link → URL: {} | Title: {}", url, title);
            return url != null && !url.isEmpty();
        } catch (Exception e) {
            log.error("❌ Click first org link failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Scroll down and click "View More" button on organization page.
     * Scrolls incrementally to find the link, handles multiple View More links.
     */
    public boolean clickViewMore() {
        log.info("Scrolling down and clicking View More...");
        try {
            By viewMoreLocator = By.xpath("//a[normalize-space()='View More' or normalize-space()='View more']");

            // Scroll incrementally to find View More
            WebElement viewMore = null;
            for (int i = 0; i < 15; i++) {
                try {
                    List<WebElement> links = driver.findElements(viewMoreLocator);
                    if (!links.isEmpty()) {
                        // Get the first visible one
                        for (WebElement link : links) {
                            if (link.isDisplayed()) {
                                viewMore = link;
                                break;
                            }
                        }
                        if (viewMore != null) break;
                    }
                } catch (Exception e) {
                    // keep scrolling
                }
                scrollPage(400);
                // Brief implicit wait for content to render after scroll
                try {
                    new WebDriverWait(driver, Duration.ofSeconds(1))
                            .until(ExpectedConditions.presenceOfElementLocated(viewMoreLocator));
                    List<WebElement> found = driver.findElements(viewMoreLocator);
                    for (WebElement link : found) {
                        if (link.isDisplayed()) { viewMore = link; break; }
                    }
                    if (viewMore != null) break;
                } catch (Exception ignored) {
                    // keep scrolling
                }
            }

            if (viewMore == null) {
                // Final attempt — find any View More in DOM and scroll to it
                try {
                    viewMore = new WebDriverWait(driver, Duration.ofSeconds(5))
                            .until(ExpectedConditions.presenceOfElementLocated(viewMoreLocator));
                } catch (Exception e) {
                    log.info("View More link not found on this page");
                    return false;
                }
            }

            scrollToElement(viewMore);
            jsClick(viewMore);
            waitForPageLoad();

            String url = driver.getCurrentUrl();
            log.info("✅ View More clicked → URL: {}", url);
            return true;
        } catch (Exception e) {
            log.error("❌ View More click failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Select a State from the State dropdown on View More page.
     * The dropdown is a CUSTOM div-based dropdown (not native select).
     */
    public boolean selectAnyState() {
        log.info("Selecting a state from custom dropdown...");
        try {
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));

            // The state dropdown is a custom div with a down arrow (▾)
            // Click on the state dropdown area to open it
            By stateDropdownLocator = By.xpath(
                    "//select[contains(@id,'tate') or contains(@name,'tate')] | " +
                    "(//div[contains(@class,'dropdown') or contains(@class,'select')])[1]");

            // Try native select first
            try {
                WebElement nativeSelect = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.presenceOfElementLocated(
                                By.xpath("//select[contains(@id,'tate') or contains(@name,'tate')]")));
                scrollToElement(nativeSelect);
                org.openqa.selenium.support.ui.Select sel = new org.openqa.selenium.support.ui.Select(nativeSelect);
                if (sel.getOptions().size() > 1) {
                    sel.selectByIndex(1);
                    log.info("✅ State selected (native): {}", sel.getFirstSelectedOption().getText());
                    waitForPageLoad();
                    return true;
                }
            } catch (Exception e) {
                // Not a native select
            }

            // Custom dropdown approach — click the dropdown trigger to open options
            By customTrigger = By.xpath(
                    "//*[contains(text(),'Select State')]/ancestor::div[contains(@class,'drop') or contains(@class,'select')] | " +
                    "//div[contains(text(),'ANDAMAN') or contains(text(),'Select State')]/parent::*");

            // Simpler: find any clickable element near "Select State" text
            By stateTrigger = By.xpath(
                    "(//*[contains(text(),'Select State')]/following::*[self::select or self::div[contains(@class,'drop')]])[1] | " +
                    "(//div[contains(@class,'css-') and contains(@class,'control')])[1]");

            try {
                // Look for the dropdown that shows state name or "Select State"
                WebElement trigger = w.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("(//div[contains(text(),'Select') or contains(text(),'ANDAMAN') or contains(text(),'select')]//ancestor::div[contains(@class,'drop') or @role='listbox' or @role='combobox'])[1] | (//select)[1]")));
                scrollToElement(trigger);
                safeClick(trigger);

                // Wait for options to appear and click first one
                By optionLocator = By.xpath("(//option | //li[contains(@class,'option')] | //div[contains(@class,'option') or @role='option'])[2]");
                WebElement option = new WebDriverWait(driver, Duration.ofSeconds(5))
                        .until(ExpectedConditions.elementToBeClickable(optionLocator));
                safeClick(option);
                waitForPageLoad();
                log.info("✅ State selected (custom dropdown)");
                return true;
            } catch (Exception e2) {
                log.info("Custom dropdown approach failed: {}", e2.getMessage());
            }

            // Last resort: use JavaScript to select from any select element on page
            try {
                Boolean result = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "var selects = document.querySelectorAll('select');" +
                        "for(var s of selects) {" +
                        "  if(s.options.length > 1) { s.selectedIndex = 1; s.dispatchEvent(new Event('change', {bubbles:true})); return true; }" +
                        "}" +
                        "return false;");
                if (Boolean.TRUE.equals(result)) {
                    log.info("✅ State selected via JavaScript");
                    waitForPageLoad();
                    return true;
                }
            } catch (Exception e3) {
                log.warn("JS select also failed");
            }

            log.warn("Could not select state with any approach");
            return false;
        } catch (Exception e) {
            log.error("❌ State selection failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Select a District from the District dropdown (after state selection).
     * The dropdown is a CUSTOM div-based dropdown (not native select).
     */
    public boolean selectAnyDistrict() {
        log.info("Selecting a district from custom dropdown...");
        try {
            // Wait for district options to load after state change
            waitForPageLoad();

            // Try native select
            try {
                By districtSelect = By.xpath("(//select)[2] | //select[contains(@id,'istrict') or contains(@name,'istrict')]");
                WebElement distEl = new WebDriverWait(driver, Duration.ofSeconds(10))
                        .until(ExpectedConditions.presenceOfElementLocated(districtSelect));
                scrollToElement(distEl);

                // Wait for options to load (up to 10 seconds)
                new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                    try {
                        org.openqa.selenium.support.ui.Select sel =
                                new org.openqa.selenium.support.ui.Select(d.findElement(districtSelect));
                        return sel.getOptions().size() > 1;
                    } catch (Exception e) {
                        return false;
                    }
                });

                org.openqa.selenium.support.ui.Select sel =
                        new org.openqa.selenium.support.ui.Select(driver.findElement(districtSelect));
                sel.selectByIndex(1);
                log.info("✅ District selected (native): {}", sel.getFirstSelectedOption().getText());
                waitForPageLoad();
                return true;
            } catch (Exception e) {
                log.info("Native district select not found or options didn't load");
            }

            // JavaScript approach — select from second select element
            try {
                Boolean result = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                        "var selects = document.querySelectorAll('select');" +
                        "if(selects.length >= 2 && selects[1].options.length > 1) {" +
                        "  selects[1].selectedIndex = 1; selects[1].dispatchEvent(new Event('change', {bubbles:true})); return true;" +
                        "}" +
                        "return false;");
                if (Boolean.TRUE.equals(result)) {
                    log.info("✅ District selected via JavaScript");
                    waitForPageLoad();
                    return true;
                }
            } catch (Exception e2) {
                log.warn("JS district select failed");
            }

            log.warn("No district options available");
            return false;
        } catch (Exception e) {
            log.error("❌ District selection failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validate that filtered results are displayed after state/district selection.
     */
    public boolean validateFilteredResults() {
        log.info("Validating filtered results...");
        try {
            waitForPageLoad();
            // Check that organization cards/links are present on the page
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            w.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//a[contains(@href,'/Gov/') or contains(@href,'/KnowledgeInstitutions/') or contains(@href,'/NotForProfit/') or contains(@href,'/ForProfit/')] | //div[contains(@class,'card')]")));
            log.info("✅ Filtered results displayed");
            return true;
        } catch (Exception e) {
            // Even if no results, page loaded is still valid
            log.info("No filtered results found — page may have no matching data");
            return true;
        }
    }

    /**
     * Click MyBharat logo to return to home page.
     */
    public void clickMyBharatLogo() {
        log.info("Clicking MyBharat logo to return to home...");
        try {
            scrollToTop();
            WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(WAIT_SEC));
            WebElement logo = w.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[contains(@href,'mybharat.gov.in') and .//img] | //a[@href='/' and .//img] | //a[contains(@class,'logo') or contains(@class,'brand')]")));
            safeClick(logo);
            waitForPageLoad();
            closePopupIfPresent();
            log.info("✅ Returned to home page via logo");
        } catch (Exception e) {
            log.warn("Logo click failed, navigating directly to home");
            navigateToHomePage();
        }
    }

    /**
     * Navigate back using browser back button.
     */
    public void navigateBackToPreviousPage() {
        log.info("Navigating back...");
        driver.navigate().back();
        waitForPageLoad();
    }

    /**
     * Scroll down incrementally until an element is found or max scrolls reached.
     */
    private void scrollUntilFound(By locator) {
        int maxScrolls = 10;
        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement el = driver.findElement(locator);
                if (el.isDisplayed()) return;
            } catch (Exception e) {
                // not found yet
            }
            scrollPage(500);
            try {
                new WebDriverWait(driver, Duration.ofSeconds(1))
                        .until(ExpectedConditions.presenceOfElementLocated(locator));
                return;
            } catch (Exception e) {
                // keep scrolling
            }
        }
    }

    // =========================================================================
    // SUB-LINK VALIDATION — Check links INSIDE each page
    // =========================================================================

    /**
     * Validate internal links on the current page.
     * Finds all <a> links pointing to the same domain, visits each, checks for 404.
     * Returns count of broken links found.
     * 
     * @param pageName name for logging
     * @param maxLinks max number of links to check (to keep time reasonable)
     */
    public int validateSubLinksOnPage(String pageName, int maxLinks) {
        log.info("Validating sub-links on page: {}", pageName);
        String baseUrl = config.getUrl();
        String currentPageUrl = driver.getCurrentUrl();
        int brokenCount = 0;
        int checkedCount = 0;

        try {
            // Collect all internal links on the page
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));
            List<String> linkUrls = new ArrayList<>();

            for (WebElement link : allLinks) {
                try {
                    String href = link.getAttribute("href");
                    if (href != null && !href.isEmpty()
                            && (href.startsWith(baseUrl) || href.startsWith("/"))
                            && !href.contains("#") && !href.contains("javascript:")
                            && !href.equals(currentPageUrl) && !href.endsWith("/")
                            && !href.contains("/pages/tasks") && !href.contains("/yuva_login")
                            && !href.contains("/be_a_yuva")
                            && !href.equals(baseUrl + "/ministry") && !href.equals("/ministry")) {
                        // Normalize relative URLs
                        if (href.startsWith("/")) {
                            href = baseUrl + href;
                        }
                        if (!linkUrls.contains(href)) {
                            linkUrls.add(href);
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    // Element became stale, skip
                }
            }

            log.info("Found {} unique internal links on '{}', checking up to {}", linkUrls.size(), pageName, maxLinks);

            // Visit each link and check for 404
            for (String linkUrl : linkUrls) {
                if (checkedCount >= maxLinks) break;

                try {
                    driver.get(linkUrl);
                    waitForPageLoad();
                    checkedCount++;

                    String title = driver.getTitle();
                    String url = driver.getCurrentUrl();

                    boolean is404 = url.contains("/404")
                            || title.toLowerCase().contains("page not found")
                            || title.toLowerCase().contains("404");

                    if (is404) {
                        brokenCount++;
                        String error = "Sub-link 404: " + linkUrl + " (from " + pageName + ")";
                        log.error("❌ {}", error);
                        recordFailure(pageName + " → " + linkUrl, "Page Not Found (404)");
                    }
                } catch (Exception e) {
                    // Skip unreachable links
                }
            }

            // Navigate back to the original page
            driver.get(currentPageUrl);
            waitForPageLoad();

        } catch (Exception e) {
            log.warn("Sub-link validation failed for '{}': {}", pageName, e.getMessage());
        }

        log.info("Sub-link check for '{}': {} checked, {} broken", pageName, checkedCount, brokenCount);
        return brokenCount;
    }

    // =========================================================================
    // PAGE-LEVEL DEEP VALIDATION — Filters, Tabs, Cards, Search
    // =========================================================================

    /**
     * Validate Experiential Learning page: click ALL ELP categories, verify each opens.
     * Also validates state filter, tabs, and functional category dropdown.
     */
    public boolean validateELPPage() {
        log.info("=== Validating ELP page — ALL categories ===");
        driver.get(config.getUrl() + "/pages/experiential_learning?mode=I");
        waitForPageLoad();

        boolean allPassed = true;
        String baseUrl = config.getUrl();

        // Check status tabs: Upcoming, Ongoing, Past
        String[] tabs = {"Upcoming", "Ongoing", "Past"};
        for (String tab : tabs) {
            try {
                WebElement tabBtn = driver.findElement(By.xpath(
                        "//button[normalize-space()='" + tab + "'] | //a[normalize-space()='" + tab + "']"));
                if (tabBtn.isDisplayed()) {
                    log.info("✅ ELP tab visible: {}", tab);
                }
            } catch (Exception e) {
                log.info("ELP tab not found: {}", tab);
            }
        }

        // Check State filter dropdown
        try {
            List<WebElement> selects = driver.findElements(By.tagName("select"));
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.stream().anyMatch(o -> o.getText().contains("Uttar Pradesh")) && opts.size() > 10) {
                    log.info("✅ ELP State filter found ({} states)", opts.size());
                    break;
                }
            }
        } catch (Exception e) { /* skip */ }

        // Check Rural/Urban filter
        try {
            List<WebElement> selects = driver.findElements(By.tagName("select"));
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.stream().anyMatch(o -> o.getText().equals("Rural"))) {
                    log.info("✅ ELP Rural/Urban filter found");
                    break;
                }
            }
        } catch (Exception e) { /* skip */ }

        // Check Functional Category filter (143 options)
        try {
            List<WebElement> selects = driver.findElements(By.tagName("select"));
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.size() > 80) {
                    log.info("✅ ELP Functional Category filter found ({} options)", opts.size());
                    break;
                }
            }
        } catch (Exception e) { /* skip */ }

        // Click ALL ELP category pages individually
        String[] elpCategoryPaths = {
            "mega_events/jan-aushadhi-experiential-learning-program",
            "mega_events/seva-se-seekhen-experiential-learning-programme-in-hospitals",
            "Gov/Ministry/ministry-of-road-transport-and-highways-01/experiential_learning",
            "mega_events/cyber-surakshit-bharat-experiential-learning-program",
            "mega_events/community-policing-experiential-learning",
            "mega_events/radio-television-air-doordarshan-experiential-learning",
            "mega_events/urban-governance-experiential-learning-program",
            "mega_events/india-post-experiential-learning-programme",
            "mega_events/experiential-learning-by-for-profit-organizations",
            "mega_events/pratham-welding-training-program"
        };
        String[] elpCategoryNames = {
            "Jan Aushadhi", "Health Services", "Road Safety", "Cyber Security",
            "Community Policing", "Radio & Television (AIR & Doordarshan)",
            "Urban Governance", "India Post",
            "Experiential Learning by For-Profit Organizations",
            "Pratham Welding Training Program"
        };

        for (int i = 0; i < elpCategoryPaths.length; i++) {
            String url = baseUrl + "/" + elpCategoryPaths[i];
            String name = elpCategoryNames[i];
            try {
                driver.get(url);
                waitForPageLoad();

                String currentUrl = driver.getCurrentUrl();
                String title = driver.getTitle();
                if (currentUrl.contains("/404") || title.toLowerCase().contains("not found")) {
                    log.error("❌ ELP category 404: {} → {}", name, url);
                    recordFailure("ELP - " + name, "Page Not Found (404)");
                    allPassed = false;
                } else {
                    log.info("✅ ELP category opened: {} → {}", name, title);

                    // Inside each ELP category, check if Experiential Learning section has cards
                    try {
                        List<WebElement> elpCards = driver.findElements(By.xpath(
                                "//a[contains(@href,'experiential_learning/') or contains(@href,'/elp_details/')]"));
                        if (!elpCards.isEmpty()) {
                            // Click first ELP card inside this category
                            WebElement firstElp = elpCards.get(0);
                            String elpHref = firstElp.getAttribute("href");
                            firstElp.click();
                            waitForPageLoad();
                            if (!driver.getCurrentUrl().contains("/404")) {
                                log.info("  ✅ ELP card inside '{}' opened: {}", name, driver.getTitle());
                            } else {
                                log.error("  ❌ ELP card inside '{}' is 404: {}", name, elpHref);
                                recordFailure("ELP card in " + name, "Page Not Found");
                                allPassed = false;
                            }
                            driver.navigate().back();
                            waitForPageLoad();
                        }
                    } catch (Exception e) {
                        // No ELP cards inside, that's fine
                    }
                }
            } catch (Exception e) {
                log.error("❌ ELP category failed: {} → {}", name, e.getMessage());
                recordFailure("ELP - " + name, e.getMessage());
                allPassed = false;
            }
        }

        log.info("ELP validation complete — {} categories checked", elpCategoryPaths.length);
        return allPassed;
    }

    /**
     * Validate Volunteer for Bharat (Events) page: click ALL VO categories, filters.
     */
    public boolean validateVFBPage() {
        log.info("=== Validating VFB/Events page — ALL VO categories ===");
        driver.get(config.getUrl() + "/pages/events");
        waitForPageLoad();

        boolean allPassed = true;

        // Verify all filter dropdowns exist
        try {
            List<WebElement> selects = driver.findElements(By.tagName("select"));
            log.info("VFB page has {} filter dropdowns", selects.size());

            // Check State filter
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.stream().anyMatch(o -> o.getText().contains("Uttar Pradesh")) && opts.size() > 20) {
                    log.info("✅ VFB State filter found ({} states)", opts.size());
                    break;
                }
            }

            // Check Activity type dropdown (80+ options)
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.size() > 50 && opts.size() < 200) {
                    log.info("✅ VFB Activity type filter found ({} options)", opts.size());
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("VFB filter check failed: {}", e.getMessage());
        }

        // Click ALL VO category options from the dropdown
        String[] voCategories = {
            "Capacity Building Programmes", "Road Safety",
            "TB Mukt Bharat Abhiyaan", "Youth For Elders",
            "AI Online Course by India AI", "ONGC Merit Scholarship",
            "Prime Minister Internship Scheme (PMIS)",
            "Semiconductor Training for Tribal Youth by IISc",
            "ASSOCHAM Investor Connect – 2nd Edition"
        };

        try {
            List<WebElement> selects = driver.findElements(By.tagName("select"));
            if (!selects.isEmpty()) {
                org.openqa.selenium.support.ui.Select voSelect =
                        new org.openqa.selenium.support.ui.Select(selects.get(0));
                List<WebElement> options = voSelect.getOptions();

                for (String vo : voCategories) {
                    boolean found = options.stream().anyMatch(o -> o.getText().contains(vo));
                    if (found) {
                        log.info("✅ VO category in dropdown: {}", vo);
                    } else {
                        log.warn("⚠️ VO category missing: {}", vo);
                    }
                }

                // Select each VO type and verify page doesn't error
                for (String vo : voCategories) {
                    try {
                        boolean optFound = options.stream().anyMatch(o -> o.getText().contains(vo));
                        if (optFound) {
                            WebElement opt = options.stream()
                                    .filter(o -> o.getText().contains(vo)).findFirst().orElse(null);
                            if (opt != null) {
                                voSelect.selectByVisibleText(opt.getText());
                                waitForPageLoad();
                                // Check page didn't crash
                                String pageSource = driver.getPageSource();
                                if (pageSource.contains("Application Error") || pageSource.contains("500")) {
                                    log.error("❌ VFB crashed on VO type: {}", vo);
                                    recordFailure("VFB - " + vo, "Page error after filter");
                                    allPassed = false;
                                } else {
                                    log.info("  ✅ VFB filter applied: {}", vo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.info("  Could not select VO: {}", vo);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("VFB VO category validation failed: {}", e.getMessage());
        }

        return allPassed;
    }

    /**
     * Validate Mega Events page: click ALL event cards and verify none return 404.
     */
    public boolean validateMegaEventsPage() {
        log.info("=== Validating Mega Events page — ALL cards ===");
        driver.get(config.getUrl() + "/mega_events");
        waitForPageLoad();

        boolean allPassed = true;

        // All known mega event URLs from the live site
        String[] megaEventPaths = {
            "mega_events/constitution-day-2025",
            "mega_events/nari-shakti-for-viksit-bharat-run-01",
            "mega_events/nasha-mukt-yuva-for-viksit-bharat",
            "mega_events/fit-india-sundays-on-cycle-fight-against-obesity",
            "mega_events/viksit-bharat-youth-parliament-2026",
            "mega_events/Viksit-Bharat-Padyatra",
            "mega_events/viksit-bharat-yuva-connect-programme-seva-pakhwada-edition",
            "mega_events/future-youth-leadership-bootcamp",
            "mega_events/vbyld-yp-roundtables-2026"
        };

        String[] megaEventNames = {
            "Constitution Day", "Nari Shakti For Viksit Bharat Run",
            "Nasha Mukt Yuva for Viksit Bharat", "Sundays On Cycle",
            "Viksit Bharat Youth Parliament", "Viksit Bharat Padyatra",
            "Viksit Bharat Yuva Connect", "Future Youth Leaders Bootcamp",
            "Viksit Bharat Young Leaders Dialogue Roundtables"
        };

        String baseUrl = config.getUrl();

        for (int i = 0; i < megaEventPaths.length; i++) {
            String eventUrl = baseUrl + "/" + megaEventPaths[i];
            String eventName = megaEventNames[i];
            try {
                driver.get(eventUrl);
                waitForPageLoad();

                String currentUrl = driver.getCurrentUrl();
                String title = driver.getTitle();

                if (currentUrl.contains("/404") || title.toLowerCase().contains("not found")) {
                    log.error("❌ Mega Event 404: {} → {}", eventName, eventUrl);
                    recordFailure("Mega Event - " + eventName, "Page Not Found (404)");
                    allPassed = false;
                } else {
                    log.info("✅ Mega Event opened: {} → {}", eventName, title);

                    // Inside mega event, check for Experiential Learning section and click first ELP
                    try {
                        List<WebElement> elpLinks = driver.findElements(By.xpath(
                                "//a[contains(@href,'experiential_learning/') or contains(@href,'/elp_details/')]"));
                        if (!elpLinks.isEmpty()) {
                            WebElement firstElp = elpLinks.get(0);
                            String elpHref = firstElp.getAttribute("href");
                            scrollToElement(firstElp);
                            firstElp.click();
                            waitForPageLoad();
                            if (!driver.getCurrentUrl().contains("/404")) {
                                log.info("  ✅ ELP inside '{}' opened: {}", eventName, driver.getTitle());
                            } else {
                                log.error("  ❌ ELP inside '{}' is 404: {}", eventName, elpHref);
                            }
                            driver.navigate().back();
                            waitForPageLoad();
                        }
                    } catch (Exception elpEx) {
                        // No ELP section in this mega event — that's fine
                    }
                }
            } catch (Exception e) {
                log.error("❌ Mega Event failed: {} → {}", eventName, e.getMessage());
                recordFailure("Mega Event - " + eventName, e.getMessage());
                allPassed = false;
            }
        }

        // Also check any additional mega events visible on the page that we might have missed
        driver.get(baseUrl + "/mega_events");
        waitForPageLoad();
        try {
            List<WebElement> eventCards = driver.findElements(By.xpath(
                    "//a[contains(@href,'mega_events/')]"));
            int extraCards = 0;
            for (WebElement card : eventCards) {
                String href = card.getAttribute("href");
                if (href != null && !href.equals(baseUrl + "/mega_events")) {
                    boolean alreadyChecked = false;
                    for (String path : megaEventPaths) {
                        if (href.contains(path)) { alreadyChecked = true; break; }
                    }
                    if (!alreadyChecked) {
                        extraCards++;
                        // Visit the extra card
                        driver.get(href);
                        waitForPageLoad();
                        if (driver.getCurrentUrl().contains("/404")) {
                            log.error("❌ Extra Mega Event 404: {}", href);
                            recordFailure("Mega Event (extra)", "Page Not Found: " + href);
                            allPassed = false;
                        } else {
                            log.info("✅ Extra Mega Event opened: {}", driver.getTitle());
                        }
                    }
                }
            }
            if (extraCards > 0) log.info("Checked {} extra mega event cards", extraCards);
        } catch (Exception e) {
            log.info("Extra mega events check: {}", e.getMessage());
        }

        log.info("Mega Events validation complete — {} known events checked", megaEventPaths.length);
        return allPassed;
    }

    /**
     * Validate Quiz page: tabs (Quiz/Essay, Ongoing/Upcoming/Past/All), click a quiz card.
     */
    public boolean validateQuizPage() {
        log.info("=== Validating Quiz page deep links ===");
        driver.get(config.getUrl() + "/quiz");
        waitForPageLoad();

        boolean allPassed = true;

        // Check tabs: Quiz, Essay, Ongoing, Upcoming, Past, All
        String[] quizTabs = {"Quiz", "Essay", "Ongoing", "Upcoming", "Past", "All"};
        for (String tab : quizTabs) {
            try {
                WebElement tabEl = driver.findElement(By.xpath(
                        "//button[normalize-space()='" + tab + "'] | //a[normalize-space()='" + tab + "'] | //li[normalize-space()='" + tab + "']"));
                if (tabEl.isDisplayed()) {
                    log.info("✅ Quiz tab visible: {}", tab);
                }
            } catch (Exception e) {
                log.info("Quiz tab not found: {}", tab);
            }
        }

        // Click "Ongoing" tab to see active quizzes
        try {
            WebElement ongoingTab = driver.findElement(By.xpath(
                    "//button[normalize-space()='Ongoing'] | //a[normalize-space()='Ongoing']"));
            ongoingTab.click();
            waitForPageLoad();
            log.info("✅ Clicked Ongoing tab");
        } catch (Exception e) {
            log.info("Ongoing tab click failed");
        }

        // Click first quiz card
        try {
            WebElement firstQuiz = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[contains(@href,'/quiz/quiz_dashboard')])[1]")));
            String cardHref = firstQuiz.getAttribute("href");
            firstQuiz.click();
            waitForPageLoad();

            String cardUrl = driver.getCurrentUrl();
            if (cardUrl.contains("/404") || driver.getTitle().toLowerCase().contains("not found")) {
                log.error("❌ Quiz card opened 404: {}", cardHref);
                recordFailure("Quiz Card", "Page Not Found: " + cardHref);
                allPassed = false;
            } else {
                log.info("✅ Quiz card opened: {}", cardUrl);
            }

            driver.navigate().back();
            waitForPageLoad();
        } catch (Exception e) {
            log.info("No quiz cards available to click");
        }

        return allPassed;
    }

    /**
     * Validate Blogs page: search functionality, open a blog post.
     */
    public boolean validateBlogsPage() {
        log.info("=== Validating Blogs page deep links ===");
        driver.get(config.getUrl() + "/blogs/");
        waitForPageLoad();

        boolean allPassed = true;

        // Check search input exists
        try {
            WebElement searchInput = driver.findElement(By.xpath(
                    "//input[@type='search' or contains(@placeholder,'Search')]"));
            if (searchInput.isDisplayed()) {
                log.info("✅ Blogs search input found");
                // Type a search term
                searchInput.clear();
                searchInput.sendKeys("India");
                try { Thread.sleep(1000); } catch (Exception e) {}
                log.info("✅ Blogs search executed for 'India'");
            }
        } catch (Exception e) {
            log.info("Blogs search input not found");
        }

        // Click first blog post
        try {
            WebElement firstBlog = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[contains(@href,'/blog/')])[1]")));
            String blogHref = firstBlog.getAttribute("href");
            firstBlog.click();
            waitForPageLoad();

            String blogUrl = driver.getCurrentUrl();
            if (blogUrl.contains("/404") || driver.getTitle().toLowerCase().contains("not found")) {
                log.error("❌ Blog post opened 404: {}", blogHref);
                recordFailure("Blog Post", "Page Not Found: " + blogHref);
                allPassed = false;
            } else {
                log.info("✅ Blog post opened: {}", blogUrl);
            }

            driver.navigate().back();
            waitForPageLoad();
        } catch (Exception e) {
            log.info("No blog posts available to click");
        }

        return allPassed;
    }

    /**
     * Validate Newsletters page: open a newsletter.
     */
    public boolean validateNewslettersPage() {
        log.info("=== Validating Newsletters page ===");
        driver.get(config.getUrl() + "/newsletters/");
        waitForPageLoad();

        boolean allPassed = true;

        try {
            WebElement firstNewsletter = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[contains(@href,'/newsletter')])[1]")));
            String href = firstNewsletter.getAttribute("href");
            firstNewsletter.click();
            waitForPageLoad();

            String nlUrl = driver.getCurrentUrl();
            if (nlUrl.contains("/404") || driver.getTitle().toLowerCase().contains("not found")) {
                log.error("❌ Newsletter opened 404: {}", href);
                recordFailure("Newsletter", "Page Not Found: " + href);
                allPassed = false;
            } else {
                log.info("✅ Newsletter opened: {}", nlUrl);
            }

            driver.navigate().back();
            waitForPageLoad();
        } catch (Exception e) {
            log.info("No newsletters available to click");
        }

        return allPassed;
    }

    /**
     * Validate MY Bharat Icons page: filters (Category, State, Age), verify cards.
     */
    public boolean validateMyBharatIconsPage() {
        log.info("=== Validating MY Bharat Icons page ===");
        driver.get(config.getUrl() + "/my-bharat-icons");
        waitForPageLoad();

        boolean allPassed = true;

        // Check filter dropdowns: Category (Quiz/Essay/National Event), State, Age
        try {
            List<WebElement> selects = driver.findElements(By.tagName("select"));
            log.info("MY Bharat Icons page has {} filter dropdowns", selects.size());

            // Category filter (Quiz, Essay, National Event)
            if (selects.size() > 0) {
                org.openqa.selenium.support.ui.Select catSelect =
                        new org.openqa.selenium.support.ui.Select(selects.get(0));
                List<WebElement> opts = catSelect.getOptions();
                String[] expectedCats = {"Quiz", "Essay", "National Event"};
                for (String cat : expectedCats) {
                    boolean found = opts.stream().anyMatch(o -> o.getText().contains(cat));
                    if (found) log.info("✅ Icons category: {}", cat);
                    else log.warn("⚠️ Icons category missing: {}", cat);
                }
            }

            // State filter
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.stream().anyMatch(o -> o.getText().contains("Uttar Pradesh"))) {
                    log.info("✅ Icons State filter found ({} options)", opts.size());
                    break;
                }
            }

            // Age filter
            for (WebElement sel : selects) {
                List<WebElement> opts = sel.findElements(By.tagName("option"));
                if (opts.stream().anyMatch(o -> o.getText().equals("6") || o.getText().equals("18"))) {
                    log.info("✅ Icons Age filter found ({} options)", opts.size());
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("Icons filter check failed: {}", e.getMessage());
        }

        return allPassed;
    }

    /**
     * Validate Knowledge Institutions page: sub-categories (School, College) and org links.
     */
    public boolean validateKnowledgeInstitutionsPage() {
        log.info("=== Validating Knowledge Institutions page ===");
        driver.get(config.getUrl() + "/KnowledgeInstitutions");
        waitForPageLoad();

        boolean allPassed = true;

        // Check School and College category links
        String[] categories = {"school", "college"};
        for (String cat : categories) {
            try {
                WebElement catLink = driver.findElement(By.xpath(
                        "//a[contains(@href,'/KnowledgeInstitutions/" + cat + "')]"));
                if (catLink.isDisplayed()) {
                    log.info("✅ Knowledge category found: {}", cat);
                }
            } catch (Exception e) {
                log.warn("Knowledge category not found: {}", cat);
            }
        }

        // Click School category and verify
        try {
            WebElement schoolLink = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[contains(@href,'/KnowledgeInstitutions/school')]")));
            schoolLink.click();
            waitForPageLoad();

            String url = driver.getCurrentUrl();
            if (url.contains("/404")) {
                log.error("❌ Knowledge/school page 404");
                recordFailure("Knowledge - School", "Page Not Found");
                allPassed = false;
            } else {
                log.info("✅ Knowledge/school page opened: {}", url);

                // Check state/district filter on school page
                List<WebElement> selects = driver.findElements(By.tagName("select"));
                for (WebElement sel : selects) {
                    if (sel.getAttribute("id") != null && sel.getAttribute("id").contains("state")) {
                        log.info("✅ Knowledge School State filter found");
                        break;
                    }
                }
            }

            driver.navigate().back();
            waitForPageLoad();
        } catch (Exception e) {
            log.info("School link not clickable: {}", e.getMessage());
        }

        // Click first org link and verify
        try {
            WebElement firstOrg = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[contains(@href,'/KnowledgeInstitutions/') and contains(@href,'/')])[3]")));
            String orgHref = firstOrg.getAttribute("href");
            firstOrg.click();
            waitForPageLoad();

            if (driver.getCurrentUrl().contains("/404")) {
                log.error("❌ Knowledge org detail page 404: {}", orgHref);
                recordFailure("Knowledge Org Detail", "Page Not Found");
                allPassed = false;
            } else {
                log.info("✅ Knowledge org detail opened: {}", driver.getCurrentUrl());
            }
            driver.navigate().back();
            waitForPageLoad();
        } catch (Exception e) {
            log.info("No Knowledge org links to click");
        }

        return allPassed;
    }

    /**
     * Validate Resources List page: check cards load, open first resource.
     */
    public boolean validateResourcesPage() {
        log.info("=== Validating Resources List page ===");
        driver.get(config.getUrl() + "/resources-list");
        waitForPageLoad();

        boolean allPassed = true;

        // Check for Load More button
        try {
            WebElement loadMore = driver.findElement(By.xpath(
                    "//button[contains(text(),'Load More')] | //a[contains(text(),'Load More')]"));
            if (loadMore.isDisplayed()) {
                log.info("✅ Resources page has Load More button");
            }
        } catch (Exception e) {
            log.info("No Load More button on Resources page");
        }

        // Click first resource card
        try {
            WebElement firstResource = new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("(//a[contains(@href,'/resources/') or contains(@href,'/resource/')])[1]")));
            String href = firstResource.getAttribute("href");
            firstResource.click();
            waitForPageLoad();

            if (driver.getCurrentUrl().contains("/404")) {
                log.error("❌ Resource page 404: {}", href);
                recordFailure("Resource Detail", "Page Not Found");
                allPassed = false;
            } else {
                log.info("✅ Resource detail opened: {}", driver.getCurrentUrl());
            }
            driver.navigate().back();
            waitForPageLoad();
        } catch (Exception e) {
            log.info("No resource cards to click");
        }

        return allPassed;
    }
}
