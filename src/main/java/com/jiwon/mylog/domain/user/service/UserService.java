package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.dto.request.UserUpdateRequest;
import com.jiwon.mylog.domain.user.dto.response.UserMainResponse;
import com.jiwon.mylog.domain.user.dto.response.UserProfileResponse;
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
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // TODO: 프로필 이미지 업데이트는 따로 구현
    @Transactional
    public UserProfileResponse update(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        user.updateInformation(
                userUpdateRequest.getUsername(),
                userUpdateRequest.getBio()
        );
        return UserProfileResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findUserWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        return UserProfileResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserMainResponse getUserMain(Long userId) {
        User user = userRepository.findUserWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        List<Post> pinnedPosts = postRepository.findPinnedPostsByUserId(userId);
        return UserMainResponse.fromUser(user, pinnedPosts);

    }
}
