package com.jiwon.mylog.global.common.error.exception;


import com.jiwon.mylog.global.common.error.ErrorCode;

public class DuplicateException extends CustomException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
