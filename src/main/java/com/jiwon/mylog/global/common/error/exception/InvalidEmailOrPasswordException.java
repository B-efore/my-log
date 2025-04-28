package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class InvalidEmailOrPasswordException extends CustomException {
    public InvalidEmailOrPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
