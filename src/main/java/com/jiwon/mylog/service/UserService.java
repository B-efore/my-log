package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.DuplicateEmailException;
import com.jiwon.mylog.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Transactional
    public Long save(UserSaveRequest userSaveRequest) {

        if (userRepository.existsByEmail(userSaveRequest.getEmail())) {
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(userSaveRequest.getPassword());
        User user = userSaveRequest.toEntity(encodedPassword);
        User savedUser = userRepository.save(user);

        return savedUser.getId();
    }
}
