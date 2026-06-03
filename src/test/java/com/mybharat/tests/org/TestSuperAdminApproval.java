package com.mybharat.tests.org;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.pages.superadmin.SuperAdminLoginPage;
import com.mybharat.pages.superadmin.OrgApprovalPage;

/**
 * TEMPORARY TEST — SuperAdmin login → Search Youth Club → Approve
 * DELETE after verification.
 */
public class TestSuperAdminApproval extends BaseTest {

    private static final Logger log = LogManager.getLogger(TestSuperAdminApproval.class);
    private static final String YOUTH_CLUB_NAME = "Youth Club Automation 5629";

    private SuperAdminLoginPage superAdminLogin;
    private OrgApprovalPage approvalPage;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        superAdminLogin = new SuperAdminLoginPage(driver);
        approvalPage = new OrgApprovalPage(driver);
    }

    @Test(priority = 1)
    public void superAdminLoginAndApprove() throws Exception {
        // Step 1: Login as SuperAdmin
        log.info("═══ SuperAdmin Login ═══");
        superAdminLogin.loginAsSuperAdmin();
        Assert.assertTrue(superAdminLogin.isLoginSuccessful(), "SuperAdmin login failed");
        log.info("✅ SuperAdmin logged in");

        // Step 2: Navigate to org list
        log.info("═══ Navigate to Org Approval ═══");
        approvalPage.navigateToOrgList();

        // Step 3: Click MBO Approval tab
        approvalPage.clickMboApprovalTab();

        // Step 4: Click Pending tab
        approvalPage.clickPendingTab();

        // Step 5: Search for Youth Club
        approvalPage.searchOrganization(YOUTH_CLUB_NAME);

        // Step 6: Click green eye icon
        approvalPage.clickActionEyeIcon();

        // Step 7: Click Approve
        approvalPage.clickApprove();

        // Step 8: Verify
        log.info("Current URL: {}", driver.getCurrentUrl());
        log.info("═══ SuperAdmin Approval Complete ═══");
    }
}
