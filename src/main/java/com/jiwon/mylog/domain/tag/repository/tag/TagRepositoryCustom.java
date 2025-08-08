package com.jiwon.mylog.domain.tag.repository.tag;

import com.jiwon.mylog.global.common.entity.PageResponse;
import org.springframework.data.domain.Pageable;

public interface TagRepositoryCustom{
    PageResponse findAllWithCountByUserId(Long userId, Pageable pageable);
}
