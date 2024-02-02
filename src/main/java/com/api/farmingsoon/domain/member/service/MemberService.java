package com.api.farmingsoon.domain.member.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.image.service.ImageService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.dto.LoginRequest;
import com.api.farmingsoon.domain.member.dto.LoginResponse;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final JwtUtils jwtUtils;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ImageService imageService;

    public Long join(JoinRequest joinRequest) {
        String profileImageUrl = imageService.uploadProfileImage(joinRequest.getProfileImg());
        return saveMemberAndProfileImage(joinRequest, profileImageUrl);
    }

    @Transactional
    private Long saveMemberAndProfileImage(JoinRequest joinRequest, String profileImageUrl) {
        Member member = joinRequest.toEntity();
        member.setEncryptedPassword(passwordEncoder.encode(joinRequest.getPassword()));
        member.setProfileImg(profileImageUrl);

        return memberRepository.save(member).getId(); // @todo 중복된 회원이 있으면 예외처리
    }
    @Transactional(readOnly = true)
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
     *  1. 로그아웃 여부 체킹
     *  2. 토큰 검증
     *  3. 인증객체 불러오기
     *  4. 기존 토큰 만료처리 and 새로운 토큰 재등록
     *  5. return
     */
    public JwtToken rotateToken(String prevRefreshToken) {
        jwtUtils.checkLogout(prevRefreshToken);
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
    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

}
