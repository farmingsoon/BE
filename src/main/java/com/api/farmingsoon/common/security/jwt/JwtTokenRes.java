package com.api.farmingsoon.common.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtTokenRes {

    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;


}
