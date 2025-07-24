package com.jiwon.mylog.domain.readme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class ReadmeResponse {
    private final String content;
}
