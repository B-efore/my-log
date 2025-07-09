package com.jiwon.mylog.domain.tag.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TagRequest {
    @NotBlank(message = "태그 이름을 입력해주세요.")
    private String name;
}
