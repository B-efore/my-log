package com.jiwon.mylog.entity.category.dto.response;

import com.jiwon.mylog.entity.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CategoryResponse {
    private final Long categoryId;
    private final String name;

    public static CategoryResponse fromCategory(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getId())
                .name(category.getName())
                .build();
    }
}
