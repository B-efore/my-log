package com.jiwon.mylog.domain.post.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record PostRelationData(
        Long userId,
        Long categoryId,
        LocalDateTime createdAt
) {
}
