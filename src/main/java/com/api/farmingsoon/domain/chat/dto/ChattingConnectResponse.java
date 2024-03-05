package com.api.farmingsoon.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ChattingConnectResponse {

    private Long connectMemberId;
    private final String type = "SEND";

    public ChattingConnectResponse(Long connectMemberId) {
        this.connectMemberId = connectMemberId;
    }
}
