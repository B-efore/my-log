package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.dto.request.UserUpdateRequest;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import com.jiwon.mylog.domain.user.dto.response.UserMainResponse;
import com.jiwon.mylog.domain.user.dto.response.UserProfilePageResponse;
import com.jiwon.mylog.domain.user.dto.response.UserProfileResponse;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

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

    @Transactional(readOnly = true)
    public UserProfilePageResponse searchWithUsername(String username, Pageable pageable) {
        Page<User> userPage = userRepository.findByUsernameContaining(username, pageable);
        List<UserProfileResponse> users = userPage.stream()
                .map(UserProfileResponse::fromUser)
                .toList();

        return UserProfilePageResponse.from(
                users,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalPages(),
                (int) userPage.getTotalElements());
    }

    public UserActivityResponse getUserActivity(Long userId, LocalDate startDate, LocalDate endDate) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_USER);
        }
        return postRepository.findUserActivities(userId, startDate, endDate);
    }
}
