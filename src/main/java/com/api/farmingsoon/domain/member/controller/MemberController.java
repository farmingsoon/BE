package com.api.farmingsoon.domain.member.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.dto.LoginRequest;
import com.api.farmingsoon.domain.member.dto.LoginResponse;
import com.api.farmingsoon.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/join")
    public Response<Long> join(@ModelAttribute @Valid JoinRequest joinRequest) throws IOException {
        Long memberId = memberService.join(joinRequest);
        return Response.success(HttpStatus.OK, "회원가입이 성공적으로 처리되었습니다.", memberId);
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = memberService.login(loginRequest, response);
        return Response.success(HttpStatus.OK, "토큰이 발급 되었습니다.", loginResponse);
    }


    @PostMapping("/refresh-token/logout")
    public Response<Void> logoutMember(HttpServletRequest request) {
        String refreshToken = JwtUtils.getRefreshToken(request);
        memberService.logout(refreshToken);
        return Response.success(HttpStatus.OK, "로그아웃 처리 되었습니다.");
    }

    @GetMapping("/refresh-token/rotate")
    public Response<Void> rotateToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = JwtUtils.getRefreshToken(request);
        memberService.rotateToken(refreshToken, response);

        return Response.success(HttpStatus.OK, "토큰이 재발급 되었습니다.");
    }

}
