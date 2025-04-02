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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        Category category = getCategory(user, categoryId);
        category.updateName(categoryRequest.getName());
        return CategoryResponse.fromCategory(category);
    }

    @Transactional
    public void delete(Long userId, Long categoryId) {
        User user = userService.findUserById(userId);
        Category category = getCategory(user, categoryId);
        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryListResponse getCategories(Long userId) {
        User user = userService.findUserById(userId);
        List<Category> categories = categoryRepository.findAllByUser(user);
        return CategoryListResponse.fromCategories(categories);
    }

    private Category getCategory(User user, Long categoryId) {
        return categoryRepository.findByUserAndId(user, categoryId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND, categoryId));
    }

    private void validateDuplicateCategory(User user, String name) {
        if(categoryRepository.existsByUserAndName(user, name)) {
            throw new DuplicateException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }
}
