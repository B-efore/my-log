package com.jiwon.mylog.domain.user.dto.request;

import com.jiwon.mylog.domain.user.entity.Role;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.entity.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSaveRequest {

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{2,10}$",
            message = "닉네임은 한글, 영문, 숫자, '_', '-' 조합의 2~10자리를 사용하세요.")
    private String username;

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{6,20}$",
            message = "아이디는 영문, 숫자, '_', '-' 조합의 6~20자리를 사용하세요.")
    private String accountId;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "올바른 이메일 형식을 입력하세요. ex: user@example.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+=-])(?=\\S+$).{8,16}",
            message = "비밀번호는 영문 대소문자, 숫자, 특수문자를 혼합해 8~16자를 사용하세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
    private String confirmPassword;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .accountId(accountId)
                .password(encodedPassword)
                .username(username)
                .status(UserStatus.ACTIVE)
                .role(Role.ROLE_USER)
                .build();
    }
}