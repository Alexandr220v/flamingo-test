package com.flamingo.qa.ui;

import com.flamingo.qa.ui.browser.BrowserManager;
import com.flamingo.qa.ui.pages.RegisterStudentFormPage;
import com.flamingo.qa.ui.pages.RegisterNewEmployeeFormPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Lifecycle hub for every UI test. Adapted from premrshankar/plawright-architecture's
 * {@code BaseTest}, but using a singleton {@link BrowserManager} instead of Spring DI.
 *
 * <ul>
 *   <li>{@link TestInstance.Lifecycle#PER_CLASS} → the {@link com.microsoft.playwright.Browser}
 *       is launched once per class and reused.</li>
 *   <li>Per test: a fresh {@link com.microsoft.playwright.BrowserContext} +
 *       {@link com.microsoft.playwright.Page} (cookies / storage isolated).</li>
 *   <li>{@link PlaywrightFailureHandler} attaches screenshot + flags video on failure.</li>
 * </ul>
 */
@Tag("ui")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(PlaywrightFailureHandler.class)
public abstract class BaseTest {

    protected final BrowserManager browserManager = BrowserManager.INSTANCE;
    protected RegisterStudentFormPage registerStudentFormPage;
    protected RegisterNewEmployeeFormPage registerNewEmployeeFormPage;

    @BeforeAll
    public void beforeAll() {
        browserManager.getBrowser();
        registerStudentFormPage = new RegisterStudentFormPage(browserManager);
        registerNewEmployeeFormPage = new RegisterNewEmployeeFormPage(browserManager);
    }

    @BeforeEach
    public void beforeEach() {
        browserManager.getPage();
    }

    @AfterEach
    public void afterEach() {
        browserManager.closeBrowserContext();
        if (browserManager.needsVideo()) {
            browserManager.captureVideo();
            browserManager.clearVideoFlag();
        }
    }

    @AfterAll
    public void afterAll() {
        browserManager.closeAll();
    }
}

