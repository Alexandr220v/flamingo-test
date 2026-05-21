package com.flamingo.qa.ui.browser;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Playwright;


@FunctionalInterface
public interface BrowserCreator {
    Browser create(Playwright playwright);
}

