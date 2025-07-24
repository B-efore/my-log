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
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByFromUserIdAndToUserId(Long fromUserId, Long toUserId);

    @Query("select f from Follow f " +
            "join fetch f.fromUser " +
            "where f.fromUser.id = :fromUserId and f.toUser.id = :toUserId")
    Optional<Follow> findByFromUserIdAndToUserId(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    @Query("select " +
            "sum(case when f.fromUser.id = :userId then 1 else 0 end), " +
            "sum(case when f.toUser.id = :userId then 1 else 0 end) " +
            "from Follow f " +
            "where f.fromUser.id = :userId or f.toUser.id = :userId")
    List<Object[]> countFollowsByUserId(@Param("userId") Long userId);

    @Query("select f.toUser from Follow f where f.fromUser.id = :fromUserId order by f.createdAt desc")
    List<User> findFollowings(@Param("fromUserId") Long fromUserId);

    @Query("select f.fromUser from Follow f where f.toUser.id = :toUserId order by f.createdAt desc")
    List<User> findFollowers(@Param("toUserId") Long toUserId);
}
