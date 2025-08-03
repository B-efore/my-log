package com.jiwon.mylog.domain.image.dto.response;

import java.util.List;

public record MultiPresignedUrlResponse(List<PresignedUrlResponse> presignedUrls) {
}
