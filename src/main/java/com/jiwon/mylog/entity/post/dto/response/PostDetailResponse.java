package com.jiwon.mylog.entity.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.entity.Visibility;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.PostStatus;
import com.jiwon.mylog.entity.tag.dto.response.TagResponse;
import com.jiwon.mylog.entity.user.dto.response.UserResponse;
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

    public static PostDetailResponse fromPost(Post post) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .postStatus(post.getPostStatus())
                .visibility(post.getVisibility())
                .user(UserResponse.fromUser(post.getUser()))
//                .category(CategoryResponse.fromCategory(post.getCategory()))
                .tags(post.getPostTags().stream()
                        .map(postTag -> TagResponse.fromTag(postTag.getTag()))
                        .toList())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
