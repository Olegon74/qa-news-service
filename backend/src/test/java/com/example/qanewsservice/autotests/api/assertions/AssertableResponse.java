package com.example.qanewsservice.autotests.api.assertions;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AssertableResponse {

    private final ValidatableResponse response;

    public AssertableResponse should(Condition condition) {
        condition.check(response);
        return this;
    }

    public <T> T as(Class<T> clazz) {
        return response.extract().body().as(clazz);
    }

    public <T> List<T> asList(Class<T> clazz) {
        return response.extract().body().jsonPath().getList("", clazz);
    }

    public Response asResponse() {
        return response.extract().response();
    }
}
