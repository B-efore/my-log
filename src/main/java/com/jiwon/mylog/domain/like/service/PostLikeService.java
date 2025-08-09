package com.jiwon.mylog.domain.like.service;

import com.jiwon.mylog.domain.event.dto.like.LikeCreatedEvent;
import com.jiwon.mylog.domain.event.dto.like.LikeDeletedEvent;
import com.jiwon.mylog.domain.like.repository.PostLikeRepository;
import com.jiwon.mylog.domain.like.entity.PostLike;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class PostLikeService implements LikeService {

    private final ApplicationEventPublisher eventPublisher;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(Long userId, Long postId) {
        return postLikeRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countLikes(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    @Transactional
    public void like(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        Post post = postRepository.findByIdWithUser(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));

        PostLike savedPostLike = postLikeRepository.save(PostLike.toPostLike(user, post));

        Long receiverId = post.getUser().getId();

        if (!receiverId.equals(userId)) {
            eventPublisher.publishEvent(
                    new LikeCreatedEvent(
                            postId,
                            receiverId,
                            user.getId(),
                            user.getUsername(),
                            savedPostLike.getCreatedAt()
                    )
            );
        }
    }

    @Override
    @Transactional
    public void unlike(Long userId, Long postId) {
        PostLike postLike = postLikeRepository.findWithDetails(userId, postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        postLikeRepository.delete(postLike);

        Long receiverId = postLike.getUser().getId();
        Long targetId = postLike.getPost().getId();

        if (!receiverId.equals(userId)) {
            eventPublisher.publishEvent(
                    new LikeDeletedEvent(
                            targetId,
                            receiverId,
                            userId,
                            postLike.getCreatedAt()
                    )
            );
        }
    }

    @Transactional(readOnly = true)
    public PageResponse getUserLikes(Long userId, Pageable pageable) {
        Page<PostSummaryResponse> postPage = postRepository.findLikedPosts(userId, pageable);
        return PageResponse.from(
                postPage.getContent(),
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                postPage.getTotalElements()
        );
    }
}
