package com.api.farmingsoon.domain.member.event;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class LoginEvent {
    private String refreshToken;
    private String email;

    public LoginEvent(String refreshToken, String email) {
        this.refreshToken = refreshToken;
        this.email = email;
    }
}
