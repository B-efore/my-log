package com.jiwon.mylog.domain.image.repository;

import com.jiwon.mylog.domain.image.entity.Image;
import com.jiwon.mylog.domain.image.entity.ImageType;
import com.jiwon.mylog.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    Optional<Image> findByUserAndImageType(User user, ImageType imageType);
}
