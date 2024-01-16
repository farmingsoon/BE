package com.api.farmingsoon.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseMessage {

    JOIN_MEMBER("회원 가입 성공"),
    UPDATE_MEMBER("회원 정보 수정 성공"),
    DELETE_MEMBER("회원 탈퇴 성공");

    private final String message;
}
