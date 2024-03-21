package com.api.farmingsoon.common.security.oauth;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.security.jwt.JwtToken;
import com.api.farmingsoon.common.util.CookieUtils;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.member.dto.LoginResponse;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.model.MemberRole;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    private final JwtUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");
        String provider = oAuth2User.getAttribute("provider");

        Optional<Member> findMember = memberRepository.findByEmail(email + "_" + provider);

        // 회원이 아닌 경우에 회원 가입 진행
        Member member = null;
        if (findMember.isEmpty()) {
            member = Member.builder()
                    .email(email + "_" + provider)
                    .nickname(name)
                    .role(MemberRole.MEMBER)
                    .profileImg(picture)
                    .build();

            memberRepository.save(member);
        } else {
            member = findMember.get();
        }

        // OAuth2User 객체에서 권한 가져옴
        JwtToken jwtToken = jwtProvider.createJwtToken(member.getEmail(), member.getRole().getValue());
        CookieUtils.createAndSetJwtCookie(jwtToken, response);
        jwtUtils.setRefreshToken(jwtToken.getRefreshToken(), email);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        response.setStatus(200);

        response.getWriter().write(
                objectMapper.writeValueAsString(LoginResponse.of(member))
        );
    }
}
