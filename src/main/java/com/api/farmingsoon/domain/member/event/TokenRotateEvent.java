package com.api.farmingsoon.domain.member.event;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class TokenRotateEvent {

    private String prevRefreshToken;
    private String newRefreshToken;
    private String email;

    @Builder
    private TokenRotateEvent(String prevRefreshToken, String newRefreshToken, String email) {
        this.prevRefreshToken = prevRefreshToken;
        this.newRefreshToken = newRefreshToken;
        this.email = email;
    }
}
