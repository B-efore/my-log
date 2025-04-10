package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.tag.Tag;
import com.jiwon.mylog.entity.tag.dto.request.TagRequest;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.repository.TagRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Transactional
    public Set<Tag> getTagsById(User user, List<TagRequest> tagRequests) {
        return tagRequests.stream()
                .map(req -> getOrCreateTag(user, req))
                .collect(Collectors.toSet());
    }

    private Tag getOrCreateTag(User user, TagRequest tagRequest) {
        return tagRepository.findTagByUserAndName(user, tagRequest.getName())
                .orElseGet(() -> createTag(user, tagRequest.getName()));
    }

    private Tag createTag(User user, String tagName) {
        Tag createdTag = Tag.builder()
                .name(tagName)
                .user(user)
                .build();
        return tagRepository.save(createdTag);
    }
}
