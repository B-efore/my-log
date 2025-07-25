package com.jiwon.mylog.domain.gpt;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class FortuneResponse {
    private final String content;
    private final LocalDateTime createdAt;
}
