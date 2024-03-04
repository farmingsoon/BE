package com.api.farmingsoon.domain.chat.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.domain.chat.dto.ChatListResponse;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.dto.ReadMessageRequest;
import com.api.farmingsoon.domain.chat.event.ChatSaveEvent;
import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chat.repository.ChatRepository;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomService;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void create(ChatMessageRequest chatMessageRequest) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessageRequest.getChatRoomId());
        Member sender = memberService.getMemberById(chatMessageRequest.getSenderId());
        Chat chat = chatRepository.save(
                Chat.builder().
                    sender(sender)
                    .message(chatMessageRequest.getMessage())
                    .isRead(false)
                    .chatRoom(chatRoom).build()
        );

        eventPublisher.publishEvent(
                ChatSaveEvent.builder()
                        .chatRoomId(chatMessageRequest.getChatRoomId())
                        .receiverId(ChatRoom.resolveToReceiver(chatRoom, sender.getEmail()).getId())
                        .chatResponse(ChatResponse.of(chat))
                .build());

    }
    @Transactional(readOnly = true)
    public ChatListResponse getChats(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        return ChatListResponse.of(chatRepository.findByChatRoomOrderByIdAsc(chatRoom, pageable));
    }

    @Transactional
    public void read(ReadMessageRequest readMessageRequest) {
        Chat chat = chatRepository.findById(readMessageRequest.getChatId()).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHAT));
        chat.read();
    }

    @Transactional
    public void readAll(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        Member member = memberService.getMemberById(memberId);
        List<Chat> myNotReadChatList = chatRepository.findMyNotReadChatList(chatRoom, member);
        myNotReadChatList.forEach(Chat::read);
    }
}
