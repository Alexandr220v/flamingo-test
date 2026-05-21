package com.flamingo.qa.api.graphql.client;

import com.flamingo.qa.api.BaseClient;
import com.flamingo.qa.api.graphql.dto.MovieResponse;
import com.flamingo.qa.api.graphql.dto.MoviesResponse;
import com.flamingo.qa.api.graphql.dto.ErrorResponse;
import com.flamingo.qa.config.AppConfig;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.flamingo.qa.api.restapi.RestAssuredSpecification.graphqlSpec;
import static io.restassured.RestAssured.given;

public class MovieGraphQLClient extends BaseClient  {

    private static final String GRAPHQL_ENDPOINT = AppConfig.INSTANCE.graphqlUrl();

    @Step("Get list of movies.")
    public MoviesResponse getMovies(String query) {
        return post(graphqlSpec(), GRAPHQL_ENDPOINT, query, 200, MoviesResponse.class);
    }

    @Step("Get list of movies by parameters.")
    public MoviesResponse getMovies(String query, Map<String, Object> variables) {
        Object body = buildGraphQLBody(query, variables);
        return post(graphqlSpec(), GRAPHQL_ENDPOINT, body, 200, MoviesResponse.class);
    }

    @Step("Get movie by id.")
    public MovieResponse getMovieById(String query, Map<String, Object> variables) {
        Object body = buildGraphQLBody(query, variables);
        return post(graphqlSpec(), GRAPHQL_ENDPOINT, body, 200, MovieResponse.class);
    }

    @Step("Execute GraphQL query error.")
    public Response executeQueryError(String query, Map<String, Object> variables) {
        Object body = buildGraphQLBody(query, variables);
        return post(graphqlSpec(), GRAPHQL_ENDPOINT, body);
    }

    public ErrorResponse toErrorResponse(Response response) {
        ErrorResponse errorResponse = response.as(ErrorResponse.class);
        errorResponse.setStatusCode(response.getStatusCode());
        return errorResponse;
    }

    @Step("Execute GraphQL query and get  error.")
    public ErrorResponse executeQueryAndGetErrorResponse(String query, Map<String, Object> variables) {
        Response response = executeQueryError(query, variables);
        return toErrorResponse(response);
    }

    @Step("Execute GraphQL query and get  error.")
    public ErrorResponse executeQueryAndGetErrorResponse(String query) {
        Response response = executeQueryError(query, null);
        return toErrorResponse(response);
    }


    private Map<String, Object> buildGraphQLBody(String query, Map<String, Object> variables) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("query", query);
        body.put("variables", variables == null ? Map.of() : variables);
        return body;
    }
}
