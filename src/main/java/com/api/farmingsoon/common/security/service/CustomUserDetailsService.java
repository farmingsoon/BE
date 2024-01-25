package com.api.farmingsoon.common.security.service;

import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.repository.MemberRepository;
import com.api.farmingsoon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.api.farmingsoon.common.exception.ErrorCode.NOT_FOUND_MEMBER;

@RequiredArgsConstructor
@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberService memberService;

    /**
     * @Description
     * 폼 로그인 진행시 수행
     * email을 통해 member를 얻어오고 이를 통해 UserDetails 객체를 만들기만 하면 되기 때문에
     * 별도로 UserDetails를 구현하는 클래스는 만들지 않았습니다.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberService.getMemberByEmail(email);
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()));
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .authorities(authorities)
                .build();
    }
}