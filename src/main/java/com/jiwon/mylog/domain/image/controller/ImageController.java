package com.jiwon.mylog.domain.image.controller;

import com.jiwon.mylog.domain.image.dto.ImageResponse;
import com.jiwon.mylog.domain.image.service.ImageService;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.ForbiddenException;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/images")
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/profile")
    public ResponseEntity<ImageResponse> uploadProfile(
            @LoginUser Long userId,
            @RequestParam("fileName") String fileName) {
        ImageResponse response = imageService.uploadProfileImage(userId, fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/profile/{userId}")
    public ResponseEntity<Void> deleteProfile(@LoginUser Long loginUserId, @PathVariable("userId") Long userId) {

        if(!loginUserId.equals(userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        imageService.deleteProfileImage(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<ImageResponse> getProfile(@PathVariable("userId") Long userId) {
        ImageResponse response = imageService.getProfileImage(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
