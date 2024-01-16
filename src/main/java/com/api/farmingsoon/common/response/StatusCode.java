package com.api.farmingsoon.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusCode {

    OK(200), CREATED(201), NOT_FOUND(404),;

    private final int status;

}
