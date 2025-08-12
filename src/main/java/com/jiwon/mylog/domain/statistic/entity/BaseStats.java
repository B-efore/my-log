package com.jiwon.mylog.domain.statistic.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@MappedSuperclass
public abstract class BaseStats {

    @Column(nullable = false)
    @ColumnDefault("0")
    private int receivedLikes = 0;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int receivedComments = 0;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int createdPosts = 0;

    @Column(nullable = false)
    @ColumnDefault("0")
    private int createdComments = 0;
}
