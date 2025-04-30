package com.jiwon.mylog.global.mail.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailRequest {
    @NotBlank
    private String email;
}
