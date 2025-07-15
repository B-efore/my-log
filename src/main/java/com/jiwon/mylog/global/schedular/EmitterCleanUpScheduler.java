package com.jiwon.mylog.global.schedular;

import com.jiwon.mylog.domain.notification.repository.SseEmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class EmitterCleanUpScheduler {

    private static final long TTL = 30 * 60 * 1000L;

    private final SseEmitterRepository emitterRepository;

    @Scheduled(fixedDelay = 10 * 60 * 1000L)
    public void cleanUpExpiredEmitters() {
        long now = System.currentTimeMillis();

        emitterRepository.findAllEmitterTimestamps().forEach((emitterId, time) -> {
            if (now - time > TTL) {
                emitterRepository.delete(emitterId);
                log.info("emitter time out: {}", emitterId);
            }
        });
    }
}