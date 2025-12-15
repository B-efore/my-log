package com.jiwon.mylog.domain.follow.service;

import com.jiwon.mylog.domain.event.dto.follow.FollowCreatedEvent;
import com.jiwon.mylog.domain.event.dto.follow.FollowDeletedEvent;
import com.jiwon.mylog.domain.follow.dto.FollowCheckResponse;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FollowService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Transactional
    public void follow(Long fromUserId, Long toUserId) {
        validateFollow(fromUserId, toUserId);
        validateIsFollowing(fromUserId, toUserId, true);

        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        followRepository.save(Follow.follow(fromUser, toUser));

        if (toUser.getId() != null) {
            eventPublisher.publishEvent(
                    new FollowCreatedEvent(
                            toUser.getId(),
                            fromUser.getId(),
                            fromUser.getUsername()
                    )
            );
        }
    }

    @Transactional
    public void unfollow(Long fromUserId, Long toUserId) {
        validateIsFollowing(fromUserId, toUserId, false);

        Follow follow = followRepository.findByFromUserIdAndToUserId(fromUserId, toUserId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));

        followRepository.delete(follow);

        if (toUserId != null) {
            eventPublisher.publishEvent(
                    new FollowDeletedEvent(
                            toUserId,
                            fromUserId,
                            follow.getFromUser().getUsername()
                    )
            );
        }
    }

    @Transactional(readOnly = true)
    public FollowCheckResponse checkFollowing(Long currentUserId, Long targetUserId) {
        boolean isFollowing = followRepository.existsByFromUserIdAndToUserId(currentUserId, targetUserId);
        return new FollowCheckResponse(isFollowing);
    }

    @Transactional(readOnly = true)
    public FollowListResponse getFollowings(Long userId) {
        List<FollowResponse> followings = followRepository.findFollowings(userId);
        return new FollowListResponse(followings);
    }

    @Transactional(readOnly = true)
    public FollowListResponse getFollowers(Long userId) {
        List<FollowResponse> followers = followRepository.findFollowers(userId);
        return new FollowListResponse(followers);
    }

    @Transactional(readOnly = true)
    public FollowCountResponse getFollowCounts(Long userId) {
        long followingsCount = followRepository.countByFromUserId(userId);
        long followersCount = followRepository.countByToUserId(userId);
        return new FollowCountResponse(followingsCount, followersCount);
    }

    private void validateFollow(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우 할 수 없습니다.");
        }
    }

    private void validateIsFollowing(Long fromUserId, Long toUserId, boolean isFollowing) {
        if (followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId) == isFollowing) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
    }
}
