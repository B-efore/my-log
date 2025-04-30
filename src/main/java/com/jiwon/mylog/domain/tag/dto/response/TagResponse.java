package com.jiwon.mylog.domain.tag.dto.response;

import com.jiwon.mylog.domain.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TagResponse {
    private final Long id;
    private final String name;

    public static TagResponse fromTag(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
