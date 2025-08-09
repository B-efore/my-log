package com.jiwon.mylog.domain.like.controller;

import com.jiwon.mylog.domain.like.service.PostLikeService;
import com.jiwon.mylog.global.common.entity.PageResponse;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class LikeController {

    private final PostLikeService postLikeService;

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> likePost(@LoginUser Long userId, @PathVariable Long postId) {
        postLikeService.like(userId, postId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<Void> unlikePost(@LoginUser Long userId, @PathVariable Long postId) {
        postLikeService.unlike(userId, postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/posts/{postId}/likes/count")
    public ResponseEntity<Long> countPostLike(@PathVariable Long postId) {
        long likeCount = postLikeService.countLikes(postId);
        return ResponseEntity.ok(likeCount);
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<Boolean> isLikedPost(@LoginUser Long userId, @PathVariable Long postId) {
        boolean likeStatus = postLikeService.isLiked(userId, postId);
        return ResponseEntity.ok(likeStatus);
    }

    @GetMapping("/users/{userId}/likes")
    public ResponseEntity<PageResponse> getUserLikes(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse response = postLikeService.getUserLikes(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
