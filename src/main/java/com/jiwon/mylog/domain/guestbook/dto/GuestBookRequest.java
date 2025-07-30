package com.jiwon.mylog.domain.guestbook.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GuestBookRequest {
    @NotNull
    private Long receiverId;
    @NotNull
    private boolean secret;
    @NotBlank
    private String content;
}
