package com.jiwon.mylog.repository;

import com.jiwon.mylog.entity.tag.Tag;
import com.jiwon.mylog.entity.user.User;
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
