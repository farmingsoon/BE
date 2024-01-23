package com.api.farmingsoon.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 400
    EMPTY_REFRESH_TOKEN("RefreshToken이 필요합니다.", HttpStatus.BAD_REQUEST),
    EMPTY_EMAIL("이메일이 필요합니다.", HttpStatus.BAD_REQUEST),
    INVALID_IMAGE_TYPE("유효하지 않은 파일 형식입니다.", HttpStatus.BAD_REQUEST),
    INVALID_CATEGORY("유효하지 않은 카테고리 입니다..", HttpStatus.BAD_REQUEST),

    // 401
    LOGOUTED_TOKEN("이미 로그아웃 처리된 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_PERIOD_ACCESS_TOKEN("기한이 만료된 AccessToken입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_PERIOD_REFRESH_TOKEN("기한이 만료된 RefreshToken입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TYPE_TOKEN("Token의 타입은 Bearer입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_ACCESS_TOKEN("유효하지 않은 AccessToken입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN("유효하지 않은 RefreshToken입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_CURRENT_PASSWORD("현재 비밀번호가 일치하지 않습니다!", HttpStatus.UNAUTHORIZED),
    INVALID_NEW_PASSWORD("새 비밀번호가 일치하지 않습니다!", HttpStatus.UNAUTHORIZED),

    // 403
    FORBIDDEN_CREATE("생성 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_DELETE("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN),
    FORBIDDEN_UPDATE("수정 권한이 없습니다.", HttpStatus.FORBIDDEN),
    EMPTY_AUTHORITY("권한 정보가 비어있습니다.", HttpStatus.FORBIDDEN),

    // 404
    NOT_FOUND_MEMBER("회원이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_ITEM("상품이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_BID("입찰이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_NOTIFICATION("알림이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    NOT_FOUND_PROVIDER("지원하지 않는 소셜 로그인 플랫폼 입니다.", HttpStatus.NOT_FOUND),

    // 409
    ALREADY_JOINED("이미 존재하는 회원입니다.", HttpStatus.CONFLICT),

    //500
    FAIL_UPLOAD_S3("AWS S3 업로드 실패!", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final HttpStatus status;

    ErrorCode(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
