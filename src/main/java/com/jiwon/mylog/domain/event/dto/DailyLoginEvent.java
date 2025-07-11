package com.jiwon.mylog.domain.event.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DailyLoginEvent {
    private final Long userId;
}
