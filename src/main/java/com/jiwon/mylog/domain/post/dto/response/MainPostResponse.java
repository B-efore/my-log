package com.jiwon.mylog.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.domain.post.entity.PostStatus;
import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;
import com.jiwon.mylog.global.common.enums.Visibility;

import java.time.LocalDateTime;

public record MainPostResponse(
        Long postId, String title, String contentPreview, PostStatus postStatus, Visibility visibility,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
        UserSummaryResponse user) {
}
