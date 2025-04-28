package com.jiwon.mylog.domain.post.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jiwon.mylog.global.common.enums.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PostStatus implements BaseEnum<PostStatus> {
    DRAFT("임시 저장됨"),
    PUBLISHED("게시됨"),
    HIDDEN("숨김 처리됨"),
    DELETED("삭제됨");

    private final String status;

    @Override
    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static PostStatus fromString(String name) {
        return BaseEnum.fromString(PostStatus.class, name);
    }
}
