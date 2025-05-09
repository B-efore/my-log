package com.jiwon.mylog.global.mail.service;

import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.MailSendFailedException;
import com.jiwon.mylog.global.redis.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Transactional(readOnly = true)
    public Boolean verifyEmailCode(String email, String code) {
        String codeFindByEmail = redisUtil.getData(email);
        if (codeFindByEmail == null) {
            return false;
        }
        return codeFindByEmail.equals(code);
    }

    @Transactional
    public void sendMail(String email)  {
        if (redisUtil.existData(email)) {
            redisUtil.deleteData(email);
        }

        String code = createCode();
        MimeMessage message = createEmail(email, code);
        redisUtil.setDataExpire(email, code, 60 * 5L);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new MailSendFailedException(ErrorCode.FAIlED_MAIL_SEND);
        }
    }

    private MimeMessage createEmail(String email, String code) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            String text = createText(code);
            helper.setFrom(senderEmail, "MyLog");
            helper.setTo(email);
            helper.setSubject("[MyLog] 인증번호입니다.");
            helper.setText(text, true);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new MailSendFailedException(ErrorCode.FAIlED_MAIL_SEND);
        }
        return message;
    }

    private String createText(String code) {
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