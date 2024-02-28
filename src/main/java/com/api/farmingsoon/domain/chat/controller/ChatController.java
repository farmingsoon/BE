package com.api.farmingsoon.domain.chat.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final JwtProvider jwtProvider;

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest chatMessageRequest, @Header("Authorization") String accessToken) {
        String ac = JwtUtils.extractBearerToken(accessToken);
        ChatResponse chatResponse = chatService.create(chatMessageRequest);
        messagingTemplate.convertAndSend("/sub/chat-room/" + chatMessageRequest.getChatRoomId(), chatResponse);
    }


}
