package com.jiwon.mylog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiwon.mylog.global.security.auth.controller.AuthController;
import com.jiwon.mylog.domain.user.dto.request.UserSaveRequest;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.security.jwt.JwtService;
import com.jiwon.mylog.global.security.auth.service.AuthService;
import com.jiwon.mylog.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    private ResultActions signUpRequest(UserSaveRequest request) throws Exception {
        given(userService.save(any(UserSaveRequest.class))).willReturn(1L);

        return mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
    }

    private void validateErrorResponse(ResultActions result, String field, String expectedMessage) throws Exception {
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_INPUT.getMessage()))
                .andExpect(jsonPath("$.errors." + field).value(expectedMessage));
    }

    private void validateSuccessResponse(ResultActions result) throws Exception {
        result.andExpect(status().isCreated())
                        .andExpect(content().string("Created User ID:1"));
    }

    @Nested
    @DisplayName("닉네임 유효성 검사 테스트")
    class 닉네임_테스트 {

        @CsvSource({"test1234얌", "hello-wow", "안녕", "10-유저_name"})
        @ParameterizedTest
        void 올바른_닉네임_회원가입(String name) throws Exception {
            UserSaveRequest request = new UserSaveRequest(name, "test@example.com", "Password123!");
            given(userService.save(any(UserSaveRequest.class))).willReturn(1L);
            validateSuccessResponse(signUpRequest(request));
        }

        @CsvSource({"안녕^-^", "happy spring", "'-'", "my10ngname2"})
        @ParameterizedTest
        void 닉네임_검증_실패(String nickname) throws Exception {
            UserSaveRequest request = new UserSaveRequest(nickname, "test@example.com", "Password123!");
            validateErrorResponse(signUpRequest(request), "username", "닉네임은 한글, 영문, 숫자, '_', '-' 조합의 2~10자리를 사용하세요.");
        }
    }

    @Nested
    @DisplayName("이메일 유효성 검사 테스트")
    class 이메일_테스트 {

        @CsvSource({"user@example.com", "hello_email00@domain.co.kr", "Test123@google.com"})
        @ParameterizedTest
        void 올바른_이메일_회원가입(String email) throws Exception {
            UserSaveRequest request = new UserSaveRequest("testUser", email, "Password123!");
            validateSuccessResponse(signUpRequest(request));
        }

        @CsvSource({"'@example.com'", "'user@'", "'Test123google.com'", "'user@examp!e.com'", "' user@example.com'"})
        @ParameterizedTest
        void 이메일_검증_실패(String email) throws Exception {
            UserSaveRequest request = new UserSaveRequest("testUser", email, "Password123!");
            validateErrorResponse(signUpRequest(request), "email", "올바른 이메일 형식을 입력하세요. ex: user@example.com");
        }
    }

    @Nested
    @DisplayName("비밀번호 유효성 검사 테스트")
    class 비밀번호_테스트 {

        @CsvSource({"He110!^^", "myPassword!@#$00", "aAzZ09!@#$"})
        @ParameterizedTest
        void 올바른_비밀번호_회원가입(String password) throws Exception {
            UserSaveRequest request = new UserSaveRequest("testUser", "test@example.com", password);
            validateSuccessResponse(signUpRequest(request));
        }

        @CsvSource({"' whgdmsqlqjs1!'", "'sh0rt!'", "'veryveryveryl0ng!'", "'justEnglish?'", "'!@#$%^&*()-=.,/'", "'1234567890'"})
        @ParameterizedTest
        void 비밀번호_검증_실패(String password) throws Exception {
            UserSaveRequest request = new UserSaveRequest("testUser", "test@example.com", password);
            validateErrorResponse(signUpRequest(request), "password", "비밀번호는 영문 대소문자, 숫자, 특수문자를 혼합해 8~16자를 사용하세요.");
        }
    }
}