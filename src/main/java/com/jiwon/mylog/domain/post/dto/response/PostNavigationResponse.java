package com.jiwon.mylog.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class PostNavigationResponse {
    private final Long postId;
    private final Long userId;
    private final Long categoryId;
    private final Long currentOffset;
    private final Long currentPage;
    private final Long totalPosts;
}
