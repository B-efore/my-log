package com.jiwon.mylog.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.PostStatus;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostDetailResponse {

    private final Long postId;
    private final String title;
    private final String content;
    private final int views;
    private final PostStatus postStatus;
    private final Visibility visibility;
    private final UserResponse user;
    private final CategoryResponse category;
    private final List<TagResponse> tags;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final boolean pinned;
    private final List<CommentResponse> comments;

    public static PostDetailResponse fromPost(Post post) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .postStatus(post.getPostStatus())
                .visibility(post.getVisibility())
                .user(UserResponse.fromUser(post.getUser()))
                .category(post.getCategory() == null ? null : CategoryResponse.fromCategory(post.getCategory()))
                .tags(post.getPostTags().stream()
                        .map(postTag -> TagResponse.fromTag(postTag.getTag()))
                        .toList())
                .createdAt(post.getCreatedAt())
                .pinned(post.isPinned())
                .comments(post.getComments().stream()
                        .map(comment -> CommentResponse.fromComment(comment))
                        .toList())
                .build();
    }
}
