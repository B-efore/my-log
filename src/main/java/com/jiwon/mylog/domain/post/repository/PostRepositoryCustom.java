package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<Post> findByCategoryAndTags(Long userId, Long categoryId, List<Long> tagIds, Pageable pageable);
    Page<Post> findByTags(Long userId, List<Long> tagIds, Pageable pageable);
}
