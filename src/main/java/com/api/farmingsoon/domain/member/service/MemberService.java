package com.api.farmingsoon.domain.member.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.common.util.CookieUtils;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.common.util.Transaction;
import com.api.farmingsoon.domain.image.event.UploadImagesRollbackEvent;
import com.api.farmingsoon.domain.image.service.ImageService;
import com.api.farmingsoon.domain.member.dto.JoinRequest;
import com.api.farmingsoon.domain.member.dto.LoginRequest;
import com.api.farmingsoon.domain.member.dto.LoginResponse;
import com.api.farmingsoon.domain.member.event.LoginEvent;
import com.api.farmingsoon.domain.member.event.LogoutEvent;
import com.api.farmingsoon.domain.member.event.TokenRotateEvent;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import io.lettuce.core.RedisCommandTimeoutException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final JwtUtils jwtUtils;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final ApplicationEventPublisher eventPublisher;
    private final ImageService imageService;
    private final Transaction transaction;

    public Long join(JoinRequest joinRequest) {
        String profileImageUrl = imageService.uploadProfileImage(joinRequest.getProfileImg());
        return saveMemberAndProfileImage(joinRequest, profileImageUrl);
    }

    @Transactional
    private Long saveMemberAndProfileImage(JoinRequest joinRequest, String profileImageUrl) {
        return transaction.invoke(() ->
            {
                eventPublisher.publishEvent(new UploadImagesRollbackEvent(List.of(profileImageUrl)));

                Member member = joinRequest.toEntity();
                member.setEncryptedPassword(passwordEncoder.encode(joinRequest.getPassword()));
                member.setProfileImg(profileImageUrl);

                return memberRepository.save(member).getId(); // @todo 중복된 회원이 있으면 예외처리
            }
        );

    }
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        String authorities = authentication.getAuthorities().stream()
                .map(a -> "ROLE_" + a.getAuthority())
                .collect(Collectors.joining(","));

        JwtToken jwtToken = jwtProvider.createJwtToken(loginRequest.getEmail(), authorities);
        CookieUtils.createAndSetJwtCookie(jwtToken, response);

        eventPublisher.publishEvent(new LoginEvent(jwtToken.getRefreshToken(), loginRequest.getEmail()));

        Member member = getMemberByEmail(loginRequest.getEmail());
        return LoginResponse.of(member);
    }


    /**
     *  @Description
     *  토큰 재발급
     *  1. 토큰 검증
     *  2. 토큰 탈취여부 검증
     *  3. 토큰 재생성
     *  4. 쿠키 재설정
     *  5. 기존 토큰 만료처리 and 새로운 토큰 재등록
     */
    public void rotateToken(String prevRefreshToken, HttpServletResponse response) {
        jwtProvider.validateRefreshToken(prevRefreshToken);
        Authentication authentication = jwtProvider.getAuthenticationByRefreshToken(prevRefreshToken);

        try { // 장애 대응을 위한 예외 캐치
            jwtUtils.checkSnatch(prevRefreshToken, authentication.getName());
        }catch (QueryTimeoutException | RedisCommandTimeoutException e1)
        {
            log.info("Redis response delay");
        }


        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        JwtToken jwtToken = jwtProvider.createJwtToken(authentication.getName(), authorities);
        CookieUtils.createAndSetJwtCookie(jwtToken, response);

        eventPublisher.publishEvent(TokenRotateEvent.builder()
                .email(authentication.getName())
                .prevRefreshToken(prevRefreshToken)
                .newRefreshToken(jwtToken.getRefreshToken()));
    }

    public void logout(String refreshToken, HttpServletResponse response) {
        jwtProvider.validateRefreshToken(refreshToken);
        Authentication authentication = jwtProvider.getAuthenticationByRefreshToken(refreshToken);

        CookieUtils.deleteJwtCookie(response);
        eventPublisher.publishEvent(new LogoutEvent(refreshToken, authentication.getName()));
    }

    @Transactional(readOnly = true)
    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }
    @Transactional(readOnly = true)
    public Optional<Member> getOptionalMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_MEMBER));
    }

}
