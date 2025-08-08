package com.jiwon.mylog.domain.tag.service;

import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.domain.tag.dto.request.TagRequest;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.tag.repository.tag.TagRepository;
import com.jiwon.mylog.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<TagResponse> getAllTags(Long userId) {
        List<Tag> tags = tagRepository.findAllByUserId(userId);
        return tags.stream()
                .map(TagResponse::fromTag)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PageResponse getAllTagsWithCount(Long userId, Pageable pageable) {
        return tagRepository.findAllWithCountByUserId(userId, pageable);
    }

    @Transactional
    public List<Tag> getOrCreateTags(User user, List<TagRequest> tagRequests) {
        List<String> names = tagRequests.stream()
                .map(TagRequest::getName)
                .distinct()
                .toList();

        return names.stream()
                .map(name -> findOrCreateTag(user, name))
                .toList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Tag findOrCreateTag(User user, String name) {
        return tagRepository.findByUserAndName(user, name)
                .orElseGet(() -> {
                    try {
                        Tag tag = Tag.builder()
                                .name(name)
                                .usageCount(0L)
                                .user(user)
                                .build();
                        return tagRepository.save(tag);
                    } catch (DataIntegrityViolationException e) {
                        return tagRepository.findByUserAndName(user, name)
                                .orElseThrow(() -> new IllegalStateException("Tag should exist but not found: " + name));
                    }
                });
    }
}
