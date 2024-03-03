package com.api.farmingsoon.domain.member.listener;

import com.api.farmingsoon.common.redis.RedisService;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.item.event.BidEndKeyExpiredEvent;
import com.api.farmingsoon.domain.member.event.LoginEvent;
import com.api.farmingsoon.domain.member.event.LogoutEvent;
import com.api.farmingsoon.domain.member.event.TokenRotateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class MemberEventListener {
    private final JwtUtils jwtUtils;

    @EventListener
    @Async("testExecutor")
    public void setRefreshTokenByLogin(LoginEvent event){
        jwtUtils.setRefreshToken(event.getRefreshToken(), event.getEmail());
    }

    @EventListener
    @Async("testExecutor")
    public void rotateRefreshToken(TokenRotateEvent event){
        jwtUtils.rotateRefreshToken(event.getPrevRefreshToken(), event.getNewRefreshToken(), event.getEmail());
    }

    @EventListener
    @Async("testExecutor")
    public void deleteRefreshToken(LogoutEvent event){
        jwtUtils.deleteRefreshToken(event.getRefreshToken(), event.getEmail());
    }

}
