package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.Visibility;
import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.PostStatus;
import com.jiwon.mylog.entity.post.PostTag;
import com.jiwon.mylog.entity.post.dto.request.PostCreateRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.entity.tag.Tag;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.repository.CategoryRepository;
import com.jiwon.mylog.repository.PostRepository;
import com.jiwon.mylog.repository.PostTagRepository;
import com.jiwon.mylog.repository.UserRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final TagService tagService;
    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public PostDetailResponse createPost(Long userId, PostCreateRequest postRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID(" + userId + ")를 가진 사용자를 찾을 수 없습니다."));

        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 ID(" + postRequest.getCategoryId() + ")를 가진 카테고리를 찾을 수 없습니다."));

        Post post = Post.builder()
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .contentPreview(postRequest.getContentPreview())
                .postStatus(PostStatus.PUBLISHED)
                .visibility(Visibility.fromString(postRequest.getVisibility()))
                .user(user)
                .category(category)
                .build();

        Post savedPost = postRepository.save(post);

        Set<Tag> tags = tagService.getTagsById(postRequest.getTagRequests());

        List<PostTag> postTags = tags.stream()
                .map(tag -> PostTag.createPostTag(savedPost, tag))
                .toList();

        postTagRepository.saveAll(postTags);

        return PostDetailResponse.fromPost(savedPost);
    }
}
