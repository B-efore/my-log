package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.domain.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.global.common.error.exception.CustomException;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.mail.service.MailService;
import com.jiwon.mylog.domain.category.service.CategoryService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final MailService mailService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public Long save(UserSaveRequest userSaveRequest) {

        validateDuplicateEmail(userSaveRequest);
        validateConfirmPassword(userSaveRequest);

        String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
        User user = userSaveRequest.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);
        categoryService.create(savedUser.getId(), new CategoryRequest("전체"));

        try {
            mailService.sendMail(savedUser.getEmail());
        } catch (MessagingException e) {
            throw new RuntimeException(e.getMessage());
        }

        return savedUser.getId();
    }

    private void validateConfirmPassword(UserSaveRequest userSaveRequest) {
        if(!userSaveRequest.getPassword().equals(userSaveRequest.getConfirmPassword())) {
            throw new CustomException(ErrorCode.NOT_CONFIRM_PASSWORD);
        }
    }

    private void validateDuplicateEmail(UserSaveRequest userSaveRequest) {
        if (userRepository.existsByEmail(userSaveRequest.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
