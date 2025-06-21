package com.jiwon.mylog.domain.category.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class CategoryCountListResponse {
    private final List<CategoryCountResponse> categories;
}
