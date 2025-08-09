package com.jiwon.mylog.domain.tag.service;

import com.jiwon.mylog.domain.tag.repository.tag.TagJdbcRepository;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.domain.tag.dto.request.TagRequest;
import com.jiwon.mylog.domain.tag.dto.response.TagResponse;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.tag.repository.tag.TagRepository;
import com.jiwon.mylog.domain.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagJdbcRepository tagJdbcRepository;
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

        List<Tag> tags = names.stream()
                .map(name -> Tag.create(user, name))
                .toList();

        tagJdbcRepository.upsert(tags);

        return tagRepository.findAllByUserAndNameIn(user, names);
    }
}
