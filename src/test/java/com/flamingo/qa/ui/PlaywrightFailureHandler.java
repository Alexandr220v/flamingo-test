package com.flamingo.qa.ui;

import com.flamingo.qa.ui.browser.BrowserManager;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.logging.Logger;

/**
 * JUnit 5 extension that fires after every UI test:
 *  - if the test threw → take a screenshot and flag the browser context for video capture
 *  - on success → no-op (so successful tests don't drown the report in attachments).
 *
 * Mirrors {@code premrshankar/plawright-architecture}'s {@code PlaywrightFailureHandler}
 * but uses our singleton {@link BrowserManager} instead of Spring beans.
 */
public class PlaywrightFailureHandler implements AfterTestExecutionCallback {

    private static final Logger LOG = Logger.getLogger(PlaywrightFailureHandler.class.getName());

    @Override
    public void afterTestExecution(ExtensionContext context) {
        boolean failed = context.getExecutionException().isPresent();
        String name = context.getDisplayName();

        if (failed) {
            LOG.warning(() -> "Test '" + name + "' failed - capturing screenshot + enabling video");
            BrowserManager.INSTANCE.captureScreenshotOnFailure();
            BrowserManager.INSTANCE.markFailure();
        } else {
            LOG.fine(() -> "Test '" + name + "' passed");
        }
    }
}

