package com.jiwon.mylog.domain.tag.repository;

import com.jiwon.mylog.domain.tag.dto.response.TagCountPageResponse;
import org.springframework.data.domain.Pageable;

public interface TagRepositoryCustom{
    TagCountPageResponse findAllWithCountByUserId(Long userId, Pageable pageable);
}
