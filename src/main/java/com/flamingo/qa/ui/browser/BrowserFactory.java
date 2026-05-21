package com.flamingo.qa.ui.browser;

import com.flamingo.qa.config.AppConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;


public class BrowserFactory {

    public Browser createBrowserInstance(BrowserTypes type, Playwright playwright) {
        BrowserType.LaunchOptions opts = createOptions();
        BrowserCreator creator = switch (type) {
            case CHROMIUM -> p -> p.chromium().launch(opts);
            case FIREFOX  -> p -> p.firefox().launch(opts);
            case WEBKIT   -> p -> p.webkit().launch(opts);
        };
        return creator.create(playwright);
    }

    private BrowserType.LaunchOptions createOptions() {
        return new BrowserType.LaunchOptions()
                .setHeadless(AppConfig.INSTANCE.uiHeadless())
                .setSlowMo(AppConfig.INSTANCE.slowMotionMs());
    }
}

