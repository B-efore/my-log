package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.dto.request.CategoryRequest;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.DuplicateException;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Long save(UserSaveRequest userSaveRequest) {

        validateDuplicateEmail(userSaveRequest);

        String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
        User user = userSaveRequest.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);
        categoryService.create(savedUser.getId(), new CategoryRequest("전체"));

        return savedUser.getId();
    }

    private void validateDuplicateEmail(UserSaveRequest userSaveRequest) {
        if (userRepository.existsByEmail(userSaveRequest.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
