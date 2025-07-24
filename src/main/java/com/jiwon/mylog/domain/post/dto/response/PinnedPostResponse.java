package com.jiwon.mylog.domain.post.dto.response;

import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PinnedPostResponse {
    private final Long postId;
    private final String title;
    private final String contentPreview;

    public PinnedPostResponse(Long postId, String title, String contentPreview) {
        this.postId = postId;
        this.title = title;
        this.contentPreview = contentPreview;
    }

    public static PinnedPostResponse fromPost(Post post) {
        return PinnedPostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .contentPreview(post.getContentPreview())
                .build();
    }
}
