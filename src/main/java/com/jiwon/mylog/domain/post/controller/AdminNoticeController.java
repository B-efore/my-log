package com.jiwon.mylog.domain.post.controller;

import com.jiwon.mylog.domain.post.dto.request.PostRequest;
import com.jiwon.mylog.domain.post.dto.response.PostDetailResponse;
import com.jiwon.mylog.domain.post.entity.Post;
import com.jiwon.mylog.domain.post.service.PostService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@RestController
@Tag(name = "notices", description = "공지글 API")
public class AdminNoticeController {

    private final PostService postService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/hello")
    public ResponseEntity<String> hello(@LoginUser Long userId) {
        return new ResponseEntity<>("hello!", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/notices")
    @Operation(
            summary = "공지글 생성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "공지글 생성 성공"),
                    @ApiResponse(responseCode = "403", description = "권한 없음")
            })
    public ResponseEntity<PostDetailResponse> createNotice(
            @LoginUser Long userId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.createPost(userId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/notices/{postId}")
    @Operation(
            summary = "공지글 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "공지글 수정 성공"),
                    @ApiResponse(responseCode = "403", description = "공지글 수정 권한이 없음"),
                    @ApiResponse(responseCode = "404", description = "해당 공지글을 찾을 수 없음")
            })
    public ResponseEntity<PostDetailResponse> updateNotice(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequest postRequest) {
        PostDetailResponse response = postService.updatePost(userId, postId, postRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
