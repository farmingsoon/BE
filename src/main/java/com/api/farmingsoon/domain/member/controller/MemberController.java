package com.api.farmingsoon.domain.member.controller;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.BadRequestException;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.common.security.jwt.JwtTokenRes;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public Response<Void> logoutMember() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        memberService.logout(username);
        return Response.success(HttpStatus.OK, "로그아웃 처리 되었습니다.");
    }

    @GetMapping("/rotate")
    public Response<JwtTokenRes> rotateToken(HttpServletRequest request){
        String refreshToken = JwtUtils.extractBearerToken(request.getHeader("refreshToken"));
        if(refreshToken.isBlank()) {
            throw new BadRequestException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }

        JwtTokenRes jwtTokenRes = memberService.rotateToken(refreshToken);

        return Response.success(HttpStatus.OK, "토큰이 재발급 되었습니다.", jwtTokenRes);
    }

}
