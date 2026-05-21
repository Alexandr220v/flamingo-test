package com.flamingo.qa.api.graphql.movies;

import com.flamingo.qa.api.graphql.client.MovieGraphQLClient;
import com.flamingo.qa.api.graphql.dto.MovieResponse;
import com.flamingo.qa.api.graphql.dto.MoviesResponse;
import com.flamingo.qa.api.graphql.dto.MoviesResponse.Movie;
import io.qameta.allure.Epic;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("api")
@Execution(ExecutionMode.CONCURRENT)
class MoviesGraphQLTests {

    private final MovieGraphQLClient movieGraphQLClient = new MovieGraphQLClient();

    @Test
    @DisplayName("Verify list of movies with pagination.")
    void verifyListOfMoviesWithPagination() {
        int movieCount = 5;
        String query = """
            query PagedMovies($first: Int!, $skip: Int!) {
              movies(first: $first, skip: $skip) {
                id
                title
                updatedAt
              }
            }
            """;

        MoviesResponse moviesResponse = movieGraphQLClient.getMovies(query, Map.of("first", movieCount, "skip", 0));

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(moviesResponse.getData().getMovies()).as("movies list size").hasSize(movieCount);
        softly.assertThat(moviesResponse.getData().getMovies()).as("each movie has id and title").allSatisfy(movie -> {
            assertThat(movie.getId()).as("id").isNotBlank();
            assertThat(movie.getTitle()).as("title").isNotBlank();
        });
        softly.assertAll();
    }

    @Test
    @DisplayName("Verify fetching a single movie by ID: movie(where: { id })")
    void verifyGetFirstMovieById() {
        MoviesResponse moviesResponse = movieGraphQLClient.getMovies(
                "query { movies(first: 1) { id title } }", Map.of());

        var firstMovie = moviesResponse.getData().getMovies().getFirst();
        String id = firstMovie.getId();
        String query = """
            query MovieById($id: ID!) {
              movie(where: { id: $id }) {
                id
                title
                slug
              }
            }
            """;

        MovieResponse movieResponseById = movieGraphQLClient.getMovieById(query, Map.of("id", id));

        var movieById = movieResponseById.getData().getMovie();
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(movieById.getId()).as("movie id").isEqualTo(firstMovie.getId());
        softly.assertThat(movieById.getTitle()).as("movie title").isEqualTo(firstMovie.getTitle());
        softly.assertAll();
    }

    @Test
    @DisplayName("Get movies with a fragment for movie details, including nested moviePoster.")
    void verifyMoviesWithFragmentAndNestedFileds() {
        String movieDetailsFragment = """
            fragment MovieDetails on Movie {
              id
              title
              slug
              moviePoster {
                url
                fileName
              }
            }
            """;

        String query = """
            query {
              movies(first: 2) {
                ...MovieDetails
              }
            }
            """;

        MoviesResponse moviesResponse = movieGraphQLClient.getMovies(movieDetailsFragment + query, null);

        List<Movie> movies = moviesResponse.getData().getMovies();
        SoftAssertions softly = new SoftAssertions();

        boolean hasMovieWithPoster = movies.stream()
            .anyMatch(movie -> movie.getMoviePoster() != null);
        softly.assertThat(hasMovieWithPoster).as("at least one movie has a moviePoster").isTrue();
        softly.assertAll();
    }
}
