package com.flamingo.qa.api.restapi;
import com.flamingo.qa.api.restapi.filter.LoggingFilter;
import com.flamingo.qa.config.AppConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
public final class RestAssuredSpecification  {

    private static final RestAssuredConfig NO_CHARSET_CONFIG = RestAssuredConfig
            .config()
            .encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false));

    private static final AtomicReference<String> TOKEN = new AtomicReference<>();
    private RestAssuredSpecification() {}

    public static RequestSpecification bookerSpec() {
        return baseBookerBuilder().build();
    }

    public static RequestSpecification bookerAuthenticatedSpec() {
        return baseBookerBuilder()
                .addCookie("token", token())
                .build();
    }
    public static RequestSpecification graphqlSpec() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setConfig(NO_CHARSET_CONFIG)
                .addFilter(new LoggingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }
   
    private static String token() {
        return TOKEN.updateAndGet(existing -> Optional.ofNullable(existing).orElseGet(RestAssuredSpecification::fetchToken));
    }
   
    public static String fetchToken() {
        return Optional.ofNullable(
                RestAssured.given()
                        .spec(new RequestSpecBuilder()
                                .setBaseUri(AppConfig.INSTANCE.bookerBaseUrl())
                                .setContentType(ContentType.JSON)
                                .setAccept("application/json")
                                .setConfig(NO_CHARSET_CONFIG)
                                .build())
                        .body(Map.of(
                                "username", AppConfig.INSTANCE.bookerUsername(),
                                "password", AppConfig.INSTANCE.bookerPassword()))
                        .when()
                        .post("/auth")
                        .then()
                        .statusCode(200)
                        .extract().jsonPath().getString("token"))
                .filter(t -> !t.isBlank())
                .orElseThrow(() -> new IllegalStateException("/auth returned no token"));
    }

    private static RequestSpecBuilder baseBookerBuilder() {
        return new RequestSpecBuilder()
                .setBaseUri(AppConfig.INSTANCE.bookerBaseUrl())
                .setContentType(ContentType.JSON)
                .setConfig(NO_CHARSET_CONFIG)
                .addFilter(new LoggingFilter())
                .addFilter(new AllureRestAssured());
    }
}
