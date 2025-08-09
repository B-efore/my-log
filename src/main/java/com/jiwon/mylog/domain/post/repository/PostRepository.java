package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.post.dto.response.PinnedPostResponse;
import com.jiwon.mylog.domain.post.entity.Post;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    @Modifying
    @Query("update Post p set p.views = :view where p.id = :postId")
    void updatePostView(@Param("postId") Long postId, @Param("view") int view);

    @Modifying
    @Query("update Post p set p.category = null where p.category.id = :categoryId")
    void updatePostCategory(@Param("categoryId") Long categoryId);

    @Query(value = "select p from Post p where p.deletedAt is null and p.type = 'NOTICE' order by p.createdAt desc",
            countQuery = "select count(p) from Post p where p.deletedAt is null and p.type = 'NOTICE'")
    Page<Post> findAllNotice(Pageable pageable);

    @Query(value = "select p from Post p " +
            "join fetch p.user u " +
            "left join fetch u.profileImage " +
            "where p.deletedAt is null and p.visibility = 'PUBLIC' and p.type = 'NORMAL' " +
            "order by p.createdAt desc",
            countQuery = "select count(p) from Post p where p.deletedAt is null and p.visibility = 'PUBLIC' and p.type = 'NORMAL'")
    Page<Post> findAll(Pageable pageable);

    @Query("select p from Post p join fetch p.user left join fetch p.category where p.id = :id")
    Optional<Post> findWithUserAndCategory(@Param("id") Long id);

    @Query("select p from Post p left join fetch p.comments where p = :post")
    Optional<Post> findWithComments(@Param("post") Post post);

    @Query("select p from Post p left join fetch p.postTags pt left join fetch pt.tag where p = :post")
    Optional<Post> findWithTags(@Param("post") Post post);

    @Query("select p from Post p left join fetch p.postTags pt left join fetch pt.tag where p.id = :postId")
    Optional<Post> findWithTags(@Param("postId") Long postId);

    @Query("select new com.jiwon.mylog.domain.post.dto.response.PinnedPostResponse(p.id, p.title, p.contentPreview) " +
            "from Post p " +
            "where p.user.id = :userId and p.pinned = true and p.deletedAt is null")
    List<PinnedPostResponse> findPinnedPostsByUserId(@Param("userId") Long userId);

    @Query("select p from Post p join fetch p.user where p.id = :postId")
    Optional<Post> findByIdWithUser(@Param("postId") Long postId);
}
