package com.jiwon.mylog.domain.like;

import java.time.LocalDateTime;

public interface LikeNotificationDetails {
    Long getReceiverId();
    LocalDateTime getCreatedAt();
}
