package com.api.farmingsoon.domain.chatroom.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomCreateRequest;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    @LoginChecking
    @PostMapping
    public Response<Void> createChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest){
        chatRoomService.handleChatRoom(chatRoomCreateRequest);
        return Response.success(HttpStatus.OK, "채팅방이 성공적으로 생성되었습니다.");
    }
}
