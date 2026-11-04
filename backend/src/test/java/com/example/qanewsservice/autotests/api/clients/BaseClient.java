package com.example.qanewsservice.autotests.api.clients;

import static io.restassured.RestAssured.given;

import com.example.qanewsservice.autotests.api.assertions.AssertableResponse;
import com.example.qanewsservice.autotests.api.filter.MaskingFilter;
import com.example.qanewsservice.autotests.api.models.LoginRequest;
import com.example.qanewsservice.autotests.api.requests.LoginRequestBuilder;
import com.example.qanewsservice.autotests.api.response.LoginResponse;
import com.example.qanewsservice.autotests.config.ConfigService;
import com.example.qanewsservice.autotests.config.JasyptConfig;
import com.example.qanewsservice.autotests.config.TestUser;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.EnumMap;
import java.util.Map;

import static com.example.qanewsservice.autotests.api.assertions.Conditions.hasStatusCode;

public abstract class BaseClient {

    private static final Object TOKEN_LOCK = new Object();
    private static final Map<TestUser, String> CACHED_TOKENS = new EnumMap<>(TestUser.class);

    protected final JasyptConfig config;
    protected final String baseUrl;

    protected BaseClient() {
        this.config = ConfigService.getConfig();
        this.baseUrl = config.getRequired("api.base_url");
    }

    protected RequestSpecification requestSpec() {
        return given().spec(baseRequestSpec());
    }

    protected RequestSpecification requestSpec(TestUser user) {
        return requestSpec().auth().oauth2(getAccessToken(user));
    }

    protected AssertableResponse postLogin(LoginRequest request) {
        return new AssertableResponse(requestSpec()
                .body(request)
                .post("/api/auth/login")
                .then());
    }

    protected String getAccessToken(TestUser user) {
        String configuredToken = user.token();
        if (!isBlank(configuredToken)) {
            return configuredToken;
        }

        if (!CACHED_TOKENS.containsKey(user)) {
            synchronized (TOKEN_LOCK) {
                if (!CACHED_TOKENS.containsKey(user)) {
                    LoginResponse response = postLogin(LoginRequestBuilder.buildLoginRequest(user))
                            .should(hasStatusCode(200))
                            .as(LoginResponse.class);
                    CACHED_TOKENS.put(user, response.getToken());
                }
            }
        }
        return CACHED_TOKENS.get(user);
    }

    private RequestSpecification baseRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.JSON)
                .addFilter(new MaskingFilter())
                .addFilter(new AllureRestAssured())
                .build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
