package com.jiwon.mylog.controller;

import com.jiwon.mylog.annotation.LoginUser;
import com.jiwon.mylog.entity.post.dto.request.PostRequest;
import com.jiwon.mylog.entity.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetailResponse> createPost(
            @LoginUser Long userId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.createPost(userId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> editPost(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.editPost(userId, postId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPost(
            @PathVariable("postId") Long postId) {
        PostDetailResponse response = postService.getPost(postId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
