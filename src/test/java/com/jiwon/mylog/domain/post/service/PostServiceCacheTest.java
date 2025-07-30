package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.entity.PostType;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class PostServiceCacheTest {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PostService postService;

    @MockitoSpyBean
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    @DisplayName("게시글 생성 시, 캐시 저장 및 관련 캐시 삭제")
    @Test
    void createPost() {
        // given
        Long userId = 1L;
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PostRequest postRequest = new PostRequest("title", "content", "preview", "공개", 1L, List.of(), true, PostType.NORMAL.getStatus());

        postService.getUserPosts(userId, pageable);
        postService.getFilteredPosts(userId, categoryId, List.of(), "", pageable);

        // when
        PostDetailResponse post = postService.createPost(userId, postRequest);
        Long postId = post.getPostId();

        postService.getPost(postId);
        postService.getUserPosts(userId, pageable); // 캐시 다시 생성
        postService.getFilteredPosts(userId, categoryId, List.of(), "", pageable); // 캐시 다시 생성

        // then
        Assertions.assertThat(post).isNotNull();
        Assertions.assertThat(post.getPostId()).isEqualTo(postId);

        verify(postRepository, times(2)).findAllByUser(userId, pageable);
        verify(postRepository, times(2)).findFilteredPosts(userId, categoryId, List.of(), "", pageable);
        verify(postRepository, times(0)).findPostDetail(postId);
    }

    @DisplayName("게시글 수정 시, 수정된 게시글에 맞춰 캐시 수정 및 삭제")
    @Test
    void updatePost() {
        // given
        Long userId = 1L;
        Long postId = 1L;
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        PostRequest postRequest = new PostRequest("title", "content", "preview", "공개", 1L, List.of(), true, PostType.NORMAL.getStatus());

        postService.getUserPosts(userId, pageable); // 캐시 생성
        postService.getFilteredPosts(userId, categoryId, List.of(), "", pageable); // 캐시 생성

        // when
        PostDetailResponse post = postService.updatePost(userId, postId, postRequest);
        postService.getPost(postId);
        postService.getUserPosts(userId, pageable); // 캐시 다시 생성
        postService.getFilteredPosts(userId, categoryId, List.of(), "", pageable); // 캐시 다시 생성

        // then
        Assertions.assertThat(post).isNotNull();
        Assertions.assertThat(post.getPostId()).isEqualTo(postId);

        verify(postRepository, times(2)).findAllByUser(userId, pageable);
        verify(postRepository, times(2)).findFilteredPosts(userId, categoryId, List.of(), "", pageable);
        verify(postRepository, times(0)).findPostDetail(postId);
    }

    @DisplayName("캐싱된 데이터 삭제 시, 관련 캐시를 제거한다.")
    @Test
    void deletePost() {
        // given
        Long postId = 1L;
        Long userId = 1L;
        PostDetailResponse post = mock(PostDetailResponse.class);
        given(postRepository.findPostDetail(postId))
                .willReturn(Optional.of(post))
                .willReturn(Optional.empty());

        // when
        postService.getPost(postId);
        postService.deletePost(userId, postId);

        // then
        assertThrows(NotFoundException.class, () -> postService.getPost(postId));
        verify(postRepository, times(2)).findPostDetail(postId);
    }

    @DisplayName("단일 포스트 조회 시, 캐시에 저장된 경우 캐시를 사용한다.")
    @Test
    void getPost() {
        // given
        Long postId = 1L;
        PostDetailResponse post = mock(PostDetailResponse.class);
        given(postRepository.findPostDetail(postId))
                .willReturn(Optional.of(post));

        // when & then
        PostDetailResponse firstPost = postService.getPost(postId);
        verify(postRepository, times(1)).findPostDetail(postId);

        PostDetailResponse secondPost = postService.getPost(postId);
        verify(postRepository, times(1)).findPostDetail(postId);

        assertThat(firstPost).isEqualTo(secondPost);

    }

    @DisplayName("user, pageable에 따라 캐시를 생성한다.")
    @Test
    void getAllPosts() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        Pageable pageable1 = PageRequest.of(0, 10);
        Pageable pageable2 = PageRequest.of(1, 10);

        // when
        postService.getUserPosts(userId1, pageable1);
        postService.getUserPosts(userId1, pageable2);
        postService.getUserPosts(userId2, pageable1);

        postService.getUserPosts(userId1, pageable1);
        postService.getUserPosts(userId1, pageable2);
        postService.getUserPosts(userId2, pageable1);

        // then
        verify(postRepository, times(1)).findAllByUser(userId1, pageable1);
        verify(postRepository, times(1)).findAllByUser(userId1, pageable2);
        verify(postRepository, times(1)).findAllByUser(userId2, pageable1);
    }
}