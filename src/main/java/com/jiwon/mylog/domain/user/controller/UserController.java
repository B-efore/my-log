package com.jiwon.mylog.domain.user.controller;

import com.jiwon.mylog.domain.user.dto.request.UserUpdateRequest;
import com.jiwon.mylog.domain.user.dto.response.UserDetailResponse;
import com.jiwon.mylog.domain.user.dto.response.UserInfoResponse;
import com.jiwon.mylog.domain.user.service.UserService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/users")
@RestController
@Tag(name = "users", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @PatchMapping
    @Operation(
            summary = "회원 정보 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
            })
    public ResponseEntity<UserDetailResponse> update(
            @LoginUser Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDetailResponse response = userService.update(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/me")
    @Operation(
            summary = "내 정보 조회",
            description = "로그인한 사용자의 정보를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
            })
    public ResponseEntity<UserInfoResponse> getMyInfo(@LoginUser Long userId) {
        UserInfoResponse response = userService.getUserInfo(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
