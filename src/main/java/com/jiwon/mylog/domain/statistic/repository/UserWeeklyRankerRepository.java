package com.jiwon.mylog.domain.statistic.repository;

import com.jiwon.mylog.domain.statistic.entity.UserWeeklyRanker;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWeeklyRankerRepository extends JpaRepository<UserWeeklyRanker, Long> {

    void deleteByWeekStart(LocalDate weekStart);

    @Query("select uwr from UserWeeklyRanker uwr "
            + "join fetch uwr.user u "
            + "where uwr.weekStart = (select max(uwr2.weekStart) from UserWeeklyRanker uwr2) "
            + "order by uwr.rankOrder asc")
    List<UserWeeklyRanker> findAllByWeekStart();
}
