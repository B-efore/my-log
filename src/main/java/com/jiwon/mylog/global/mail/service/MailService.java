package com.jiwon.mylog.global.mail.service;

import com.jiwon.mylog.global.redis.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    public Boolean verifyEmailCode(String email, String code) {
        String codeFindByEmail = redisUtil.getData(email);
        if (codeFindByEmail == null) {
            return false;
        }
        return codeFindByEmail.equals(code);
    }

    public void sendMail(String email) throws MessagingException {
        if (redisUtil.existData(email)) {
            redisUtil.deleteData(email);
        }

        String code = createCode();
        MimeMessage message = createEmail(email, code);
        redisUtil.setDataExpire(email, code, 60 * 5L);

        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private MimeMessage createEmail(String email, String code) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
        String text = creatText(code);
        helper.setFrom(senderEmail);
        helper.setTo(email);
        helper.setSubject("[Babo] 이메일 인증");
        helper.setText(text, true);
        return message;
    }

    private String creatText(String code) {
        return """
            <h3>인증 번호를 입력해주세요.</h3>
            <h1>%s</h1>
            <p>본 인증 코드는 5분간 유효합니다.</p>
            <h3>감사합니다.</h3>
            """.formatted(code);
    }

    private String createCode() {
        Random random = new Random();
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
