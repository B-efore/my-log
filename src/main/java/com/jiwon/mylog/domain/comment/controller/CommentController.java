package com.jiwon.mylog.domain.comment.controller;

import com.jiwon.mylog.domain.comment.service.CommentService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import com.jiwon.mylog.domain.comment.dto.request.CommentCreateRequest;
import com.jiwon.mylog.domain.comment.dto.request.CommentUpdateRequest;
import com.jiwon.mylog.domain.comment.dto.response.CommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
@Tag(name = "comments", description = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @Operation(
            summary = "댓글 생성",
            responses = {
                    @ApiResponse(responseCode = "201", description = "댓글 생성 성공"),
                    @ApiResponse(responseCode = "404", description = "게시글 또는 댓글 작성자를 찾을 수 없음")
            }
    )
    public ResponseEntity<CommentResponse> create(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        CommentResponse response = commentService.create(userId, postId, commentCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/posts/{postId}/comments/{commentId}")
    @Operation(
            summary = "댓글 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
                    @ApiResponse(responseCode = "403", description = "댓글 수정 권한이 없음"),
                    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
            }
    )
    public ResponseEntity<CommentResponse> update(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        CommentResponse response = commentService.update(userId, postId, commentId, commentUpdateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    @Operation(
            summary = "댓글 삭제",
            responses = {
                    @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
                    @ApiResponse(responseCode = "403", description = "댓글 삭제 권한이 없음"),
                    @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
            }
    )
    public ResponseEntity<Void> delete(
            @LoginUser Long userId,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId) {
        commentService.delete(userId, postId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}