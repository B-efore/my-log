package com.jiwon.mylog.domain.like;

import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.entity.SliceResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @CacheEvict(value = "like::count", key="'postId:' + #postId", condition = "#postId != null")
    @Transactional
    public void createLike(Long userId, Long postId) {
        validateUserExists(userId);
        validatePostExists(postId);
        likeRepository.saveLike(userId, postId);
    }

    @CacheEvict(value = "like::count", key="'postId:' + #postId", condition = "#postId != null")
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
    public SliceResponse getUserLikes(Long userId, Pageable pageable) {
        validateUserExists(userId);
        Slice<Like> likeSlice = likeRepository.findAllByUserId(userId, pageable);
        return SliceResponse.from(
                likeSlice.getContent(),
                likeSlice.getNumber(),
                likeSlice.getSize(),
                likeSlice.isFirst(),
                likeSlice.isLast()
        );
    }

    private void validateUserExists(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
    }

    private void validatePostExists(Long postId) {
        if(!postRepository.existsById(postId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_POST);
        }
    }
}
