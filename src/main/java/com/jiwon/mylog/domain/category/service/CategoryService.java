package com.jiwon.mylog.domain.category.service;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountListResponse;
import com.jiwon.mylog.domain.category.entity.Category;
import com.jiwon.mylog.domain.category.dto.response.CategoryListResponse;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import com.jiwon.mylog.domain.category.repository.CategoryRepository;
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

    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest categoryRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        validateDuplicateCategory(userId, categoryRequest.getName());
        Category category = Category.create(categoryRequest, user);
        categoryRepository.save(category);
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public CategoryResponse update(Long userId, Long categoryId, CategoryRequest categoryRequest) {
        validateDuplicateCategory(userId, categoryRequest.getName());
        Category category = getCategory(userId, categoryId);
        category.updateName(categoryRequest.getName());
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public void delete(Long userId, Long categoryId) {
        Category category = getCategory(userId, categoryId);
        categoryRepository.delete(category);
    }

    @Transactional(readOnly = true)
    public CategoryListResponse getCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        return CategoryListResponse.fromCategories(categories);
    }

    @Transactional(readOnly = true)
    public CategoryCountListResponse getCategoriesWithCount(Long userId) {
        return categoryRepository.findAllWithCountByUserId(userId);
    }

    private Category getCategory(Long userId, Long categoryId) {
        return categoryRepository.findByUserIdAndId(userId, categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_CATEGORY));
    }

    private void validateDuplicateCategory(Long userId, String name) {
        if(categoryRepository.existsByUserIdAndName(userId, name)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }
}
