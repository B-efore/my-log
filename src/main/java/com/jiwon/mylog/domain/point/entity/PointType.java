package com.jiwon.mylog.domain.point.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PointType {
    EARN("적립"),
    SPEND("사용");

    private final String type;
}
