package com.jiwon.mylog.exception;


public class DuplicateException extends CustomException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
