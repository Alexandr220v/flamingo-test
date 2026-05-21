package com.flamingo.qa.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class BaseClient {

    protected <T> T post(RequestSpecification spec, String endpoint, Object body, int expectedStatus, Class<T> responseClass) {
        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract().as(responseClass);
    }

    protected <T> T get(RequestSpecification spec, String endpoint, int expectedStatus, Class<T> responseClass) {
        return given()
                .spec(spec)
                .when()
                .get(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract().as(responseClass);
    }

    protected <T> T put(RequestSpecification spec, String endpoint, Object body, int expectedStatus, Class<T> responseClass) {
        return given()
                .spec(spec)
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .statusCode(expectedStatus)
                .extract().as(responseClass);
    }

    protected void delete(RequestSpecification spec, String endpoint, int expectedStatus) {
        given()
                .spec(spec)
                .when()
                .delete(endpoint)
                .then()
                .statusCode(expectedStatus);
    }

    protected Response get(RequestSpecification spec, String endpoint) {
        return given()
                .spec(spec)
                .when()
                .get(endpoint);
    }

    protected Response post(RequestSpecification spec, String endpoint, Object body) {
        return given()
                .spec(spec)
                .body(body)
                .when()
                .post(endpoint);
    }

    protected Response put(RequestSpecification spec, String endpoint, Object body) {
        return given()
                .spec(spec)
                .body(body)
                .when()
                .put(endpoint);
    }

    protected Response delete(RequestSpecification spec, String endpoint) {
        return given()
                .spec(spec)
                .when()
                .delete(endpoint);
    }
}

