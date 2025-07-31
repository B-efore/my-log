package com.jiwon.mylog.domain.post.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RelatedPostResponse {
    private final Long postId;
    private final String title;

    public RelatedPostResponse(Long postId, String title) {
        this.postId = postId;
        this.title = title;
    }
}
