package com.jiwon.mylog.global.mail.controller;

import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.mail.dto.request.MailVerifyRequest;
import com.jiwon.mylog.global.mail.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@RequestMapping("/emails")
@RestController
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMail(@RequestBody MailRequest request) {
        mailService.sendMail(request.getEmail());
        return new ResponseEntity<>("인증 코드가 발송되었습니다.", HttpStatus.OK);
    }
}
