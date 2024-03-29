package com.api.farmingsoon.domain.chat.service;

import com.api.farmingsoon.domain.chat.dto.ChatListResponse;
import com.api.farmingsoon.domain.chat.dto.ChatMessageRequest;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.event.ChatSaveEvent;
import com.api.farmingsoon.domain.chat.model.Chat;
import com.api.farmingsoon.domain.chat.repository.ChatRepository;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomRedisService;
import com.api.farmingsoon.domain.chatroom.service.ChatRoomService;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import com.api.farmingsoon.domain.notification.event.NotReadChatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatRoomService chatRoomService;
    private final MemberService memberService;
    private final ApplicationEventPublisher eventPublisher;
    private final ChatRoomRedisService chatRoomRedisService;

    @Transactional
    public void create(ChatMessageRequest chatMessageRequest) {
        Long connectMemberSize = chatRoomRedisService.getConnectMemberSize("chatRoom_" + chatMessageRequest.getChatRoomId());
        log.info("connectMemberSize : " + connectMemberSize);
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessageRequest.getChatRoomId());
        Member sender = memberService.getMemberById(chatMessageRequest.getSenderId());
        Chat chat = chatRepository.save(
                Chat.builder().
                    sender(sender)
                    .message(chatMessageRequest.getMessage())
                    .isRead(connectMemberSize == 2) // 채팅방에 둘 모두 존재한다면 읽음으로 처리
                    .chatRoom(chatRoom).build()
        );

        Member receiver = ChatRoom.resolveToReceiver(chatRoom, sender.getEmail());

        if(connectMemberSize == 1) // 채팅방에 상대방이 없다면 알림을 전송
            eventPublisher.publishEvent(new NotReadChatEvent(receiver.getId()));

        eventPublisher.publishEvent(
                ChatSaveEvent.builder()
                        .chatRoomId(chatMessageRequest.getChatRoomId())
                        .receiverId(receiver.getId())
                        .chatResponse(ChatResponse.of(chat))
                .build());

    }
    @Transactional(readOnly = true)
    public ChatListResponse getChats(Long chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        return ChatListResponse.of(chatRepository.findByChatRoomOrderByIdDesc(chatRoom, pageable));
    }

    @Transactional
    public void readAllMyNotReadChatList(Long chatRoomId, Long memberId) {
        ChatRoom chatRoom = chatRoomService.getChatRoom(chatRoomId);
        Member member = memberService.getMemberById(memberId);
        chatRepository.readAllMyNotReadChatList(chatRoom, member);

        /*
        List<Chat> myNotReadChatList = chatRepository.findMyNotReadChatList(chatRoom, member);
        myNotReadChatList.forEach(Chat::read);
        */
    }
}
