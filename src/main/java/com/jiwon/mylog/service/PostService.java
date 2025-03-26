package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.post.Post;
import com.jiwon.mylog.entity.post.dto.request.PostCreateRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.CategoryRepository;
import com.jiwon.mylog.repository.PostRepository;
import com.jiwon.mylog.repository.UserRepository;
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
    public PostDetailResponse createPost(Long userId, PostCreateRequest postRequest) {

        User user = getUserById(userId);
        Category category = getCategoryById(postRequest.getCategoryId());
        Post post = Post.create(postRequest, user, category);

        postRepository.save(post);
        tagService.createAndSavePostTags(post, postRequest.getTagRequests());

        return PostDetailResponse.fromPost(post);
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, categoryId));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, userId));
    }
}
