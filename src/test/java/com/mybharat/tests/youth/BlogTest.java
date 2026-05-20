package com.mybharat.tests.youth;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.mybharat.base.BaseTest;
import com.mybharat.listeners.Retry;
import com.mybharat.listeners.TestListeners;
import com.mybharat.pages.youth.BlogPage;

/**
 * BlogTest - Creates a blog post on MY Bharat.
 * 
 * Runs on the SAME browser session AFTER QuizCertificateVerificationTest.
 * User is already logged in.
 * 
 * Flow: Navigate to Blogs → Write a Blog → Fill form → Preview → Post → Verify success
 */
@Listeners(TestListeners.class)
public class BlogTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(BlogTest.class);

    private BlogPage blogPage;

    @BeforeClass(alwaysRun = true)
    public void initPages() {
        blogPage = new BlogPage(driver);
    }

    @Test(priority = 1, groups = {"regression", "blog"}, retryAnalyzer = Retry.class,
          description = "Write and publish a blog: Navigate to Blogs → Write a Blog → Fill Title, Category, Cover Image, Description → Preview → Post")
    public void writeAndPublishBlog() throws Exception {
        log.info("Starting: Write and Publish Blog");

        // Step 1: Navigate to Blogs page
        blogPage.navigateToBlogs();
        log.info("Step 1: Navigated to Blogs page");

        // Step 2: Click Write a Blog
        blogPage.clickWriteABlog();
        log.info("Step 2: Blog form opened");

        // Step 3: Fill blog form
        blogPage.fillBlogForm();
        log.info("Step 3: Blog form filled");

        // Step 4: Click Preview
        blogPage.clickPreview();
        log.info("Step 4: Preview opened");

        // Step 5: Click Post
        blogPage.clickPost();
        log.info("Step 5: Blog posted");

        // Step 6: Verify success
        boolean success = blogPage.isBlogPostedSuccessfully();
        Assert.assertTrue(success, "Blog should be posted successfully");
        log.info("✅ Blog published successfully");
    }
}
