package com.jiwon.mylog.entity.base;

import java.util.Arrays;

public interface BaseEnum<T extends Enum<T>>  {

    String getStatus();

    static <T extends Enum<T> & BaseEnum<T>> T fromString(Class<T> enumType, String name) {
        return Arrays.stream(enumType.getEnumConstants())
                .filter(e -> e.getStatus().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 값: " + name));
    }
}
