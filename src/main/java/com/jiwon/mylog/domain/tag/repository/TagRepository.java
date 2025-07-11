package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>, TagRepositoryCustom {
    List<Tag> findAllByUserAndNameIn(User user, List<String> names);

    @Query("select t from Tag t where t.user.id = :userId")
    List<Tag> findAllByUserId(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Query("update Tag t set t.usageCount = (select coalesce(count(pt.id), 0) from PostTag pt where pt.tag.id = t.id and pt.post.deletedAt is null)")
    void updateAllPostCounts();
}
