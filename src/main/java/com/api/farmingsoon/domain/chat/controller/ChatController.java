package com.api.farmingsoon.domain.chat.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    @LoginChecking
    @MessageMapping("/chat/message")
    public void sendMessage(ChatMessageRequest chatMessageRequest) {
        ChatResponse chatResponse = chatService.create(chatMessageRequest);
        messagingTemplate.convertAndSend("/sub/chat-room/" + chatMessageRequest.getChatRoomId(), chatResponse);
    }


}
