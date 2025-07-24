package com.jiwon.mylog.domain.readme.service;

import com.jiwon.mylog.domain.readme.repository.ReadmeRepository;
import com.jiwon.mylog.domain.readme.dto.ReadmeRequest;
import com.jiwon.mylog.domain.readme.dto.ReadmeResponse;
import com.jiwon.mylog.domain.readme.entity.Readme;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReadmeService {

    private final UserRepository userRepository;
    private final ReadmeRepository readmeRepository;

    @CacheEvict(value = "blog::home", key = "#userId", condition = "#userId != null")
    @Transactional
    public ReadmeResponse editReadme(Long userId, ReadmeRequest request) {
        Readme readme = readmeRepository.findByUserId(userId).orElse(null);

        if (readme == null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
            readme = Readme.from(user, request.getContent());
        } else {
            readme.updateContent(request.getContent());
        }

        return ReadmeResponse.from(readmeRepository.save(readme));
    }

    @CacheEvict(value = "blog::home", key = "#userId", condition = "#userId != null")
    @Transactional
    public void deleteReadme(Long userId) {
        Readme readme = readmeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        readmeRepository.delete(readme);
    }
}