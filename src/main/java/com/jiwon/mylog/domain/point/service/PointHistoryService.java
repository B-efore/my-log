package com.jiwon.mylog.domain.point.service;

import com.jiwon.mylog.domain.point.dto.response.PointHistoryResponse;
import com.jiwon.mylog.domain.point.entity.PointHistory;
import com.jiwon.mylog.domain.point.repository.PointHistoryRepository;
import com.jiwon.mylog.global.common.entity.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PointHistoryService {

    private final PointHistoryRepository pointHistoryRepository;

    @Transactional(readOnly = true)
    public PageResponse getUserHistoriesByPeriod(Long userId, LocalDate fromDate, Pageable pageable) {
        Page<PointHistory> historyPage = pointHistoryRepository.findByUserIdAndDateAfter(userId, fromDate, pageable);
        List<PointHistoryResponse> histories = historyPage.stream()
                .map(PointHistoryResponse::from)
                .toList();
        return PageResponse.from(
                histories,
                historyPage.getNumber(),
                historyPage.getSize(),
                historyPage.getTotalPages(),
                historyPage.getTotalElements());
    }
}
