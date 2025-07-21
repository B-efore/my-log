package com.jiwon.mylog.domain.category.service;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountListResponse;
import com.jiwon.mylog.domain.category.dto.response.CategoryCountResponse;
import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.category.dto.response.CategoryListResponse;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.global.common.error.exception.DuplicateException;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CategoryResponse createCategory(Long userId, CategoryRequest categoryRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        validateDuplicateCategory(userId, categoryRequest.getName());

        Category category = Category.create(categoryRequest, user);
        categoryRepository.save(category);
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long userId, Long categoryId, CategoryRequest categoryRequest) {
        validateDuplicateCategory(userId, categoryRequest.getName());
        Category category = getUserCategory(userId, categoryId);
        category.updateName(categoryRequest.getName());
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {
        Category category = getUserCategory(userId, categoryId);
        postRepository.updatePostCategory(categoryId);
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryListResponse getCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        return CategoryListResponse.fromCategories(categories);
    }

    @Transactional(readOnly = true)
    public CategoryCountListResponse getCategoriesWithCount(Long userId) {
        List<CategoryCountResponse> categories = categoryRepository.findAllWithCountByUserId(userId);
        long totalCount = categories.stream()
                .mapToLong(CategoryCountResponse::getPostCount)
                .sum();
        return new CategoryCountListResponse(categories, totalCount);
    }

    private Category getUserCategory(Long userId, Long categoryId) {
        return categoryRepository.findByUserIdAndId(userId, categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private void validateDuplicateCategory(Long userId, String name) {
        if(categoryRepository.existsByUserIdAndName(userId, name)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }
}
