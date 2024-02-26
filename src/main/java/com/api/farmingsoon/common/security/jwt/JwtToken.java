package com.api.farmingsoon.common.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtToken {

    private String tokenType;
    private String accessToken;
    private String refreshToken;
    private Long accessExpirationTime;
    private Long refreshExpirationTime;

    @Builder
    private JwtToken(String tokenType, String accessToken, String refreshToken, Long accessExpirationTime, Long refreshExpirationTime) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessExpirationTime = accessExpirationTime;
        this.refreshExpirationTime = refreshExpirationTime;
    }
}
