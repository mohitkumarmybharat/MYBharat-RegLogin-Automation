package com.mybharat.tests.youth;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.Retry;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.youth.PublicPage;

/**
 * PublicPageTest - Comprehensive end-to-end test for all public-facing pages on MYBharat.
 *
 * Purpose: Validates every publicly accessible navigation path — header menus (top-level
 *          and dropdowns), organization section cards with filtering, and footer links.
 *          All validations run in a SINGLE @Test method so it appears as 1 test case in reports.
 *
 * Validates:
 *   - Header menus: Youth, Quiz &amp; Essay, Resources (Voices, Blogs, Newsletters, Other),
 *     Events (Experiential Learning, VFB, Mega Events, VBYLD-2026), Podcast, VVVP 2026
 *   - Organization section: Government, Knowledge Institutions, Not for Profits, For Profits
 *     (including org link clicks, View More, State/District filtering)
 *   - Footer: Important Links, Useful Links, Powered By (Digital India logo, DIC text)
 *
 * Test Strategy: Each navigation returns to homepage before the next check. Failures are
 *                collected and reported as a summary at the end.
 *
 * Run:
 *   mvn test "-Denv=prod" "-Dbrowser=chrome" "-Dsurefire.suiteXmlFiles=testSuites/testng-public-page.xml"
 *
 * Dependencies: BaseTest, PublicPage, TestListeners
 * Developer: Nishant Sharma (QA Team)
 *
 * @see PublicPage
 */
@Listeners(TestListeners.class)
public class PublicPageTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(PublicPageTest.class);
    private PublicPage publicPage;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        publicPage = new PublicPage(driver);
    }

    @Test(priority = 1, groups = {"smoke", "publicpages"}, retryAnalyzer = Retry.class)
    public void publicPages() throws Exception {
        log.info("=== PUBLIC PAGES — Full Validation Start ===");
        SoftAssert softAssert = new SoftAssert();

        // =====================================================================
        // STEP 1: LAUNCH & VALIDATE HOMEPAGE
        // =====================================================================
        log.info("--- Step 1: Launch homepage ---");
        publicPage.navigateToHomePage();
        String url = publicPage.getCurrentUrl();
        softAssert.assertTrue(url.contains("mybharat"), "Should be on mybharat.gov.in. URL: " + url);
        softAssert.assertTrue(publicPage.isSignInDisplayed(), "Sign In button should be visible");
        softAssert.assertTrue(publicPage.isRegisterNowDisplayed(), "Register Now button should be visible");
        log.info("✅ Homepage validated");

        // =====================================================================
        // STEP 2: HEADER MENU NAVIGATION
        // =====================================================================

        // Youth
        log.info("--- Step 2: Click Youth ---");
        softAssert.assertTrue(publicPage.clickYouth(), "Youth menu navigation should succeed");
        publicPage.navigateToHomePage();

        // Quiz & Essay
        log.info("--- Step 3: Click Quiz & Essay ---");
        softAssert.assertTrue(publicPage.clickQuizAndEssay(), "Quiz & Essay navigation should succeed");
        publicPage.navigateToHomePage();

        // Resources → Blogs
        log.info("--- Step 4: Resources → Blogs ---");
        softAssert.assertTrue(publicPage.clickResourcesBlogs(), "Blogs navigation should succeed");
        publicPage.navigateToHomePage();

        // Resources → Newsletters
        log.info("--- Step 5: Resources → Newsletters ---");
        softAssert.assertTrue(publicPage.clickResourcesNewsletters(), "Newsletters navigation should succeed");
        publicPage.navigateToHomePage();

        // Resources → Other Resources
        log.info("--- Step 6: Resources → Other Resources ---");
        softAssert.assertTrue(publicPage.clickResourcesOtherResources(), "Other Resources navigation should succeed");
        publicPage.navigateToHomePage();

        // Events → Experiential Learning
        log.info("--- Step 8: Events → Experiential Learning ---");
        softAssert.assertTrue(publicPage.clickEventsExperientialLearning(), "Experiential Learning should succeed");
        publicPage.navigateToHomePage();

        // Events → Volunteer for Bharat
        log.info("--- Step 9: Events → Volunteer for Bharat ---");
        softAssert.assertTrue(publicPage.clickEventsVolunteerForBharat(), "Volunteer for Bharat should succeed");
        publicPage.navigateToHomePage();

        // Events → Mega Events
        log.info("--- Step 10: Events → Mega Events ---");
        softAssert.assertTrue(publicPage.clickEventsMegaEvents(), "Mega Events should succeed");
        publicPage.navigateToHomePage();

        // Events → VBYLD-2026
        log.info("--- Step 11: Events → VBYLD-2026 ---");
        softAssert.assertTrue(publicPage.clickEventsVBYLD2026(), "VBYLD-2026 should succeed");
        publicPage.navigateToHomePage();

        // MY Bharat Podcast
        log.info("--- Step 12: MY Bharat Podcast ---");
        softAssert.assertTrue(publicPage.clickMyBharatPodcast(), "MY Bharat Podcast should succeed");
        publicPage.navigateToHomePage();

        // VVVP 2026
        log.info("--- Step 13: VVVP 2026 ---");
        softAssert.assertTrue(publicPage.clickVVVP2026(), "VVVP 2026 should succeed");
        publicPage.navigateToHomePage();

        // MY Bharat Icon
        log.info("--- Step 14: MY Bharat Icon ---");
        softAssert.assertTrue(publicPage.clickMyBharatIcon(), "MY Bharat Icon should succeed");
        publicPage.navigateToHomePage();

        // BRICS 2026
        log.info("--- Step 15: BRICS 2026 ---");
        softAssert.assertTrue(publicPage.clickBRICS2026(), "BRICS 2026 should succeed");
        publicPage.navigateToHomePage();

        // Nation First
        log.info("--- Step 16: Nation First ---");
        softAssert.assertTrue(publicPage.clickNationFirst(), "Nation First should succeed");
        publicPage.navigateToHomePage();

        // International Day of Yoga
        log.info("--- Step 17: International Day of Yoga ---");
        softAssert.assertTrue(publicPage.clickIDY2026(), "International Day of Yoga should succeed");
        publicPage.navigateToHomePage();

        // Header Summary
        log.info("HEADER PASSED ({}): {}", publicPage.getPassedMenus().size(), publicPage.getPassedMenus());

        // =====================================================================
        // STEP 2B: DEEP PAGE VALIDATION — Filters, Tabs, Cards, Search
        // =====================================================================

        log.info("--- Deep: ELP Page (categories, tabs, state filter, card click) ---");
        softAssert.assertTrue(publicPage.validateELPPage(), "ELP page deep validation should pass");

        log.info("--- Deep: VFB/Events Page (filter dropdowns, state filter, card click) ---");
        softAssert.assertTrue(publicPage.validateVFBPage(), "VFB page deep validation should pass");

        log.info("--- Deep: Mega Events Page (event cards, open one) ---");
        softAssert.assertTrue(publicPage.validateMegaEventsPage(), "Mega Events deep validation should pass");

        log.info("--- Deep: Quiz Page (tabs, ongoing quizzes, card click) ---");
        softAssert.assertTrue(publicPage.validateQuizPage(), "Quiz page deep validation should pass");

        log.info("--- Deep: Blogs Page (search, open a blog post) ---");
        softAssert.assertTrue(publicPage.validateBlogsPage(), "Blogs page deep validation should pass");

        log.info("--- Deep: Newsletters Page (open a newsletter) ---");
        softAssert.assertTrue(publicPage.validateNewslettersPage(), "Newsletters page deep validation should pass");

        log.info("--- Deep: MY Bharat Icons Page (category/state/age filters) ---");
        softAssert.assertTrue(publicPage.validateMyBharatIconsPage(), "MY Bharat Icons deep validation should pass");

        log.info("--- Deep: Knowledge Institutions Page (School/College categories, org links) ---");
        softAssert.assertTrue(publicPage.validateKnowledgeInstitutionsPage(), "Knowledge Institutions deep validation should pass");

        log.info("--- Deep: Resources Page (cards, Load More) ---");
        softAssert.assertTrue(publicPage.validateResourcesPage(), "Resources page deep validation should pass");

        // Navigate home before org section
        publicPage.navigateToHomePage();

        // =====================================================================
        // STEP 3: ORGANIZATION SECTION — "MY Bharat connects you with"
        // =====================================================================

        // Government
        log.info("--- Step 14: Click Government ---");
        softAssert.assertTrue(publicPage.clickGovernment(), "Government page should load");

        log.info("--- Step 15: Click Government org link ---");
        publicPage.clickFirstAvailableOrgLink();
        publicPage.navigateBackToPreviousPage();

        log.info("--- Step 16: Click Government View More ---");
        publicPage.clickViewMore();

        log.info("--- Step 17: Select State and District ---");
        publicPage.selectAnyState();
        publicPage.selectAnyDistrict();
        publicPage.validateFilteredResults();

        // Return home
        publicPage.clickMyBharatLogo();

        // Knowledge Institutions
        log.info("--- Step 18: Click Knowledge Institutions ---");
        softAssert.assertTrue(publicPage.clickKnowledgeInstitutions(), "Knowledge Institutions should load");

        log.info("--- Step 19: Click Knowledge Institutions org link ---");
        publicPage.clickFirstAvailableOrgLink();
        publicPage.navigateBackToPreviousPage();

        log.info("--- Step 20: Click Knowledge Institutions View More ---");
        publicPage.clickViewMore();

        log.info("--- Step 21: Select State and District ---");
        publicPage.selectAnyState();
        publicPage.selectAnyDistrict();
        publicPage.validateFilteredResults();

        // Return home
        publicPage.clickMyBharatLogo();

        // Not for Profits
        log.info("--- Step 22: Click Not for Profits ---");
        softAssert.assertTrue(publicPage.clickNotForProfits(), "Not for Profits should load");

        log.info("--- Step 23: Click Not for Profits org link ---");
        publicPage.clickFirstAvailableOrgLink();
        publicPage.navigateBackToPreviousPage();

        log.info("--- Step 24: Click Not for Profits View More ---");
        publicPage.clickViewMore();

        // Return home
        publicPage.clickMyBharatLogo();

        // For Profits
        log.info("--- Step 25: Click For Profits ---");
        softAssert.assertTrue(publicPage.clickForProfits(), "For Profits should load");

        log.info("--- Step 26: Click For Profits org link ---");
        publicPage.clickFirstAvailableOrgLink();
        publicPage.navigateBackToPreviousPage();

        log.info("--- Step 27: Click For Profits View More ---");
        publicPage.clickViewMore();

        // Return home
        publicPage.clickMyBharatLogo();

        // =====================================================================
        // STEP 4: FOOTER SECTION
        // =====================================================================

        // Important Links
        log.info("--- Step 28: Footer — Mega Events ---");
        publicPage.navigateToHomePage();
        softAssert.assertTrue(publicPage.clickFooterMegaEvents(), "Footer Mega Events should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 29: Footer — Experiential Learning ---");
        softAssert.assertTrue(publicPage.clickFooterExperientialLearning(), "Footer EL should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 30: Footer — Volunteer for Bharat ---");
        softAssert.assertTrue(publicPage.clickFooterVolunteerForBharat(), "Footer VFB should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 31: Footer — About ---");
        softAssert.assertTrue(publicPage.clickFooterAbout(), "Footer About should succeed");
        publicPage.navigateToHomePage();

        // Useful Links
        log.info("--- Step 32: Footer — Privacy Policy ---");
        softAssert.assertTrue(publicPage.clickFooterPrivacyPolicy(), "Footer Privacy Policy should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 33: Footer — Resources ---");
        softAssert.assertTrue(publicPage.clickFooterResources(), "Footer Resources should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 34: Footer — Support ---");
        softAssert.assertTrue(publicPage.clickFooterSupport(), "Footer Support should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 35: Footer — Sitemap ---");
        softAssert.assertTrue(publicPage.clickFooterSitemap(), "Footer Sitemap should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 36: Footer — Feedback ---");
        softAssert.assertTrue(publicPage.clickFooterFeedback(), "Footer Feedback should succeed");
        publicPage.navigateToHomePage();

        log.info("--- Step 37: Footer — Terms & Conditions ---");
        softAssert.assertTrue(publicPage.clickFooterTermsAndConditions(), "Footer Terms & Conditions should succeed");
        publicPage.navigateToHomePage();

        // Powered By
        log.info("--- Step 37: Footer — Digital India Logo ---");
        softAssert.assertTrue(publicPage.validateDigitalIndiaLogo(), "Digital India logo should be visible");

        log.info("--- Step 38: Footer — DIC Text ---");
        softAssert.assertTrue(publicPage.validateDICText(), "DIC text should be visible");

        // =====================================================================
        // FINAL SUMMARY
        // =====================================================================
        List<Map<String, String>> failures = publicPage.getFailedMenus();
        if (!failures.isEmpty()) {
            log.warn("FAILED ({}):", failures.size());
            for (Map<String, String> f : failures) {
                log.warn("  ❌ {} — {}", f.get("menuName"), f.get("error"));
            }
        }

        log.info("=== ✅ PUBLIC PAGES — {} checks PASSED, {} FAILED ===",
                publicPage.getPassedMenus().size(), failures.size());

        // SoftAssert — will throw if any assertion failed (including 404 pages)
        softAssert.assertAll();
    }
}
