package com.example.qanewsservice.autotests.api.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import java.nio.charset.StandardCharsets;

public class MaskingFilter implements Filter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().findAndRegisterModules();

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext context) {
        logRequest(requestSpec);
        Response response = context.next(requestSpec, responseSpec);
        logResponse(response);
        return response;
    }

    private void logRequest(FilterableRequestSpecification requestSpec) {
        System.out.println("===== REQUEST =====");
        System.out.println("Method: " + requestSpec.getMethod());
        System.out.println("URL: " + requestSpec.getURI());
        System.out.println("Query params: " + requestSpec.getQueryParams());
        System.out.println("Form params: " + maskSecrets(String.valueOf(requestSpec.getFormParams())));
        System.out.println("Request params: " + maskSecrets(String.valueOf(requestSpec.getRequestParams())));
        System.out.println("Headers: " + maskSecrets(String.valueOf(requestSpec.getHeaders())));

        String body = bodyToString(requestSpec.getBody());
        if (!body.isBlank()) {
            System.out.println("Body: " + maskSecrets(body));
        }
    }

    private void logResponse(Response response) {
        String body = response.getBody().asString();

        System.out.println("===== RESPONSE =====");
        System.out.println("Status Code: " + response.getStatusCode());
        System.out.println("Headers: " + maskSecrets(String.valueOf(response.getHeaders())));
        if (!body.isBlank()) {
            System.out.println("Body: " + maskSecrets(body));
        }
    }

    static String bodyToString(Object body) {
        if (body == null) {
            return "";
        }
        if (body instanceof String text) {
            return text;
        }
        if (body instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(body);
        } catch (JsonProcessingException e) {
            return String.valueOf(body);
        }
    }

    static String maskSecrets(String value) {
        return value
                .replaceAll("(\"token\"\\s*:\\s*\")(.*?)(\")", "$1***masked***$3")
                .replaceAll("(\"access_token\"\\s*:\\s*\")(.*?)(\")", "$1***masked***$3")
                .replaceAll("(\"refresh_token\"\\s*:\\s*\")(.*?)(\")", "$1***masked***$3")
                .replaceAll("(\"password\"\\s*:\\s*\")(.*?)(\")", "$1***masked***$3")
                .replaceAll("(?i)(Authorization\\s*[:=]\\s*Bearer\\s+)[^,\\]\\r\\n]+", "$1***masked***")
                .replaceAll("(?i)(password\\s*=\\s*)[^,}\\]\\r\\n]+", "$1***masked***")
                .replaceAll("(?i)(access_token\\s*=\\s*)[^,}\\]\\r\\n]+", "$1***masked***")
                .replaceAll("(?i)(refresh_token\\s*=\\s*)[^,}\\]\\r\\n]+", "$1***masked***");
    }
}
