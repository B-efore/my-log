package com.jiwon.mylog.global.schedular;

import com.jiwon.mylog.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime fourteenDaysAgo = LocalDateTime.now().minusDays(14);
        try {
            notificationRepository.deleteAllByCreatedAtBefore(fourteenDaysAgo);
        } catch (Exception e) {
            log.error("알림 삭제 실패: {}", e.getMessage());
        }
    }
}
