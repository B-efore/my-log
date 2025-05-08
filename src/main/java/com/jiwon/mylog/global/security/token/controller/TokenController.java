package com.jiwon.mylog.global.security.token.controller;

import com.jiwon.mylog.global.security.token.dto.request.TokenRequest;
import com.jiwon.mylog.global.security.token.sevice.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/tokens")
@RestController
@Tag(name = "tokens", description = "토큰 API")
public class TokenController {

    private final TokenService tokenService;

    @PostMapping
    @Operation(
            summary = "리프레시 토큰 저장",
            responses = {
                    @ApiResponse(responseCode = "200", description = "리프레시 토큰을 DB에 저장")
            }
    )
    public ResponseEntity<Void> saveToken(@Valid @RequestBody TokenRequest request) {
        tokenService.saveToken(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
