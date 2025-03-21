package com.jiwon.mylog.entity.post.dto.request;

import com.jiwon.mylog.entity.tag.dto.request.TagRequest;
import java.util.List;
import lombok.Getter;

@Getter
public class PostCreateRequest {
    private String title;
    private String content;
    private String contentPreview;
    private String visibility;
    private Long categoryId;
    private List<TagRequest> tagRequests;
}
