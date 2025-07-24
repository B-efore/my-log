package com.jiwon.mylog.domain.readme.dto;

import com.jiwon.mylog.domain.readme.entity.Readme;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReadmeResponse {
    private final String content;

    public static ReadmeResponse from(Readme readme) {
        if (readme == null) {
            return null;
        }
        return new ReadmeResponse(readme.getContent());
    }
}