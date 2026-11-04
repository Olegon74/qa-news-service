package com.example.qanewsservice.autotests.api.clients;

import com.example.qanewsservice.autotests.api.assertions.AssertableResponse;
import com.example.qanewsservice.autotests.api.models.LoginRequest;
import io.qameta.allure.Step;

public class AuthControllerClient extends BaseClient {

    @Step("Авторизоваться с логином: {request.username}")
    public AssertableResponse login(LoginRequest request) {
        return postLogin(request);
    }
}
