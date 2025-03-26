package com.jiwon.mylog.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_EMAIL_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 이메일 또는 비밀번호입니다."),

    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용중인 이메일입니다."),
    DUPLICATE_CATEGORY(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리입니다."),

    NOT_FOUND(HttpStatus.NOT_FOUND, "에 해당하는 값을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
