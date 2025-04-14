package com.jiwon.mylog.entity.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.entity.Visibility;
import com.jiwon.mylog.entity.comment.Comment;
import com.jiwon.mylog.entity.comment.CommentStatus;
import com.jiwon.mylog.entity.user.dto.response.UserResponse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
    private final Long commentId;
    private final Long parentId;
    private final int depth;
    private final Visibility visibility;
    private final CommentStatus commentStatus;
    private final String content;
    private final UserResponse user;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdAt;

    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .depth(comment.getDepth())
                .visibility(comment.getVisibility())
                .commentStatus(comment.getCommentStatus())
                .content(comment.getContent())
                .user(UserResponse.fromUser(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
