package com.mybharat.tests.org;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.Retry;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.org.JoinYouthClubPage;
import com.mybharat.pages.youth.LoginPage;
import com.mybharat.utils.ConfigReader;

/**
 * JoinYouthClubTest — Joins a Youth Club organization end-to-end.
 *
 * Per document: Designation options = President / General Secretary / Treasurer / Member
 * Data: youthClub_<env>.xlsx (JoinOrgData sheet)
 * Login user: Youth_<env>.xlsx (last registered youth user)
 *
 * Run:
 *   mvn test -Denv=prod -Dbrowser=chrome -Dsurefire.suiteXmlFiles=testSuites/testng-youthclub.xml
 */
@Listeners(TestListeners.class)
public class JoinYouthClubTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(JoinYouthClubTest.class);

    private LoginPage loginPage;
    private JoinYouthClubPage joinOrgPage;

    // Read from youthClub_<env>.xlsx JoinOrgData sheet
    private String orgType       = "Not For Profit";
    private String subCategory   = "Youth Club";
    private String orgName       = "Youth Club Automation";
    private String addressLine1  = "123 Youth Club Building, Sector 10";
    private String addressLine2  = "Near Community Center, Block A";

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        loginPage  = new LoginPage(driver);
        joinOrgPage = new JoinYouthClubPage(driver);

        // Read join org data from youthClub_<env>.xlsx
        ConfigReader cfg = new ConfigReader();
        String env = cfg.getEnv();
        String filePath = System.getProperty("user.dir") + File.separator
                + "resources" + File.separator + "youthClub_" + env + ".xlsx";

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheet("JoinOrgData");
            if (sheet != null && sheet.getLastRowNum() >= 1) {
                Row row = sheet.getRow(1); // First data row
                if (row != null) {
                    orgType      = getCellValue(row, 0, orgType);
                    subCategory  = getCellValue(row, 1, subCategory);
                    orgName      = getCellValue(row, 2, orgName);
                    addressLine1 = getCellValue(row, 4, addressLine1);
                    addressLine2 = getCellValue(row, 5, addressLine2);
                }
            }
            log.info("[SETUP] Join Org: type={}, subCat={}, org={}", orgType, subCategory, orgName);
        } catch (Exception e) {
            log.warn("[SETUP] youthClub Excel not found, using defaults: {}", e.getMessage());
        }
    }

    private String getCellValue(Row row, int col, String defaultVal) {
        try {
            String val = row.getCell(col).getStringCellValue().trim();
            return val.isEmpty() ? defaultVal : val;
        } catch (Exception e) {
            return defaultVal;
        }
    }

    @Test(priority = 1, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class)
    public void step1_login() throws Exception {
        log.info("═══ STEP 1: Youth Login ═══");
        // Check if already logged in (e.g., after CreateYouthClubTest in combined suite)
        if (loginPage.isLoginSuccessful()) {
            log.info("═══ ✅ Already logged in — skipping login ═══");
            return;
        }
        loginPage.performLogin();
        Assert.assertTrue(loginPage.isLoginSuccessful(), "Login failed");
        log.info("═══ ✅ LOGIN PASSED ═══");
    }

    @Test(priority = 2, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step1_login")
    public void step2_navigateToJoinOrg() {
        log.info("═══ STEP 2: Navigate to Join Organization ═══");
        joinOrgPage.clickJoinOrganization();
        Assert.assertTrue(joinOrgPage.isPageLoaded(), "Join Org page not loaded");
        log.info("═══ ✅ JOIN ORG PAGE LOADED ═══");
    }

    @Test(priority = 3, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step2_navigateToJoinOrg")
    public void step3_selectOrgType() {
        log.info("═══ STEP 3: Organization Type = {} ═══", orgType);
        joinOrgPage.selectOrganizationType(orgType);
        log.info("═══ ✅ ORG TYPE SELECTED ═══");
    }

    @Test(priority = 4, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step3_selectOrgType")
    public void step4_selectSubcategory() {
        log.info("═══ STEP 4: Sub Category = {} ═══", subCategory);
        joinOrgPage.selectSubcategory(subCategory);
        log.info("═══ ✅ SUB CATEGORY SELECTED ═══");
    }

    @Test(priority = 5, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step4_selectSubcategory")
    public void step5_selectOrgName() {
        log.info("═══ STEP 5: Organization Name = {} ═══", orgName);
        joinOrgPage.selectOrganizationName(orgName);
        log.info("═══ ✅ ORG NAME SELECTED ═══");
    }

    @Test(priority = 6, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step5_selectOrgName")
    public void step6_selectDesignation() {
        log.info("═══ STEP 6: Designation ═══");
        // Per document: Youth Club designations are President/General Secretary/Treasurer/Member
        String d = joinOrgPage.selectDesignation(
                new String[]{"President", "General Secretary", "Treasurer", "Member"});
        Assert.assertNotNull(d, "Designation not selected");
        log.info("═══ ✅ DESIGNATION = {} ═══", d);
    }

    @Test(priority = 7, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step6_selectDesignation")
    public void step7_selectHierarchy() {
        log.info("═══ STEP 7: Hierarchy ═══");
        joinOrgPage.selectHierarchy();
        log.info("═══ ✅ HIERARCHY COMPLETE ═══");
    }

    @Test(priority = 8, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step7_selectHierarchy")
    public void step8_enterAddress() {
        log.info("═══ STEP 8: Address ═══");
        joinOrgPage.enterAddress(addressLine1, addressLine2);
        log.info("═══ ✅ ADDRESS ENTERED ═══");
    }

    @Test(priority = 9, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step8_enterAddress")
    public void step9_clickPreview() {
        log.info("═══ STEP 9: Preview ═══");
        joinOrgPage.clickPreview();
        Assert.assertTrue(joinOrgPage.isPreviewLoaded(), "Preview page not loaded");
        log.info("═══ ✅ PREVIEW LOADED ═══");
    }

    @Test(priority = 10, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step9_clickPreview")
    public void step10_checkboxDownloadSubmit() {
        log.info("═══ STEP 10: Checkbox + Download + Submit ═══");
        joinOrgPage.clickCheckbox();
        joinOrgPage.clickDownload();
        joinOrgPage.clickSubmit();
        Assert.assertTrue(joinOrgPage.isSubmissionSuccessful(), "Submission failed");
        log.info("═══ ✅ JOIN YOUTH CLUB COMPLETED SUCCESSFULLY ═══");
    }

    @Test(priority = 11, groups = {"youthclub", "join"}, retryAnalyzer = Retry.class,
          dependsOnMethods = "step10_checkboxDownloadSubmit")
    public void step11_goToProfile() {
        log.info("═══ STEP 11: Go To My Bharat Profile ═══");
        joinOrgPage.clickGoToProfile();
        log.info("═══ ✅ NAVIGATED TO PROFILE ═══");
    }
}
