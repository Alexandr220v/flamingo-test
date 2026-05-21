package com.flamingo.qa.api.graphql.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieResponse {

    private DataNode data;
    private Extensions extensions;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataNode {
        private Movie movie;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Movie {
        private String id;
        private String title;
        private String slug;
        private String createdAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Extensions {
        @JsonProperty("Complexity-Cost-Left")
        private long complexityCostLeft;

        @JsonProperty("Effective-Complexity-Limit")
        private long effectiveComplexityLimit;
    }
}
