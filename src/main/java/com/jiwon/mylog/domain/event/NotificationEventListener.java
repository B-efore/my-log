package com.jiwon.mylog.domain.event;

import com.jiwon.mylog.domain.event.dto.comment.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.follow.FollowCreatedEvent;
import com.jiwon.mylog.domain.event.dto.follow.FollowDeletedEvent;
import com.jiwon.mylog.domain.event.dto.like.LikeCreatedEvent;
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
        if (event.postWriterId().equals(event.commentWriterId())) {
            return;
        }

        try {
            handleNotification(
                    event.postWriterId(),
                    event.commentWriterName() + "왹이 외계 수집물에 발  사를 남기다!",
                    "/posts/" + event.postId(),
                    NotificationType.COMMENT
            );
        } catch (Exception e) {
            log.warn("댓글 생성 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    @Transactional
    @EventListener
    public void handleLikeCreated(LikeCreatedEvent event) {
        try {
            handleNotification(
                    event.receiverId(),
                    event.senderName() + "왹이 외계 수집물에 푸  딩을 달았다!",
                    "/posts/" + event.targetId(),
                    NotificationType.LIKE
            );
        } catch (Exception e) {
            log.warn("좋아요 생성 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    @Transactional
    @EventListener
    public void handleFollowCreated(FollowCreatedEvent event) {
        try {
            handleNotification(
                    event.receiverId(),
                    event.followerName() + "왹이 잡 았다! 너 잡혔다!",
                    "/" + event.followerId(),
                    NotificationType.FOLLOW
            );
        } catch (Exception e) {
            log.warn("팔로우 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    @Transactional
    @EventListener
    public void handleUnFollowCreated(FollowDeletedEvent event) {
        try {
            handleNotification(
                    event.receiverId(),
                    "오오자비로운" + event.followerName() + "왹께서널놓아주시니",
                    "/" + event.followerId(),
                    NotificationType.FOLLOW
            );
        } catch (Exception e) {
            log.warn("언팔로우 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    private void handleNotification(Long receiverId, String content, String url, NotificationType type) {
        User receiver = getReceiver(receiverId);
        Notification notification = Notification.create(receiver, content, url, type);
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
