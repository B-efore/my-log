package com.jiwon.mylog.domain.tag.service;

import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.domain.tag.dto.request.TagRequest;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.tag.repository.TagRepository;
import com.jiwon.mylog.domain.user.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
    public List<Tag> getTagsById(User user, List<TagRequest> tagRequests) {

        List<String> names = tagRequests.stream()
                .map(TagRequest::getName)
                .distinct()
                .collect(Collectors.toList());

        // 이미 존재하는 태그
        List<Tag> existingTags = tagRepository.findAllByUserAndNameIn(user, names);

        List<String> existNames = existingTags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());

        // 새로운 태그
        List<Tag> createdTags = names.stream()
                .filter(name -> !existNames.contains(name))
                .map(name -> createTag(user, name))
                .collect(Collectors.toList());

        tagRepository.saveAll(createdTags);

        return Stream.of(existingTags, createdTags)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private Tag createTag(User user, String tagName) {
        Tag tag = Tag.builder()
                .name(tagName)
                .usageCount(0L)
                .user(user)
                .build();
        return tag;
    }
}
