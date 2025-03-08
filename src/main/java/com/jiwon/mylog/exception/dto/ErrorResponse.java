package com.jiwon.mylog.exception.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private Map<String, String> errors;
}
