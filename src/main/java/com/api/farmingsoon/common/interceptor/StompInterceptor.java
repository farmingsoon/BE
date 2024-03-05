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
        StompCommand command = accessor.getCommand();
        log.info("command : " + command);
        /**
         * @Description
         * 1. 모든 메시지 읽음 처리
         * 2. Redis에 채팅방 참여 정보 저장
         */
        if (StompCommand.CONNECT.equals(command))
            eventPublisher.publishEvent(ChatRoomConnectEvent.builder()
                            .memberId(Long.valueOf(accessor.getFirstNativeHeader("memberId")))
                            .chatRoomId(Long.valueOf(accessor.getFirstNativeHeader("chatRoomId")))
                            .sessionId(accessor.getSessionId())
                            .build()
            );
        else if (StompCommand.DISCONNECT.equals(command)) {

        }

        return message;
    }
}
