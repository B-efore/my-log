package com.jiwon.mylog.domain.image.repository;

import com.jiwon.mylog.domain.image.entity.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
    @Query("select pi from ProfileImage pi where pi.user.id = :userId")
    Optional<ProfileImage> findByUserId(@Param("userId") Long userId);
}