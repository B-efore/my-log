package com.jiwon.mylog.global.common.error.exception;

import com.jiwon.mylog.global.common.error.ErrorCode;

public class AmazonS3Exception extends CustomException{
    public AmazonS3Exception(ErrorCode errorCode) {
        super(errorCode);
    }
}
