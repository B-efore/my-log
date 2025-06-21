package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.tag.dto.response.TagCountListResponse;

public interface TagRepositoryCustom{
    TagCountListResponse findAllWithCountByUserId(Long userId);
}
