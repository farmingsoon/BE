package com.api.farmingsoon.domain.chatroom.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomCreateRequest;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.chatroom.repository.ChatRoomRepository;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ItemService itemService;
    private final MemberService memberService;

    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM));
    }

    /**
     * @Description
     * 구매자의 nickname(지금은 email) 값과 item의 id를 받아와 채팅방을 생성합니다.
     * 구매자가 채팅을 걸던 판매자가 채팅을 걸던 동일한 요청 값
     * 기존 채팅방이 있다면 기존 채팅방의 아이디를 넘기고 없다면 새로 생성합니다.
     * */
    @Transactional
    public Long handleChatRoom(ChatRoomCreateRequest chatRoomCreateRequest) {
        Item item = itemService.getItemById(chatRoomCreateRequest.getItemId());
        Member buyer = memberService.getMemberByEmail(chatRoomCreateRequest.getBuyerName());
        Member seller = item.getMember();

        ChatRoom chatRoom = chatRoomRepository.findChatRoomByBuyerAndItem(buyer, item).orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_ITEM));
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomByBuyerAndItem(buyer, item);

        return optionalChatRoom.orElseGet(()
                -> createChatRoom(seller, buyer, item)).getId();

    }
    public ChatRoom createChatRoom(Member seller, Member buyer, Item item) {
        return chatRoomRepository.save(ChatRoom.of(seller, buyer, item));
    }

}
