package com.api.farmingsoon.common.aop;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginCheckingAop {

    @Pointcut("@annotation(com.api.farmingsoon.common.annotation.LoginChecking)")
    private void enableLoginChecking(){}

    @Around("enableLoginChecking()")
    public void around(ProceedingJoinPoint joinPoint) throws Throwable {
         if(SecurityContextHolder.getContext().getAuthentication().getPrincipal().equals("anonymousUser")) {
            throw new ForbiddenException(ErrorCode.NOT_LOGIN);
        }
        joinPoint.proceed();
    }
}
