package com.jiwon.mylog.domain.point.repository;

import com.jiwon.mylog.domain.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    @Query("select p from Point p join fetch p.user where p.user.id = :userId")
    Optional<Point> findByUserId(@Param("userId") Long userId);
}
