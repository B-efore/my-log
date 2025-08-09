package com.jiwon.mylog.domain.like.service;

public interface LikeService {

    boolean isLiked(Long userId, Long targetId);

    void like(Long userId, Long targetId);

    void unlike(Long userId, Long targetId);

    Long countLikes(Long targetId);
}
