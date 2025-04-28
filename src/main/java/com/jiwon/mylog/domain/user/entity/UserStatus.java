package com.jiwon.mylog.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jiwon.mylog.global.common.enums.BaseEnum;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus implements BaseEnum<UserStatus> {
    ACTIVE("활성화"),
    INACTIVE("비활성화"),
    BANNED("정지됨"),
    DELETED("삭제됨"),
    PENDING("인증 대기중");

    private final String status;

    @Override
    @JsonValue
    public String getStatus() {
        return status;
    }

    @JsonCreator
    public static UserStatus fromString(String name) {
        return BaseEnum.fromString(UserStatus.class, name);
    }
}
