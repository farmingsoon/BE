package com.api.farmingsoon.domain.chat.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public List<ChatResponse> chatHistory(
                    @PathVariable(name = "chatRoomId") Long chatRoomId,
                    @PageableDefault(size = 8, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable)
    {
        return chatService.getChats(chatRoomId, pageable);
    }

}
