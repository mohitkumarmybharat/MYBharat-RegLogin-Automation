package com.mybharat.tests.vo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.vo.VOEventEditPage;

/**
 * VOEventEditTest - Org logs back in after youth applies,
 * navigates to Events, clicks Edit on latest event, clicks "Save as draft", logs out.
 */
@Listeners(TestListeners.class)
public class VOEventEditTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(VOEventEditTest.class);

    private VOEventEditPage editPage;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        editPage = new VOEventEditPage(driver);
    }

    @Test(priority = 1, groups = {"vo", "edit"})
    public void editEventAndSaveAsDraft() throws Exception {
        log.info("=== Starting: Edit Event and Save as Draft ===");

        editPage.navigateToEventsPage();
        editPage.clickEditEventOnLatestCard();
        editPage.clickSaveAsDraft();
        editPage.clickLogoAndLogout();

        log.info("=== ✅ Edit Event → Save as Draft → Logout PASSED ===");
    }
}
