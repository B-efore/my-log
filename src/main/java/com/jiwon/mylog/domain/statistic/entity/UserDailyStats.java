package com.jiwon.mylog.domain.statistic.entity;

import com.jiwon.mylog.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(
                name = "user_daily_stats_uk",
                columnNames = {"user_id", "date"}
        )
)
public class UserDailyStats extends BaseStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate date;

    public static UserDailyStats empty(LocalDate date) {
        return UserDailyStats.builder()
                .date(date)
                .receivedComments(0)
                .receivedLikes(0)
                .createdComments(0)
                .createdPosts(0)
                .build();
    }
}
