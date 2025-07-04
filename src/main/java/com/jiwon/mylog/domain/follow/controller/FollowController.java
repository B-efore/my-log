package com.jiwon.mylog.domain.follow.controller;

import com.jiwon.mylog.domain.follow.dto.FollowCountResponse;
import com.jiwon.mylog.domain.follow.dto.FollowListResponse;
import com.jiwon.mylog.domain.follow.service.FollowService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class FollowController {

    private final FollowService followService;

    @PostMapping("/follow/{toUserId}")
    public ResponseEntity<Void> follow(
            @LoginUser Long fromUserId,
            @PathVariable("toUserId") Long toUserId) {
        followService.follow(fromUserId, toUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/follow/{toUserId}")
    public ResponseEntity<Void> unfollow(
            @LoginUser Long fromUserId,
            @PathVariable("toUserId") Long toUserId) {
        followService.unfollow(fromUserId, toUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{currentUserId}/followings/{targetUserId}")
    public ResponseEntity<?> checkFollowing(
            @PathVariable("currentUserId") Long currentUserId,
            @PathVariable("targetUserId") Long targetUserId) {
        Boolean response = followService.checkFollowing(currentUserId, targetUserId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/follows")
    public ResponseEntity<FollowCountResponse> getFollowCounts(@PathVariable Long userId) {
        FollowCountResponse response = followService.getFollowCounts(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable("userId") Long userId) {
        FollowListResponse response = followService.getFollowers(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/followings")
    public ResponseEntity<?> getFollowings(@PathVariable("userId") Long userId) {
        FollowListResponse response = followService.getFollowings(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}