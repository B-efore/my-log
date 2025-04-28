package com.jiwon.mylog.global.mail.controller;

import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.mail.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/emails")
@RestController
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public String sendMail(@RequestBody MailRequest request) throws MessagingException {
        mailService.sendMail(request.getEmail());
        return "인증 코드가 발송되었습니다.";
    }
}
