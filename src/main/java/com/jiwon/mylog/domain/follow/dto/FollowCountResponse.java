package com.jiwon.mylog.domain.follow.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FollowCountResponse {
    private final Long followingCount;
    private final Long followerCount;
}
