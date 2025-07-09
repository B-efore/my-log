package com.jiwon.mylog.global.schedular;

import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CountScheduler {

    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void updateAllPostCounts() {
        try {
            updateCategoryPostCounts();
            updateTagPostCounts();
        } catch (Exception e) {
            log.error("업데이트 실패: {}", e.getMessage());
        }
    }

    @Transactional
    public void updateCategoryPostCounts() {
        try {
            categoryRepository.updateAllPostCount();
        } catch (Exception e) {
            log.error("카테고리 게시글 수 업데이트 실패: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void updateTagPostCounts() {
        try {
            tagRepository.updateAllPostCounts();
        } catch (Exception e) {
            log.error("태그 게시글 수 업데이트 실패: {}", e.getMessage());
            throw e;
        }
    }
}
