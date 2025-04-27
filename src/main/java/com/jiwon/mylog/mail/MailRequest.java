package com.jiwon.mylog.mail;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MailRequest {
    private String email;
    private String code;
}
