package com.jiwon.mylog.domain.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Getter
public class FindIdResponse {
    private String accountId;
    private String provider;

    public static FindIdResponse toLocal(String accountId) {
        return new FindIdResponse(accountId, "local");
    }

    public static FindIdResponse toSocial(String provider) {
        return new FindIdResponse(null, provider);
    }
}
