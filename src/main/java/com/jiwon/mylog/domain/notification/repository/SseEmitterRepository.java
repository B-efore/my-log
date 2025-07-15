package com.jiwon.mylog.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Long> connectionsByUserId = new ConcurrentHashMap<>();
    private final Map<String, Long> emitterTimestamps = new ConcurrentHashMap<>();

    public String save(Long userId, SseEmitter emitter) {
        String emitterId = generateConnectionId(userId);
        emitters.put(emitterId, emitter);
        connectionsByUserId.put(emitterId, userId);
        emitterTimestamps.put(emitterId, System.currentTimeMillis());
        return emitterId;
    }

    public Optional<SseEmitter> get(String emitterId) {
        return Optional.ofNullable(emitters.get(emitterId));
    }

    public void delete(String emitterId) {
        emitters.remove(emitterId);
        connectionsByUserId.remove(emitterId);
        emitterTimestamps.remove(emitterId);
    }

    public List<String> findEmitterIdsByUserId(Long userId) {
        return connectionsByUserId.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(Map.Entry::getKey)
                .toList();
    }

    public List<SseEmitter> findEmittersByUserId(Long userId) {
        return connectionsByUserId.entrySet().stream()
                .filter(entry -> entry.getValue().equals(userId))
                .map(entry -> emitters.get(entry.getKey()))
                .filter(Objects::nonNull)
                .toList();
    }

    public Map<String, Long> findAllEmitterTimestamps() {
        return new HashMap<>(emitterTimestamps);
    }

    private String generateConnectionId(Long userId) {
        return userId + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
