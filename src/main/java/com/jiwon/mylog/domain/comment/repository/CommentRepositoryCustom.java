package com.jiwon.mylog.domain.comment.repository;

import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<CommentResponse> findByPostId(Long postId, Pageable pageable);
}
