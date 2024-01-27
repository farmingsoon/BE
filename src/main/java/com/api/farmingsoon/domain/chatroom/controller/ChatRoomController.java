package com.api.farmingsoon.domain.chatroom.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomCreateRequest;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomResponse;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @LoginChecking
    @GetMapping
    public Response<List<ChatRoomResponse>> getChatRoomList() {
        return Response.success(HttpStatus.OK,"나의 채팅방 목록", chatRoomService.getChatRooms());
    }

    @LoginChecking
    @PostMapping
    public Response<Void> createChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest){
        chatRoomService.handleChatRoom(chatRoomCreateRequest);
        return Response.success(HttpStatus.OK, "채팅방이 성공적으로 생성되었습니다.");
    }
}
