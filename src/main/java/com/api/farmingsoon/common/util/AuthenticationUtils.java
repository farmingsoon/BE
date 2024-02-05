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

    /**
     * @Description
     * 자기 게시물이 아닌데 ADMIN도 아니라면 에러
     * OSIV 때문에 서비스단에서 꺼내서 사용해야함
     */

    public static void checkUpdatePermission(String email, MemberRole memberRole) {
        String authenticationMemberName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!authenticationMemberName.equals(email))
        {
            if (!memberRole.equals(MemberRole.ADMIN)) {
                throw new ForbiddenException(ErrorCode.FORBIDDEN_UPDATE);
            }
        }
    }

    public static void checkDeletePermission(String email, MemberRole memberRole) {
        String authenticationMemberName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!authenticationMemberName.equals(email))
        {
            if (!memberRole.equals(MemberRole.ADMIN)) {
                throw new ForbiddenException(ErrorCode.FORBIDDEN_DELETE);
            }
        }
    }
    public Member getAuthenticationMember()
    {
        return memberService.getMemberByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
