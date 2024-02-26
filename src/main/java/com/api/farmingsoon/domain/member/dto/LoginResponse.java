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

    @Builder
    private LoginResponse(String nickname, String profileImgUrl, Long memberId){
        this.memberId = memberId;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
    }

    public static LoginResponse of(Member member) {
        return LoginResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImg())
                .build();
    }
}
