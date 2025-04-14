package com.jiwon.mylog.entity.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "게시글 ID는 필수값입니다.")
    private Long postId;

    private Long parentId;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    @Size(max = 500, message = "댓글은 최대 500자까지 입력할 수 있습니다.")
    private String content;

    @NotNull(message = "게시글 공개 범위를 지정해주세요.")
    private String visibility;
}
