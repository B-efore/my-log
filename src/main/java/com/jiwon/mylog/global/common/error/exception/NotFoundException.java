package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class NotFoundException extends CustomException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, long id) {
        super(errorCode, id + errorCode.getMessage());
    }
}
