package com.jiwon.mylog.service;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.category.request.CategoryRequest;
import com.jiwon.mylog.entity.user.User;
import com.jiwon.mylog.exception.DuplicateException;
import com.jiwon.mylog.exception.ErrorCode;
import com.jiwon.mylog.exception.NotFoundException;
import com.jiwon.mylog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Transactional
    public CategoryResponse create(Long userId, CategoryRequest categoryRequest) {
        User user = userService.findUserById(userId);
        validateDuplicateCategory(user, categoryRequest.getName());
        Category category = Category.create(categoryRequest, user);
        categoryRepository.save(category);
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public CategoryResponse update(Long userId, Long categoryId, CategoryRequest categoryRequest) {
        User user = userService.findUserById(userId);
        validateDuplicateCategory(user, categoryRequest.getName());
        Category category = getCategory(categoryId);
        category.updateName(categoryRequest.getName());
        return CategoryResponse.fromCategory(category);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, categoryId));
    }

    private void validateDuplicateCategory(User user, String name) {
        if(categoryRepository.existsByUserAndName(user, name)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }
}
