package com.jiwon.mylog.global.mail.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailRequest {
    private String email;
    private String code;
}
