package com.jiwon.mylog.global.mail.controller;

import com.jiwon.mylog.global.mail.dto.request.MailRequest;
import com.jiwon.mylog.global.mail.dto.request.MailVerifyRequest;
import com.jiwon.mylog.global.mail.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/emails")
@RestController
@Tag(name = "mails", description = "메일 API")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    @Operation(
            summary = "인증 코드 메일 발송",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 코드를 정상적으로 전송"),
                    @ApiResponse(responseCode = "500", description = "인증 코드 전송 실패")
            }
    )
    public ResponseEntity<String> sendCodeMail(@RequestBody MailRequest request) {
        mailService.sendCodeMailAsync(request.getEmail());
        return new ResponseEntity<>("인증 코드가 발송되었습니다.", HttpStatus.OK);
    }

    @PostMapping("/verify")
    @Operation(
            summary = "인증 코드 검증",
            responses = {
                    @ApiResponse(responseCode = "200", description = "인증 성공"),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 인증 코드"),
                    @ApiResponse(responseCode = "404", description = "인증 코드를 찾을 수 없음")
            }
    )
    public ResponseEntity<Void> verifyCode(@RequestBody MailVerifyRequest request) {
        mailService.verifyEmailCode(request.getEmail(), request.getCode());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
