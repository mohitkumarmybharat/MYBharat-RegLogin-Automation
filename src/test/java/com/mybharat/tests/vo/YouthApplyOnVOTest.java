package com.mybharat.tests.vo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.vo.YouthApplyOnVOPage;
import com.mybharat.pages.vo.YouthUploadImagesPage;

/**
 * YouthApplyOnVOTest - Youth applies on a published VO event and uploads images.
 *
 * Flow:
 *   Test 1: Navigate to VO → Search event → Upload images
 *   Test 2: Apply on event
 */
@Listeners(TestListeners.class)
public class YouthApplyOnVOTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(YouthApplyOnVOTest.class);

    private YouthApplyOnVOPage applyPage;
    private YouthUploadImagesPage uploadPage;

    private String eventName;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        applyPage = new YouthApplyOnVOPage(driver);
        uploadPage = new YouthUploadImagesPage(driver);
        eventName = getLastEventNameFromExcel();
        log.info("Event name from Excel: {}", eventName);
    }

    @Test(priority = 1, groups = {"vo", "youth-upload"})
    public void uploadImagesForEvent() throws Exception {
        log.info("=== Starting: Youth Upload Images for VO Event ===");

        applyPage.openVolunteerForBharat();
        applyPage.searchEvent(eventName);
        applyPage.clickEventByName(eventName);
        uploadPage.uploadAndSubmitImages();

        log.info("=== ✅ Youth Upload Images PASSED ===");
    }

    @Test(priority = 2, groups = {"vo", "youth-apply"}, dependsOnMethods = "uploadImagesForEvent")
    public void applyOnVOEvent() throws Exception {
        log.info("=== Starting: Youth Apply on VO Event ===");

        applyPage.clickApplyButton();

        log.info("=== ✅ Youth Apply on VO PASSED ===");
    }

    private String getLastEventNameFromExcel() {
        String path = System.getProperty("user.dir") + java.io.File.separator
                + "resources" + java.io.File.separator + "Event_Name.xlsx";
        try (java.io.FileInputStream fis = new java.io.FileInputStream(path);
             org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook(fis)) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheet("Event_Data");
            if (sheet == null) sheet = workbook.getSheetAt(0);

            int lastRow = sheet.getLastRowNum();
            org.apache.poi.ss.usermodel.Row row = sheet.getRow(lastRow);
            if (row != null && row.getCell(0) != null) {
                return row.getCell(0).getStringCellValue().trim();
            }
        } catch (Exception e) {
            log.error("Failed to read Event_Name.xlsx: {}", e.getMessage());
        }
        return "VO Automation Event";
    }
}
