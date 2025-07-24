package com.jiwon.mylog.domain.user.service;

import com.jiwon.mylog.domain.post.dto.response.PinnedPostResponse;
import com.jiwon.mylog.domain.post.repository.PostRepository;
import com.jiwon.mylog.domain.readme.dto.ReadmeResponse;
import com.jiwon.mylog.domain.readme.entity.Readme;
import com.jiwon.mylog.domain.readme.repository.ReadmeRepository;
import com.jiwon.mylog.domain.user.dto.response.UserActivityResponse;
import com.jiwon.mylog.domain.user.dto.response.UserMainResponse;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserBlogService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReadmeRepository readmeRepository;

    @Transactional(readOnly = true)
    public UserMainResponse getUserMain(Long userId) {
        User user = userRepository.findUserWithProfileImage(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));

        ReadmeResponse readme = getUserReadme(userId);
        List<PinnedPostResponse> pinnedPosts = getPinnedPosts(userId);
        List<UserActivityResponse> activities = getUserAnnualActivities(userId);

        return UserMainResponse.fromUser(user, readme, pinnedPosts, activities);
    }

    private ReadmeResponse getUserReadme(Long userId) {
        Readme readme = readmeRepository.findByUserId(userId).orElse(null);
        return ReadmeResponse.from(readme);
    }

    private List<PinnedPostResponse> getPinnedPosts(Long userId) {
        return postRepository.findPinnedPostsByUserId(userId);
    }

    private List<UserActivityResponse> getUserAnnualActivities(Long userId) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);
        return postRepository.findUserActivities(userId, startDate, endDate);
    }
}
