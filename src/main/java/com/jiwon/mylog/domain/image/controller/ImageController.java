package com.jiwon.mylog.domain.image.controller;

import com.jiwon.mylog.domain.image.dto.response.ImageResponse;
import com.jiwon.mylog.domain.image.service.ImageService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/users/me/profile")
    @Operation(
            summary = "로그인한 유저 프로필 업로드",
            description = "로그인 한 유저의 프로필 이미지를 업로드한다. (Presigned-Url 발급)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "프로필 이미지 업로드 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 이미지 요청"),
                    @ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없음")
            }
    )
    public ResponseEntity<ImageResponse> uploadProfile(
            @LoginUser Long userId,
            @RequestParam("fileName") String fileName) {
        ImageResponse response = imageService.uploadProfileImage(userId, fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<ImageResponse> getProfile(@PathVariable("userId") Long userId) {
        ImageResponse response = imageService.getProfileImage(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/users/me/profile")
    @Operation(
            summary = "로그인한 유저 프로필 삭제",
            description = "로그인 한 유저의 프로필 이미지를 삭제한다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "프로필 이미지 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 유저 혹은 이미지를 찾을 수 없음")
            }
    )
    public ResponseEntity<Void> deleteProfile(@LoginUser Long userId) {
        imageService.deleteProfileImage(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
