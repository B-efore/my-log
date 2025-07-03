package com.jiwon.mylog.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import com.jiwon.mylog.domain.post.entity.PostStatus;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class PostDetailResponse {

    private Long postId;
    private String title;
    private String content;
    private int views;
    private PostStatus postStatus;
    private Visibility visibility;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private boolean pinned;
    private UserResponse user;
    private CategoryResponse category;
    private List<TagResponse> tags;
    private List<CommentResponse> comments;

    public PostDetailResponse(Long postId, String title, String content, int views, PostStatus postStatus, Visibility visibility, LocalDateTime createdAt, boolean pinned, UserResponse user, CategoryResponse category) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.views = views;
        this.postStatus = postStatus;
        this.visibility = visibility;
        this.createdAt = createdAt;
        this.pinned = pinned;
        this.user = user;
        this.category = category;
    }

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

    public void setTagsAndComments(List<TagResponse> tags, List<CommentResponse> comments) {
        this.tags = tags;
        this.comments = comments;
    }
}
