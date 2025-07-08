package com.jiwon.mylog.domain.tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class TagCountPageResponse {
    private final List<TagCountResponse> tags;
    private final int page;
    private final int size;
    private final int totalPages;
    private final int totalElements;

    public static TagCountPageResponse from(
            List<TagCountResponse> tags,
            int page, int size, int totalPages, int totalElements) {
        return TagCountPageResponse.builder()
                .tags(tags)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}
