package com.jiwon.mylog.domain.statistic.repository;

import com.jiwon.mylog.domain.statistic.UserDailyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserStatsRepository extends JpaRepository<UserDailyStats, Long>, UserStatsCustomRepository {
    Optional<UserDailyStats> findByUserIdAndDate(Long userId, LocalDate date);

    @Modifying
    @Query(value = "insert into user_daily_stats(user_id, date, received_comments, received_likes, created_comments, created_posts)" +
            "values (:userId, :date, :receivedComments, :receivedLikes, :createdComments, :createdPosts)",
    nativeQuery = true)
    void saveDailyStat(@Param("userId") Long userId,
                       @Param("date") LocalDate date,
                       @Param("receivedComments") int receivedComments,
                       @Param("receivedLikes") int receivedLikes,
                       @Param("createdComments") int createdComments,
                       @Param("createdPosts") int createdPosts
    );
}
