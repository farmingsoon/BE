package com.api.farmingsoon.domain.chat.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ChatApiController {
    private final ChatService chatService;

    @LoginChecking
    @GetMapping("/api/chats/{chatRoomId}")
    public List<ChatResponse> chatHistory(@PathVariable(name = "chatRoomId") Long chatRoomId) {
        return chatService.getChats(chatRoomId);
    }

}
