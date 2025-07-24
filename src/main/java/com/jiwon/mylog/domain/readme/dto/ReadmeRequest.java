package com.jiwon.mylog.domain.readme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReadmeRequest {
    @NotBlank
    private final String content;
}
