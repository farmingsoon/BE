package com.api.farmingsoon.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class Response<T> {

    private int status;
    private String message;
    private T result;

    public static Response<Void> success(HttpStatus status, String message) {
        return new Response<>(status.value(), message, null);
    }

    public static <T> Response<T> success(HttpStatus status, String message, T result) {
        return new Response<>(status.value(), message, result);
    }

    public static Response<Void> error(HttpStatus status, String message) {
        return new Response<>(status.value(), message, null);
    }

    public static <T> Response<T> error(HttpStatus status, T result) {
        return new Response<>(status.value(), null, result);
    }
}
