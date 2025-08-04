package com.jiwon.mylog.domain.event;

import com.jiwon.mylog.domain.event.dto.comment.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.DailyLoginEvent;
import com.jiwon.mylog.domain.event.dto.post.PostCreatedEvent;
import com.jiwon.mylog.domain.point.repository.PointHistoryRepository;
import com.jiwon.mylog.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PointEventListener {

    private final static String DAILY_LOGIN_DESCRIPTION = "일일 로그인";
    private final static Long DAILY_LOGIN_LIMIT = 1L;
    private final static String POST_EARN_DESCRIPTION = "게시글 작성";
    private final static Long POST_POINT_LIMIT = 3L;
    private final static String COMMENT_EARN_DESCRIPTION = "댓글 작성";
    private final static Long COMMENT_POINT_LIMIT = 5L;

    private final PointService pointService;
    private final PointHistoryRepository historyRepository;

    @Transactional
    @EventListener
    public void handleDailyLogin(DailyLoginEvent event) {
        try {
            if (historyRepository.countDailyPointByDescription(
                    event.userId(),
                    DAILY_LOGIN_DESCRIPTION) >= DAILY_LOGIN_LIMIT) {
                return;
            }
            pointService.earnPoint(event.userId(), 404, DAILY_LOGIN_DESCRIPTION);
        } catch (Exception e) {
            log.warn("로그인 포인트 이벤트 처리 실패: {}", event, e);
        }
    }

    @Transactional
    @EventListener
    public void handlePostCreated(PostCreatedEvent event) {
        try {
            if (historyRepository.countDailyPointByDescription(
                    event.userId(),
                    POST_EARN_DESCRIPTION) >= POST_POINT_LIMIT) {
                return;
            }
            pointService.earnPoint(event.userId(), 77, POST_EARN_DESCRIPTION);
        } catch (Exception e) {
            log.warn("게시글 작성 포인트 이벤트 처리 실패: {}", event, e);
        }
    }

    @Transactional
    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        try {
            if (historyRepository.countDailyPointByDescription(
                    event.commentWriterId(),
                    COMMENT_EARN_DESCRIPTION) >= COMMENT_POINT_LIMIT) {
                return;
            }
            pointService.earnPoint(event.commentWriterId(), 44, COMMENT_EARN_DESCRIPTION);
        } catch (Exception e) {
            log.warn("댓글 작성 포인트 이벤트 처리 실패: {}", event, e);
        }
    }
}
