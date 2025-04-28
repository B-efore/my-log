package com.jiwon.mylog.domain.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "카테고리명은 필수 입력값입니다.")
    @Size(max = 10, message = "카테고리명은 최대 10자까지 입력할 수 있습니다.")
    private String name;
}
