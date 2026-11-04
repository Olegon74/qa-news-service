package com.example.qanewsservice.autotests.api.assertions.conditions;

import com.example.qanewsservice.autotests.api.assertions.Condition;
import io.restassured.response.ValidatableResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StatusCodeCondition implements Condition {

    private final int expectedCode;

    @Override
    public void check(ValidatableResponse response) {
        response.statusCode(expectedCode);
    }
}
