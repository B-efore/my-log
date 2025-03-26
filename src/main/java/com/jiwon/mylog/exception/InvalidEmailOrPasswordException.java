package com.jiwon.mylog.exception;

public class InvalidEmailOrPasswordException extends CustomException {
    public InvalidEmailOrPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
