package com.flamingo.qa.ui.pages;

import com.flamingo.qa.ui.browser.BrowserManager;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

public abstract class BasePage {

    protected final BrowserManager browserManager;

    protected BasePage(BrowserManager browserManager) {
        this.browserManager = browserManager;
    }

    protected Page getPage() {
        return browserManager.getPage();
    }

    protected void navigate(String url) {
        getPage().navigate(url);
        waitForPageLoad();
    }

    protected void waitForPageLoad() {
        getPage().waitForLoadState(LoadState.LOAD);
    }

    protected void waitForVisible(String selector) {
        getPage().waitForSelector(selector,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    protected void click(String selector) {
        waitForVisible(selector);
        getPage().locator(selector).click();
    }

    protected void scrollIntoView(Locator locator) {
        locator.scrollIntoViewIfNeeded();
    }

    @Step("Get page title")
    public String getTitle() {
        return getPage().title();
    }

    public String url() {
        return getPage().url();
    }
}

