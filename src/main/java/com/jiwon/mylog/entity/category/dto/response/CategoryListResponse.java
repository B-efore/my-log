package com.jiwon.mylog.entity.category.dto.response;

import com.jiwon.mylog.entity.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CategoryListResponse {
    private final List<CategoryResponse> categories;

    public static CategoryListResponse fromCategories(List<Category> categories) {
        return CategoryListResponse.builder()
                .categories(
                        categories.stream()
                                .map(CategoryResponse::fromCategory)
                                .toList())
                .build();


    }
}
