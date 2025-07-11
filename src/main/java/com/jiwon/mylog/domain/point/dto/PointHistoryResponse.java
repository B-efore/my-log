package com.jiwon.mylog.domain.point.dto;

import com.jiwon.mylog.domain.point.entity.PointHistory;
import com.jiwon.mylog.domain.point.entity.PointType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PointHistoryResponse {

    private final int amount;
    private final PointType type;
    private final String description;
    private final LocalDateTime createdAt;

    public static PointHistoryResponse from(PointHistory pointHistory) {
        return PointHistoryResponse.builder()
                .amount(pointHistory.getAmount())
                .type(pointHistory.getType())
                .description(pointHistory.getDescription())
                .createdAt(pointHistory.getCreatedAt())
                .build();
    }
}
