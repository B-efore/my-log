package com.jiwon.mylog.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Query(value = "SELECT p.user_id, l.created_at " +
            "FROM likes l " +
            "INNER JOIN posts p ON l.post_id = p.id " +
            "WHERE l.user_id = :userId AND l.post_id = :postId",
            nativeQuery = true)
    Object[] findLikeDetails(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Query(value = "insert ignore into likes(user_id, post_id) values(:userId, :postId)", nativeQuery = true)
    void saveLike(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Query(value = "delete from likes where user_id = :userId and post_id = :postId", nativeQuery = true)
    void deleteLike(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query(value = "select count(*) from likes where post_id = :postId", nativeQuery = true)
    long countByPostId(@Param("postId") Long postId);
}
