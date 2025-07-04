package com.jiwon.mylog.domain.follow.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class FollowListResponse {
    private final List<FollowResponse> follows;
}
