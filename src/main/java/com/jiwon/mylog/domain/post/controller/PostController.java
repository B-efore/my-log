package com.jiwon.mylog.domain.post.controller;

import com.jiwon.mylog.domain.post.service.PostService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.dto.response.PostSummaryPageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<PostDetailResponse> createPost(
            @LoginUser Long userId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.createPost(userId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> updatePost(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.updatePost(userId, postId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId) {
        postService.deletePost(userId, postId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable("postId") Long postId) {
        PostDetailResponse response = postService.getPost(postId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<PostSummaryPageResponse> getAllPosts(
            @PathVariable("userId") Long userId,
            @PageableDefault(size = 10, page = 0,
                    sort = "createdAt", direction = Direction.DESC) Pageable pageable) {
        PostSummaryPageResponse response = postService.getAllPosts(userId, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
