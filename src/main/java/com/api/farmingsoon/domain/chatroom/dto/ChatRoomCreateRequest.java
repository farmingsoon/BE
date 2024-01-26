package com.api.farmingsoon.domain.chatroom.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomCreateRequest {

    /**
     * @Description
     * 추후에 모든 회원들은 nickname을 이용하며 이 부분도 email이 아닌 nickname을 사용하도록 만들면 좋을 것 같습니다.
     */
    @NotNull
    private String buyerName;

    @NotNull
    private Long itemId;

    @Builder
    public ChatRoomCreateRequest(String buyerName, Long itemId) {
        this.buyerName = buyerName;
        this.itemId = itemId;
    }

    public static ChatRoomCreateRequest of(String buyerName, Long itemId) {
        return ChatRoomCreateRequest
                .builder()
                .buyerName(buyerName)
                .itemId(itemId)
                .build();
    }
}
