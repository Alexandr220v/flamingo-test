package com.flamingo.qa.ui.browser;

import com.flamingo.qa.config.AppConfig;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.qameta.allure.Attachment;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BrowserManager {

    public static final BrowserManager INSTANCE = new BrowserManager();

    private final ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
    private final ThreadLocal<Browser> browserTL = new ThreadLocal<>();
    private final ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
    private final ThreadLocal<Page> pageTL = new ThreadLocal<>();
    private final ThreadLocal<Path> recordedVideoPath = new ThreadLocal<>();
    private final ThreadLocal<Boolean> needVideo = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private final BrowserFactory factory = new BrowserFactory();

    private BrowserManager() {}

    public Playwright getPlaywright() {
        if (playwrightTL.get() == null) playwrightTL.set(Playwright.create());
        return playwrightTL.get();
    }

    public Browser getBrowser() {
        if (browserTL.get() == null) {
            BrowserTypes type = BrowserTypes.valueOf(AppConfig.INSTANCE.uiBrowser().toUpperCase());
            browserTL.set(factory.createBrowserInstance(type, getPlaywright()));
        }
        return browserTL.get();
    }

    public BrowserContext getBrowserContext() {
        if (contextTL.get() == null) {
            Browser.NewContextOptions opts = new Browser.NewContextOptions()
                    .setViewportSize(AppConfig.INSTANCE.screenWidth(), AppConfig.INSTANCE.screenHeight());
            if (AppConfig.INSTANCE.videoEnabled()) {
                opts.setRecordVideoDir(Paths.get(AppConfig.INSTANCE.videoPath()))
                        .setRecordVideoSize(AppConfig.INSTANCE.videoWidth(), AppConfig.INSTANCE.videoHeight());
            }
            BrowserContext ctx = getBrowser().newContext(opts);
            ctx.setDefaultTimeout(AppConfig.INSTANCE.uiTimeoutMs());
            contextTL.set(ctx);
        }
        return contextTL.get();
    }

    public Page getPage() {
        if (pageTL.get() == null) {
            pageTL.set(getBrowserContext().newPage());
        }
        return pageTL.get();
    }

    public void markFailure() { needVideo.set(Boolean.TRUE); }

    public boolean needsVideo() { return Boolean.TRUE.equals(needVideo.get()); }

    public void clearVideoFlag() { needVideo.set(Boolean.FALSE); }

    @Attachment(value = "Failure screenshot", type = "image/png")
    public byte[] captureScreenshotOnFailure() {
        Page p = pageTL.get();
        return p == null ? new byte[0] : p.screenshot();
    }

    @Attachment(value = "Test video", type = "video/webm")
    @SneakyThrows
    public byte[] captureVideo() {
        Path path = recordedVideoPath.get();
        return path == null ? new byte[0] : Files.readAllBytes(path);
    }


    public void closeBrowserContext() {
        Page p = pageTL.get();
        if (p != null) {
            try {
                if (p.video() != null) recordedVideoPath.set(p.video().path());
            } catch (Exception ignored) { }
            p.close();
            pageTL.remove();
        }
        BrowserContext c = contextTL.get();
        if (c != null) {
            c.close();
            contextTL.remove();
        }
    }

    public void closeAll() {
        closeBrowserContext();
        Browser b = browserTL.get();
        if (b != null) { b.close(); browserTL.remove(); }
        Playwright pw = playwrightTL.get();
        if (pw != null) { pw.close(); playwrightTL.remove(); }
        needVideo.remove();
        recordedVideoPath.remove();
    }
}

