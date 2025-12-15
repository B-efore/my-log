package com.jiwon.mylog.domain.follow.repository;

import com.jiwon.mylog.domain.follow.entity.Follow;
import com.jiwon.mylog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long>, CustomFollowRepository {
    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    @Query("select f from Follow f " +
            "join fetch f.fromUser " +
            "where f.fromUser.id = :fromUserId and f.toUser.id = :toUserId")
    Optional<Follow> findByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    long countByFromUserId(Long fromUserId);
    long countByToUserId(Long toUserId);
}
