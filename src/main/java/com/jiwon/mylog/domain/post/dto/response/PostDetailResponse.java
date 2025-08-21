package com.jiwon.mylog.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.PostType;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
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

    private Long postId;
    private String title;
    private String content;
    private String contentPreview;
    private int views;
    private PostStatus postStatus;
    private Visibility visibility;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    private boolean pinned;
    private PostType type;
    private UserResponse user;
    private CategoryResponse category;
    private List<TagResponse> tags;
    private RelatedPostResponse previousPost;
    private RelatedPostResponse nextPost;

    public PostDetailResponse(Long postId, String title, String content, String contentPreview, int views, PostStatus postStatus, Visibility visibility, LocalDateTime createdAt, boolean pinned, PostType type, UserResponse user, CategoryResponse category) {
        this.postId = postId;
        this.title = title;
        this.content = content;
        this.contentPreview = contentPreview;
        this.views = views;
        this.postStatus = postStatus;
        this.visibility = visibility;
        this.createdAt = createdAt;
        this.pinned = pinned;
        this.type = type;
        this.user = user;
        this.category = category;
    }

    public static PostDetailResponse fromPost(Post post) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .contentPreview(post.getContentPreview())
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
                .type(post.getType())
                .build();
    }

    public void setRelatedPosts(RelatedPostResponse previous, RelatedPostResponse next) {
        this.previousPost = previous;
        this.nextPost = next;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setTags(List<TagResponse> tags) {
        this.tags = tags;
    }
}
