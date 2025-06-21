package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class InvalidAccountIdOrPasswordException extends CustomException {
    public InvalidAccountIdOrPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
