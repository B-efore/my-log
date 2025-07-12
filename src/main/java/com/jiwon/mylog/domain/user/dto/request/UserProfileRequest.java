package com.jiwon.mylog.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserProfileRequest {
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9_-]{2,10}$",
            message = "닉네임은 한글, 영문, 숫자, '_', '-' 조합의 2~10자리를 사용하세요.")
    private String username;
    private String bio;
}
