package com.jiwon.mylog.global.aws;

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
import software.amazon.awssdk.services.s3.model.StorageClass;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;
    private final AwsProperties awsProperties;

    public String uploadFile(MultipartFile multipartFile) {
        try {
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            PutObjectRequest putObjectRequest = getPutObjectRequest(multipartFile, fileName);
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(multipartFile.getInputStream().readAllBytes())
            );

            return s3Client.utilities().getUrl(url -> url.bucket(awsProperties.getS3().getBucket()).key(fileName)).toString();
        } catch (S3Exception e) {
            throw new AmazonS3Exception(ErrorCode.S3_FAILED_FILE_UPLOAD);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
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

    private PutObjectRequest getPutObjectRequest(MultipartFile multipartFile, String fileName) {
        return PutObjectRequest.builder()
                .bucket(awsProperties.getS3().getBucket())
                .key(fileName)
                .contentLength(multipartFile.getSize())
                .storageClass(StorageClass.GLACIER)
                .build();
    }
}
