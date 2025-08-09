package com.jiwon.mylog.domain.like.repository;

import com.jiwon.mylog.domain.like.entity.PostLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    @Query("select pl from PostLike pl "
            + "join fetch pl.user "
            + "join fetch pl.post  "
            + "where pl.user.id = :userId and pl.post.id = :postId")
    Optional<PostLike> findWithDetails(@Param("userId") Long userId, @Param("postId") Long postId);

    boolean existsByUserIdAndPostId(Long userId, Long postId);

    @Query(value = "select count(*) from post_like where post_id = :postId", nativeQuery = true)
    long countByPostId(@Param("postId") Long postId);
}
