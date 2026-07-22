package com.example.qanewsservice.exception;

public class InvalidNewsStatusException extends RuntimeException {

    public InvalidNewsStatusException(String message) {
        super(message);
    }
}
