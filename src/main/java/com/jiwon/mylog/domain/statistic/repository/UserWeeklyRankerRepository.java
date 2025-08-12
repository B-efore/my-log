package com.jiwon.mylog.domain.statistic.repository;

import com.jiwon.mylog.domain.statistic.entity.UserWeeklyRanker;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWeeklyRankerRepository extends JpaRepository<UserWeeklyRanker, Long> {

    void deleteByWeekStart(LocalDate weekStart);

    @Query("select MAX(uwr.weekStart) from UserWeeklyRanker uwr")
    Optional<LocalDate> findLatestWeekStartDate();

    @Query("select uwr from UserWeeklyRanker uwr "
            + "join fetch uwr.user u "
            + "left join fetch u.profileImage "
            + "where uwr.weekStart = :weekStart "
            + "order by uwr.rankOrder asc ")
    List<UserWeeklyRanker> findAllByWeekStart(@Param("weekStart") LocalDate weekStart);
}
