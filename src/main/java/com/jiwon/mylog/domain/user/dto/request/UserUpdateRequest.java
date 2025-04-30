package com.jiwon.mylog.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserUpdateRequest {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(max = 20)
    private String username;
    private String bio;
    private String profileImageUrl;
}
