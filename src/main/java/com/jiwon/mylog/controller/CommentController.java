package com.jiwon.mylog.controller;

import com.jiwon.mylog.annotation.LoginUser;
import com.jiwon.mylog.entity.comment.dto.request.CommentCreateRequest;
import com.jiwon.mylog.entity.comment.dto.request.CommentUpdateRequest;
import com.jiwon.mylog.entity.comment.dto.response.CommentResponse;
import com.jiwon.mylog.service.CommentService;
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
@RequestMapping("/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponse> create(
            @LoginUser Long userId,
            @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        CommentResponse response = commentService.create(userId, commentCreateRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @LoginUser Long userId,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        CommentResponse response = commentService.update(userId, commentId, commentUpdateRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(
            @LoginUser Long userId,
            @PathVariable("commentId") Long commentId) {
        commentService.delete(userId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}