package com.jiwon.mylog.domain.category.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountResponse;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<CategoryCountResponse> findAllWithCountByUserId(Long userId);
}
