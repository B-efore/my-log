package com.jiwon.mylog.domain.gpt;

import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/openai")
@RestController
public class OpenAiController {

    private final OpenAiService openAiService;

    @GetMapping("/fortune")
    public ResponseEntity<FortuneResponse> getDailyFortune(@LoginUser Long userId) {
        FortuneResponse response = openAiService.getDailyFortune(userId);
        return ResponseEntity.ok(response);
    }
}
