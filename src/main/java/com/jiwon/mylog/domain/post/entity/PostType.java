package com.jiwon.mylog.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jiwon.mylog.global.common.enums.BaseEnum;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PostType implements BaseEnum<PostType> {
    NORMAL("일반 글"),
    NOTICE("공지");

    private final String label;

    @JsonValue
    @Override
    public String getStatus() {
        return label;
    }

    @JsonCreator
    public static PostType fromString(String value) {
        return BaseEnum.fromString(PostType.class, value);
    }
}
