package com.jiwon.mylog.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.entity.PostStatus;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.global.common.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MainPostResponse {
    private final Long postId;
    private final String title;
    private final String contentPreview;
    private final PostStatus postStatus;
    private final Visibility visibility;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;
    private final UserResponse user;

    public static MainPostResponse fromPost(Post post) {
        return MainPostResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .contentPreview(post.getContentPreview())
                .postStatus(post.getPostStatus())
                .visibility(post.getVisibility())
                .createdAt(post.getCreatedAt())
                .user(UserResponse.fromUser(post.getUser()))
                .build();
    }
}
