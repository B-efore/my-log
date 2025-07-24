package com.jiwon.mylog.domain.readme.controller;

import com.jiwon.mylog.domain.readme.dto.ReadmeRequest;
import com.jiwon.mylog.domain.readme.dto.ReadmeResponse;
import com.jiwon.mylog.domain.readme.service.ReadmeService;
import com.jiwon.mylog.global.security.auth.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class ReadmeController {

    private final ReadmeService readmeService;

    @PostMapping("/readme")
    public ResponseEntity<ReadmeResponse> createReadme(
            @LoginUser Long userId, @RequestBody @Valid ReadmeRequest request) {
        ReadmeResponse response = readmeService.createReadme(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/readme")
    public ResponseEntity<ReadmeResponse> updateReadme(
            @LoginUser Long userId, @RequestBody @Valid ReadmeRequest request) {
        ReadmeResponse response = readmeService.updateReadme(userId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/readme")
    public ResponseEntity<Void> deleteReadme(@LoginUser Long userId) {
        readmeService.deleteReadme(userId);
        return ResponseEntity.noContent().build();
    }
}
