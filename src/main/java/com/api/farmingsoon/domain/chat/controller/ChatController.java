package com.api.farmingsoon.domain.chat.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.security.jwt.JwtProvider;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.dto.ReadMessageRequest;
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

    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest chatMessageRequest) {
        chatService.create(chatMessageRequest);
    }
    @MessageMapping("/chat/read")
    public void readMessage(ReadMessageRequest readMessageRequest) {
        chatService.read(readMessageRequest);
    }


}
