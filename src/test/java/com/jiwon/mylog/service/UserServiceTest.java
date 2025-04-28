package com.jiwon.mylog.service;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.jiwon.mylog.domain.user.service.UserService;
import com.jiwon.mylog.domain.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserSaveRequest user;

    @BeforeEach
    void setUp() {
         user = new UserSaveRequest("testUser", "test@example.com", "password123!");
    }

    @DisplayName("회원가입 완료")
    @Test
    void 회원가입() {
        //given
        User mockUser = new User(1L, "testUser", "test@example.com", "password123!", UserStatus.PENDING, null, null, null, null, null);
        given(userRepository.existsByEmail(user.getEmail())).willReturn(false);
        given(userRepository.save(any(User.class))).willReturn(mockUser);

        //when
        Long savedId = userService.save(user);

        //then
        Assertions.assertThat(savedId).isEqualTo(1L);
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @DisplayName("중복된 이메일이 존재할 경우 예외가 발생한다.")
    @Test
    void 회원가입_중복_예외() {
        //given
        given(userRepository.existsByEmail(user.getEmail())).willReturn(true);

        //when & then
        Assertions.assertThatThrownBy(() -> userService.save(user))
                .isInstanceOf(DuplicateException.class)
                .hasMessage(ErrorCode.DUPLICATE_EMAIL.getMessage());

        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
}