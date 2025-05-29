package com.jiwon.mylog.domain.image.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ImageResponse {
    private final String presignedUrl;
    private final String key;
    private final String type;

    public static ImageResponse create(String presignedUrl, String key, String type) {
        return ImageResponse.builder()
                .presignedUrl(presignedUrl)
                .key(key)
                .type(type)
                .build();
    }
}
