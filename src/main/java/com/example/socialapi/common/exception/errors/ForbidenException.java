package com.example.socialapi.common.exception.errors;

public class ForbidenException extends RuntimeException{
    private final String message;
    public ForbidenException(String message) {
        this.message = "Cannot access";
        throw new ForbidenException(this.message);
    }
}
