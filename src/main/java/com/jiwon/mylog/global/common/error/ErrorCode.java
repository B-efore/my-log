package com.jiwon.mylog.global.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    INVALID_ACCOUNT_ID_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "잘못된 아이디 또는 비밀번호입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),

    FAIlED_MAIL_SEND(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송에 실패했습니다."),
    INVALID_MAIL_CODE(HttpStatus.BAD_REQUEST, "인증 코드가 유효하지 않습니다."),
    NOT_FOUND_MAIL_CODE(HttpStatus.NOT_FOUND, "인증 코드를 찾을 수 없습니다."),

    S3_FAILED_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 업로드에 실패했습니다"),
    S3_FAILED_FILE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "S3 파일 삭제에 실패했습니다."),

    NOT_CONFIRM_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    DUPLICATE(HttpStatus.CONFLICT, "중복된 값이 존재합니다."),
    DUPLICATE_ACCOUNT_ID(HttpStatus.CONFLICT, "이미 사용중인 아이디입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),
    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),

    NOT_FOUND(HttpStatus.NOT_FOUND, "해당 객체를 찾을 수 없습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다.");

    private final HttpStatus status;
    private final String message;
}
