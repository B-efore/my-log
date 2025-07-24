package com.jiwon.mylog.domain.event;

import com.jiwon.mylog.domain.event.dto.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.FollowCreatedEvent;
import com.jiwon.mylog.domain.event.dto.FollowDeletedEvent;
import com.jiwon.mylog.domain.event.dto.LikeCreatedEvent;
import com.jiwon.mylog.domain.notification.entity.Notification;
import com.jiwon.mylog.domain.notification.repository.NotificationRepository;
import com.jiwon.mylog.domain.notification.service.NotificationService;
import com.jiwon.mylog.domain.notification.entity.NotificationType;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        Long receiverId = event.getPostWriterId();
        User receiver = getReceiver(receiverId);

        Notification notification = Notification.create(
                receiver,
                event.getCommentWriterName() + "왹이 외계 수집물에 발  사를 남기다!",
                "/posts/" + event.getPostId(),
                NotificationType.COMMENT
        );
        notificationRepository.save(notification);

        sendSSE(receiverId, notification);
    }

    @Transactional
    @EventListener
    public void handleLikeCreated(LikeCreatedEvent event) {
        Long receiverId = event.getPostWriterId();
        User receiver = getReceiver(receiverId);

        Notification notification = Notification.create(
                receiver,
                event.getLikeWriterName() + "왹이 외계 수집물에 푸  딩을 달았다!",
                "/posts/" + event.getPostId(),
                NotificationType.LIKE
        );
        notificationRepository.save(notification);

        sendSSE(receiverId, notification);
    }

    @Transactional
    @EventListener
    public void handleFollowCreated(FollowCreatedEvent event) {
        Long receiverId = event.getReceiverId();
        User receiver = getReceiver(event.getReceiverId());

        Notification notification = Notification.create(
                receiver,
                event.getFollowerName() + "왹이 잡 았다! 너 잡혔다!",
                "/" + event.getFollowerId(),
                NotificationType.FOLLOW
        );
        notificationRepository.save(notification);

        sendSSE(receiverId, notification);
    }

    @Transactional
    @EventListener
    public void handleUnFollowCreated(FollowDeletedEvent event) {
        Long receiverId = event.getReceiverId();
        User receiver = getReceiver(receiverId);

        Notification notification = Notification.create(
                receiver,
                "오오자비로운" + event.getFollowerName() + "왹께서널놓아주시니",
                "/" + event.getFollowerId(),
                NotificationType.FOLLOW
        );
        notificationRepository.save(notification);

        sendSSE(receiverId, notification);
    }

    private User getReceiver(Long receiverId) {
        return userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }

    private void sendSSE(Long receiverId, Notification notification) {
        try {
            notificationService.sendNotification(receiverId, notification);
        } catch(Exception e) {
            log.warn("SSE 전송 실패");
        }
    }
}
