package com.jiwon.mylog.domain.image.service;

import com.jiwon.mylog.domain.image.dto.ImageResponse;
import com.jiwon.mylog.domain.image.dto.PresignedUrlResponse;
import com.jiwon.mylog.domain.image.entity.Image;
import com.jiwon.mylog.domain.image.repository.ImageRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.aws.S3Service;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@RequiredArgsConstructor
@Service
public class ImageService {

    private static final String[] EXTENSIONS = {"jpg", "jpeg", "png"};
    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Transactional
    public ImageResponse uploadProfileImage(Long userId, String fileName) {
        validateImage(fileName);
        PresignedUrlResponse response = s3Service.generatePutPresignedUrl(fileName);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
        deleteOldImage(user.getProfileImage());

        Image newImage = imageRepository.save(Image.forUserProfile(user, response.getKey()));
        return ImageResponse.create(response.getPresignedUrl(), fileName, newImage.getImageType().getStatus());
    }

    private void deleteOldImage(Image oldImage) {
        if (oldImage != null) {
            s3Service.deleteFile(oldImage.getFileKey());
            imageRepository.delete(oldImage);
        }
    }

    private void validateImage(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension.isBlank() || Arrays.stream(EXTENSIONS).noneMatch(extension::equals)) {
            throw new IllegalArgumentException("잘못된 확장자입니다.");
        }
    }
}
