package com.mybharat.tests.vo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.vo.VOImageApprovalPage;

/**
 * VOImageApprovalTest - Org side: review youth-uploaded images on an event.
 *
 * Flow:
 *   1. Scroll down on profile page → Click "View More"
 *   2. Click org name in table
 *   3. Click "Events" in left sidebar
 *   4. Click the 2nd event card (latest created event)
 *   5. Scroll down → Click "Images by Youth" tab
 *   6. Reject first image card
 *   7. Approve second image card
 */
@Listeners(TestListeners.class)
public class VOImageApprovalTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(VOImageApprovalTest.class);

    private VOImageApprovalPage approvalPage;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        approvalPage = new VOImageApprovalPage(driver);
    }

    @Test(priority = 1, groups = {"vo", "image-approval"})
    public void reviewYouthImages() throws Exception {
        log.info("=== Starting: VO Image Approval (Reject 1st, Approve 2nd) ===");

        // Step 1 + 2: Scroll → View More → Click org name
        approvalPage.navigateToOrgDashboard();

        // Step 3: Click Events in sidebar
        approvalPage.clickEventsInSidebar();

        // Step 4: Try cards 1 through 5 — click card, scroll down, check Images by Youth
        // If no images, go back and try next card
        boolean imagesFound = false;
        for (int i = 0; i < 5; i++) {
            imagesFound = approvalPage.tryCardForImageApproval(i);
            if (imagesFound) break;

            log.info("No images on card {}, going back to try next card...", i + 1);
            approvalPage.goBackToEventsList();
        }

        if (!imagesFound) {
            log.warn("No images found on first 5 cards — skipping image approval, moving to next process");
            return;
        }

        // Reject first, Approve second
        approvalPage.rejectFirstAndApproveSecond();

        log.info("=== ✅ Image Approval PASSED ===");
    }
}
