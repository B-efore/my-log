package com.jiwon.mylog.domain.post.dto.request;

import com.jiwon.mylog.domain.tag.dto.request.TagRequest;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "제목은 필수 입력값입니다.")
    @Size(max = 255, message = "제목은 최대 255자까지 입력할 수 있습니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력값입니다.")
    private String content;

    @Size(max = 100, message = "미리보기는 최대 100자까지 입력할 수 있습니다.")
    private String contentPreview;

    @NotBlank(message = "게시글 공개 범위를 지정해주세요.")
    private String visibility;

    private Long categoryId;

    private List<@Valid TagRequest> tagRequests = new ArrayList<>();

    private boolean pinned;
}