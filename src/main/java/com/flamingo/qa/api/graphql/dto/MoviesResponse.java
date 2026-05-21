package com.flamingo.qa.api.graphql.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoviesResponse {

    private DataNode data;

    @JsonProperty("extensions")
    private Map<String, Object> extensions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataNode {
        private List<Movie> movies;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Movie {
        private String id;
        private String title;
        private String slug;

        @JsonProperty("updatedAt")
        private String updatedAt;

        @JsonProperty("moviePoster")
        private MoviePoster moviePoster;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoviePoster {
        private String url;
        private String fileName;
    }
}