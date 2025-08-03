package com.jiwon.mylog.domain.image.dto.request;

import java.util.List;

public record MultiImageRequest(List<ImageRequest> images) {
}
