package com.jiwon.mylog.domain.gpt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class OpenAiService {

    private final OpenAiChatModel chatModel;
    private final OpenAiChatOptions openAiChatOptions;

    @Cacheable(value = "dailyFortune", key = "#userId + '_' + T(java.time.LocalDate).now()")
    public FortuneResponse getDailyFortune(Long userId) {
        try {
            ChatResponse response = callOpenAiApi(userId);
            String content = extractContent(response);
            return FortuneResponse.builder()
                    .content(content)
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return getDefaultFortune();
        }
    }

    @CacheEvict(value = "dailyFortune", allEntries = true)
    public void evictAllFortunes() {
        log.info("All dailyFortune Cache deleted");
    }

    private ChatResponse callOpenAiApi(Long userId) {
        SystemMessage systemMessage = new SystemMessage("""
                    넌 오늘의 운세를 나폴리탄 괴담 형식으로 알려주는 예언자야.
                    기묘한 단어를 조금 섞어서 무서운 괴담 느낌 가득하게 운세를 말해줘.
                    추가로 운세에는 아래 내용을 짧게 포함해야 해.
                    - 행운의 색, 행운의 물건, 나폴리탄 형식 괴담 주의사항 하나
                """);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        UserMessage userMessage = new UserMessage(
                String.format("사용자 %d, %s 기준으로 오늘의 외계인 운세 알려줘.", userId, date));

        List<Message> messages = Arrays.asList(systemMessage, userMessage);

        Prompt prompt = Prompt.builder()
                .chatOptions(openAiChatOptions)
                .messages(messages)
                .build();

        return chatModel.call(prompt);
    }

    private String extractContent(ChatResponse response) {
        if (response == null || response.getResult() == null) {
            throw new IllegalArgumentException("GPT 응답 오류");
        }

        String content = response.getResult().getOutput().getText();
        if (content == null || content.trim().isBlank()) {
            throw new IllegalArgumentException("GPT 응답 오류");
        }
        return content;
    }

    private FortuneResponse getDefaultFortune() {
        return FortuneResponse.builder()
                .content("외계 통신 오 류! 외  계인을 구 해줘...")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
