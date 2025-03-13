package com.jiwon.mylog.exception;

public class InvalidEmailOrPasswordException extends RuntimeException{
    public InvalidEmailOrPasswordException(String message) {
        super(message);
    }
}
