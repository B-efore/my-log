package com.jiwon.mylog.domain.follow.service;

import com.jiwon.mylog.domain.follow.dto.FollowCountResponse;
import com.jiwon.mylog.domain.follow.dto.FollowListResponse;
import com.jiwon.mylog.domain.follow.dto.FollowResponse;
import com.jiwon.mylog.domain.follow.entity.Follow;
import com.jiwon.mylog.domain.follow.repository.FollowRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(Long fromUserId, Long toUserId) {
        if (fromUserId == null || fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }

        if (followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
            throw new IllegalArgumentException("이미 팔로우한 유저입니다.");
        }

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        followRepository.save(Follow.follow(fromUser, toUser));
    }

    @Transactional
    public void unfollow(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }

        if (!followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)) {
            throw new IllegalArgumentException("팔로우 하지 않은 유저입ㄴ디ㅏ.");
        }

        Follow follow = followRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        followRepository.delete(follow);
    }

    @Transactional(readOnly = true)
    public Boolean checkFollowing(Long currentUserId, Long targetUserId) {
        return followRepository.existsByFromUserIdAndToUserId(currentUserId, targetUserId);
    }

    @Transactional(readOnly = true)
    public FollowListResponse getFollowings(Long userId) {
        List<FollowResponse> followings = followRepository.findFollowings(userId).stream()
                .map(FollowResponse::fromUser)
                .toList();
        return new FollowListResponse(followings);
    }

    @Transactional(readOnly = true)
    public FollowListResponse getFollowers(Long userId) {
        List<FollowResponse> followers = followRepository.findFollowers(userId).stream()
                .map(FollowResponse::fromUser)
                .toList();
        return new FollowListResponse(followers);
    }

    public FollowCountResponse getFollowCounts(Long userId) {
        List<Object[]> counts = followRepository.countFollowsByUserId(userId);
        Object[] row = counts.get(0);
        Long followersCount = row[0] != null ? ((Number) row[0]).longValue() : 0L;
        Long followingsCount = row[1] != null ? ((Number) row[1]).longValue() : 0L;
        return new FollowCountResponse(followingsCount, followersCount);
    }
}
