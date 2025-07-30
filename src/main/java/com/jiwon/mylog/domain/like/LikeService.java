package com.jiwon.mylog.domain.like;

import com.jiwon.mylog.domain.event.dto.LikeCreatedEvent;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class LikeService {

    private final ApplicationEventPublisher eventPublisher;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @CacheEvict(value = "like::count", key = "'postId:' + #postId", condition = "#postId != null")
    @Transactional
    public void createLike(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        Long receiverId = post.getUser().getId();

        Like like = Like.toLike(user, post);
        likeRepository.save(like);

        if (!receiverId.equals(userId)) {
            eventPublisher.publishEvent(
                    new LikeCreatedEvent(
                            postId,
                            receiverId,
                            userId,
                            user.getUsername())
            );
        }
    }

    @CacheEvict(value = "like::count", key = "'postId:' + #postId", condition = "#postId != null")
    @Transactional
    public void deleteLike(Long userId, Long postId) {
        validateUserExists(userId);
        validatePostExists(postId);
        likeRepository.deleteLike(userId, postId);
    }

    @Cacheable(value = "like::count", key = "'postId:' + #postId", condition = "#postId != null")
    @Transactional(readOnly = true)
    public long getLikeCount(Long postId) {
        validatePostExists(postId);
        return likeRepository.countByPostId(postId);
    }

    @Transactional(readOnly = true)
    public boolean getLikeStatus(Long userId, Long postId) {
        validateUserExists(userId);
        validatePostExists(postId);
        return likeRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Transactional(readOnly = true)
    public PageResponse getUserLikes(Long userId, Pageable pageable) {
        validateUserExists(userId);
        Page<PostSummaryResponse> postPage = postRepository.findLikedPosts(userId, pageable);

        return PageResponse.from(
                postPage.getContent(),
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.getTotalElements()
        );
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_POST);
        }
    }
}
