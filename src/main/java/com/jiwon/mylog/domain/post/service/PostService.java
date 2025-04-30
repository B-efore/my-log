package com.jiwon.mylog.domain.post.service;

import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryPageResponse;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.tag.entity.Tag;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.ForbiddenException;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import java.util.List;

import com.jiwon.mylog.domain.tag.service.TagService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final TagService tagService;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public PostDetailResponse createPost(Long userId, PostRequest postRequest) {
        User user = getUserById(userId);
        Category category = getCategoryById(userId, postRequest.getCategoryId());
        Set<Tag> tags = tagService.getTagsById(user, postRequest.getTagRequests());
        Post post = Post.create(postRequest, user, category, tags);
        return PostDetailResponse.fromPost(postRepository.save(post));
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, PostRequest postRequest) {
        Post post = getPostWithDetails(postId);

        validateOwner(post, userId);

        Category category = getCategoryById(userId, postRequest.getCategoryId());
        Set<Tag> tags = tagService.getTagsById(post.getUser(), postRequest.getTagRequests());
        post.update(postRequest, category, tags);

        return PostDetailResponse.fromPost(post);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = getPostById(postId);
        validateOwner(post, userId);
        post.delete();
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId) {
        Post post = getPostWithDetails(postId);
        validateNotDeleted(post);
        return PostDetailResponse.fromPost(post);
    }

    private void validateNotDeleted(Post post) {
        if (post.isDeleted()) {
           throw new NotFoundException(ErrorCode.NOT_FOUND_POST);
        }
    }

    @Transactional(readOnly = true)
    public PostSummaryPageResponse getAllPosts(Long userId, Pageable pageable) {
        Page<Post> postPage = postRepository.findAllByUser(userId, pageable);
        List<PostSummaryResponse> posts = postPage.stream()
                .map(post -> PostSummaryResponse.fromPost(post))
                .toList();

        return PostSummaryPageResponse.from(
                posts,
                postPage.getNumber(),
                postPage.getSize(),
                postPage.getTotalPages(),
                (int) postPage.getTotalElements());
    }

    private Post getPostWithDetails(Long postId) {
        Post post = postRepository.findWithUserAndCategory(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
        postRepository.findWithTags(post);
        postRepository.findWithComments(post);
        return post;
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
    }

    private Category getCategoryById(Long userId, Long categoryId) {
        return categoryRepository.findByUserIdAndId(userId, categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }

    private void validateOwner(Post post, Long userId) {
        if(!post.getUser().getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
