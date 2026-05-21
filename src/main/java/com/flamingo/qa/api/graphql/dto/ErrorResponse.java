package com.flamingo.qa.api.graphql.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private List<Map<String, Object>> errors;
    private Map<String, Object> data;
    private Map<String, Object> extensions;
}
