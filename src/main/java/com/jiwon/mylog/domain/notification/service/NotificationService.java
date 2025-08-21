package com.jiwon.mylog.domain.notification.service;

import com.jiwon.mylog.domain.event.dto.NotificationSendEvent;
import com.jiwon.mylog.domain.notification.dto.NotificationCountResponse;
import com.jiwon.mylog.domain.notification.dto.NotificationResponse;
import com.jiwon.mylog.domain.notification.entity.Notification;
import com.jiwon.mylog.domain.notification.entity.NotificationType;
import com.jiwon.mylog.domain.notification.repository.NotificationRepository;
import com.jiwon.mylog.domain.notification.repository.SseEmitterRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 30 * 60 * 1000L;

    private final ApplicationEventPublisher eventPublisher;

    private final SseEmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public void saveNotification(Long receiverId, String content, String url, NotificationType type) {
        User receiver = getReceiver(receiverId);
        Notification notification = Notification.create(receiver, content, url, type);
        notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationSendEvent(receiverId, content));
    }

    @CacheEvict(value = "notification::count", key="'userId:' + #userId", condition = "#userId != null")
    @Transactional
    public void updateNotificationRead(Long userId) {
        notificationRepository.updateReadStateByReceiverId(userId);
    }

    @Transactional(readOnly = true)
    public PageResponse getAllNotifications(Long userId, Pageable pageable) {
        Page<Notification> notificationPage = notificationRepository.findAllByReceiverId(userId, pageable);
        List<NotificationResponse> notifications = notificationPage.stream()
                .map(NotificationResponse::from)
                .toList();

        return PageResponse.from(
                notifications,
                notificationPage.getNumber(),
                notificationPage.getSize(),
                notificationPage.getTotalPages(),
                notificationPage.getTotalElements()
        );
    }

    @Cacheable(value = "notification::count", key = "'userId:' + #userId", condition = "#userId != null")
    @Transactional(readOnly = true)
    public NotificationCountResponse countUnreadNotifications(Long userId) {
        long countedNotification = notificationRepository.countByReceiverIdAndReadIsFalse(userId);
        return new NotificationCountResponse(countedNotification);
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        String emitterId = emitterRepository.save(userId, emitter);

        emitter.onCompletion(() -> emitterRepository.delete(emitterId));
        emitter.onTimeout(() -> emitterRepository.delete(emitterId));
        emitter.onError((e) -> emitterRepository.delete(emitterId));

        send(emitter, emitterId, "connect", "connect completed");

        return emitter;
    }

    public void sendNotification(Long userId, String content) {
        List<String> emitterIds = emitterRepository.findEmitterIdsByUserId(userId);

        for (String emitterId : emitterIds) {
            SseEmitter emitter = emitterRepository.get(emitterId).orElse(null);
            if (emitter == null) {
                emitterRepository.delete(emitterId);
                continue;
            }

            log.info("notification: {}", emitterId);
            send(emitter, emitterId, "notification", content);
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

    private User getReceiver(Long receiverId) {
        return userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }
}
