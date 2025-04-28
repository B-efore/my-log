package com.jiwon.mylog.global.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Visibility implements BaseEnum<Visibility> {
    PUBLIC("공개"),
    PRIVATE("비공개");

    private final String status;

    @Override
    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static Visibility fromString(String name) {
        return BaseEnum.fromString(Visibility.class, name);
    }
}
