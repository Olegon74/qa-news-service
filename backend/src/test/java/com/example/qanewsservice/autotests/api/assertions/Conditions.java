package com.example.qanewsservice.autotests.api.assertions;

import com.example.qanewsservice.autotests.api.assertions.conditions.StatusCodeCondition;

public final class Conditions {

    private Conditions() {
    }

    public static Condition hasStatusCode(int expectedCode) {
        return new StatusCodeCondition(expectedCode);
    }
}
