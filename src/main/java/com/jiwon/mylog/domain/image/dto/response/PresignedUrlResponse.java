package com.jiwon.mylog.domain.image.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PresignedUrlResponse {
    private String key;
    private String presignedUrl;

    public static PresignedUrlResponse create(String key, String presignedUrl) {
        return PresignedUrlResponse.builder()
                .key(key)
                .presignedUrl(presignedUrl)
                .build();
    }
}
