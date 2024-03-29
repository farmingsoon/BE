package com.api.farmingsoon.domain.chatroom.service;

import com.api.farmingsoon.common.exception.ErrorCode;
import com.api.farmingsoon.common.exception.custom_exception.NotFoundException;
import com.api.farmingsoon.common.util.AuthenticationUtils;
import com.api.farmingsoon.domain.chat.dto.ChatResponse;
import com.api.farmingsoon.domain.chat.service.ChatService;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomCreateRequest;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomDetailResponse;
import com.api.farmingsoon.domain.chatroom.dto.ChatRoomResponse;
import com.api.farmingsoon.domain.chatroom.model.ChatRoom;
import com.api.farmingsoon.domain.chatroom.repository.ChatRoomRepository;
import com.api.farmingsoon.domain.item.domain.Item;
import com.api.farmingsoon.domain.item.service.ItemService;
import com.api.farmingsoon.domain.member.model.Member;
import com.api.farmingsoon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ItemService itemService;
    private final MemberService memberService;
    private final AuthenticationUtils authenticationUtils;

    @Transactional
    public ChatRoom createChatRoom(Member seller, Member buyer, Item item) {
        return chatRoomRepository.save(ChatRoom.of(seller, buyer, item));
    }
    @Transactional(readOnly = true)
    public ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM));
    }

    /**
     * @Description
     * 내가 판매자 또는 구매자로 참가하고 있는 채팅방 목록 조회
     * 채팅이 하나라도 있어야 조회됨
     */
    @Transactional(readOnly = true)
    public List<ChatRoomResponse> getChatRooms() {
        Member fromMember = memberService.getMemberByEmail(authenticationUtils.getAuthenticationMember().getEmail());
        List<ChatRoom> myChatRooms = chatRoomRepository.findChatRoomByBuyerOrSeller(fromMember, fromMember);
        return myChatRooms.stream().
                filter(chatRoom -> !chatRoom.getChatList().isEmpty()).
                map(chatRoom -> ChatRoomResponse.of(chatRoom, fromMember.getEmail()))
                .toList();
    }

    /**
     * @Description
     * 채팅방에서 사용할 상단의 상품 정보와 상대방 정보
     * 채팅목록을 따로 뺴야할 지 고민해 봐야 함
     */
    @Transactional(readOnly = true)
    public ChatRoomDetailResponse getChatRoomDetail(Long chatRoomId) {
        String fromUserEmail = authenticationUtils.getAuthenticationMember().getEmail();
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CHATROOM));

        return ChatRoomDetailResponse.of(chatRoom, fromUserEmail);
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
        Member buyer = memberService.getMemberById(chatRoomCreateRequest.getBuyerId());
        Member seller = item.getMember();

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findChatRoomByBuyerAndItem(buyer, item);

        return optionalChatRoom.orElseGet(()
                -> createChatRoom(seller, buyer, item)).getId();

    }
}
