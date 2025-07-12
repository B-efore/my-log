package com.jiwon.mylog.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserActivitiesResponse {
    private final List<UserActivityResponse> activities;
}
