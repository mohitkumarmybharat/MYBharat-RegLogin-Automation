package com.mybharat.tests.youth;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.Retry;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.youth.PublicPage;

/**
 * PublicPageTest - Header Navigation Flow for https://mybharat.gov.in
 *
 * Sequence:
 *   1. Youth
 *   2. Quiz & Essay
 *   3. Resources → Voices, Blogs, Newsletters, Other Resources
 *   4. Events & Program → Experiential Learning, Volunteer for Bharat, Mega Events, VBYLD-2026
 *   5. MY Bharat Podcast
 *   6. VVVP 2026
 *
 * Run:
 *   mvn test "-Denv=prod" "-Dbrowser=chrome" "-Dsurefire.suiteXmlFiles=testSuites/testng-public-page.xml"
 */
@Listeners(TestListeners.class)
public class PublicPageTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(PublicPageTest.class);
    private PublicPage publicPage;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        publicPage = new PublicPage(driver);
    }

    // =========================================================================
    // STEP 1: LAUNCH & VALIDATE HOMEPAGE
    // =========================================================================

    @Test(priority = 1, groups = {"smoke", "header"}, retryAnalyzer = Retry.class)
    public void launchAndValidateHomepage() {
        log.info("=== Step 1: Launch homepage ===");
        publicPage.navigateToHomePage();

        String url = publicPage.getCurrentUrl();
        Assert.assertTrue(url.contains("mybharat"), "Should be on mybharat.gov.in. URL: " + url);
        Assert.assertTrue(publicPage.isSignInDisplayed(), "Sign In button should be visible");
        Assert.assertTrue(publicPage.isRegisterNowDisplayed(), "Register Now button should be visible");

        log.info("✅ Homepage validated — URL: {} | Title: {}", url, publicPage.getPageTitle());
    }

    // =========================================================================
    // STEP 2: YOUTH MENU
    // =========================================================================

    @Test(priority = 2, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickYouthMenu() {
        log.info("=== Step 2: Click Youth ===");
        boolean result = publicPage.clickYouth();
        Assert.assertTrue(result, "Youth menu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Youth page should load");
        log.info("✅ Youth — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 3: QUIZ & ESSAY MENU
    // =========================================================================

    @Test(priority = 3, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickQuizAndEssayMenu() {
        log.info("=== Step 3: Click Quiz & Essay ===");
        boolean result = publicPage.clickQuizAndEssay();
        Assert.assertTrue(result, "Quiz & Essay menu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Quiz & Essay page should load");
        log.info("✅ Quiz & Essay — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 4: RESOURCES → VOICES
    // =========================================================================

    @Test(priority = 4, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickResourcesVoices() {
        log.info("=== Step 4: Resources → Voices ===");
        boolean result = publicPage.clickResourcesVoices();
        Assert.assertTrue(result, "Voices submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Voices page should load");
        log.info("✅ Voices — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 5: RESOURCES → BLOGS
    // =========================================================================

    @Test(priority = 5, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickResourcesBlogs() {
        log.info("=== Step 5: Resources → Blogs ===");
        boolean result = publicPage.clickResourcesBlogs();
        Assert.assertTrue(result, "Blogs submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Blogs page should load");
        log.info("✅ Blogs — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 6: RESOURCES → NEWSLETTERS
    // =========================================================================

    @Test(priority = 6, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickResourcesNewsletters() {
        log.info("=== Step 6: Resources → Newsletters ===");
        boolean result = publicPage.clickResourcesNewsletters();
        Assert.assertTrue(result, "Newsletters submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Newsletters page should load");
        log.info("✅ Newsletters — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 7: RESOURCES → OTHER RESOURCES
    // =========================================================================

    @Test(priority = 7, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickResourcesOtherResources() {
        log.info("=== Step 7: Resources → Other Resources ===");
        boolean result = publicPage.clickResourcesOtherResources();
        Assert.assertTrue(result, "Other Resources submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Other Resources page should load");
        log.info("✅ Other Resources — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 8: EVENTS & PROGRAM → EXPERIENTIAL LEARNING
    // =========================================================================

    @Test(priority = 8, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickEventsExperientialLearning() {
        log.info("=== Step 8: Events & Program → Experiential Learning ===");
        boolean result = publicPage.clickEventsExperientialLearning();
        Assert.assertTrue(result, "Experiential Learning submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Experiential Learning page should load");
        log.info("✅ Experiential Learning — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 9: EVENTS & PROGRAM → VOLUNTEER FOR BHARAT
    // =========================================================================

    @Test(priority = 9, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickEventsVolunteerForBharat() {
        log.info("=== Step 9: Events & Program → Volunteer for Bharat ===");
        boolean result = publicPage.clickEventsVolunteerForBharat();
        Assert.assertTrue(result, "Volunteer for Bharat submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Volunteer for Bharat page should load");
        log.info("✅ Volunteer for Bharat — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 10: EVENTS & PROGRAM → MEGA EVENTS
    // =========================================================================

    @Test(priority = 10, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickEventsMegaEvents() {
        log.info("=== Step 10: Events & Program → Mega Events ===");
        boolean result = publicPage.clickEventsMegaEvents();
        Assert.assertTrue(result, "Mega Events submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Mega Events page should load");
        log.info("✅ Mega Events — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 11: EVENTS & PROGRAM → VBYLD-2026
    // =========================================================================

    @Test(priority = 11, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickEventsVBYLD2026() {
        log.info("=== Step 11: Events & Program → VBYLD-2026 ===");
        boolean result = publicPage.clickEventsVBYLD2026();
        Assert.assertTrue(result, "VBYLD-2026 submenu navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "VBYLD-2026 page should load");
        log.info("✅ VBYLD-2026 — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 12: MY BHARAT PODCAST
    // =========================================================================

    @Test(priority = 12, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickMyBharatPodcast() {
        log.info("=== Step 12: Click MY Bharat Podcast ===");
        boolean result = publicPage.clickMyBharatPodcast();
        Assert.assertTrue(result, "MY Bharat Podcast navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Podcast page should load");
        log.info("✅ MY Bharat Podcast — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // STEP 13: VVVP 2026
    // =========================================================================

    @Test(priority = 13, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickVVVP2026() {
        log.info("=== Step 13: Click VVVP 2026 ===");
        boolean result = publicPage.clickVVVP2026();
        Assert.assertTrue(result, "VVVP 2026 navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "VVVP 2026 page should load");
        log.info("✅ VVVP 2026 — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
    }

    // =========================================================================
    // STEP 14: SUMMARY REPORT
    // =========================================================================

    @Test(priority = 14, groups = {"smoke", "header"}, dependsOnMethods = "launchAndValidateHomepage")
    public void printHeaderSummary() {
        log.info("========== HEADER NAVIGATION SUMMARY ==========");
        log.info("PASSED ({}): {}", publicPage.getPassedMenus().size(), publicPage.getPassedMenus());

        List<Map<String, String>> failures = publicPage.getFailedMenus();
        if (!failures.isEmpty()) {
            log.warn("FAILED ({}):", failures.size());
            for (Map<String, String> f : failures) {
                log.warn("  ❌ {} — {}", f.get("menuName"), f.get("error"));
            }
        }
        log.info("================================================");

        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // ORGANIZATION SECTION - "MY Bharat connects you with"
    // =========================================================================

    @Test(priority = 15, groups = {"smoke", "organization"}, dependsOnMethods = "launchAndValidateHomepage")
    public void clickGovernment() {
        log.info("=== Click Government ===");
        boolean result = publicPage.clickGovernment();
        Assert.assertTrue(result, "Government page navigation should succeed");
        Assert.assertTrue(publicPage.getCurrentUrl().contains("government"),
                "URL should contain 'government'. Actual: " + publicPage.getCurrentUrl());
        log.info("✅ Government — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
    }

    @Test(priority = 16, groups = {"smoke", "organization"}, dependsOnMethods = "clickGovernment")
    public void clickGovernmentOrgLink() {
        log.info("=== Click any Government org link ===");
        boolean result = publicPage.clickFirstAvailableOrgLink();
        Assert.assertTrue(result, "Should navigate to an org detail page");
        Assert.assertTrue(publicPage.isPageLoaded(), "Org page should load");
        log.info("✅ Org detail — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateBackToPreviousPage();
    }

    @Test(priority = 17, groups = {"smoke", "organization"}, dependsOnMethods = "clickGovernment")
    public void clickGovernmentViewMore() {
        log.info("=== Scroll down and click View More ===");
        boolean result = publicPage.clickViewMore();
        Assert.assertTrue(result, "View More page should load");
        log.info("✅ View More — URL: {}", publicPage.getCurrentUrl());
    }

    @Test(priority = 18, groups = {"smoke", "organization"}, dependsOnMethods = "clickGovernmentViewMore")
    public void selectGovernmentStateAndDistrict() {
        log.info("=== Select State and District ===");
        boolean stateSelected = publicPage.selectAnyState();
        Assert.assertTrue(stateSelected, "State should be selected");

        boolean districtSelected = publicPage.selectAnyDistrict();
        if (!districtSelected) {
            log.warn("District not available for selected state — continuing");
        }

        publicPage.validateFilteredResults();
        log.info("✅ State & District selected, results filtered");
    }

    @Test(priority = 19, groups = {"smoke", "organization"}, dependsOnMethods = "clickGovernment")
    public void returnHomeFromGovernment() {
        publicPage.clickMyBharatLogo();
        Assert.assertTrue(publicPage.getCurrentUrl().contains("mybharat"), "Should be back on homepage");
        log.info("✅ Returned to homepage from Government");
    }

    // --- Knowledge Institutions ---

    @Test(priority = 20, groups = {"smoke", "organization"}, dependsOnMethods = "returnHomeFromGovernment")
    public void clickKnowledgeInstitutions() {
        log.info("=== Click Knowledge Institutions ===");
        boolean result = publicPage.clickKnowledgeInstitutions();
        Assert.assertTrue(result, "Knowledge Institutions page should load");
        log.info("✅ Knowledge Institutions — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
    }

    @Test(priority = 21, groups = {"smoke", "organization"}, dependsOnMethods = "clickKnowledgeInstitutions")
    public void clickKnowledgeInstitutionsOrgLink() {
        log.info("=== Click any Knowledge Institution link ===");
        boolean result = publicPage.clickFirstAvailableOrgLink();
        Assert.assertTrue(result, "Should navigate to institution detail page");
        log.info("✅ Institution detail — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateBackToPreviousPage();
    }

    @Test(priority = 22, groups = {"smoke", "organization"}, dependsOnMethods = "clickKnowledgeInstitutions")
    public void clickKnowledgeInstitutionsViewMore() {
        log.info("=== Scroll down and click View More ===");
        boolean result = publicPage.clickViewMore();
        Assert.assertTrue(result, "View More page should load");
        log.info("✅ View More — URL: {}", publicPage.getCurrentUrl());
    }

    @Test(priority = 23, groups = {"smoke", "organization"}, dependsOnMethods = "clickKnowledgeInstitutionsViewMore")
    public void selectKnowledgeInstitutionsStateAndDistrict() {
        log.info("=== Select State and District ===");
        boolean stateSelected = publicPage.selectAnyState();
        if (!stateSelected) {
            log.warn("State dropdown not available — continuing");
            return;
        }
        publicPage.selectAnyDistrict();
        publicPage.validateFilteredResults();
        log.info("✅ Filters applied");
    }

    @Test(priority = 24, groups = {"smoke", "organization"}, dependsOnMethods = "clickKnowledgeInstitutions")
    public void returnHomeFromKnowledgeInstitutions() {
        publicPage.clickMyBharatLogo();
        Assert.assertTrue(publicPage.getCurrentUrl().contains("mybharat"), "Should be back on homepage");
        log.info("✅ Returned to homepage from Knowledge Institutions");
    }

    // --- Not for Profits ---

    @Test(priority = 25, groups = {"smoke", "organization"}, dependsOnMethods = "returnHomeFromKnowledgeInstitutions")
    public void clickNotForProfits() {
        log.info("=== Click Not for Profits ===");
        boolean result = publicPage.clickNotForProfits();
        Assert.assertTrue(result, "Not for Profits page should load");
        log.info("✅ Not for Profits — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
    }

    @Test(priority = 26, groups = {"smoke", "organization"}, dependsOnMethods = "clickNotForProfits")
    public void clickNotForProfitsOrgLink() {
        log.info("=== Click any Not for Profit org link ===");
        boolean result = publicPage.clickFirstAvailableOrgLink();
        Assert.assertTrue(result, "Should navigate to org detail page");
        log.info("✅ Org detail — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateBackToPreviousPage();
    }

    @Test(priority = 27, groups = {"smoke", "organization"}, dependsOnMethods = "clickNotForProfits")
    public void clickNotForProfitsViewMore() {
        log.info("=== Scroll down and click View More ===");
        boolean result = publicPage.clickViewMore();
        if (!result) log.warn("View More not available — continuing");
        else log.info("✅ View More — URL: {}", publicPage.getCurrentUrl());
    }

    @Test(priority = 28, groups = {"smoke", "organization"}, dependsOnMethods = "clickNotForProfits")
    public void returnHomeFromNotForProfits() {
        publicPage.clickMyBharatLogo();
        Assert.assertTrue(publicPage.getCurrentUrl().contains("mybharat"), "Should be back on homepage");
        log.info("✅ Returned to homepage from Not for Profits");
    }

    // --- For Profits ---

    @Test(priority = 29, groups = {"smoke", "organization"}, dependsOnMethods = "returnHomeFromNotForProfits")
    public void clickForProfits() {
        log.info("=== Click For Profits ===");
        boolean result = publicPage.clickForProfits();
        Assert.assertTrue(result, "For Profits page should load");
        log.info("✅ For Profits — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
    }

    @Test(priority = 30, groups = {"smoke", "organization"}, dependsOnMethods = "clickForProfits")
    public void clickForProfitsOrgLink() {
        log.info("=== Click any For Profit org link ===");
        boolean result = publicPage.clickFirstAvailableOrgLink();
        Assert.assertTrue(result, "Should navigate to org detail page");
        log.info("✅ Org detail — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());

        publicPage.navigateBackToPreviousPage();
    }

    @Test(priority = 31, groups = {"smoke", "organization"}, dependsOnMethods = "clickForProfits")
    public void clickForProfitsViewMore() {
        log.info("=== Scroll down and click View More ===");
        boolean result = publicPage.clickViewMore();
        if (!result) log.warn("View More not available — continuing");
        else log.info("✅ View More — URL: {}", publicPage.getCurrentUrl());
    }

    @Test(priority = 32, groups = {"smoke", "organization"}, dependsOnMethods = "clickForProfits")
    public void returnHomeFromForProfits() {
        publicPage.clickMyBharatLogo();
        Assert.assertTrue(publicPage.getCurrentUrl().contains("mybharat"), "Should be back on homepage");
        log.info("✅ Returned to homepage — Organization section complete");
    }

    // =========================================================================
    // FOOTER SECTION - Important Links
    // =========================================================================

    @Test(priority = 33, groups = {"smoke", "footer"}, dependsOnMethods = "returnHomeFromForProfits")
    public void clickFooterMegaEvents() {
        log.info("=== Footer: Mega Events ===");
        publicPage.navigateToHomePage();
        boolean result = publicPage.clickFooterMegaEvents();
        Assert.assertTrue(result, "Footer Mega Events navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Mega Events page should load");
        log.info("✅ Footer Mega Events — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 34, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterMegaEvents")
    public void clickFooterExperientialLearning() {
        log.info("=== Footer: Experiential Learning ===");
        boolean result = publicPage.clickFooterExperientialLearning();
        Assert.assertTrue(result, "Footer Experiential Learning navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer Experiential Learning — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 35, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterExperientialLearning")
    public void clickFooterVolunteerForBharat() {
        log.info("=== Footer: Volunteer for Bharat ===");
        boolean result = publicPage.clickFooterVolunteerForBharat();
        Assert.assertTrue(result, "Footer Volunteer for Bharat navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer Volunteer for Bharat — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 36, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterVolunteerForBharat")
    public void clickFooterAbout() {
        log.info("=== Footer: About ===");
        boolean result = publicPage.clickFooterAbout();
        Assert.assertTrue(result, "Footer About navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer About — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // FOOTER SECTION - Useful Links
    // =========================================================================

    @Test(priority = 37, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterAbout")
    public void clickFooterPrivacyPolicy() {
        log.info("=== Footer: Privacy Policy ===");
        boolean result = publicPage.clickFooterPrivacyPolicy();
        Assert.assertTrue(result, "Footer Privacy Policy navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer Privacy Policy — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 38, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterPrivacyPolicy")
    public void clickFooterResources() {
        log.info("=== Footer: Resources ===");
        boolean result = publicPage.clickFooterResources();
        Assert.assertTrue(result, "Footer Resources navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer Resources — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 39, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterResources")
    public void clickFooterSupport() {
        log.info("=== Footer: Support ===");
        boolean result = publicPage.clickFooterSupport();
        Assert.assertTrue(result, "Footer Support navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer Support — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 40, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterSupport")
    public void clickFooterSitemap() {
        log.info("=== Footer: Sitemap ===");
        boolean result = publicPage.clickFooterSitemap();
        Assert.assertTrue(result, "Footer Sitemap navigation should succeed");
        Assert.assertTrue(publicPage.isPageLoaded(), "Page should load");
        log.info("✅ Footer Sitemap — URL: {} | Title: {}", publicPage.getCurrentUrl(), publicPage.getPageTitle());
        publicPage.navigateToHomePage();
    }

    @Test(priority = 41, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterSitemap")
    public void clickFooterFeedback() {
        log.info("=== Footer: Feedback ===");
        boolean result = publicPage.clickFooterFeedback();
        Assert.assertTrue(result, "Footer Feedback should be clickable");
        log.info("✅ Footer Feedback handled");
        publicPage.navigateToHomePage();
    }

    // =========================================================================
    // FOOTER SECTION - Powered By
    // =========================================================================

    @Test(priority = 42, groups = {"smoke", "footer"}, dependsOnMethods = "clickFooterFeedback")
    public void validateDigitalIndiaLogo() {
        log.info("=== Footer: Digital India Logo ===");
        boolean result = publicPage.validateDigitalIndiaLogo();
        Assert.assertTrue(result, "Digital India logo should be visible in footer");
        log.info("✅ Digital India logo validated");
    }

    @Test(priority = 43, groups = {"smoke", "footer"}, dependsOnMethods = "validateDigitalIndiaLogo")
    public void validateDICText() {
        log.info("=== Footer: DIC Text ===");
        boolean result = publicPage.validateDICText();
        Assert.assertTrue(result, "Digital India Corporation text should be visible");
        log.info("✅ DIC text validated — Public Page automation complete");
    }
}
