package com.api.farmingsoon.domain.member.dto;

import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginResponse {

    private final Long memberId;
    private final String nickname;
    private final String profileImgUrl;
    private final String tokenType;
    private final String accessToken;
    private final String refreshToken;

    @Builder
    private LoginResponse(String accessToken, String refreshToken, String nickname, String profileImgUrl, Long memberId){
        this.memberId = memberId;
        this.tokenType = "Bearer";
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static LoginResponse of(JwtToken jwtToken, Member member) {
        return LoginResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImg())
                .accessToken(jwtToken.getAccessToken())
                .refreshToken(jwtToken.getRefreshToken())
                .build();
    }
}
