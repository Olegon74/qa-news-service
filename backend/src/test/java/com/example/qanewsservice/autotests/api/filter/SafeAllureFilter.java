package com.example.qanewsservice.autotests.api.filter;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/** Attaches HTTP traffic without exposing credentials on public GitHub Pages. */
public final class SafeAllureFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext context) {
        String request = requestSpec.getMethod() + " " + requestSpec.getURI()
                + "\nHeaders: " + requestSpec.getHeaders()
                + "\nBody: " + MaskingFilter.bodyToString(requestSpec.getBody());
        Allure.addAttachment("HTTP request", "text/plain", MaskingFilter.maskSecrets(request));

        Response response = context.next(requestSpec, responseSpec);
        String responseText = "Status: " + response.getStatusCode()
                + "\nHeaders: " + response.getHeaders()
                + "\nBody: " + response.getBody().asString();
        Allure.addAttachment("HTTP response", "text/plain", MaskingFilter.maskSecrets(responseText));
        return response;
    }
}
