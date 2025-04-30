package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.tag.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
