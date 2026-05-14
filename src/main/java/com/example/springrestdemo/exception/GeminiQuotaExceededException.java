package com.example.springrestdemo.exception;

/** Gemini ücretsiz katman / dakika kotası aşıldığında (HTTP 429). */
public class GeminiQuotaExceededException extends RuntimeException {

    public GeminiQuotaExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
