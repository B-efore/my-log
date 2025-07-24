package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {
    Page<Post> findByCategoryAndTags(Long userId, Long categoryId, List<Long> tagIds, Pageable pageable);
    Page<Post> findByTags(Long userId, List<Long> tagIds, Pageable pageable);
    Optional<PostDetailResponse> findPostDetail(Long postId);

    List<UserActivityResponse> findUserActivities(Long userId, LocalDate start, LocalDate end);
}
