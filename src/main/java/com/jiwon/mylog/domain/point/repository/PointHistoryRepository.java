package com.jiwon.mylog.domain.point.repository;

import com.jiwon.mylog.domain.point.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    @Query("select count(ph) from PointHistory ph where ph.user.id = :userId and ph.description = :description and date(ph.createdAt) = CURRENT_DATE")
    long countDailyPointByDescription(@Param("userId") Long userId, @Param("description") String description);

    @Query("select ph from PointHistory ph where ph.user.id = :userId and date(ph.createdAt) >= :date order by ph.createdAt")
    Page<PointHistory> findByUserIdAndDateAfter(@Param("userId") Long userId, @Param("date") LocalDate date, Pageable pageable);
}
