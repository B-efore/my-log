package com.jiwon.mylog.domain.tag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class TagCountListResponse {
    private final List<TagCountResponse> tags;
}
