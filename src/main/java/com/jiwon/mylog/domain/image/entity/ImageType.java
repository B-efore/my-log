package com.jiwon.mylog.domain.image.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jiwon.mylog.global.common.enums.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ImageType implements BaseEnum<ImageType> {

    PROFILE("프로필"),
    POST("게시글 이미지"),
    ETC("기타");

    private final String type;

    @Override
    @JsonValue
    public String getStatus() {
        return type;
    }

    @JsonCreator
    public static ImageType fromString(String type) {
        return BaseEnum.fromString(ImageType.class, type);
    }
}
