package com.api.farmingsoon.common.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static org.springframework.web.servlet.mvc.method.annotation.SseEmitter.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = createEmitter(userId);
        log.info("구독 시도");
        sendToClient(userId, "EventStream Created. [userId=" + userId + "]");
        return emitter;
    }


    public void sendToClient(Long receiverId, Object data) {
        SseEmitter emitter = sseEmitterRepository.get(receiverId);
        if (emitter != null) {
            try {
                log.info("메시지 전송 전");
                emitter.send(
                    event().name("sse")
                            .data(data)
                );
                log.info("메시지 전송 후");
            } catch (IOException exception) {
                sseEmitterRepository.deleteById(receiverId);
                emitter.completeWithError(exception);
            }
        }
    }

    private SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(id, emitter);

        emitter.onError((callback) ->
        {
            sseEmitterRepository.deleteById(id);
            log.info(String.valueOf(callback));
            log.info("모든 데이터 전송 완료");
        });

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.

        emitter.onCompletion(() ->
        {
            sseEmitterRepository.deleteById(id);
            log.info("모든 데이터 전송 완료");
        });
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() ->
        {
            sseEmitterRepository.deleteById(id);
            log.info("타임아웃");
        });

        return emitter;
    }
}
