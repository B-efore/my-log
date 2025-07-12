package com.jiwon.mylog.domain.user.controller;

import com.jiwon.mylog.domain.post.dto.response.PageResponse;
import com.jiwon.mylog.domain.user.dto.request.UserProfileRequest;
import com.jiwon.mylog.domain.user.dto.response.UserActivitiesResponse;
import com.jiwon.mylog.domain.user.dto.response.UserMainResponse;
import com.jiwon.mylog.domain.user.dto.response.UserResponse;
import com.jiwon.mylog.domain.user.service.UserService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
@Tag(name = "users", description = "사용자 API")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(
            summary = "내 정보 조회",
            description = "로그인한 사용자의 정보를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
            })
    public ResponseEntity<UserResponse> getMyProfile(@LoginUser Long userId) {
        UserResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me")
    @Operation(
            summary = "회원 정보 수정",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 정보"),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
            })
    public ResponseEntity<UserResponse> updateMyProfile(
            @LoginUser Long userId,
            @Valid @RequestBody UserProfileRequest request) {
        UserResponse response = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "회원 메인 정보 조회",
            description = "회원의 메인 화면에 출력될 전반적인 정보를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
            })
    public ResponseEntity<UserMainResponse> getUserMain(@PathVariable("userId") Long userId) {
        UserMainResponse response = userService.getUserMain(userId);
        return ResponseEntity.ok(response);

    }

    @GetMapping("/{userId}/activity")
    @Operation(
            summary = "유저 활동 내역 조회 (활동 날짜 - 횟수)",
            description = "일정 기간 동안의 유저 활동 일자와 활동 횟수를 조회한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "유저 활동 내역 조회 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
            }
    )
    public ResponseEntity<UserActivitiesResponse> getUserActivity(
            @PathVariable("userId") Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        UserActivitiesResponse response = userService.getUserActivity(userId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(
            summary = "회원 조회 (유저 이름으로 검색)",
            description = "회원 이름을 통해 회원을 조회한다."
    )
    public ResponseEntity<PageResponse> searchWithUsername(
            @RequestParam String username,
            @PageableDefault(size = 10, page = 0,
            sort = "username", direction = Sort.Direction.ASC) Pageable pageable) {
        PageResponse response = userService.searchWithUsername(username, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/me/items/{itemId}")
    @Operation(
            summary = "회원 아이템 구매",
            description = "회원에 아이템을 추가한다."
    )
    public ResponseEntity<Void> purchaseItem(@LoginUser Long userId, @PathVariable("itemId") Long itemId) {
        userService.purchaseItem(userId, itemId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
