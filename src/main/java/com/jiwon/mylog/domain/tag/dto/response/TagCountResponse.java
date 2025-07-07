package com.jiwon.mylog.domain.tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TagCountResponse {
    private final Long id;
    private final String name;
    private final Long usageCount;
}
