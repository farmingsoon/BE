package com.api.farmingsoon.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response<T> {

    private int status;
    private String message;
    private T result;

    public static Response<Void> success(StatusCode status, ResponseMessage message) {
        return new Response<>(status.getStatus(), message.getMessage(), null);
    }

    public static <T> Response<T> success(StatusCode status, ResponseMessage message, T result) {
        return new Response<>(status.getStatus(), message.getMessage(), result);
    }

    public static Response<Void> error(StatusCode statusCode, ResponseMessage message) {
        return new Response<>(statusCode.getStatus(), message.getMessage(), null);
    }
}
