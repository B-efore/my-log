package com.jiwon.mylog.security.token;

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
public class TokenController {

    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<Void> saveToken(@Valid @RequestBody TokenRequest request) {
        tokenService.saveToken(request);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
