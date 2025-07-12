package com.jiwon.mylog.domain.tag.dto.response;

import com.jiwon.mylog.domain.tag.entity.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TagResponse {
    private final Long tagId;
    private final String name;

    public static TagResponse fromTag(Tag tag) {
        return TagResponse.builder()
                .tagId(tag.getId())
                .name(tag.getName())
                .build();
    }
}
