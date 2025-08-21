package com.jiwon.mylog.domain.event;

import com.jiwon.mylog.domain.event.dto.NotificationSendEvent;
import com.jiwon.mylog.domain.event.dto.comment.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.follow.FollowCreatedEvent;
import com.jiwon.mylog.domain.event.dto.follow.FollowDeletedEvent;
import com.jiwon.mylog.domain.event.dto.like.LikeCreatedEvent;
import com.jiwon.mylog.domain.notification.service.NotificationService;
import com.jiwon.mylog.domain.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async("Async")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCommentCreated(CommentCreatedEvent event) {
        for (Long receiverId : event.receiverIds()) {
            try {
                notificationService.saveNotification(
                        receiverId,
                        event.commentWriterName() + "왹이 외계 수집물에 발  사를 남기다!",
                        "/posts/" + event.postId(),
                        NotificationType.COMMENT
                );
            } catch (Exception e) {
                log.warn("댓글 생성 이벤트 알림 처리 실패: {}", event, e);
            }
        }
    }

    @Async("Async")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleLikeCreated(LikeCreatedEvent event) {
        try {
            notificationService.saveNotification(
                    event.receiverId(),
                    event.senderName() + "왹이 외계 수집물에 푸  딩을 달았다!",
                    "/posts/" + event.targetId(),
                    NotificationType.LIKE
            );
        } catch (Exception e) {
            log.warn("좋아요 생성 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    @Async("Async")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFollowCreated(FollowCreatedEvent event) {
        try {
            notificationService.saveNotification(
                    event.receiverId(),
                    event.followerName() + "왹이 잡 았다! 너 잡혔다!",
                    "/" + event.followerId(),
                    NotificationType.FOLLOW
            );
        } catch (Exception e) {
            log.warn("팔로우 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    @Async("Async")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleUnFollowCreated(FollowDeletedEvent event) {
        try {
            notificationService.saveNotification(
                    event.receiverId(),
                    "오오자비로운" + event.followerName() + "왹께서널놓아주시니",
                    "/" + event.followerId(),
                    NotificationType.UNFOLLOW
            );
        } catch (Exception e) {
            log.warn("언팔로우 이벤트 알림 처리 실패: {}", event, e);
        }
    }

    @Async("Async")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendSSE(NotificationSendEvent event) {
        try {
            notificationService.sendNotification(event.receiverId(), event.content());
        } catch(Exception e) {
            log.warn("SSE 전송 실패");
        }
    }
}
