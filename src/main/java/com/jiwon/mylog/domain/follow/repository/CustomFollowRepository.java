package com.jiwon.mylog.domain.follow.repository;

import com.jiwon.mylog.domain.follow.dto.FollowResponse;
import java.util.List;

public interface CustomFollowRepository {
    List<FollowResponse> findFollowings(Long fromUserId);
    List<FollowResponse> findFollowers(Long toUserId);
}
