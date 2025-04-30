package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query(value = "select t from Tag t where t.user = :user and t.name = :name")
    Optional<Tag> findTagByUserAndName(@Param("user") User user, @Param("name") String name);
}
