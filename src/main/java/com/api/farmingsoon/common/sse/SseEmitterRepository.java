package com.api.farmingsoon.common.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class SseEmitterRepository {
    // 모든 Emitters를 저장하는 ConcurrentHashMap
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 주어진 아이디와 이미터를 저장
     * @param id      - 사용자 아이디.
     * @param emitter - 이벤트 Emitter.
     */
    public void save(Long id, SseEmitter emitter) {
        emitters.put(id, emitter);
    }
    public void deleteById(Long id) {
        emitters.remove(id);
    }

    public SseEmitter get(Long id) {
        return emitters.get(id);
    }
}
