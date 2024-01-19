package com.api.farmingsoon.domain.member.service;

import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.security.jwt.JwtTokenRes;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final JwtUtils jwtUtils;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    /**
     *  @Description
     *  토큰 재발급
     *  1. 토큰 검증 및 인증객체 불러오기
     *  2. 기존 토큰 만료처리 and 새로운 토큰 재등록
     *  3. return
     */
    public JwtTokenRes rotateToken(String prevRefreshToken) {
        jwtProvider.validateRefreshToken(prevRefreshToken);
        Authentication authentication = jwtProvider.getAuthenticationByRefreshToken(prevRefreshToken);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtTokenRes jwtToken = jwtProvider.createJwtToken(authentication.getName(), authorities);
        jwtUtils.rotateRefreshToken(prevRefreshToken, jwtToken.getRefreshToken(), authentication.getName());
        return jwtToken;
    }

    public void logout(String refreshToken) {
        jwtUtils.deleteRefreshToken(refreshToken);
    }
}
