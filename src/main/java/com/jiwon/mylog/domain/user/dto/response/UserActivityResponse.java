package com.jiwon.mylog.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;
import java.time.LocalDate;


@AllArgsConstructor
@Getter
public class UserActivityResponse {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate date;
    private final Long count;

    public UserActivityResponse(Date date, Long count) {
        this.date = date.toLocalDate();
        this.count = count;
    }
}
