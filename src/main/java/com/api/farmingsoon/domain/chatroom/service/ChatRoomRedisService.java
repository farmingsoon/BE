package com.api.farmingsoon.domain.chatroom.service;

import com.api.farmingsoon.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomRedisService {
    private final RedisService redisService;

    public void ConnectChatRoom(Long chatRoomId, String sessionId) {
        redisService.addToSet("chatRoom_" + chatRoomId, sessionId);
    }
}
