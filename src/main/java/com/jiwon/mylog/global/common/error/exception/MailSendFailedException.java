package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class MailSendFailedException extends CustomException{
    public MailSendFailedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
