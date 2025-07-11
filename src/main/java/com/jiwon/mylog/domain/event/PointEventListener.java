package com.jiwon.mylog.domain.event;

import com.jiwon.mylog.domain.event.dto.CommentCreatedEvent;
import com.jiwon.mylog.domain.event.dto.DailyLoginEvent;
import com.jiwon.mylog.domain.event.dto.PostCreatedEvent;
import com.jiwon.mylog.domain.point.repository.PointHistoryRepository;
import com.jiwon.mylog.domain.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        if(historyRepository.countDailyPointByDescription(
                event.getUserId(),
                DAILY_LOGIN_DESCRIPTION) >= DAILY_LOGIN_LIMIT) {
            return;
        }
        pointService.earnPoint(event.getUserId(), 404, DAILY_LOGIN_DESCRIPTION);
    }

    @Transactional
    @EventListener
    public void handlePostCreated(PostCreatedEvent event) {
        if (historyRepository.countDailyPointByDescription(
                event.getUserId(),
                POST_EARN_DESCRIPTION) >= POST_POINT_LIMIT) {
            return;
        }
        pointService.earnPoint(event.getUserId(), 77, POST_EARN_DESCRIPTION);
    }

    @Transactional
    @EventListener
    public void handleCommentCreated(CommentCreatedEvent event) {
        if (historyRepository.countDailyPointByDescription(
                event.getUserId(),
                COMMENT_EARN_DESCRIPTION) >= COMMENT_POINT_LIMIT) {
            return;
        }
        pointService.earnPoint(event.getUserId(), 44, COMMENT_EARN_DESCRIPTION);
    }
}
