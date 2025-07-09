package com.jiwon.mylog.domain.category.repository;

import com.jiwon.mylog.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

    boolean existsByUserIdAndName(Long userId, String name);

    @Query("select c from Category c where c.user.id = :userId and c.id = :id")
    Optional<Category> findByUserIdAndId(@Param("userId") Long userId, @Param("id") Long id);

    @Query("select c from Category c where c.user.id = :userId")
    List<Category> findAllByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update Category c set c.postCount = (select coalesce(count(p.id), 0) from Post p where p.category.id = c.id and p.deletedAt is null)")
    void updateAllPostCount();
}
