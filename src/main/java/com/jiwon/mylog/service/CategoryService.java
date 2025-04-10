package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.category.dto.response.CategoryListResponse;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.category.dto.request.CategoryRequest;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.DuplicateException;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.CategoryRepository;
import com.jiwon.mylog.repository.UserRepository;
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

    @Transactional
    public CategoryListResponse getCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        return CategoryListResponse.fromCategories(categories);
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
