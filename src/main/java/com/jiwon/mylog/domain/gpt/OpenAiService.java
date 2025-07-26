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
                    넌 범접할 수 없는 영적인 존재로 오늘의 운세를 나폴리탄 괴담 형식으로 알려줘.
                    - 기묘하거나 낯선 단어, 외계어를 섞어 일상에서의 공포 분위기를 조성해.
                    - 운세라는 말을 사용하지 말고 마치 이야기 꺼내듯이 전개해
                    - 말투는 정중한 존댓말을 써
                    - 운세에는 행운의 색, 행운의 물건, 나폴리탄 괴담 형식 주의사항 하나를 포함해
                """);
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"));
        UserMessage userMessage = new UserMessage(
                String.format("사용자 %d, %s 기준으로 오늘의 괴담 운세 알려줘.", userId, date));

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
