package com.jiwon.mylog.domain.category.repository;

import com.jiwon.mylog.domain.category.dto.response.CategoryCountListResponse;

public interface CategoryRepositoryCustom {
    CategoryCountListResponse findAllWithCountByUserId(Long userId);
}
