package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.user.dto.request.UserUpdateRequest;
import com.jiwon.mylog.domain.user.dto.response.UserDetailResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDetailResponse update(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        user.updateInformation(
                userUpdateRequest.getUsername(),
                userUpdateRequest.getBio(),
                userUpdateRequest.getProfileImageUrl()
        );
        return UserDetailResponse.fromUser(user);
    }
}
