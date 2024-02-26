package com.api.farmingsoon.domain.chatroom.controller;

import com.api.farmingsoon.common.annotation.LoginChecking;
import com.api.farmingsoon.common.response.Response;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomCreateRequest;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomDetailResponse;
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
    @GetMapping("/me")
    public Response<List<ChatRoomResponse>> getChatRoomList() {
        return Response.success(HttpStatus.OK,"나의 채팅방 목록", chatRoomService.getChatRooms());
    }
    @LoginChecking
    @GetMapping("/{chatRoomId}")
    public Response<ChatRoomDetailResponse> getChatRoomDetail(@PathVariable(name = "chatRoomId") Long chatRoomId){
        return Response.success(HttpStatus.OK, "채팅방 상세", chatRoomService.getChatRoomDetail(chatRoomId));
    }
    /**
     * 채팅방 생성 후 해당 채팅방을 선택하도록 구현
     */
    @LoginChecking
    @PostMapping
    public Response<Long> createChatRoom(@RequestBody ChatRoomCreateRequest chatRoomCreateRequest){
        Long chatRoomId = chatRoomService.handleChatRoom(chatRoomCreateRequest);
        return Response.success(HttpStatus.OK, "채팅방이 성공적으로 생성되었습니다.", chatRoomId);
    }


}
