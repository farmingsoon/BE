package com.api.farmingsoon.domain.chatroom.service;

import com.api.farmingsoon.common.redis.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomRedisService {
    private final RedisService redisService;

    public void connectChatRoom(Long chatRoomId, String sessionId) {
        log.info("Chatroom Connect : chatRoomId = " + chatRoomId + " sessionId = " + sessionId);
        redisService.addToSet("chatRoom_" + chatRoomId, sessionId);
        redisService.setData(sessionId, chatRoomId); // session에 대한 채팅방 정보 저장
    }

    /**
     * 채팅방에 남은 사람이 한명이라면 나갈 때 키 삭제
     */
    public void disConnectChatRoom(String sessionId) {
        Long chatRoomId = (Long) redisService.getData(sessionId);
        if(redisService.getSetSize("chatRoom_" + chatRoomId) == 1){
            redisService.deleteData("chatRoom_" + chatRoomId);
        }
        else {
            redisService.deleteToSet("chatRoom_" + chatRoomId, sessionId);
        }
        log.info("Chatroom DisConnect : chatRoomId = " + chatRoomId + " sessionId = " + sessionId);
    }
    public Long getConnectMemberSize(String key){
        return redisService.getSetSize(key);
    }
}
