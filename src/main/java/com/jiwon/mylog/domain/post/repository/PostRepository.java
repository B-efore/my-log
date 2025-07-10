package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Query(value = "select p from Post p where p.deletedAt is null and p.isNotice = true order by p.createdAt desc",
            countQuery = "select count(p) from Post p where p.deletedAt is null and p.isNotice = true")
    Page<Post> findAllNotice(Pageable pageable);

    @Query(value = "select p from Post p " +
            "join fetch p.user u " +
            "left join fetch u.profileImage " +
            "where p.deletedAt is null and p.visibility = 'PUBLIC' and p.isNotice = false " +
            "order by p.createdAt desc",
            countQuery = "select count(p) from Post p where p.deletedAt is null and p.visibility = 'PUBLIC' and p.isNotice = false")
    Page<Post> findAll(Pageable pageable);

    @Query(value = "select p from Post p left join fetch p.category left join fetch p.postTags pt left join fetch pt.tag t where p.deletedAt is null and p.user.id = :userId",
            countQuery = "select count(p) from Post p where p.user.id = :userId")
    Page<Post> findAllByUser(@Param("userId") Long userId, Pageable pageable);

    @Query("select p from Post p join fetch p.user left join fetch p.category where p.id = :id")
    Optional<Post> findWithUserAndCategory(@Param("id") Long id);

    @Query("select p from Post p left join fetch p.comments where p = :post")
    Optional<Post> findWithComments(@Param("post") Post post);

    @Query("select p from Post p left join fetch p.postTags pt left join fetch pt.tag where p = :post")
    Optional<Post> findWithTags(@Param("post") Post post);

    @Query("select p from Post p left join fetch p.postTags pt left join fetch pt.tag where p.id = :postId")
    Optional<Post> findWithTags(@Param("postId") Long postId);

    @Query("select p from Post p where p.user.id = :userId and p.pinned = true and p.deletedAt is null")
    List<Post> findPinnedPostsByUserId(@Param("userId") Long userId);
}
