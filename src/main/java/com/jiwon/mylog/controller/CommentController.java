package com.jiwon.mylog.controller;

import com.jiwon.mylog.annotation.LoginUser;
import com.jiwon.mylog.entity.comment.dto.request.CommentRequest;
import com.jiwon.mylog.entity.comment.dto.response.CommentResponse;
import com.jiwon.mylog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse response = commentService.create(userId, commentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}