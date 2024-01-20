package com.api.farmingsoon.domain.member.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.DuplicateException;
import com.api.farmingsoon.common.exception.custom_exception.InvalidException;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.dto.LoginRequest;
import com.api.farmingsoon.domain.member.dto.LoginResponse;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.model.MemberRole;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final JwtUtils jwtUtils;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void join(JoinRequest joinRequest) {
        // 중복된 회원이 있는지 확인
        memberRepository.findByEmail(joinRequest.getEmail()).ifPresent(it -> {
            throw new DuplicateException(ErrorCode.ALREADY_JOINED);
        });

        String profileImgUrl = "";
        /**
         * @Todo
         * 이미지 업로드 로직
         */

        Member member = Member.builder()
                .nickname(joinRequest.getNickname())
                .email(joinRequest.getEmail())
                .profileImg(profileImgUrl)
                .role(MemberRole.MEMBER)
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .build();


        memberRepository.save(member);
    }
    public LoginResponse login(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String authorities = authentication.getAuthorities().stream()
                .map(a -> "ROLE_" + a.getAuthority())
                .collect(Collectors.joining(","));

        JwtToken jwtToken = jwtProvider.createJwtToken(loginRequest.getEmail(), authorities);
        Member member = getMemberByEmail(loginRequest.getEmail());
        return LoginResponse.of(jwtToken, member);
    }


    /**
     *  @Description
     *  토큰 재발급
     *  1. 토큰 검증 및 인증객체 불러오기
     *  2. 기존 토큰 만료처리 and 새로운 토큰 재등록
     *  3. return
     */
    public JwtToken rotateToken(String prevRefreshToken) {
        jwtProvider.validateRefreshToken(prevRefreshToken);
        Authentication authentication = jwtProvider.getAuthenticationByRefreshToken(prevRefreshToken);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtToken jwtToken = jwtProvider.createJwtToken(authentication.getName(), authorities);
        jwtUtils.rotateRefreshToken(prevRefreshToken, jwtToken.getRefreshToken(), authentication.getName());
        return jwtToken;
    }

    public void logout(String refreshToken) {
        jwtUtils.deleteRefreshToken(refreshToken);
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

}
