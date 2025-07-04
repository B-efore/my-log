package com.jiwon.mylog.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserActivityResponse {
    private final List<ActivityResponse> activities;
}
