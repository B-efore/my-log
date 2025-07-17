package com.jiwon.mylog.global.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class SliceResponse<T> {
    private final List<T> objects;
    private final int currentPage;
    private final int size;
    private final boolean first;
    private final boolean last;

    public static <T> SliceResponse<T> from(
            List<T> objects,
            int currentPage, int size, boolean first, boolean last) {
        return SliceResponse.<T>builder()
                .objects(objects)
                .currentPage(currentPage)
                .size(size)
                .first(first)
                .last(last)
                .build();
    }
}
