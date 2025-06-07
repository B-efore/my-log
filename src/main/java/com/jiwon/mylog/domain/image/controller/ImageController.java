package com.jiwon.mylog.domain.image.controller;

import com.jiwon.mylog.domain.image.dto.ImageResponse;
import com.jiwon.mylog.domain.image.service.ImageService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/images")
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/profile")
    public ResponseEntity<ImageResponse> uploadProfile(
            @LoginUser Long userId,
            @RequestParam String fileName) {
        ImageResponse response = imageService.uploadProfileImage(userId, fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
