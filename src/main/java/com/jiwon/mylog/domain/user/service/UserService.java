package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.user.dto.request.UserUpdateRequest;
import com.jiwon.mylog.domain.user.dto.response.UserDetailResponse;
import com.jiwon.mylog.domain.user.dto.response.UserInfoResponse;
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

    // TODO: 프로필 이미지 업데이트는 따로 구현
    @Transactional
    public UserDetailResponse update(Long userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        user.updateInformation(
                userUpdateRequest.getUsername(),
                userUpdateRequest.getBio()
        );
        return UserDetailResponse.fromUser(user);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        return UserInfoResponse.fromUser(user);
    }
}
