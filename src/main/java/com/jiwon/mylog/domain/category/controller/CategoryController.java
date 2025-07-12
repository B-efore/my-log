package com.jiwon.mylog.domain.category.controller;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountListResponse;
import com.jiwon.mylog.domain.category.service.CategoryService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import com.jiwon.mylog.domain.category.dto.response.CategoryListResponse;
import com.jiwon.mylog.domain.category.dto.response.CategoryResponse;
import com.jiwon.mylog.domain.category.dto.request.CategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api")
@RestController
@Tag(name = "categories", description = "카테고리 API")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categories")
    @Operation(
            summary = "카테고리 생성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "유효하지 않은 카테고리명"),
                    @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자"),
                    @ApiResponse(responseCode = "409", description = "중복된 카테고리명이 존재")
            }
    )
    public ResponseEntity<CategoryResponse> createCategory(
            @LoginUser Long userId,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse response = categoryService.createCategory(userId, categoryRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/categories/{categoryId}")
    @Operation(
            summary = "카테고리 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 카테고리를 찾을 수 없음"),
                    @ApiResponse(responseCode = "409", description = "중복된 카테고리명이 존재")
            }
    )
    public ResponseEntity<CategoryResponse> updateCategory(
            @LoginUser Long userId,
            @PathVariable("categoryId") Long categoryId,
            @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse response = categoryService.updateCategory(userId, categoryId, categoryRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(
            summary = "카테고리 삭제",
            responses = {
                    @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 카테고리를 찾을 수 없음")
            }
    )
    public ResponseEntity<Void> deleteCategory(
            @LoginUser Long userId,
            @PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(userId, categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/{userId}/categories")
    @Operation(
            summary = "특정 유저 카테고리 전체 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리 조회 성공")
            }
    )
    public ResponseEntity<CategoryListResponse> getUserCategories(
            @PathVariable("userId") Long userId) {
        CategoryListResponse response = categoryService.getCategories(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/categories/with-counts")
    @Operation(
            summary = "특정 유저 카테고리 전체 조회 (게시글 수 포함)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "카테고리 조회 성공")
            }
    )
    public ResponseEntity<CategoryCountListResponse> getUserCategoriesWithCount(
            @PathVariable("userId") Long userId) {
        CategoryCountListResponse response = categoryService.getCategoriesWithCount(userId);
        return ResponseEntity.ok(response);
    }
}
