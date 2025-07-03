package com.jiwon.mylog.domain.comment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jiwon.mylog.global.common.enums.Visibility;
import com.jiwon.mylog.domain.comment.entity.Comment;
import com.jiwon.mylog.domain.comment.entity.CommentStatus;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime updatedAt;

    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .parentId(comment.getParent() == null ? null : comment.getParent().getId())
                .depth(comment.getDepth())
                .visibility(comment.getVisibility())
                .commentStatus(comment.getCommentStatus())
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .user(UserResponse.fromUser(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
