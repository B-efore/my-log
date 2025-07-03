package com.jiwon.mylog.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserProfilePageResponse {
    private final List<UserProfileResponse> users;
    private final int page;
    private final int size;
    private final int totalPages;
    private final int totalElements;

    public static UserProfilePageResponse from(
            List<UserProfileResponse> users,
            int page, int size, int totalPages, int totalElements) {
        return UserProfilePageResponse.builder()
                .users(users)
                .page(page)
                .size(size)
                .totalPages(totalPages)
                .totalElements(totalElements)
                .build();
    }
}
