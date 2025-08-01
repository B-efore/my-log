package com.jiwon.mylog.domain.post.repository;

import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.dto.response.PostNavigationResponse;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.dto.response.RelatedPostResponse;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {
    Page<PostSummaryResponse> findLikedPosts(Long userId, Pageable pageable);

    Page<PostSummaryResponse> findFilteredPosts(Long userId, Long categoryId, List<Long> tagIds, String keyword, Pageable pageable);

    Optional<PostDetailResponse> findPostDetail(Long postId);

    List<UserActivityResponse> findUserActivities(Long userId, LocalDate start, LocalDate end);

    PostNavigationResponse findPostNavigation(Long postId);

    Page<RelatedPostResponse> findCategorizedPosts(Long categoryId, Long userId, Pageable pageable);
}
