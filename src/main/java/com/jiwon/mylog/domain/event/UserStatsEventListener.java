package com.jiwon.mylog.domain.event;

import com.jiwon.mylog.domain.event.dto.comment.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.comment.CommentDeletedEvent;
import com.jiwon.mylog.domain.event.dto.like.LikeCreatedEvent;
import com.jiwon.mylog.domain.event.dto.like.LikeDeletedEvent;
import com.jiwon.mylog.domain.event.dto.post.PostCreatedEvent;
import com.jiwon.mylog.domain.event.dto.post.PostDeletedEvent;
import com.jiwon.mylog.domain.statistic.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserStatsEventListener {

    private final UserStatsService userStatsService;

    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        if (!validateIsToday(event.createdAt())) return;

        try {
            if (!event.postWriterId().equals(event.commentWriterId())) {
                userStatsService.updateReceivedComments(event.postWriterId(), 1);
            }
            userStatsService.updateCreatedComments(event.commentWriterId(), 1);
        } catch (Exception e) {
            log.warn("댓글 생성 이벤트 통계 처리 실패: {}", event, e);
        }
    }

    @EventListener
    public void handleCommentDeleted(CommentDeletedEvent event) {
        if (!validateIsToday(event.createdAt())) return;

        try {
            if (!event.postWriterId().equals(event.commentWriterId())) {
                userStatsService.updateReceivedComments(event.postWriterId(), -1);
            }
            userStatsService.updateCreatedComments(event.commentWriterId(), -1);
        } catch (Exception e) {
            log.warn("댓글 삭제 이벤트 통계 처리 실패: {}", event, e);
        }
    }

    @EventListener
    public void handleLikeCreated(LikeCreatedEvent event) {
        if (!validateIsToday(event.createdAt())) return;

        try {
            userStatsService.updateReceivedLikes(event.postWriterId(), 1);
        } catch (Exception e) {
            log.warn("좋아요 생성 이벤트 통계 처리 실패: {}", event, e);
        }
    }

    @EventListener
    public void handleLikeDeleted(LikeDeletedEvent event) {
        if (!validateIsToday(event.createdAt())) return;

        try {
            userStatsService.updateReceivedLikes(event.postWriterId(), -1);
        } catch (Exception e) {
            log.warn("좋아요 삭제 이벤트 통계 처리 실패: {}", event, e);
        }
    }

    @EventListener
    public void handlePostCreated(PostCreatedEvent event) {
        if (!validateIsToday(event.createdAt())) return;

        try {
            userStatsService.updateCreatedPosts(event.userId(), 1);
        } catch (Exception e) {
            log.warn("게시글 생성 이벤트 통계 처리 실패: {}", event, e);
        }
    }

    @EventListener
    public void handlePostDeleted(PostDeletedEvent event) {
        if (!validateIsToday(event.createdAt())) return;

        try {
            userStatsService.updateCreatedPosts(event.userId(), -1);
        } catch (Exception e) {
            log.warn("게시글 삭제 이벤트 통계 처리 실패: {}", event, e);
        }
    }

    private boolean validateIsToday(LocalDateTime event) {
        return event.toLocalDate().equals(LocalDate.now());
    }
}
