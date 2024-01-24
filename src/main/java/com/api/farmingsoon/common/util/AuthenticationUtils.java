package com.api.farmingsoon.common.util;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.ForbiddenException;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.model.MemberRole;
import com.api.farmingsoon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthenticationUtils {

    private final MemberService memberService;

    public static void checkUpdatePermission(Member member) {
        String authenticationMemberName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!authenticationMemberName.equals(member.getEmail()) || !member.getRole().getValue().equals(MemberRole.ADMIN))
        {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_UPDATE);
        }
    }

    public static void checkDeletePermission(Member member) {
        String authenticationMemberName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authenticationMemberName.equals(member.getEmail()) || !member.getRole().getValue().equals(MemberRole.ADMIN)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN_DELETE);
        }
    }
    public Member getAuthenticationMember()
    {
        return memberService.getMemberByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
