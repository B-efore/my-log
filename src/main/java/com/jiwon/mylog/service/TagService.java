package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.PostTag;
import com.jiwon.mylog.entity.tag.Tag;
import com.jiwon.mylog.entity.tag.dto.request.TagRequest;
import com.jiwon.mylog.repository.PostTagRepository;
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
    private final PostTagRepository postTagRepository;

    @Transactional
    public void createAndSavePostTags(Post post, List<TagRequest> tagRequests) {
        Set<Tag> tags = getTagsById(tagRequests);

        List<PostTag> postTags = tags.stream()
                .map(tag -> PostTag.createPostTag(post, tag))
                .toList();

        postTagRepository.saveAll(postTags);
    }

    private Set<Tag> getTagsById(List<TagRequest> tagRequests) {
        return tagRequests.stream()
                .map(this::getOrCreateTag)
                .collect(Collectors.toSet());
    }

    private Tag getOrCreateTag(TagRequest tagRequest) {
        return tagRepository.findTagByName(tagRequest.getName())
                .orElseGet(() -> createTag(tagRequest.getName()));
    }

    private Tag createTag(String tagName) {
        Tag createdTag = Tag.builder()
                .name(tagName)
                .build();
        return tagRepository.save(createdTag);
    }
}
