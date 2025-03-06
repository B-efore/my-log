package com.jiwon.mylog.dto;

import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.entity.user.UserStatus;
import lombok.Getter;

@Getter
public class UserSaveRequest {

    private String username;
    private String email;
    private String password;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(email)
                .password(encodedPassword)
                .username(username)
                .status(UserStatus.PENDING)
                .build();
    }
}