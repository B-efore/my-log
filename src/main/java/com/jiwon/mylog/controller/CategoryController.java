package com.jiwon.mylog.controller;

import com.jiwon.mylog.annotation.LoginUser;
import com.jiwon.mylog.entity.category.dto.response.CategoryListResponse;
import com.jiwon.mylog.entity.category.dto.response.CategoryResponse;
import com.jiwon.mylog.entity.category.dto.request.CategoryRequest;
import com.jiwon.mylog.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(
            @LoginUser Long userId,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse response = categoryService.create(userId, categoryRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> update(
            @LoginUser Long userId,
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse response = categoryService.update(userId, categoryId, categoryRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> delete(
            @LoginUser Long userId,
            @PathVariable("categoryId") Long categoryId) {
        categoryService.delete(userId, categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<CategoryListResponse> getCategories(
            @LoginUser Long userId) {
        CategoryListResponse response = categoryService.getCategories(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
