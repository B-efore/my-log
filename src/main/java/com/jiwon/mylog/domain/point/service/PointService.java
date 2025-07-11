package com.jiwon.mylog.domain.point.service;

import com.jiwon.mylog.domain.point.entity.Point;
import com.jiwon.mylog.domain.point.entity.PointHistory;
import com.jiwon.mylog.domain.point.entity.PointType;
import com.jiwon.mylog.domain.point.repository.PointHistoryRepository;
import com.jiwon.mylog.domain.point.repository.PointRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PointService {

    private final PointRepository pointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public void earnPoint(Long userId, int amount, String description) {

        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        point.earnPoint(amount);

        PointHistory history = PointHistory.builder()
                .user(point.getUser())
                .amount(amount)
                .type(PointType.EARN)
                .description(description)
                .build();

        pointHistoryRepository.save(history);
    }

    @Transactional
    public void spendPoint(Long userId, int amount, String description) {
        Point point = pointRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        if (point.getCurrentAmount() < amount || point.getTotalAmount() < amount) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }

        point.spendPoint(amount);

        PointHistory history = PointHistory.builder()
                .user(point.getUser())
                .amount(amount)
                .type(PointType.SPEND)
                .description(description)
                .build();

        pointHistoryRepository.save(history);
    }
}
