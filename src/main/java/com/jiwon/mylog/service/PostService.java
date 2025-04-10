package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.dto.request.PostRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.entity.post.dto.response.PostSummaryPageResponse;
import com.jiwon.mylog.entity.post.dto.response.PostSummaryResponse;
import com.jiwon.mylog.entity.tag.Tag;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.ForbiddenException;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.CategoryRepository;
import com.jiwon.mylog.repository.PostRepository;
import com.jiwon.mylog.repository.UserRepository;
import java.util.List;
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
        Post post = getPostById(postId);

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
        Post post = getPostById(postId);
        validateNotDeleted(post);
        return PostDetailResponse.fromPost(post);
    }

    private static void validateNotDeleted(Post post) {
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
