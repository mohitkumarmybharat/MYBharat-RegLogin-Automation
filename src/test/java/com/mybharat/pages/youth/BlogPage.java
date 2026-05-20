package com.mybharat.pages.youth;

import java.io.File;
import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.mybharat.pages.BasePage;
import com.mybharat.utils.ConfigReader;

/**
 * BlogPage - Handles blog creation flow on MY Bharat.
 * 
 * Flow: Navigate to Blogs → Click "Write a Blog" → Fill form → Preview → Submit
 * 
 * URL: /voices/blogs (listing) → /post-blog (create form)
 */
public class BlogPage extends BasePage {

    private static final Logger log = LogManager.getLogger(BlogPage.class);

    private final ConfigReader config = new ConfigReader();
    private final WebDriverWait longWait;

    // Locators - Navigation
    private static final By RESOURCES_MENU = By.xpath("//span[normalize-space()='Resources'] | //a[normalize-space()='Resources']");
    private static final By BLOGS_LINK = By.xpath("//a[normalize-space()='Blogs']");

    // Locators - Blog Listing Page
    private static final By WRITE_BLOG_BUTTON = By.xpath("//a[normalize-space()='Write a Blog'] | //button[normalize-space()='Write a Blog']");

    // Locators - Blog Form
    private static final By TITLE_INPUT = By.id("blog-title");
    private static final By CATEGORY_INPUT = By.id("blog-category");
    private static final By COVER_IMAGE_INPUT = By.xpath("//input[@type='file' and @aria-label='Upload cover image']");
    private static final By BLOG_DESCRIPTION_EDITOR = By.xpath("//div[contains(@class,'ProseMirror') or contains(@class,'tiptap')]");
    private static final By AUTHOR_BIO_EDITOR = By.xpath("(//div[contains(@class,'ProseMirror') or contains(@class,'tiptap')])[2]");

    // Locators - Actions
    private static final By PREVIEW_BUTTON = By.xpath("//button[normalize-space()='Preview']");
    private static final By POST_BUTTON = By.xpath("//button[normalize-space()='Post'] | //button[normalize-space()='Submit']");
    private static final By SUCCESS_MESSAGE = By.xpath("//*[contains(text(),'successfully posted') or contains(text(),'successfully')]");

    public BlogPage(WebDriver driver) {
        super(driver);
        this.longWait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    /**
     * Navigate to Blogs page via Resources dropdown in header.
     */
    public void navigateToBlogs() throws InterruptedException {
        log.info("Navigating to Blogs...");

        // Navigate to home first
        driver.get(config.getUrl());
        waitForPageLoad();
        Thread.sleep(2000);

        // Close popup if present
        try {
            WebElement popup = driver.findElement(By.xpath("//i[@class='fa fa-times']"));
            if (popup.isDisplayed()) popup.click();
        } catch (Exception e) { /* no popup */ }

        // Hover on Resources menu
        WebElement resources = longWait.until(ExpectedConditions.elementToBeClickable(RESOURCES_MENU));
        Actions actions = new Actions(driver);
        actions.moveToElement(resources).perform();
        Thread.sleep(1000);

        // Click Blogs
        WebElement blogs = longWait.until(ExpectedConditions.elementToBeClickable(BLOGS_LINK));
        jsClick(blogs);
        waitForPageLoad();
        Thread.sleep(2000);
        log.info("Navigated to Blogs page");
    }

    /**
     * Click "Write a Blog" button to open the blog creation form.
     */
    public void clickWriteABlog() throws InterruptedException {
        log.info("Clicking 'Write a Blog'...");
        scrollPage(500);
        Thread.sleep(1000);

        WebElement writeBtn = longWait.until(ExpectedConditions.elementToBeClickable(WRITE_BLOG_BUTTON));
        scrollToElement(writeBtn);
        Thread.sleep(500);
        jsClick(writeBtn);
        waitForPageLoad();
        Thread.sleep(2000);
        log.info("Blog creation form opened");
    }

    /**
     * Fill the blog creation form with test data.
     */
    public void fillBlogForm() throws InterruptedException {
        log.info("Filling blog form...");

        // Title
        WebElement titleInput = longWait.until(ExpectedConditions.visibilityOfElementLocated(TITLE_INPUT));
        scrollToElement(titleInput);
        titleInput.clear();
        titleInput.sendKeys("Automation Test Blog - MY Bharat Youth Initiative " + System.currentTimeMillis());
        log.info("Title entered");
        Thread.sleep(500);

        // Category
        WebElement categoryInput = longWait.until(ExpectedConditions.visibilityOfElementLocated(CATEGORY_INPUT));
        categoryInput.clear();
        categoryInput.sendKeys("Technology");
        log.info("Category entered");
        Thread.sleep(500);

        // Cover Image
        uploadCoverImage();
        Thread.sleep(2000);

        // Blog Description (Rich Text Editor)
        fillBlogDescription();
        Thread.sleep(500);

        // Author Bio (optional)
        fillAuthorBio();
        Thread.sleep(500);

        log.info("Blog form filled successfully");
    }

    /**
     * Upload cover image for the blog.
     */
    private void uploadCoverImage() {
        try {
            String imagePath = System.getProperty("user.dir") + File.separator
                    + "UploadImages" + File.separator + "test_upload_photo.jpg";
            File imageFile = new File(imagePath);

            if (!imageFile.exists()) {
                // Fallback to any available image
                imagePath = System.getProperty("user.dir") + File.separator
                        + "UploadImages" + File.separator + "JPG1.jpg";
            }

            WebElement fileInput = driver.findElement(COVER_IMAGE_INPUT);
            fileInput.sendKeys(imagePath);
            log.info("Cover image uploaded: {}", imagePath);
        } catch (Exception e) {
            log.warn("Cover image upload failed: {}", e.getMessage());
        }
    }

    /**
     * Fill the blog description rich text editor.
     */
    private void fillBlogDescription() {
        try {
            WebElement editor = longWait.until(ExpectedConditions.visibilityOfElementLocated(BLOG_DESCRIPTION_EDITOR));
            scrollToElement(editor);
            editor.click();
            Thread.sleep(300);

            String blogContent = "This is an automated test blog post created by MY Bharat QA Automation. "
                    + "The purpose of this blog is to verify the blog creation workflow on the MY Bharat platform. "
                    + "MY Bharat is a platform for Indian youth to engage in nation-building activities, "
                    + "volunteer work, and skill development programs across the country.";

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].innerHTML = arguments[1];", editor, "<p>" + blogContent + "</p>");

            // Trigger input event for React to pick up the change
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", editor);

            log.info("Blog description filled");
        } catch (Exception e) {
            log.warn("Blog description fill failed: {}", e.getMessage());
        }
    }

    /**
     * Fill the author bio rich text editor (optional).
     */
    private void fillAuthorBio() {
        try {
            WebElement bioEditor = driver.findElement(AUTHOR_BIO_EDITOR);
            scrollToElement(bioEditor);
            bioEditor.click();
            Thread.sleep(300);

            String bio = "QA Automation Engineer at MY Bharat, passionate about quality and testing.";
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].innerHTML = arguments[1];", bioEditor, "<p>" + bio + "</p>");
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].dispatchEvent(new Event('input', {bubbles: true}));", bioEditor);

            log.info("Author bio filled");
        } catch (Exception e) {
            log.warn("Author bio fill skipped: {}", e.getMessage());
        }
    }

    /**
     * Click Preview button to preview the blog before posting.
     */
    public void clickPreview() throws InterruptedException {
        log.info("Clicking Preview...");
        scrollPage(300);
        Thread.sleep(500);

        WebElement previewBtn = longWait.until(ExpectedConditions.elementToBeClickable(PREVIEW_BUTTON));
        scrollToElement(previewBtn);
        Thread.sleep(500);
        jsClick(previewBtn);
        Thread.sleep(2000);
        log.info("Preview opened");
    }

    /**
     * Click Post/Submit button from the preview page.
     */
    public void clickPost() throws InterruptedException {
        log.info("Clicking Post...");

        WebElement postBtn = longWait.until(ExpectedConditions.elementToBeClickable(POST_BUTTON));
        scrollToElement(postBtn);
        Thread.sleep(500);
        jsClick(postBtn);
        Thread.sleep(3000);
        log.info("Blog posted");
    }

    /**
     * Verify blog was posted successfully.
     */
    public boolean isBlogPostedSuccessfully() {
        try {
            longWait.until(ExpectedConditions.visibilityOfElementLocated(SUCCESS_MESSAGE));
            log.info("✅ Blog posted successfully");
            return true;
        } catch (Exception e) {
            log.warn("Success message not found: {}", e.getMessage());
            return false;
        }
    }
}
