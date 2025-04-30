package com.jiwon.mylog.domain.post.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostSummaryPageResponse {
    private final List<PostSummaryResponse> posts;
    private final int page;
    private final int size;
    private final int totalPages;
    private final int totalElements;

    public static PostSummaryPageResponse from(
            List<PostSummaryResponse> posts,
            int page, int size, int totalPages, int totalElements) {
        return PostSummaryPageResponse.builder()
                .posts(posts)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}
