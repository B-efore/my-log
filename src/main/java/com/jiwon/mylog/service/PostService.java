package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.dto.request.PostRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.entity.tag.Tag;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.ForbiddenException;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.CategoryRepository;
import com.jiwon.mylog.repository.PostRepository;
import com.jiwon.mylog.repository.UserRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Category category = getCategoryById(user, postRequest.getCategoryId());
        Set<Tag> tags = tagService.getTagsById(postRequest.getTagRequests());
        Post post = Post.create(postRequest, user, category, tags);
        return PostDetailResponse.fromPost(postRepository.save(post));
    }

    @Transactional
    public PostDetailResponse editPost(Long userId, Long postId, PostRequest postRequest) {
        User user = getUserById(userId);
        Post post = getPostById(postId);

        validateOwner(post, user);

        Category category = getCategoryById(user, postRequest.getCategoryId());
        Set<Tag> tags = tagService.getTagsById(postRequest.getTagRequests());
        post.update(postRequest, category, tags);

        return PostDetailResponse.fromPost(post);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPost(Long postId) {
        Post post = getPostById(postId);
        return PostDetailResponse.fromPost(post);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_POST));
    }

    private Category getCategoryById(User user, Long categoryId) {
        return categoryRepository.findByUserAndId(user, categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }

    private void validateOwner(Post post, User user) {
        if(!post.getUser().equals(user)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
