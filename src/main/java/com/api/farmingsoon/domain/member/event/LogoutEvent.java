package com.api.farmingsoon.domain.member.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LogoutEvent {
    private String refreshToken;
    private String email;
    public LogoutEvent(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
