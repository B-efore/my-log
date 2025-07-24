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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReadmeService {

    private final UserRepository userRepository;
    private final ReadmeRepository readmeRepository;

    @Transactional
    public ReadmeResponse createReadme(Long userId, ReadmeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        Readme readme = Readme.from(user, request.getContent());
        readmeRepository.save(readme);
        return new ReadmeResponse(readme.getContent());
    }

    @Transactional
    public ReadmeResponse updateReadme(Long userId, ReadmeRequest request) {
        Readme readme = readmeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        readme.updateContent(request.getContent());
        return new ReadmeResponse(readme.getContent());
    }

    @Transactional
    public void deleteReadme(Long userId) {
        Readme readme = readmeRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        readmeRepository.delete(readme);
    }
}