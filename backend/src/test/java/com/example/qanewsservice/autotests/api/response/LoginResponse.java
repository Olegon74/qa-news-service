package com.example.qanewsservice.autotests.api.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    private String token;
    private String tokenType;
    private String username;
    private String role;
}
