package com.jiwon.mylog.repository;

import com.jiwon.mylog.entity.post.Post;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query(value = "select p from Post p join fetch p.category left join fetch p.postTags pt left join fetch pt.tag t where p.deletedAt is null and p.user.id = :userId",
            countQuery = "select count(p) from Post p where p.user.id = :userId")
    Page<Post> findAllByUser(@Param("userId") Long userId, Pageable pageable);

    @Query(value = "select p from Post p join fetch p.user join fetch p.category left join fetch p.postTags pt left join fetch pt.tag t where p.id = :id")
    Optional<Post> findById(@Param("id") Long id);
}
