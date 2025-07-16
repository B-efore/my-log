package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
