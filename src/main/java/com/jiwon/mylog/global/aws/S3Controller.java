package com.jiwon.mylog.global.aws;

import com.jiwon.mylog.domain.image.dto.request.ImageRequest;
import com.jiwon.mylog.domain.image.dto.request.MultiImageRequest;
import com.jiwon.mylog.domain.image.dto.response.MultiPresignedUrlResponse;
import com.jiwon.mylog.domain.image.dto.response.PresignedUrlResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api/s3")
@RestController
@Tag(name = "s3", description = "amazon s3 API")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String response = s3Service.uploadFile(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlResponse> generatePutPresignedUrl(@RequestBody ImageRequest request) {
        PresignedUrlResponse response = s3Service.generatePutPresignedUrl(request.fileName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/presigned-urls")
    public ResponseEntity<MultiPresignedUrlResponse> generatePutMultiPresignedUrl(
            @RequestBody MultiImageRequest request) {
        MultiPresignedUrlResponse response = s3Service.generatePutPresignedUrls(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFile(@RequestParam String fileName) {
        s3Service.deleteFile(fileName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
