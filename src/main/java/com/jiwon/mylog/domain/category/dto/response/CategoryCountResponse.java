package com.jiwon.mylog.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryCountResponse {
    private final Long categoryId;
    private final String name;
    private final Long postCount;
}
