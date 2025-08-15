package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class InvalidTokenException extends CustomException{
    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }

    public InvalidTokenException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
