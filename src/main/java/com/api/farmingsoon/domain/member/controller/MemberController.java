package com.api.farmingsoon.domain.member.controller;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.BadRequestException;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.dto.LoginRequest;
import com.api.farmingsoon.domain.member.dto.LoginResponse;
import com.api.farmingsoon.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;


    @PostMapping(value = "/join")
    public Response<Void> join(@ModelAttribute @Valid JoinRequest joinRequest) throws IOException {
        memberService.join(joinRequest);
        return Response.success(HttpStatus.OK, "회원가입이 성공적으로 처리되었습니다.");
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse loginResponse = memberService.login(loginRequest);
        return Response.success(HttpStatus.OK, "토큰이 재발급 되었습니다.", loginResponse);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public Response<Void> logoutMember() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        memberService.logout(username);
        return Response.success(HttpStatus.OK, "로그아웃 처리 되었습니다.");
    }

    @GetMapping("/rotate")
    public Response<JwtToken> rotateToken(HttpServletRequest request){
        String refreshToken = JwtUtils.extractBearerToken(request.getHeader("refreshToken"));
        if(refreshToken.isBlank()) {
            throw new BadRequestException(ErrorCode.EMPTY_REFRESH_TOKEN);
        }

        JwtToken jwtToken = memberService.rotateToken(refreshToken);

        return Response.success(HttpStatus.OK, "토큰이 재발급 되었습니다.", jwtToken);
    }

}
