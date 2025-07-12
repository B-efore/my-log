package com.jiwon.mylog.domain.follow.controller;

import com.jiwon.mylog.domain.follow.dto.FollowCountResponse;
import com.jiwon.mylog.domain.follow.dto.FollowListResponse;
import com.jiwon.mylog.domain.follow.service.FollowService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Operation(
            summary = "팔로우",
            description = "로그인 한 유저가 다른 유저를 팔로우 한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팔로우 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 팔로우 요청")
            }
    )
    public ResponseEntity<Void> follow(
            @LoginUser Long fromUserId,
            @PathVariable("toUserId") Long toUserId) {
        followService.follow(fromUserId, toUserId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/follow/{toUserId}")
    @Operation(
            summary = "언팔로우",
            description = "로그인 한 유저가 다른 유저를 언팔로우 한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "언팔로우 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 팔로우 요청")
            }
    )
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

    @GetMapping("/{userId}/followings")
    @Operation(
            summary = "특정 유저의 팔로잉 목록 확인",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팔로잉 목록 조회 성공"),
            }
    )
    public ResponseEntity<?> getFollowings(@PathVariable("userId") Long userId) {
        FollowListResponse response = followService.getFollowings(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/followers")
    @Operation(
            summary = "특정 유저의 팔로워 목록 확인",
            responses = {
                    @ApiResponse(responseCode = "200", description = "팔로워 목록 조회 성공"),
            }
    )
    public ResponseEntity<?> getFollowers(@PathVariable("userId") Long userId) {
        FollowListResponse response = followService.getFollowers(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{userId}/follows")
    @Operation(
            summary = "특정 유저의 팔로잉/팔로워 수 조회",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
            }
    )
    public ResponseEntity<FollowCountResponse> getFollowCounts(@PathVariable("userId") Long userId) {
        FollowCountResponse response = followService.getFollowCounts(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}