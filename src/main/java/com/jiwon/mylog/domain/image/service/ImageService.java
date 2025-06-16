package com.jiwon.mylog.domain.image.service;

import com.jiwon.mylog.domain.image.dto.ImageResponse;
import com.jiwon.mylog.domain.image.dto.PresignedUrlResponse;
import com.jiwon.mylog.domain.image.entity.ProfileImage;
import com.jiwon.mylog.domain.image.repository.ProfileImageRepository;
import com.jiwon.mylog.domain.user.entity.User;
import com.jiwon.mylog.domain.user.repository.UserRepository;
import com.jiwon.mylog.global.aws.S3Service;
import com.jiwon.mylog.global.common.error.ErrorCode;
import com.jiwon.mylog.global.common.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

    private static final String IMAGE_PROFILE = "PROFILE";
    private static final String[] EXTENSIONS = {"jpg", "jpeg", "png"};
    private final S3Service s3Service;
    private final UserRepository userRepository;
    private final ProfileImageRepository profileImageRepository;

    @Transactional(readOnly = true)
    public ImageResponse getProfileImage(Long userId) {
        ProfileImage profileImage = profileImageRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND));
        return ImageResponse.create(null, profileImage.getFileKey(), IMAGE_PROFILE);
    }

    @Transactional
    public ImageResponse uploadProfileImage(Long userId, String fileName) {

        validateImage(fileName);
        PresignedUrlResponse response = s3Service.generatePutPresignedUrl(fileName);

        User user = getUser(userId);

        ProfileImage profileImage = profileImageRepository.findByUserId(userId)
                .orElseGet(() -> ProfileImage.forUserProfile(user));
        profileImage.updateProfile(response.getKey());

        if (profileImage.getId() == null) {
            profileImageRepository.save(profileImage);
        }

        return ImageResponse.create(response.getPresignedUrl(), response.getKey(), IMAGE_PROFILE);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = getUser(userId);

        if (user.getProfileImage() != null) {
            String fileKey = user.getProfileImage().getFileKey();
            user.deleteProfileImage();
            deleteImageFromS3(fileKey);
        }
    }
    private void deleteImageFromS3(String fileKey) {
        s3Service.deleteFile(fileKey);
    }

    private void validateImage(String fileName) {
        String extension = StringUtils.getFilenameExtension(fileName);
        if (extension.isBlank() || Arrays.stream(EXTENSIONS).noneMatch(e -> e.equalsIgnoreCase(extension))) {
            throw new IllegalArgumentException("잘못된 확장자입니다.");
        }
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_FOUND_USER));
    }
}
