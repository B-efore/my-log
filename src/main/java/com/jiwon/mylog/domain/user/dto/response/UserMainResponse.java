package com.jiwon.mylog.domain.user.dto.response;

import com.jiwon.mylog.domain.post.dto.response.PinnedPostResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.readme.dto.ReadmeResponse;
import com.jiwon.mylog.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
public class UserMainResponse {
    private final Long userId;
    private final String username;
    private final String bio;
    private final String imageKey;
    private final ReadmeResponse readme;
    private final List<PinnedPostResponse> pinnedPosts;
    private final List<UserActivityResponse> activities;

    public static UserMainResponse fromUser(
            User user,
            ReadmeResponse readme,
            List<PinnedPostResponse> pinnedPosts,
            List<UserActivityResponse> activities
    ) {
        return UserMainResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .bio(user.getBio())
                .imageKey(user.getProfileImage() == null ? "" : user.getProfileImage().getFileKey())
                .readme(readme)
                .pinnedPosts(pinnedPosts)
                .activities(activities)
                .build();
    }
}
