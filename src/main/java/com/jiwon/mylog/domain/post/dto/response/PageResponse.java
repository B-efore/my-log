package com.jiwon.mylog.domain.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PageResponse<T> {
    private final List<T> objects;
    private final int page;
    private final int size;
    private final int totalPages;
    private final long totalElements;

    public static <T> PageResponse<T> from(
            List<T> objects,
            int page, int size, int totalPages, long totalElements) {
        return PageResponse.<T>builder()
                .objects(objects)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}
