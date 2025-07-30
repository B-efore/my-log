package com.jiwon.mylog.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.dto.response.UserSummaryResponse;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.post.entity.PostStatus;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostSummaryResponse {
    private final Long postId;
    private final String title;
    private final String contentPreview;
    private final PostStatus postStatus;
    private final Visibility visibility;
    private final CategoryResponse category;
    private final UserSummaryResponse user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private List<TagResponse> tags;

    public PostSummaryResponse(Long postId, String title, String contentPreview, PostStatus postStatus, Visibility visibility, CategoryResponse category, UserSummaryResponse user, LocalDateTime createdAt) {
        this.postId = postId;
        this.title = title;
        this.contentPreview = contentPreview;
        this.postStatus = postStatus;
        this.visibility = visibility;
        this.category = category;
        this.user = user;
        this.createdAt = createdAt;
    }

    public void setTags(List<TagResponse> tags) {
        this.tags = tags;
    }

    public static PostSummaryResponse fromPost(Post post) {
        return PostSummaryResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .contentPreview(post.getContentPreview())
                .postStatus(post.getPostStatus())
                .visibility(post.getVisibility())
                .category(post.getCategory() == null ? null : CategoryResponse.fromCategory(post.getCategory()))
                .tags(post.getPostTags().stream()
                        .map(postTag -> TagResponse.fromTag(postTag.getTag()))
                        .toList())
                .user(UserSummaryResponse.fromUser(post.getUser()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
