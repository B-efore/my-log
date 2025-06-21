package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class MailException extends CustomException{
    public MailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
