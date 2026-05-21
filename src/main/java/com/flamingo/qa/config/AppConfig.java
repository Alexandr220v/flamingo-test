package com.flamingo.qa.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:config.properties"
})
public interface AppConfig extends Config {

    AppConfig INSTANCE = ConfigFactory.create(AppConfig.class);

    @Key("api.booker.baseUrl")
    String bookerBaseUrl();

    @Key("api.booker.username")
    String bookerUsername();

    @Key("api.booker.password")
    String bookerPassword();

    @Key("api.graphql.url")
    String graphqlUrl();

    @Key("api.graphql.ecommerce.url")
    String graphqlEcommerceUrl();

    @Key("ui.demoqa.baseUrl")
    String demoqaBaseUrl();

    @Key("ui.browser")
    @DefaultValue("chromium")
    String uiBrowser();

    @Key("ui.headless")
    @DefaultValue("true")
    boolean uiHeadless();

    @Key("ui.timeoutMs")
    @DefaultValue("15000")
    int uiTimeoutMs();

    @Key("ui.screenshotsDir")
    @DefaultValue("target/screenshots")
    String screenshotsDir();

    @Key("ui.slowMotionMs")
    @DefaultValue("0")
    int slowMotionMs();

    @Key("ui.video.enabled")
    @DefaultValue("false")
    boolean videoEnabled();

    @Key("ui.video.path")
    @DefaultValue("target/videos")
    String videoPath();

    @Key("ui.video.width")
    @DefaultValue("1280")
    int videoWidth();

    @Key("ui.video.height")
    @DefaultValue("720")
    int videoHeight();

    @Key("ui.screen.width")
    @DefaultValue("1440")
    int screenWidth();

    @Key("ui.screen.height")
    @DefaultValue("900")
    int screenHeight();
}



