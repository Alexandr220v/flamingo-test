package com.flamingo.qa.api.graphql.movies;

import com.flamingo.qa.api.graphql.client.MovieGraphQLClient;
import com.flamingo.qa.api.graphql.dto.ErrorResponse;
import io.qameta.allure.Feature;
import org.apache.http.HttpStatus;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.Map;


@Tag("api")
@Execution(ExecutionMode.CONCURRENT)
@Feature("Movies GraphQL - negative scenarios")
public class MovieGraphQLNegativeScenariosTests {

    private final MovieGraphQLClient movieGraphQLClient = new MovieGraphQLClient();

    @Test
    @DisplayName("Verify get movie by Non-existent ID.")
    void verifyGetNonExistentIdReturnsNullOrError() {
        String query = """
                query($id: ID!) {
                  movie(where: { id: $id }) {
                    id
                    title
                  }
                }
                """;
        ErrorResponse errorResponse = movieGraphQLClient.executeQueryAndGetErrorResponse(query, Map.of("id", "clzzzzzzzzzzzzzzzzzzzzzz"));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(errorResponse.getStatusCode())
                .as("GraphQL return 200 for invalid ID").isEqualTo(HttpStatus.SC_OK);
        softly.assertThat(errorResponse.getData())
                .as("The 'movie' field should be null for a non-existent ID")
                .containsEntry("movie", null);
        softly.assertAll();
    }

    @Test
    @DisplayName("Verify get movie by Malformed query.")
    void verifyMalformedQueryReturnsErrors() {
        String malformed = "query { movies(first: 1 { id } }";
        ErrorResponse errorResponse = movieGraphQLClient.executeQueryAndGetErrorResponse(malformed);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(errorResponse.getStatusCode()).as("HTTP status").isEqualTo(HttpStatus.SC_BAD_REQUEST);
        softly.assertThat(errorResponse.getErrors()).as("errors array").isNotNull().isNotEmpty();
        softly.assertThat(errorResponse.getErrors().stream().findFirst().map(e -> e.get("message")))
                .asString().as("first error message").isNotBlank();
        softly.assertThat(errorResponse.getData())
                .as("no data on syntax error").isNull();
        softly.assertAll();
    }

    @Test
    @DisplayName("Verify get movie by Non-existent field.")
    @Tag("negative")
    void verifyUnknownFieldReturnsValidationError() {
        String query = "query { movies(first: 1) { id totallyMadeUpField } }";
        ErrorResponse errorResponse = movieGraphQLClient.executeQueryAndGetErrorResponse(query);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(errorResponse.getStatusCode()).as("HTTP status").isEqualTo(HttpStatus.SC_BAD_REQUEST);
        softly.assertThat(errorResponse.getErrors()).as("validation errors").isNotNull().isNotEmpty();
        softly.assertThat(errorResponse.getErrors().stream().findFirst().map(e -> e.get("message"))).asString()
                .as("error message mentions the unknown field")
                .containsIgnoringCase("totallyMadeUpField");
        softly.assertAll();
    }
}
