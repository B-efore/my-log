package com.jiwon.mylog.global.mail.service;

import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.MailException;
import com.jiwon.mylog.global.redis.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Duration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Transactional(readOnly = true)
    public void verifyEmailCode(String email, String code) {
        String codeFindByEmail = redisUtil.get(email);
        if (codeFindByEmail == null) {
            throw new MailException(ErrorCode.NOT_FOUND_MAIL_CODE);
        }
        if (!codeFindByEmail.equals(code)) {
            throw new MailException(ErrorCode.INVALID_MAIL_CODE);
        }
        redisUtil.delete(email);
    }

    @Async("Async")
    public void sendCodeMailAsync(String email) {
        if (redisUtil.exist(email)) {
            redisUtil.delete(email);
        }
        String subject = "[MyLog] 인증번호입니다.";

        String code = createCode();
        String text = createCodeText(code);
        redisUtil.set(email, code, Duration.ofMinutes(5));

        try {
            log.info("인증코드 전송 - email:{}", email);
            MimeMessage message = createEmail(email ,subject, text );
            javaMailSender.send(message);
        } catch (org.springframework.mail.MailException e) {
            log.error("인증코드 전송 실패 - email:{}", email);
            throw new MailException(ErrorCode.FAIlED_MAIL_SEND);
        }
    }

    private MimeMessage createEmail(String email, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(senderEmail, "MyLog");
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailException(ErrorCode.FAIlED_MAIL_SEND);
        }
        return message;
    }

    private String createCodeText(String code) {
        return """
            <h3>인증 번호를 입력해주세요.</h3>
            <h1>%s</h1>
            <p>본 인증 코드는 5분간 유효합니다.</p>
            <h3>감사합니다.</h3>
            """.formatted(code);
    }

    private String createCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(2);
            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 'A'));
                case 1 -> key.append(random.nextInt(10));
            }
        }
        return key.toString();
    }
}