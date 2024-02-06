package com.api.farmingsoon.domain.chat.service;

import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.chat.dto.ChatListResponse;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chat.repository.ChatRepository;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomService;
import com.api.farmingsoon.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomService chatRoomService;
    private final AuthenticationUtils authenticationUtils;

    public ChatResponse create(ChatMessageRequest chatMessageRequest) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessageRequest.getChatRoomId());
        Member sender = authenticationUtils.getAuthenticationMember();
        Chat chat = chatRepository.save(Chat.of(chatMessageRequest.getMessage(), sender, chatRoom));

        return ChatResponse.of(chat);

    }

    public ChatListResponse getChats(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        return ChatListResponse.of(chatRepository.findByChatRoom(chatRoom, pageable));
    }
}
