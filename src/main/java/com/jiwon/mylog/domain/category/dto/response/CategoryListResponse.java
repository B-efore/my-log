package com.jiwon.mylog.domain.category.dto.response;

import com.jiwon.mylog.domain.category.entity.Category;
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
