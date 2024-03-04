package com.api.farmingsoon.common.interceptor;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.ForbiddenException;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.util.CookieUtils;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.chatroom.event.ChatRoomConnectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInterceptor implements ChannelInterceptor {
    private final JwtProvider jwtProvider;
    private final ApplicationEventPublisher eventPublisher;
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("command : " + accessor.getCommand());


        if (StompCommand.CONNECT.equals(accessor.getCommand()))
            eventPublisher.publishEvent(ChatRoomConnectEvent.builder()
                            .memberId(Long.valueOf(accessor.getFirstNativeHeader("memberId")))
                            .chatRoomId(Long.valueOf(accessor.getFirstNativeHeader("chatRoomId")))
                            .build()
            );



        return message;
    }
}
