package com.jiwon.mylog.domain.post.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RelatedPostResponse {
    private final Long postId;
    private final String title;
    private final LocalDateTime createdAt;

    public RelatedPostResponse(Long postId, String title, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.createdAt = createdAt;
    }
}
