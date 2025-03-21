package com.jiwon.mylog.entity.category.dto.response;

import com.jiwon.mylog.entity.category.Category;
import com.jiwon.mylog.entity.post.Post;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private final String name;
    private final int postCount;
    private final List<Post> posts;

    public static CategoryResponse fromCategory(Category category) {
        return CategoryResponse.builder()
                .name(category.getName())
                .postCount(category.getPostCount())
                .posts(category.getPosts())
                .build();
    }
}
