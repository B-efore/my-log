package com.jiwon.mylog.domain.notification.service;

import com.jiwon.mylog.domain.notification.entity.Notification;
import com.jiwon.mylog.domain.notification.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;

    private final SseEmitterRepository emitterRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterId = emitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(emitterId));
        emitter.onTimeout(() -> emitterRepository.delete(emitterId));
        emitter.onError((e) -> emitterRepository.delete(emitterId));

        send(emitter, emitterId, "connect", "connect completed");

        return emitter;
    }

    public void sendNotification(Long userId, Notification notification) {
        List<String> emitterIds = emitterRepository.findEmitterIdsByUserId(userId);

        for (String emitterId : emitterIds) {
            SseEmitter emitter = emitterRepository.get(emitterId).orElse(null);
            if (emitter == null) {
                emitterRepository.delete(emitterId);
                continue;
            }

            log.info("notification: {}", emitterId);
            send(emitter, emitterId, "notification", notification.getContent());
            log.info("notification: {} 성공", emitterId);
        }
    }

    private void send(SseEmitter emitter, String emitterId, String name, String content) {
        try {
            emitter.send(SseEmitter.event()
                    .id(emitterId)
                    .name(name)
                    .data(content));
        } catch (IOException e) {
            emitter.completeWithError(e);
            emitterRepository.delete(emitterId);
            throw new RuntimeException();
        }
    }
}
