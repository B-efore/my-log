package com.jiwon.mylog.global.aws;

import com.jiwon.mylog.domain.image.dto.PresignedUrlResponse;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.AmazonS3Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsProperties awsProperties;

    public String uploadFile(MultipartFile multipartFile) {
        try {
            String key = generateKey(multipartFile.getOriginalFilename());
            PutObjectRequest putObjectRequest = getPutObjectRequest(key);

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(multipartFile.getInputStream().readAllBytes())
            );

            return s3Client.utilities().getUrl(url -> url.bucket(awsProperties.getS3().getBucket()).key(key)).toString();
        } catch (S3Exception e) {
            throw new AmazonS3Exception(ErrorCode.S3_FAILED_FILE_UPLOAD);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    public PresignedUrlResponse generatePutPresignedUrl(String fileName) {
        String key = generateKey(fileName);
        PutObjectRequest putObjectRequest = getPutObjectRequest(key);
        String url = s3Presigner.presignPutObject(builder ->
                        builder
                                .signatureDuration(Duration.ofMinutes(30))
                                .putObjectRequest(putObjectRequest)
                )
                .url()
                .toString();

        return PresignedUrlResponse.create(key, url);
    }

    public void deleteFile(String fileName) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(awsProperties.getS3().getBucket())
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (S3Exception e) {
            throw new AmazonS3Exception(ErrorCode.S3_FAILED_FILE_DELETE);
        }
    }

    private PutObjectRequest getPutObjectRequest(String key) {
        return PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(key)
                .build();
    }

    private String generateKey(String fileName) {
        return UUID.randomUUID() + "_" + fileName;
    }
}
